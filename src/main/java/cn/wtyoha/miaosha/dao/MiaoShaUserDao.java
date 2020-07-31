package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MiaoShaUserDao extends tk.mybatis.mapper.common.Mapper<MiaoShaUser> {

    @Insert("insert into advices(uid, data) values(#{uid}, #{msg})")
    void insertAdvice(@Param("uid") long uid, @Param("msg") String msg);
}
