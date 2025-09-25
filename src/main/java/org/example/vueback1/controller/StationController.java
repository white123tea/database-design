
package org.example.vueback1.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import java.util.List;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.Station;
import org.example.vueback1.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/station"})
public class StationController {
    @Autowired
    private StationService stationService;

    @GetMapping({"/list"})
    public List<Station> list() {
        return this.stationService.list();
    }

    @PostMapping({"/save"})
    public Result save(@RequestBody Station station) {
        return this.stationService.save(station) ? Result.suc() : Result.fail();
    }

    @PostMapping({"/mod"})
    public Result mod(@RequestBody Station station) {
        System.out.println(station);
        return this.stationService.updateById(station) ? Result.suc() : Result.fail();
    }

    @PostMapping({"/info"})
    public Result info(@RequestBody String id) {
        Station station = this.stationService.getById(id);
        return station == null ? Result.fail() : Result.suc(station);
    }
    @GetMapping({"/delete"})
    public Result delete(@RequestParam String id) {
        System.out.println(id);
        return this.stationService.removeById(id) ? Result.suc() : Result.fail();
    }

    @PostMapping({"/saveOrMod"})
    public Result saveOrMod(@RequestBody Station station) {
        return this.stationService.saveOrUpdate(station) ? Result.suc() : Result.fail();
    }

    @GetMapping({"/findByName"})
    public Result findByNo(@RequestParam String name) {
        System.out.println("name:" + name);
        List list = ((LambdaQueryChainWrapper)this.stationService.lambdaQuery().eq(Station::getStationName, name)).list();
        return list.size() > 0 ? Result.suc(list.get(0)) : Result.fail();
    }

    @GetMapping({"/getAddrById"})
    public Result getAddrById(@RequestParam Integer id) {
        System.out.println("id:" + id);
        Station station = (Station)this.stationService.getById(id);
        return station != null ? Result.suc(station.getStationAddr()) : Result.fail();
    }

    @GetMapping({"/findByArea"})
    public Result findByArea(@RequestParam String Addr) {
        System.out.println("Addr:" + Addr);
        List list = ((LambdaQueryChainWrapper)this.stationService.lambdaQuery().like(Station::getStationAddr, Addr)).list();
        return list.size() > 0 ? Result.suc(list) : Result.fail();
    }
}
