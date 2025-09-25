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
@ApiModel(value="StationView对象", description="VIEW")
public class StationView implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer stationId;

    private String stationName;

    private String stationAddr;

    private String name;

    private String phone;


}
