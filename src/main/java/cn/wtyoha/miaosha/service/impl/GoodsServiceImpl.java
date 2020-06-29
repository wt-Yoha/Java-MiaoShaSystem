package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.GoodsKey;
import cn.wtyoha.miaosha.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public List<Goods> goodList() {
        List<Goods> goodsList = null;
        // 先尝试从缓存中取数据
        if ((goodsList = redisUtils.getList(GoodsKey.GOODS_LIST.getFullKey(), Goods.class)) == null) {
            goodsList = goodsDao.selectAll();
            redisUtils.set(GoodsKey.GOODS_LIST.getFullKey(), goodsList);
        }
        return goodsList;
    }

    @Override
    public Goods getGoodsById(Long id) {
        Goods goods = null;
        // 先验证id的有效性，防止恶意的缓存穿透
        if (isValidId(id) && (goods = redisUtils.get(GoodsKey.GOODS_ITEM.getFullKey(String.valueOf(id)), Goods.class)) == null) {
            goods = goodsDao.selectById(id);
            redisUtils.set(GoodsKey.GOODS_ITEM.getFullKey(String.valueOf(id)), Goods.class, RedisUtils.THIRTY_SECONDS);
        }
        if (goods == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        return goods;
    }

    private boolean isValidId(Long id) {
        return id > 0 && id <= getValidMaxGoodsID();
    }

    private long getValidMaxGoodsID() {
        Long validMaxId = null;
        if ((validMaxId = redisUtils.get(GoodsKey.VALID_MAX_ID.getFullKey(), Long.class)) == null) {
            validMaxId = goodsDao.maxValidId();
            redisUtils.set(GoodsKey.VALID_MAX_ID.getFullKey(), validMaxId);
            return validMaxId;
        }
        return Integer.MAX_VALUE;
    }

}
