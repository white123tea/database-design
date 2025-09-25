package org.example.vueback1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 主键
账号
名字
密码
性别
电话
角色
有效位
 * </p>
 *
 * @author lyx
 * @since 2025-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="主键 账号 名字 密码 性别 电话 角色 有效位")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String no;

    private String name;

    private Integer age;

    private String phone;

    private Integer roleId;

    private String isvalid;

    private String password;

    private Integer sex;

    private Integer stationId;


}
