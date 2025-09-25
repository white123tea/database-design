package org.example.vueback1.service.Impl;

import org.example.vueback1.entity.Station;
import org.example.vueback1.mapper.StationMapper;
import org.example.vueback1.service.StationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lyx
 * @since 2025-08-18
 */
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Override
    public List<Station> listC(String stationName, String region) {
        // 直接调用不分页的listC方法
        return baseMapper.listC(stationName, region);
    }
}
