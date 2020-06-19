package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.Goods;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GoodsDao extends tk.mybatis.mapper.common.Mapper<Goods> {

    @Select("select * from goods where id = #{id}")
    @Results(id = "GoodsWithMiaoShaGoodsInfo", value = {
            @Result(column = "id", property = "miaoShaGoods", one = @One(select = "cn.wtyoha.miaosha.dao.MiaoShaGoodsDao.getByGoodsId"))
    })
    Goods selectById(@Param("id")Integer id);

    @Select("select * from goods")
    @ResultMap("GoodsWithMiaoShaGoodsInfo")
    List<Goods> selectAll();
}
