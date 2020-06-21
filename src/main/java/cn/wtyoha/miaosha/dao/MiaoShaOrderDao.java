package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.MiaoShaOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MiaoShaOrderDao extends tk.mybatis.mapper.common.Mapper<MiaoShaOrder> {
    @Select("select * from miaosha_order where user_id = #{uid} and goods_id = #{gid}")
    MiaoShaOrder selectByUserGoodsId(@Param("uid") Long uid, @Param("gid") Long goodsId);
}
