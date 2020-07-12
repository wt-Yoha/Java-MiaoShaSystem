package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;

import java.awt.image.BufferedImage;
import java.util.List;

public interface GoodsService {
    List<Goods> goodList();

    Goods getGoodsById(Long id);

    BufferedImage createVerifyCodeImage(Long goodsId, MiaoShaUser user);

    boolean checkVerifyCode(MiaoShaUser loginUser, Long goodsId, String verifyCode);

}
