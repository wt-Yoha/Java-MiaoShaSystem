package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OderInfoDao extends tk.mybatis.mapper.common.Mapper<OrderInfo> {
}
