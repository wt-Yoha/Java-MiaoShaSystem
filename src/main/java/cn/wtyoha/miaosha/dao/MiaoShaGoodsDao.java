package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.MiaoShaGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MiaoShaGoodsDao extends tk.mybatis.mapper.common.Mapper <MiaoShaGoods>{
    @Select("select * from miaosha_goods where goods_id = #{goodsId}")
    MiaoShaGoods getByGoodsId(Integer goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where id = #{id} and stock_count > 0")
    int subStock(@Param("id")Long id);
}
