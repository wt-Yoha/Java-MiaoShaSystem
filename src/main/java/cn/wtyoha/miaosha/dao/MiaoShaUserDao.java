package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MiaoShaUserDao extends tk.mybatis.mapper.common.Mapper<MiaoShaUser> {
}
