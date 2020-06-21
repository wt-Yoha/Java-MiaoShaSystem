package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OrderInfoDao extends tk.mybatis.mapper.common.Mapper<OrderInfo> {

    @Select("select * from order_info where user_id = #{uid}")
    List<OrderInfo> selectByUserId(@Param("uid") Long uid);

    @Update("update order_info set status = #{value} where id = #{orderId}")
    boolean setStatus(@Param("orderId") Long orderId, @Param("value") int value);
}
