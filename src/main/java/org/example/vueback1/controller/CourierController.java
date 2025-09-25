package org.example.vueback1.controller;


import org.example.vueback1.common.Result;
import org.example.vueback1.entity.Courier;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyx
 * @since 2025-08-31
 */
@RestController
@RequestMapping("/courier")
public class CourierController {
    @Autowired
    private CourierService courierService;
    @PostMapping("/save")
    public Result save(@RequestBody Courier courier){
        return courierService.save(courier)?Result.suc():Result.fail();
    }

}
