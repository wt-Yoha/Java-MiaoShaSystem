package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MiaoShaUserService {
    boolean register(MiaoShaUser user);

    boolean login(MiaoShaUser user);

    public boolean isLogin(MiaoShaUser user);
}
