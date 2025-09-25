package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.*;
import org.example.vueback1.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private UserService userService;

    @Autowired
    private IndentService indentService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @Autowired
    private CourierService courierService;

    @PostMapping("/getData")
    public Result getData() {
        Map<String, Integer> map = new HashMap<>();

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getRoleId, 1);
        map.put("courier",courierService.count());
        map.put("userNum", userService.count(userWrapper));
        map.put("lineNum", lineService.count());
        map.put("indentNum", indentService.count());
        map.put("stationNum", stationService.count());

        LambdaQueryWrapper<Indent> indentWrapper = new LambdaQueryWrapper<>();
        for (int status = 0; status <= 6; status++) {
            // 每次循环前清空条件，复用同一个Wrapper
            indentWrapper.clear();
            indentWrapper.eq(Indent::getStatus, status);
            map.put("indentNum" + status, indentService.count(indentWrapper));
        }

        return Result.suc(map);
    }

}
