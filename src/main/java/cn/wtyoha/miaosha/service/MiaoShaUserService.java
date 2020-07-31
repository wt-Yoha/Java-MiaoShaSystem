package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.MiaoShaUser;

public interface MiaoShaUserService {
    boolean register(MiaoShaUser user);
    MiaoShaUser login(MiaoShaUser user);
    boolean isLogin();
    MiaoShaUser getLoginUser();
    void logout();

    void submitAdvice(String msg);
}
