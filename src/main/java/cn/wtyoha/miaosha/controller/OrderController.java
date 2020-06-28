package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.dao.OrderInfoDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.domain.result.Result;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import cn.wtyoha.miaosha.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
@ResponseBody
public class OrderController {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    OrderInfoDao orderInfoDao;

    @Autowired
    OrderInfoService orderInfoService;

    /**
     * 根据orderid查询order信息
     * @param id
     * @return
     */
    @RequestMapping("/query")
    public Result<Object> queryOrder(@RequestParam("orderId") Long id) {
        OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(id);
        Goods goods = null;
        if (orderInfo != null) {
            goods = goodsDao.selectById(orderInfo.getGoodsId());
            Map<String, Object> pack = new HashMap<>();
            pack.put("order", orderInfo);
            pack.put("goods", goods);
            return Result.success(pack);
        }
        throw new GlobalException(CodeMsg.SERVER_ERROR);
    }

    /**
     * 下单api
     * @param id
     * @param quantity
     * @param model
     * @return
     */
    @RequestMapping("/take")
    public Result<Map<String, Object>> takeOrder(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, Model model) {
        // 检查登陆
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        if (loginUser == null) {
            // 返回登陆页面，requestLogin字段为true表示页面提示登陆
            throw new GlobalException(CodeMsg.USER_UNLOGIN);
        }
        OrderInfo orderInfo = null;
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
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        Map<String, Object> pack = new HashMap<>();
        pack.put("order", orderInfo);
        pack.put("goods", goods);
        return Result.success(pack);
    }

    /**
     * 查看用户所有订单
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping("/myOrders")
    public Result<Object> myOrders(Model model) {
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

        Map<String, Object> pack = new HashMap<>();
        pack.put("orders", orders);
        pack.put("goods", goodsList);
        return Result.success(pack);
    }

    /**
     * 支付api
     * @param orderId
     * @return
     */
    @ResponseBody
    @RequestMapping("/pay")
    public Result<Object> payOrder(@RequestParam("orderInfoId") Long orderId) {
        if (!orderInfoService.pay(orderId)){
            throw new GlobalException(CodeMsg.ERROR_PAYMENT);
        }
        return Result.success(null);
    }
}
