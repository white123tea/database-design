package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.Line;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyx
 * @since 2025-08-18
 */
@RestController
@RequestMapping("/line")
public class LineController {
    @Autowired
    private LineService lineService;

    @GetMapping("/list")
    public List<Line> list(){
        return lineService.list();
    }

    @PostMapping("/save")
    public Result save(@RequestBody Line line){
        Integer id1=line.getStationAId();
        Integer id2=line.getStationBId();
        LambdaQueryWrapper<Line> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Line::getStationAId,id1).eq(Line::getStationBId,id2);
        if(lineService.count(queryWrapper)>0){return Result.fail();}
        return lineService.save(line)?Result.suc(lineService.getById(line.getId())):Result.fail();
    }

    //delete
    @GetMapping("/delete")
    public Result delete(@RequestParam String id){
        System.out.println(id);
        return lineService.removeById(id)?Result.suc():Result.fail();
    }
}
