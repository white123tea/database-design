package org.example.vueback1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.vueback1.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 主键
账号
名字
密码
性别
电话
角色
有效位 服务类
 * </p>
 *
 * @author lyx
 * @since 2025-08-02
 */
public interface UserService extends IService<User> {

    IPage pageC(IPage<User> page);
    IPage<User> pageC(Page<User> page, String name);
}
