package cn.wtyoha.miaosha.controller;

import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.Result;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
@ResponseBody
public class MiaoShaUserController {

    @Autowired
    MiaoShaUserService userService;

    /**
     * 用户注册接口
     *
     * @param user
     * @return
     */
    @RequestMapping("/register")
    public Result<Object> register(@Valid MiaoShaUser user) {
        userService.register(user);
        return Result.success(null);
    }

    /**
     * 用户登录接口
     *
     * @param loginUser
     * @return
     */
    @RequestMapping("/login")
    public Result<Object> login(@Valid MiaoShaUser loginUser) {
        userService.login(loginUser);
        return Result.success(null);
    }


    @RequestMapping("/loginMsg")
    public Result<MiaoShaUser> loginUser() {
        return Result.success(userService.getLoginUser());
    }
}
