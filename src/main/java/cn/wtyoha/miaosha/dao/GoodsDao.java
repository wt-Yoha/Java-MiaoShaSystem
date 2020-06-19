package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.Goods;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface GoodsDao extends tk.mybatis.mapper.common.Mapper<Goods> {

    @Select("select * from goods where id = #{id}")
    @Results({
            @Result(column = "id", property = "miaoShaGoods", one = @One(select = "cn.wtyoha.miaosha.dao.MiaoShaGoodsDao.getByGoodsId"))
    })
    Goods selectById(@Param("id")Integer id);
}
