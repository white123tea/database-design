package org.example.vueback1.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.vueback1.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
/**
 * <p>
 * 主键
账号
名字
密码
性别
电话
角色
有效位 Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2025-08-02
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage pageC(IPage<User> page);

    IPage<User> pageC(Page<User> page, @Param("name") String name);
}
