package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.vueback1.common.QueryPageParam;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.IndentRecord;
import org.example.vueback1.entity.Line;
import org.example.vueback1.entity.Station;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.IndentRecordService;
import org.example.vueback1.service.IndentService;
import org.example.vueback1.service.StationService;
import org.example.vueback1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyx
 * @since 2025-08-24
 */
@RestController
@RequestMapping("/indent-record")
public class IndentRecordController {
    @Autowired
    private IndentRecordService indentRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private IndentService indentService;
    @Autowired
    private StationService stationService;

    @GetMapping("/listById")
    public Result listById(@RequestParam String indentId){
        System.out.println("indentId:"+indentId);
        List list= indentRecordService.lambdaQuery().like(IndentRecord::getIndentId,indentId).list();
        return list.size()>0?Result.suc(list):Result.fail();
    }

    @PostMapping({"/save"})
    public Result save(@RequestBody IndentRecord indentRecord) {
        return this.indentRecordService.save(indentRecord) ? Result.suc() : Result.fail();
    }


    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam){
        HashMap param=queryPageParam.getParam();
        Integer indent_id=(Integer)param.get("indentId");
        Page<IndentRecord>page=new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        System.out.println(queryPageParam.getPageNum());
        System.out.println(queryPageParam.getPageSize());
        LambdaQueryWrapper<IndentRecord> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(IndentRecord::getIndentId,indent_id);
//        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.like(User::getName,(String)param.get("name"));
        IPage result= indentRecordService.page(page,lambdaQueryWrapper);
        System.out.println("total bum=="+result.getTotal());
        return Result.suc(result.getTotal(), result.getRecords());
    }
}
