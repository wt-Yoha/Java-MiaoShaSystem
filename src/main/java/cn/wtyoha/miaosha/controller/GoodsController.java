package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @RequestMapping("/list")
    public String goodsList(Model model) {
        List<Goods> goods = goodsService.goodList();
        while (goods.size() > 12) {
            goods.remove(goods.size() - 1);
        }
        model.addAttribute("goodsList", goods);
        return "goodsList";
    }

}
