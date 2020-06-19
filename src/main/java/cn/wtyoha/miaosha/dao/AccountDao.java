package cn.wtyoha.miaosha.dao;

import cn.wtyoha.miaosha.domain.Account;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AccountDao extends tk.mybatis.mapper.common.Mapper<Account> {
}
