package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.MiaoShaGoods;

import java.util.List;

public interface MiaoShaGoodsService {
    List<MiaoShaGoods> getAllMiaoShaGoodsFromDB();

    Integer subMiaoShaGoodsStock(Long id);
}
