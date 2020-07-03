package cn.wtyoha.miaosha.service;

import cn.wtyoha.miaosha.domain.MiaoShaOrder;

public interface MiaoShaOrderService {
    MiaoShaOrder queryByUserIdAndGoodsId(Long uid, Long gid);

    void insertMiaoShaOrder(MiaoShaOrder miaoShaOrder);
}
