package org.example.vueback1.controller;


import org.example.vueback1.common.Result;
import org.example.vueback1.entity.Station;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.StationService;
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
@RequestMapping("/station")
public class StationController {
    @Autowired
    private StationService stationService;

    @GetMapping("/list")
    public List<Station> list(){
        return stationService.list();
    }

    @PostMapping("/save")
    public Result save(@RequestBody Station station){
        return stationService.save(station)?Result.suc():Result.fail();
    }

    @PostMapping("/mod")
    public Result mod(@RequestBody Station station){
        System.out.println(station);
        return stationService.updateById(station)?Result.suc():Result.fail();
    }

    //delete
    @GetMapping("/delete")
    public Result delete(@RequestParam String id){
        System.out.println(id);
        return stationService.removeById(id)?Result.suc():Result.fail();
    }

    //new or modify
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Station station){
        return stationService.saveOrUpdate(station)?Result.suc():Result.fail();
    }

    @GetMapping("/findByName")
    public Result findByNo(@RequestParam String name){
        System.out.println("name:"+name);
        List list= stationService.lambdaQuery().eq(Station::getStationName,name).list();
        return list.size()>0?Result.suc(list.get(0)):Result.fail();
    }

    @GetMapping("/getAddrById")
    public Result getAddrById(@RequestParam Integer id) {
        System.out.println("id:" + id);
        // 直接查询单个用户（更高效）
        Station station=stationService.getById(id);
        if (station != null) {
            // 只返回账号（no字段），符合前端需求
            return Result.suc(station.getStationAddr());
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/findByArea")
    public Result findByArea(@RequestParam String Addr){
        System.out.println("Addr:"+Addr);
        List list= stationService.lambdaQuery().like(Station::getStationAddr,Addr).list();
        return list.size()>0?Result.suc(list):Result.fail();
    }

}
