package org.example.vueback1.entity;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyx
 * @since 2025-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Courier对象", description="")
public class Courier implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer courierStatus;


}
