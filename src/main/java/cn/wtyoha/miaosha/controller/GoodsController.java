package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaGoods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.domain.result.Page;
import cn.wtyoha.miaosha.domain.result.Result;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.service.GoodsService;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/goods")
@ResponseBody
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @RequestMapping("/list")
    public Result<List<Goods>> goodsList(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "12") Integer pageSize, @RequestParam(defaultValue = "") String searchKeys) {
        List<Goods> goods = goodsService.goodList(currentPage, pageSize, searchKeys);
        return Result.success(goods);
    }

    @RequestMapping("/queryPages")
    public Result<Page<List<Goods>>> queryGoodsPage(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "12") Integer pageSize, @RequestParam(defaultValue = "") String searchKeys) {
        List<Goods> goods = goodsService.goodList(currentPage, pageSize, searchKeys);
        int records = goodsService.queryGoodsCount(searchKeys);
        int allPages = (int) Math.ceil(records * 1.0 / pageSize);
        Page<List<Goods>> page = new Page<>(currentPage, pageSize, records, allPages, goods);
        return Result.success(page);
    }

    @RequestMapping("/detail/{id}")
    public Result<Map<String, Object>> goodsDetail(@PathVariable("id") Long id) {
        Goods goods = goodsService.getGoodsById(id);
        long start = -1, end = -1, now = new Date().getTime(), remainTime = -1, continueTime = -1;
        int status = -1;
        if (goods.getMiaoShaGoods() != null) {
            MiaoShaGoods miaoShaGoods = goods.getMiaoShaGoods();
            start = miaoShaGoods.getStartDate().getTime();
            end = miaoShaGoods.getEndDate().getTime();
            continueTime = end - start;
            if (start > now) {
                // 秒杀未开始
                remainTime = start - now;
                status = 0;
            } else if (now < end) {
                // 秒杀进行中
                remainTime = end - now;
                status = 1;
            } else {
                // 秒杀已结束
                status = -1;
            }
        }
        Map<String, Object> pack = new HashMap<>();
        pack.put("status", status);
        pack.put("goods", goods);
        pack.put("remainTime", remainTime);
        pack.put("continueTime", continueTime);
        return Result.success(pack);
    }

    /**
     * 生成验证码图片
     * @param goodsId
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/verifyCode")
    public Result<Object> getGoodsVerifyCode(Long goodsId, HttpServletResponse response) throws IOException {
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        if (loginUser == null) {
            throw new GlobalException(CodeMsg.USER_UNLOGIN);
        }
        BufferedImage image = goodsService.createVerifyCodeImage(goodsId, loginUser);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "JPEG", outputStream);
        outputStream.flush();
        outputStream.close();
        return Result.success(null);
    }
}
