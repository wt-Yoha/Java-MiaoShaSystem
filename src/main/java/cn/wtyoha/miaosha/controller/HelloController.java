package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.dao.AccountDao;
import cn.wtyoha.miaosha.dao.GoodsDao;
import cn.wtyoha.miaosha.dao.MiaoShaUserDao;
import cn.wtyoha.miaosha.domain.Goods;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.Result;
import cn.wtyoha.miaosha.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HelloController {

    @Autowired
    AccountDao accountDao;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    GoodsDao goodsDao;

    @ResponseBody
    @RequestMapping("/hello{id}")
    public Result<List<MiaoShaUser>> Hello(@PathVariable("id") int id){
//        Account query = new Account();
//        query.setId(id);
//        Account account = accountDao.selectOne(query);
//        redisUtils.set("account", account);
//        Account redisAccount = redisUtils.get("account", Account.class);
//        return "Hello spring boot!\n"+redisAccount;

        List<MiaoShaUser> miaoShaUsers = miaoShaUserDao.selectAll();
        return Result.success(miaoShaUsers);
    }

    @RequestMapping("/index")
    public String Index(ModelMap modelMap){
        modelMap.addAttribute("name", "Mike");
//        int x = 2/0;
//        throw new GlobalException(CodeMsg.SERVER_ERROR);
        return "HelloPage";
//        return "index";
    }

    @ResponseBody
    @RequestMapping("/goods")
    public List<Goods> goodsList() {
        return goodsDao.selectAll();
    }

}
