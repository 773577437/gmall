package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.pms.entity.Album;
import com.test.gmall.pms.mapper.AlbumMapper;
import com.test.gmall.pms.service.AlbumService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 相册表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {

}
