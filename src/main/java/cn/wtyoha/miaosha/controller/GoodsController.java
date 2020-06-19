package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    GoodsDao goodsDao;

    @RequestMapping("/list")
    public String goodsList(Model model) {
        List<Goods> goods = goodsService.goodList();
        while (goods.size() > 12) {
            goods.remove(goods.size() - 1);
        }
        model.addAttribute("goodsList", goods);
        return "goodsList";
    }

    @RequestMapping("/detail{id}")
    public String goodsDetail(@PathVariable("id") Integer id, Model model) {
        Goods goods = goodsDao.selectByPrimaryKey(id);
        model.addAttribute("goodsItem", goods);
        return "goodsDetail";
    }

}
