package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaGoods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.service.GoodsService;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @RequestMapping("/list")
    public String goodsList(Model model) {
        List<Goods> goods = goodsService.goodList();
        while (goods.size() > 12) {
            goods.remove(goods.size() - 1);
        }
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        model.addAttribute("user", loginUser);
        model.addAttribute("goodsList", goods);
        return "goodsList";
    }

    @RequestMapping("/detail/{id}")
    public String goodsDetail(@PathVariable("id") Long id, Model model) {
        Goods goods = goodsDao.selectById(id);
        boolean isMiaoShaGoods = false;
        long start = -1, end = -1, now = new Date().getTime(), remainTime = -1, continueTime = -1;
        int status = -1;
        if (goods.getMiaoShaGoods() != null) {
            isMiaoShaGoods = true;
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
        model.addAttribute("goodsItem", goods);
        model.addAttribute("isMiaoShaGoods", isMiaoShaGoods);
        model.addAttribute("status", status);
        model.addAttribute("remainTime", remainTime);
        model.addAttribute("continueTime", continueTime);
        return "goodsDetail";
    }

}
