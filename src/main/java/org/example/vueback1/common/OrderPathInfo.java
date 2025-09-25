package org.example.vueback1.common;

import lombok.Data;
import java.util.List;

/**
 * 订单路径信息DTO：对应indent.allLines的JSON格式
 */
@Data // 用Lombok自动生成getter（Jackson依赖getter获取字段值）
public class OrderPathInfo {
    private boolean valid; // 对应JSON的valid字段
    private List<Integer> path; // 对应JSON的path数组（驿站ID列表）
    private Integer totalDistance; // 对应JSON的totalDistance字段
}