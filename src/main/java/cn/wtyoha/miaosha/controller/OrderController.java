package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaGoods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.OrderInfo;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.domain.result.Result;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.rabbitmq.msgdomain.TakeOrder;
import cn.wtyoha.miaosha.rabbitmq.service.sender.ClearCacheSender;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.GoodsKey;
import cn.wtyoha.miaosha.service.GoodsService;
import cn.wtyoha.miaosha.service.MiaoShaGoodsService;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import cn.wtyoha.miaosha.service.OrderInfoService;
import org.springframework.beans.factory.InitializingBean;
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
public class OrderController implements InitializingBean {
    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    MiaoShaGoodsService miaoShaGoodsService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ClearCacheSender clearCacheSender;

    /**
     * 初始化，载入秒杀商品库存
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<MiaoShaGoods> miaoShaGoodsList = miaoShaGoodsService.getAllMiaoShaGoodsFromDB();
        for (MiaoShaGoods goods : miaoShaGoodsList) {
            redisUtils.set(GoodsKey.MIAO_SHA_GOODS_STOCK.getFullKey(goods.getGoodsId()), goods.getStockCount());
        }
    }

    /**
     * 根据orderid查询order信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/query")
    public Result<Object> queryOrder(@RequestParam("orderId") Long id) {
        OrderInfo orderInfo = orderInfoService.selectById(id);
        Goods goods = null;
        if (orderInfo != null) {
            goods = goodsService.getGoodsById(orderInfo.getGoodsId());
            Map<String, Object> pack = new HashMap<>();
            pack.put("order", orderInfo);
            pack.put("goods", goods);
            return Result.success(pack);
        }
        throw new GlobalException(CodeMsg.SERVER_ERROR);
    }

    /**
     * 下单api
     *
     * @param id
     * @param quantity
     * @param model
     * @return
     */
    @RequestMapping("/take")
    public Result<Object> takeOrder(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, Model model) {
        // 检查登陆
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        if (loginUser == null) {
            // 返回登陆页面，requestLogin字段为true表示页面提示登陆
            throw new GlobalException(CodeMsg.USER_UNLOGIN);
        }
        TakeOrder takeOrderMsg = null;
        // 检查是否是秒杀商品
        Goods goods = goodsService.getGoodsById(id);
        if (goods == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        if (goods.getMiaoShaGoods() == null) {
            takeOrderMsg = orderInfoService.takeNormalOder(loginUser, goods, quantity);
        } else {
            takeOrderMsg = orderInfoService.takeMiaoShaOrder(loginUser, goods, quantity);
        }
        return Result.success(takeOrderMsg);
    }

    @RequestMapping("/queryTakeOrderStatus")
    public Result<Object> queryTakeOrderStatus(@RequestParam("id") String id) {
        TakeOrder takeOrderMsg = orderInfoService.queryTakeOrderStatus(id);
        switch (takeOrderMsg.getStatus()) {
            // 正在排队处理、下单成功
            case 0:
            case 1:
                return Result.success(takeOrderMsg);
            // 下单失败
            default:
                // 如果是秒杀商品下单失败，需要更新redis内的库存
                clearCacheSender.sendClearCache(GoodsKey.MIAO_SHA_GOODS_STOCK.getFullKey(takeOrderMsg.getGoods().getId()));
                return Result.error(takeOrderMsg.getErrorMsg());
        }
    }

    /**
     * 查看用户所有订单
     *
     * @param model
     * @return
     */
    @RequestMapping("/myOrders")
    public Result<Object> myOrders(Model model) {
        MiaoShaUser loginUser = miaoShaUserService.getLoginUser();
        if (loginUser == null) {
            throw new GlobalException(CodeMsg.USER_UNLOGIN);
        }
        List<OrderInfo> orders = orderInfoService.getUserAllOrders(loginUser);
        List<Goods> goodsList = new ArrayList<>(orders.size());

        for (int i = 0; i < orders.size(); i++) {
            Goods goods = goodsService.getGoodsById(orders.get(i).getGoodsId());
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
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/pay")
    public Result<Object> payOrder(@RequestParam("orderInfoId") Long orderId) {
        if (!orderInfoService.pay(orderId)) {
            throw new GlobalException(CodeMsg.ERROR_PAYMENT);
        }
        return Result.success(null);
    }
}
