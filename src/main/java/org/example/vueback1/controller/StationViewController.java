package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.example.vueback1.common.QueryPageParam;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.CourierView;
import org.example.vueback1.entity.StationView;
import org.example.vueback1.service.StationViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * <p>
 * VIEW 前端控制器
 * </p>
 *
 * @author lyx
 * @since 2025-09-01
 */
@RestController
@RequestMapping("/station-view")
public class StationViewController {
    @Autowired
    private StationViewService stationViewService;

    @PostMapping("/list")
    public Result list(@RequestBody QueryPageParam queryPageParam){
        HashMap param=queryPageParam.getParam();
        LambdaQueryWrapper<StationView> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        String name=(String)param.get("stationName");
        if(StringUtils.isNotBlank(name)&&!"null".equals(name)){
            lambdaQueryWrapper.like(StationView::getStationName,name);
        }
        return Result.suc(stationViewService.list(lambdaQueryWrapper));
    }

}
