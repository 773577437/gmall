package com.test.gmall.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.gmall.pms.entity.Comment;
import com.test.gmall.pms.mapper.CommentMapper;
import com.test.gmall.pms.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-02-22
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
