package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.MiaoShaOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MiaoShaOrderDao extends tk.mybatis.mapper.common.Mapper<MiaoShaOrder> {
}
