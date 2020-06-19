package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface GoodsDao extends tk.mybatis.mapper.common.Mapper<Goods> {

    @Select("select * from goods where id = #{id}")
    Goods findById(Integer id);
}
