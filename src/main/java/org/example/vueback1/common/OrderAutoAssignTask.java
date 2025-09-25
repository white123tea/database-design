package org.example.vueback1.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vueback1.entity.*;
import org.example.vueback1.common.OrderPathInfo;
import org.example.vueback1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class OrderAutoAssignTask {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IndentService indentService;

    @Autowired
    private UserService userService;
    @Autowired
    private IndentRecordService indentRecordService;
    @Autowired
    private CourierViewService courierViewService;
    @Autowired
    private CourierService courierService;

    @Scheduled(cron = "0/10 * * * * ?")
    @Transactional
    public void autoScanCourier(){
        List<CourierView>couriers=courierViewService.list(
                new LambdaQueryWrapper<CourierView>().eq(CourierView::getCourierStatus,1)
        );
        for(CourierView courier:couriers){
            Integer courierId = courier.getId();
            List<Indent> Orders = indentService.list(
                    new LambdaQueryWrapper<Indent>()
                            .eq(Indent::getCourierId, courierId)
            );
            if(Orders.isEmpty()){
                System.out.println("快递员："+courier.getName()+" 修改为空闲状态");
                Courier cou=courierService.getById(courier.getId());
                cou.setCourierStatus(0);
                courierService.updateById(cou);
            }
        }
    }

    // 每5分钟执行一次
    @Scheduled(cron = "0/10 * * * * ?")
    @Transactional
    public void autoAssignOrders() {
        try {
            //查询待分配订单（状态0 + allLines非空）
            List<Indent> pendingOrders = indentService.list(
                    new LambdaQueryWrapper<Indent>()
                            .eq(Indent::getStatus, 0).or().eq(Indent::getStatus, 5)
                            .isNotNull(Indent::getAllLines)
                            .ne(Indent::getAllLines, "")
            );
            System.out.println("待分配订单总数：" + pendingOrders.size());

            if (pendingOrders.isEmpty()) {
                System.out.println("没有待分配的订单，跳过本次分配");
                return;
            }

            //查询可用快递员：roleId=2（快递员）+ isvalid=1（账号有效）+ courierStatus=0（空闲）
            List<CourierView>couriers=courierViewService.list(
                    new LambdaQueryWrapper<CourierView>().eq(CourierView::getCourierStatus,0)
            );
            System.out.println("可用空闲快递员总数：" + couriers.size());

            if (couriers.isEmpty()) {
                System.out.println("没有可用的空闲快递员，跳过本次分配");
                return;
            }

            //按「当前驿站-下一驿站」分组订单
            Map<String, List<Indent>> groupedOrders = groupOrdersByStations(pendingOrders);

            //为每个分组分配订单
            for (Map.Entry<String, List<Indent>> entry : groupedOrders.entrySet()) {
                String groupKey = entry.getKey();
                List<Indent> ordersInGroup = entry.getValue();
                System.out.printf("处理分组：%s，待分配订单数：%d%n", groupKey, ordersInGroup.size());

                assignOrdersToCouriers(ordersInGroup, couriers);
            }

        } catch (Exception e) {
            System.err.println("自动分配订单异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 按「当前驿站ID-下一驿站ID」分组订单
     */
    private Map<String, List<Indent>> groupOrdersByStations(List<Indent> orders) {
        Map<String, List<Indent>> stationGroupMap = new HashMap<>();

        for (Indent order : orders) {
            try {
                OrderPathInfo pathInfo = objectMapper.readValue(order.getAllLines(), OrderPathInfo.class);
                List<Integer> stationPath = pathInfo.getPath();
                Integer currentStationId = order.getCurStationId();
                System.out.println(currentStationId);

                if (stationPath == null || stationPath.isEmpty() || currentStationId == null) {
                    System.out.printf("订单ID：%d，路径或当前驿站为空，跳过分组%n", order.getId());
                    continue;
                }

                // 确定下一驿站
                int currentIndex = stationPath.indexOf(currentStationId);
                if (currentIndex == -1) {
                    System.out.printf("订单ID：%d，当前驿站（ID：%d）不在路径中，跳过分组%n", order.getId(), currentStationId);
                    continue;
                }
                if (currentIndex >= stationPath.size() - 1) {
                    System.out.printf("订单ID：%d，当前驿站是路径终点，无需分配%n", order.getId());
                    continue;
                }

                // 生成分组键并添加订单
                Integer nextStationId = stationPath.get(currentIndex + 1);
                String groupKey = currentStationId + "-" + nextStationId;
                stationGroupMap.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(order);

            } catch (Exception e) {
                System.err.printf("订单ID：%d，路径解析失败：%s%n", order.getId(), e.getMessage());
            }
        }

        return stationGroupMap;
    }

    /**
     * 为一组订单分配快递员（核心改动：分配后更新快递员为忙碌状态）
     */
    private void assignOrdersToCouriers(List<Indent> orders, List<CourierView> couriers) {
        //按订单价值降序：优先分配高价值订单
        orders.sort(Comparator.comparingInt(Indent::getGoodsValue).reversed());

        //计算每个快递员当前负载（已分配未完成订单总价值）
        Map<Integer, Integer> courierLoadMap = calculateCourierCurrentLoad(couriers);

        //逐个订单分配
        for (Indent order : orders) {
            CourierView suitableCourier = findSuitableCourier(order, couriers, courierLoadMap);
            if (suitableCourier == null) {
                System.out.printf("订单ID：%d，无可用快递员（驿站不匹配/负载超限/非空闲），跳过%n", order.getId());
                continue;
            }

            Courier cou=courierService.getById(suitableCourier.getId());
            cou.setCourierStatus(1);
            boolean updateCourierStatus = courierService.updateById(cou);
            if (!updateCourierStatus) {
                System.err.printf("订单ID：%d，快递员ID：%d 状态更新失败，跳过该订单%n", order.getId(), suitableCourier.getId());
                continue;
            }

            //更新订单信息（分配快递员+改状态为已分配）
            order.setCourierId(suitableCourier.getId());
            order.setStatus(1);
            boolean updateOrder = indentService.updateById(order);
            if (updateOrder) {
                // 更新快递员负载（仅作记录，状态已控制不可再分配）
                int newLoad = courierLoadMap.get(suitableCourier.getId()) + order.getGoodsValue();
                courierLoadMap.put(suitableCourier.getId(), newLoad);
                //记录订单轨迹新状态
                IndentRecord record = new IndentRecord();
                record.setTime(LocalDateTime.now());
                record.setCourierId(suitableCourier.getId());
                record.setIndentId(order.getId());
                record.setStationId(order.getCurStationId());
                record.setStatus(1);
                indentRecordService.save(record);

                System.out.printf("订单ID：%d，成功分配给快递员ID：%d（当前负载：%d，状态已设为忙碌）%n",
                        order.getId(), suitableCourier.getId(), newLoad);
            } else {
                // 订单更新失败时，回滚快递员状态（避免快递员被误设为忙碌）
                cou.setCourierStatus(0);
                courierService.updateById(cou);
                System.err.printf("订单ID：%d 更新失败，已回滚快递员ID：%d 状态为空闲%n", order.getId(), suitableCourier.getId());
            }
        }
    }

    /**
     * 计算快递员当前负载（无改动，仅保留完整逻辑）
     */
    private Map<Integer, Integer> calculateCourierCurrentLoad(List<CourierView> couriers) {
        Map<Integer, Integer> loadMap = new HashMap<>();

        for (CourierView courier : couriers) {
            List<Indent> assignedOrders = indentService.list(
                    new LambdaQueryWrapper<Indent>()
                            .eq(Indent::getCourierId, courier.getId())
                            .eq(Indent::getStatus, 1) // 仅统计已分配未完成的订单
            );

            // 求和订单总价值（空列表时默认0）
            int totalValue = assignedOrders.stream()
                    .mapToInt(Indent::getWeight)
                    .sum();

            loadMap.put(courier.getId(), totalValue);
        }

        return loadMap;
    }

    /**
     * 找到合适的快递员（核心改动：新增快递员空闲状态校验，修复排序逻辑）
     */
    private CourierView findSuitableCourier(Indent order, List<CourierView> couriers, Map<Integer, Integer> courierLoadMap) {
        int orderValue = order.getWeight();
        Integer orderCurStationId = order.getCurStationId();

        return couriers.stream()
                // 筛选条件
                .filter(courier -> {
                    //排除空值
                    if (orderCurStationId == null
                            || courier.getStationId() == null
                            || courier.getCourierStatus() == null) {
                        return false;
                    }
                    //快递员所属驿站 = 订单当前驿站
                    boolean isSameStation = courier.getStationId().equals(orderCurStationId);
                    //负载+当前订单重量
                    int currentLoad = courierLoadMap.getOrDefault(courier.getId(), 0);
                    boolean isLoadEnough = currentLoad + orderValue <= 1000;

                    return isSameStation && isLoadEnough;
                })
                // 排序：优先选当前负载最大的快递员
                .max(Comparator.comparingInt(courier -> courierLoadMap.get(courier.getId())))
                .orElse(null); // 无符合条件的快递员返回null
    }

}