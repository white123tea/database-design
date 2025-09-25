package org.example.vueback1.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.vueback1.entity.User;
import org.example.vueback1.mapper.UserMapper;
import org.example.vueback1.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 主键
账号
名字
密码
性别
电话
角色
有效位 服务实现类
 * </p>
 *
 * @author lyx
 * @since 2025-08-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public IPage pageC(IPage<User> page) {
        return userMapper.pageC(page);
    }

    @Override
    public IPage<User> pageC(Page<User> page, String name) {
        return baseMapper.pageC(page, name);
    }
}
