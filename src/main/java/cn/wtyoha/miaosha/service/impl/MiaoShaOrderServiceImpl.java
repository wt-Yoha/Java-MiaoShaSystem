package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.MiaoShaOrderDao;
import cn.wtyoha.miaosha.domain.MiaoShaOrder;
import cn.wtyoha.miaosha.service.MiaoShaOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MiaoShaOrderServiceImpl implements MiaoShaOrderService {
    @Autowired
    MiaoShaOrderDao miaoShaOrderDao;

    @Override
    public MiaoShaOrder queryByUserIdAndGoodsId(Long uid, Long gid) {
        return miaoShaOrderDao.selectByUserGoodsId(uid, gid);
    }

    @Override
    public void insertMiaoShaOrder(MiaoShaOrder miaoShaOrder) {
        miaoShaOrderDao.insert(miaoShaOrder);
    }
}
