package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.vueback1.common.QueryPageParam;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.CourierView;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.CourierViewService;
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
@RequestMapping("/courier-view")
public class CourierViewController {
    @Autowired
    private CourierViewService courierViewService;

    @PostMapping("/list")
    public Result list(@RequestBody QueryPageParam queryPageParam){
        HashMap param=queryPageParam.getParam();
        Page<CourierView> page=new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        LambdaQueryWrapper<CourierView> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(param.get("stationId")!=null){
            lambdaQueryWrapper.eq(CourierView::getStationId,param.get("stationId"));
        }
        String name=(String)param.get("name");
        if(StringUtils.isNotBlank(name)&&!"null".equals(name)){
            lambdaQueryWrapper.like(CourierView::getName,name);
        }
        IPage result=courierViewService.page(page,lambdaQueryWrapper);
        return Result.suc(result.getTotal(),result.getRecords());
    }


}
