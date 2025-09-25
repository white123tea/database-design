package org.example.vueback1.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.vueback1.entity.Station;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2025-08-18
 */
@Mapper
public interface StationMapper extends BaseMapper<Station> {
    List<Station> listC(@Param("stationName") String stationName,
                        @Param("region") String region);
}
