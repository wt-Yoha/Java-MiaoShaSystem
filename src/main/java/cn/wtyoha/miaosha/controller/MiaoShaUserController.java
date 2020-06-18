package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.domain.result.Result;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class MiaoShaUserController {

    @Autowired
    MiaoShaUserService userService;

    /**
     * 用户注册接口
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/register")
    public Result<String> register(@Valid MiaoShaUser user) {
        userService.register(user);
        return Result.success(CodeMsg.SUCCESS.getMsg());
    }

    /**
     * 用户登录接口
     * @param loginUser
     * @return
     */
    @RequestMapping("/login")
    public String login(@Valid MiaoShaUser loginUser) {
        userService.login(loginUser);
        return "HelloPage";
    }

}
