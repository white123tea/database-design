package org.example.vueback1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.example.vueback1.common.PasswordUtil;
import org.example.vueback1.common.QueryPageParam;
import org.example.vueback1.common.Result;
import org.example.vueback1.entity.Courier;
import org.example.vueback1.entity.Indent;
import org.example.vueback1.entity.StationView;
import org.example.vueback1.entity.User;
import org.example.vueback1.service.CourierService;
import org.example.vueback1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 主键
账号
名字
密码
性别
电话
角色
有效位 前端控制器
 * </p>
 *
 * @author lyx
 * @since 2025-08-02
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourierService courierService;
    @PostMapping("/list")
    public Result list(@RequestBody QueryPageParam queryPageParam){
        HashMap param=queryPageParam.getParam();
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        String name=(String)param.get("name");
        if(StringUtils.isNotBlank(name)&&!"null".equals(name)){
            lambdaQueryWrapper.like(User::getName,name);
        }
        return Result.suc(userService.list(lambdaQueryWrapper));
    }

    @PostMapping("/changPasswoed")
    public Result changPasswoed(@RequestBody User user){
        User user1=userService.getById(user.getId());
        System.out.println(user1);
        String encodedPwd = PasswordUtil.encryptPassword(user1.getPassword());
        user1.setPassword(encodedPwd);
        userService.updateById(user1);
        return Result.suc();
    }
    //login
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        List list=userService.lambdaQuery().eq(User::getNo,user.getNo()).list();
        if(list.size()==1) {
            User user1=(User)list.get(0);
            System.out.println(user1);
            boolean isMatch = PasswordUtil.matchPassword(user.getPassword(), user1.getPassword());
            return isMatch ? Result.suc(list.get(0)) : Result.fail();
        }else{
            return Result.fail();
        }
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        System.out.println(user);
        String encodedPwd = PasswordUtil.encryptPassword(user.getPassword());
        user.setPassword(encodedPwd);
        return userService.save(user)?Result.suc():Result.fail();
    }
    //new
    @PostMapping("/save")
    public Result save(@RequestBody User user)
    {
        String encodedPwd = PasswordUtil.encryptPassword(user.getPassword());
        user.setPassword(encodedPwd);
        Boolean userSave=userService.save(user);
        if(!userSave){
            return Result.fail();
        }
        if(user.getRoleId()==2){
            System.out.println("新增快递员");
            System.out.println(user);
            Courier courier=new Courier();
            courier.setId(user.getId());
            courierService.save(courier);
        }
        return Result.suc();
    }
    //modify
    @PostMapping("/mod")
    public Result mod(@RequestBody User user){
        System.out.println(user);
        String encodedPwd = PasswordUtil.encryptPassword(user.getPassword());
        user.setPassword(encodedPwd);
        return userService.updateById(user)?Result.suc():Result.fail();
    }


    @PostMapping("/getCourierNum")
    public Result getCourierNum(@RequestBody String stationId){
        System.out.println(stationId);
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getRoleId,2).eq(User::getStationId,stationId);
        return Result.suc(userService.list(lambdaQueryWrapper).size());
    }

    //delete
    @GetMapping("/delete")
    public Result delete(@RequestParam String id){
        System.out.println(id);
        return userService.removeById(id)?Result.suc():Result.fail();
    }
    //new or modify
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody User user){
        return userService.saveOrUpdate(user)?Result.suc():Result.fail();
    }
    //find
    @PostMapping("/listP")
    public Result listP(@RequestBody User user){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(user.getName())){
            lambdaQueryWrapper.like(User::getName,user.getName());
        }
        return Result.suc(userService.list(lambdaQueryWrapper));
    }

    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no){
        System.out.println("no:"+no);
        List list= userService.lambdaQuery().like(User::getNo,no).list();
        return list.size()>0?Result.suc(list):Result.fail();
    }

    @GetMapping("/findOneByNo")
    public Result findOneByNo(@RequestParam String no){
        System.out.println("no:"+no);
        List list= userService.lambdaQuery().eq(User::getNo,no).list();
        return list.size()>0?Result.suc(list.get(0)):Result.fail();
    }

    @GetMapping("getById")
    public Result getById(@RequestParam Integer id){
        User user=userService.getById(id);
        if(user == null){
            return Result.fail();
        }
        return Result.suc(user);
    }

    @GetMapping("/getNoById")
    public Result getNoById(@RequestParam Integer id) {
        System.out.println("id:" + id);
        // 直接查询单个用户（更高效）
        User user = userService.getById(id);
        if (user != null) {
            // 只返回账号（no字段），符合前端需求
            return Result.suc(user.getNo());
        } else {
            return Result.fail();
        }
    }

    @PostMapping("/listPage")
    public List<User> listPage(@RequestBody QueryPageParam queryPageParam){
//        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(User::getName,user.getName());
//        return userService.list(lambdaQueryWrapper);

//        System.out.println(queryPageParam);
//        System.out.println(queryPageParam.getPageSize());
//        System.out.println(queryPageParam.getPageNum());
//        System.out.println(queryPageParam.getParam());
        HashMap param=queryPageParam.getParam();
        Page<User>page=new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());


        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(User::getName,(String)param.get("name"));
        IPage result= userService.page(page,lambdaQueryWrapper);
        System.out.println("total bum=="+result.getTotal());
        return result.getRecords();

    }

    @PostMapping("/listPageAdmin")
    public Result listPageAdmin(@RequestBody QueryPageParam queryPageParam){
//        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(User::getName,user.getName());
//        return userService.list(lambdaQueryWrapper);

//        System.out.println(queryPageParam);
//        System.out.println(queryPageParam.getPageSize());
//        System.out.println(queryPageParam.getPageNum());
//        System.out.println(queryPageParam.getParam());
        HashMap param=queryPageParam.getParam();
        Page<User>page=new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        String name=(String)param.get("name");
        if(StringUtils.isNotBlank(name)&&!"null".equals(name)){
            lambdaQueryWrapper.like(User::getName,name);
        }
        lambdaQueryWrapper.eq(User::getRoleId,0);
        IPage result= userService.page(page,lambdaQueryWrapper);
        System.out.println("total bum=="+result.getTotal());
        return Result.suc(result.getTotal(), result.getRecords());

    }

    @PostMapping("/listPageC")
    public Result listPageC(@RequestBody QueryPageParam queryPageParam) {
        HashMap<String, Object> param = queryPageParam.getParam();
        // 获取name参数（可能为null，需兼容）
        String name = (String) param.getOrDefault("name", ""); // 无值时默认为空字符串

        Page<User> page = new Page<>();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        // 调用service时传递name参数
        IPage<User> result = userService.pageC(page, name);

        return Result.suc(result.getTotal(), result.getRecords());
    }


}
