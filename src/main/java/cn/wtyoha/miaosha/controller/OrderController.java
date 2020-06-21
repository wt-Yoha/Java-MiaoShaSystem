package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.dao.OrderInfoDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import cn.wtyoha.miaosha.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    OrderInfoDao orderInfoDao;

    @Autowired
    OrderInfoService orderInfoService;

    @RequestMapping("/take")
    public String takeOrder(@RequestParam("id") Long id, @RequestParam("quantity[25]") int quantity, Model model) {
        // 检查登陆
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        if (loginUser == null) {
            // 返回登陆页面，requestLogin字段为true表示页面提示登陆
            throw new GlobalException(CodeMsg.USER_UNLOGIN);
        }
        Object orderInfo = null;
        // 检查是否是秒杀商品
        Goods goods = goodsDao.selectById(id);
        if (goods == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        if (goods.getMiaoShaGoods() == null) {
            orderInfo = orderInfoService.takeNormalOder(loginUser, goods, quantity);
        } else {
            orderInfo = orderInfoService.takeMiaoShaOrder(loginUser, goods, quantity);
        }

        if (orderInfo == null) {
            // 下单失败，返回商品详情页
            return "forwad:/goods/detail/" + id;
        }

        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goodsItem", goods);
        return "order";
    }

    @RequestMapping("/myOrders")
    public String myOrders(Model model) {
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        if (loginUser == null) {
            throw new GlobalException(CodeMsg.USER_UNLOGIN);
        }
        List<OrderInfo> orders = orderInfoService.getUserAllOrders(loginUser);
        List<Goods> goodsList = new ArrayList<>(orders.size());

        for (int i = 0; i < orders.size(); i++) {
            Goods goods = goodsDao.selectById(orders.get(i).getGoodsId());
            goodsList.add(goods);
        }
        model.addAttribute("orderList", orders);
        model.addAttribute("goodsList", goodsList);
        return "myOrders";
    }

    @RequestMapping("/pay")
    public String payOrder(@RequestParam("orderInfoId") Long orderId) {
        if (!orderInfoService.pay(orderId)){
            throw new GlobalException(CodeMsg.ERROR_PAYMENT);
        }
        return "redirect:/order/myOrders";
    }
}
