package org.example.vueback1.service;

import org.example.vueback1.entity.Station;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyx
 * @since 2025-08-18
 */
public interface StationService extends IService<Station> {
    List<Station> listC(String stationName, String region);
}
