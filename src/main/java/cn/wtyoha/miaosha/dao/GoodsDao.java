package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.Goods;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GoodsDao extends tk.mybatis.mapper.common.Mapper<Goods> {

    @Select("select * from goods where id = #{id}")
    @Results(id = "GoodsWithMiaoShaGoodsInfo", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "miaoShaGoods", one = @One(select = "cn.wtyoha.miaosha.dao.MiaoShaGoodsDao.getByGoodsId"))
    })
    Goods selectById(@Param("id")Long id);

    @Select("select * from goods where not #{useSearch} or name like #{searchKeys} limit #{startIndex}, #{pageSize}")
    @ResultMap("GoodsWithMiaoShaGoodsInfo")
    List<Goods> searchGoods(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("useSearch") Boolean useSearch, @Param("searchKeys") String searchKeys);

    @Update("update goods set stock = stock - #{num} where id = #{id} and stock > #{num}")
    int subStock(@Param("id") Long id, @Param("num") int num);

    @Select("select max(id) from goods")
    Long maxValidId();

    @Select("select count(*) from goods where name like #{searchKeys}")
    int queryGoodsCount(@Param("searchKeys") String searchKeys);
}
