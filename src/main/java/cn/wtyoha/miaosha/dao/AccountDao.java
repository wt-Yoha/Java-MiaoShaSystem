package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AccountDao extends tk.mybatis.mapper.common.Mapper<Account> {
}
