package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.MiaoShaGoodsDao;
import cn.wtyoha.miaosha.domain.MiaoShaGoods;
import cn.wtyoha.miaosha.service.MiaoShaGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MiaoShaGoodsServiceImpl implements MiaoShaGoodsService {
    @Autowired
    MiaoShaGoodsDao miaoShaGoodsDao;

    @Override
    public List<MiaoShaGoods> getAllMiaoShaGoodsFromDB() {
        return miaoShaGoodsDao.selectAll();
    }

    @Override
    public Integer subMiaoShaGoodsStock(Long id) {
        return miaoShaGoodsDao.subStock(id);
    }
}
