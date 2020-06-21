package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class MiaoShaUserController {

    @Autowired
    MiaoShaUserService userService;

    @RequestMapping("/loginPage")
    public String loginPage() {
        return "login";
    }

    /**
     * 用户注册接口
     * @param user
     * @return
     */
    @RequestMapping("/register")
    public String register(@Valid MiaoShaUser user) {
        userService.register(user);
        return "goodsList";
    }

    /**
     * 用户登录接口
     * @param loginUser
     * @return
     */
    @RequestMapping("/login")
    public String login(@Valid MiaoShaUser loginUser) {
        userService.login(loginUser);
        return "redirect:/goods/list";
    }

}
