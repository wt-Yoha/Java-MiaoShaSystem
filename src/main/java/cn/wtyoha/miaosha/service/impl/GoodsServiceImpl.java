package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.GoodsKey;
import cn.wtyoha.miaosha.service.GoodsService;
import org.apache.commons.lang3.CharSetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public List<Goods> goodList(Integer currentPage, Integer pageSize, String searchKeys) {
        boolean useSearch = false;
        Integer startIndex = (currentPage - 1) * pageSize;
        if (!"".equals(searchKeys)) {
            useSearch = true;
            searchKeys = formattingSearchKeys(searchKeys);
        }
        List<Goods> goodsList = null;
        String redisGoodsKey = GoodsKey.GOODS_LIST.getFullKey() + "_" + currentPage + "_" + pageSize + "_" + searchKeys;
        // 先尝试从缓存中取数据
        if ((goodsList = redisUtils.getList(redisGoodsKey, Goods.class)) == null) {
            goodsList = goodsDao.searchGoods(startIndex, pageSize, useSearch, searchKeys);
            redisUtils.set(redisGoodsKey, goodsList, RedisUtils.THIRTY_MINUTE);
        }
        return goodsList;
    }

    @Override
    public Goods getGoodsById(Long id) {
        Goods goods = null;
        // 先验证id的有效性，防止恶意的缓存穿透
        if (isValidId(id) && (goods = redisUtils.get(GoodsKey.GOODS_ITEM.getFullKey(id), Goods.class)) == null) {
            goods = goodsDao.selectById(id);
            redisUtils.set(GoodsKey.GOODS_ITEM.getFullKey(id), goods, RedisUtils.THIRTY_SECONDS);
        }
        if (goods == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        return goods;
    }

    @Override
    public BufferedImage createVerifyCodeImage(Long goodsId, MiaoShaUser user) {
        if (goodsId == null || user == null) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(user, goodsId);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //输出图片
        return image;
    }

    @Override
    public boolean checkVerifyCode(MiaoShaUser loginUser, Long goodsId, String verifyCode) {
        if (verifyCode == null || verifyCode.length() == 0) {
            return false;
        }
        String val = redisUtils.get(GoodsKey.VERIFY_CODE.getFullKey(loginUser.getNickname() + goodsId), String.class);
        if (verifyCode.equals(val)) {
            return true;
        }
        return false;
    }

    @Override
    public int queryGoodsCount(String searchKeys) {
        searchKeys = formattingSearchKeys(searchKeys);
        return goodsDao.queryGoodsCount(searchKeys);
    }

    /**
     * 将搜索关键字格式化：key1 key2 ==> %key1%kye2
     * @param searchKeys
     * @return
     */
    private String formattingSearchKeys(String searchKeys) {
        if (!"".equals(searchKeys)) {
            searchKeys = URLDecoder.decode(searchKeys, StandardCharsets.UTF_8);
            searchKeys =searchKeys.replace(" ", "%");
        }
        searchKeys = "%" + searchKeys + "%";
        return searchKeys;
    }

    /**
     * 生成验证码
     * @param loginUser
     * @param goodsId
     * @return
     */
    private String generateVerifyCode(MiaoShaUser loginUser, Long goodsId) {
        String ops = "+-*/";
        int max = 20;
        int a = (int) (Math.random() * max) + 1, b = (int) (Math.random() * max) + 1, op = (int) (Math.random() * 4), c = 0;
        switch (op) {
            case 0:
                c = a + b;
                break;
            case 1:
                c = a - b;
                break;
            case 2:
                c = a * b;
                break;
            case 3:
                c = a / b;
        }
        String formula = "" + a + " " + ops.charAt(op) + " " + b + "=";
        redisUtils.set(GoodsKey.VERIFY_CODE.getFullKey(loginUser.getNickname() + goodsId), c, RedisUtils.THIRTY_MINUTE);
        return formula;
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
