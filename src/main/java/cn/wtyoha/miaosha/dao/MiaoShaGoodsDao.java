package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.MiaoShaGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MiaoShaGoodsDao extends tk.mybatis.mapper.common.Mapper <MiaoShaGoods>{
    @Select("select * from miaosha_goods where goods_id = #{goodsId}")
    MiaoShaGoods getByGoodsId(Integer goodsId);
}
