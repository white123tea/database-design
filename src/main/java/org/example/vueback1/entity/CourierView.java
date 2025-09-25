package org.example.vueback1.entity;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * VIEW
 * </p>
 *
 * @author lyx
 * @since 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="CourierView对象", description="VIEW")
public class CourierView implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String no;

    private String name;

    private Integer age;

    private String phone;

    private Integer roleId;

    private Integer sex;

    private Integer stationId;

    private Integer courierStatus;
}
