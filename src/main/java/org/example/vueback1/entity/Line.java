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
 * 
 * </p>
 *
 * @author lyx
 * @since 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Line对象", description="")
public class Line implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer stationAId;

    private Integer stationBId;

    private Integer distance;


}
