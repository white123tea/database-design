package org.example.vueback1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
 * @since 2025-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="IndentRecord对象", description="")
public class IndentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer indentId;

    private Integer courierId;

    private Integer stationId;

    private Integer status;
    /*
    0表示 已下单
    1表示 已揽收
    2表示 运输中
    3表示 已到达
    4表示 已取消
    5表示 已取出
    * */

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime time;


}
