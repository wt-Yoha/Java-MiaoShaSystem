package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.MiaoShaUser;

public interface MiaoShaUserService {
    boolean register(MiaoShaUser user);

    boolean login(MiaoShaUser user);
    boolean isLogin();
    MiaoShaUser getLoginUser();
}
