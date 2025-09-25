package org.example.vueback1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import springfox.documentation.spring.web.json.Json;
import com.baomidou.mybatisplus.annotation.FieldStrategy; // 关键导入
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
@ApiModel(value="Indent对象", description="")
public class Indent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer sendStationId;

    private Integer receiveStationId;

    private Integer curStationId;

    private Integer senderId;

    private Integer receiverId;

    private Integer goodsValue;

    private Integer status;

    private LocalDateTime time;

    private String allLines;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer courierId;

    private Integer weight;

    private Integer fee;

}
