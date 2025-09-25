package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.example.vueback1.common.QueryPageParam;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.Indent;
import org.example.vueback1.entity.IndentRecord;
import org.example.vueback1.entity.Line;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.IndentRecordService;
import org.example.vueback1.service.IndentService;
import org.example.vueback1.service.LineService;
import org.example.vueback1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyx
 * @since 2025-08-18
 */
@RestController
@RequestMapping("/indent")
public class IndentController {
    @Autowired
    private IndentService indentService;
    @Autowired
    private UserService userService;
    @Autowired
    private LineService lineService;
    @Autowired
    private IndentRecordService indentRecordService;

    @GetMapping("/list")
    public List<Indent> list(){
        return indentService.list();
    }

    @GetMapping("getById")
    public Result getById(@RequestParam Integer id){
        Indent indent = indentService.getById(id);
        if(indent == null){
            return Result.fail();
        }
        return Result.suc(indentService.getById(id));
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 计算单个订单的最佳路径，返回JSON格式字符串（适配varchar存储）
     * @param indentId 订单ID
     * @return 路径信息字符串（JSON格式，可直接存入varchar字段）
     */
    public String calculateBestPath(Integer indentId) {
        // 校验订单合法性
        Indent indent = indentService.getById(indentId);
        if (indent == null) {
            return "{}"; // 空路径（varchar兼容）
        }

        Integer startId = indent.getSendStationId();
        Integer endId = indent.getReceiveStationId();

        // 校验起点终点
        if (startId == null || endId == null || startId.equals(endId)) {
            return "{}";
        }

        // 获取线路并构建图
        List<Line> lines = lineService.list();
        if (CollectionUtils.isEmpty(lines)) {
            return "{}";
        }
        Map<Integer, List<Map<String, Integer>>> graph = buildGraph(lines);

        // 计算最短路径
        Map<String, Object> pathResult = dijkstra(graph, startId, endId);

        Integer totalDistance = (Integer) pathResult.get("totalDistance");
        System.out.println("totalDistance: " + totalDistance);
        try {
            return objectMapper.writeValueAsString(pathResult);
        } catch (JsonProcessingException e) {
            return "{}"; // 序列化失败时返回空字符串
        }
    }

    /**
     * 构建双向图的邻接表
     */
    private Map<Integer, List<Map<String, Integer>>> buildGraph(List<Line> lines) {
        Map<Integer, List<Map<String, Integer>>> graph = new HashMap<>();

        for (Line line : lines) {
            Integer a = line.getStationAId();
            Integer b = line.getStationBId();
            Integer distance = line.getDistance();

            if (a == null || b == null || distance == null || distance <= 0) {
                continue; // 跳过无效线路
            }

            // 添加A→B的边
            Map<String, Integer> edgeAB = new HashMap<>();
            edgeAB.put("stationId", b);
            edgeAB.put("distance", distance);
            graph.computeIfAbsent(a, k -> new ArrayList<>()).add(edgeAB);

            // 添加B→A的边（双向）
            Map<String, Integer> edgeBA = new HashMap<>();
            edgeBA.put("stationId", a);
            edgeBA.put("distance", distance);
            graph.computeIfAbsent(b, k -> new ArrayList<>()).add(edgeBA);
        }

        return graph;
    }

    /**
     * Dijkstra算法计算最短路径
     */
    private Map<String, Object> dijkstra(Map<Integer, List<Map<String, Integer>>> graph,
                                         Integer start, Integer end) {
        Map<Integer, Integer> distances = new HashMap<>(); // 起点到各节点的距离
        Map<Integer, Integer> predecessors = new HashMap<>(); // 前驱节点
        PriorityQueue<Map<String, Integer>> queue = new PriorityQueue<>(
                Comparator.comparingInt(a -> a.get("distance"))
        );

        // 初始化距离（默认无穷大）
        graph.keySet().forEach(stationId -> distances.put(stationId, Integer.MAX_VALUE));
        distances.put(start, 0);

        // 起点入队
        Map<String, Integer> startNode = new HashMap<>();
        startNode.put("stationId", start);
        startNode.put("distance", 0);
        queue.add(startNode);

        // 算法核心逻辑
        while (!queue.isEmpty()) {
            Map<String, Integer> current = queue.poll();
            Integer currentId = current.get("stationId");
            Integer currentDist = current.get("distance");

            // 到达终点或当前路径非最短，跳过
            if (currentId.equals(end) || currentDist > distances.get(currentId)) {
                continue;
            }

            // 遍历邻接节点
            for (Map<String, Integer> neighbor : graph.getOrDefault(currentId, new ArrayList<>())) {
                Integer nextId = neighbor.get("stationId");
                Integer weight = neighbor.get("distance");
                Integer newDist = currentDist + weight;

                // 发现更短路径
                if (newDist < distances.get(nextId)) {
                    distances.put(nextId, newDist);
                    predecessors.put(nextId, currentId);

                    // 新节点入队
                    Map<String, Integer> nextNode = new HashMap<>();
                    nextNode.put("stationId", nextId);
                    nextNode.put("distance", newDist);
                    queue.add(nextNode);
                }
            }
        }

        // 构建路径
        List<Integer> path = new ArrayList<>();
        Integer current = end;
        while (current != null) {
            path.add(current);
            current = predecessors.get(current);
        }
        Collections.reverse(path);

        // 封装结果（varchar存储的字符串内容）
        Map<String, Object> result = new HashMap<>();
        result.put("path", path); // 路径：[1,3,5]
        result.put("totalDistance", distances.get(end) == Integer.MAX_VALUE ? 0 : distances.get(end)); // 总距离
        result.put("valid", path.size() > 1); // 是否有效路径

        return result;
    }


    @PostMapping("/update1")
    public Boolean update1(@RequestBody Indent inden) {
        Integer id = inden.getId();
        System.out.println("Id: "+id);
        Indent indent = indentService.getById(id);
        //System.out.println(indent);
        if (indent == null) {
            return false; // 空路径（varchar兼容）
        }
        indent.setAllLines(this.calculateBestPath(indent.getId()));
        System.out.println(indent.getAllLines());
        return indentService.updateById(indent)?true:false;
    }

    public Boolean update(Integer id) {
        System.out.println("Id: "+id);
        Indent indent = indentService.getById(id);
        //System.out.println(indent);
        if (indent == null) {
            return false; // 空路径（varchar兼容）
        }
        indent.setAllLines(this.calculateBestPath(indent.getId()));
        System.out.println(indent.getAllLines());
        return indentService.updateById(indent)?true:false;
    }



    public Boolean saveRecord(Indent indent){
        IndentRecord indentRecord = new IndentRecord();
        indentRecord.setIndentId(indent.getId());
        indentRecord.setStationId(indent.getCurStationId());
        indentRecord.setTime(LocalDateTime.now());
        indentRecord.setStatus(indent.getStatus());
        if(indentRecordService.save(indentRecord)){
            return true;
        }else{
            return false;
        }
    }
    @PostMapping("/save")
    public Result save(@RequestBody Indent indent){
        System.out.println("save:"+indent);
        indent.setCurStationId(indent.getSendStationId());
        System.out.println("curStationId: "+indent.getCurStationId()+" status: "+(indent.getCurStationId()==indent.getSendStationId()));
        if(indentService.save(indent)){
            this.update(indent.getId());
            this.saveRecord(indent);
            return Result.suc();
        }else{
            return Result.fail();
        }
    }

    @PostMapping("/try")
    public Result trySave(@RequestBody Indent indent){
        Indent indent1 = indentService.getById(indent.getId());
        indent1.setCourierId(null);
        return indentService.updateById(indent1)?Result.suc():Result.fail();
    }


    @PostMapping("/getIndentNum")
    public Result getIndentNum(@RequestBody String stationId){

        return Result.suc(indentService.list().size());
    }


    @PostMapping("/sign")
    public Result sign(@RequestBody Indent indent){
        Indent indent1 = indentService.getById(indent.getId());
        indent1.setCourierId(null);
        indent1.setStatus(indent.getStatus());
        System.out.println(indent);
        System.out.println(indent1);
        this.saveRecord(indent1);
        return indentService.updateById(indent1)?Result.suc():Result.fail();
    }


    /**
     * 计算物流运费（输入为整数重量/距离，最低运费1元）
     */
    public static int calculateFreight(int weight, int distance) {
        int[] weightRanges = {0, 1, 3, 10};
        int[] distanceRanges = {0, 100, 300, 500};
        int[][] unitPrices = {
                {12, 15, 18, 20},   // 0-1kg（含0kg，不含1kg）
                {10, 12, 15, 17},   // 1-3kg（含1kg，不含3kg）
                {8, 10, 12, 14},    // 3-10kg（含3kg，不含10kg）
                {6, 8, 10, 12}      // 10kg及以上
        };

        int weightIndex = unitPrices.length - 1; // 默认最后一个区间（10kg及以上）
        for (int i = 0; i < weightRanges.length - 1; i++) {
            // 整数重量：若小于当前区间上限，归为当前区间
            if (weight < weightRanges[i + 1]) {
                weightIndex = i;
                break;
            }
        }
        int distanceIndex = unitPrices[0].length - 1; // 默认最后一个区间（500km及以上）
        for (int i = 0; i < distanceRanges.length - 1; i++) {
            if (distance < distanceRanges[i + 1]) {
                distanceIndex = i;
                break;
            }
        }
        int baseFreight = weight * unitPrices[weightIndex][distanceIndex];
        return Math.max(baseFreight, 1);
    }



    //modify
    @PostMapping("/mod")
    public Result mod(@RequestBody Indent indent){
        Indent indent1=indentService.getById(indent.getId());
        indent1.setCurStationId(indent.getCurStationId());
        indent1.setStatus(indent.getStatus());
        indent1.setCourierId(indent.getCourierId());
        System.out.println(indent);
        System.out.println(indent1);
        this.saveRecord(indent1);
        if(indent1.getStatus()==3||indent1.getStatus()==5){
            indent1.setCourierId(null);
        }
        System.out.println(indent1);
        return indentService.updateById(indent1)?Result.suc():Result.fail();
    }

    //delete
    @GetMapping("/delete")
    public Result delete(@RequestParam Integer id){
        System.out.println("id:"+id);
        return indentService.removeById(id)?Result.suc():Result.fail();
    }

    //new or modify
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Indent indent){
        indent.setCurStationId(indent.getSendStationId());
        indent.setAllLines(this.calculateBestPath(indent.getId()));
        return indentService.saveOrUpdate(indent)?Result.suc():Result.fail();
    }

    //find
    @PostMapping("/listP")
    public Result listP(@RequestBody Indent indent){
        LambdaQueryWrapper<Indent> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(indent.getName())){
            lambdaQueryWrapper.like(Indent::getName,indent.getName());
        }
        return Result.suc(indentService.list(lambdaQueryWrapper));
    }

    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no){
        System.out.println("no:"+no);
        List list= indentService.lambdaQuery().eq(Indent::getName,no).list();
        return list.size()>0?Result.suc(list.get(0)):Result.fail();
    }

    @GetMapping("/findById")
    public Result findById(@RequestParam Integer id){
        System.out.println("id:"+id);
        List list= indentService.lambdaQuery().eq(Indent::getId,id).list();
        return list.size()>0?Result.suc(list.get(0)):Result.fail();
    }



    @PostMapping("/listPageC")
    public Result listPageC(@RequestBody QueryPageParam queryPageParam){
//        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(User::getName,user.getName());
//        return userService.list(lambdaQueryWrapper);

//        System.out.println(queryPageParam);
//        System.out.println(queryPageParam.getPageSize());
//        System.out.println(queryPageParam.getPageNum());
//        System.out.println(queryPageParam.getParam());
        HashMap param=queryPageParam.getParam();
        String name=(String)param.get("name");
        Page<Indent> page=new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        System.out.println(queryPageParam.getPageNum());
        System.out.println(queryPageParam.getPageSize());
        LambdaQueryWrapper<Indent> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)&&!"null".equals(name)){
            lambdaQueryWrapper.like(Indent::getName,name);
        }
//        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.like(User::getName,(String)param.get("name"));

        IPage result= indentService.page(page,lambdaQueryWrapper);
        System.out.println("total bum=="+result.getTotal());
        return Result.suc(result.getTotal(), result.getRecords());

    }

    @PostMapping("/listPageMy")
    public Result listPageMy(@RequestBody QueryPageParam queryPageParam) {
        // 获取分页参数
        Integer pageNum = queryPageParam.getPageNum();
        Integer pageSize = queryPageParam.getPageSize();
        Page<Indent> page = new Page<>(pageNum, pageSize); // 直接在构造器中设置分页参数
        // 获取查询参数
        HashMap<String, Object> param = queryPageParam.getParam();
        String name = (String) param.get("name");
        Integer userId = (Integer) param.get("userId"); // 要匹配的id（用户id）
        Integer status=(Integer) param.get("status");
        System.out.println("userId: "+userId);
        // 构建查询条件
        LambdaQueryWrapper<Indent> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 1. 添加姓名模糊查询（保留原有逻辑，按需调整）
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(Indent::getName, name);
        }

        if (userId != null) { // 避免userId为null时添加无效条件
            lambdaQueryWrapper.eq(Indent::getReceiverId, userId);
        }

        if(status != null){
            lambdaQueryWrapper.eq(Indent::getStatus, status);
        }

        // 执行分页查询
        IPage<Indent> result = indentService.page(page, lambdaQueryWrapper);

        // 返回结果
        return Result.suc(result.getTotal(), result.getRecords());
    }

    @PostMapping("/listPageCourier")
    public Result listPageCourier(@RequestBody QueryPageParam queryPageParam) {
        // 获取分页参数
        Integer pageNum = queryPageParam.getPageNum();
        Integer pageSize = queryPageParam.getPageSize();
        Page<Indent> page = new Page<>(pageNum, pageSize); // 直接在构造器中设置分页参数

        // 获取查询参数
        HashMap<String, Object> param = queryPageParam.getParam();
        String name = (String) param.get("name");
        Integer courierId = (Integer) param.get("courierId"); // 要匹配的id（用户id）

        // 构建查询条件
        LambdaQueryWrapper<Indent> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 1. 添加姓名模糊查询（保留原有逻辑，按需调整）
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(Indent::getName, name);
        }

        // 2. 核心：添加 "receiverId = userId 或 senderId = userId" 条件
        if (courierId != null) { // 避免userId为null时添加无效条件
            lambdaQueryWrapper.eq(Indent::getCourierId, courierId);
        }

        // 执行分页查询
        IPage<Indent> result = indentService.page(page, lambdaQueryWrapper);

        // 返回结果
        return Result.suc(result.getTotal(), result.getRecords());
    }
}
