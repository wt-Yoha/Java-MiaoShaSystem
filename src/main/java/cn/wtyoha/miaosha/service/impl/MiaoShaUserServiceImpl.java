package cn.wtyoha.miaosha.service.impl;

import cn.wtyoha.miaosha.dao.MiaoShaUserDao;
import cn.wtyoha.miaosha.domain.MiaoShaUser;
import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.globalexception.GlobalException;
import cn.wtyoha.miaosha.redis.RedisUtils;
import cn.wtyoha.miaosha.redis.commonkey.UserKey;
import cn.wtyoha.miaosha.service.MiaoShaUserService;
import cn.wtyoha.miaosha.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
@Transactional
public class MiaoShaUserServiceImpl implements MiaoShaUserService {

    // redis 和 cookei 的保存时间 秒
    private static int TOKEN_TIMEOUT = 60 * 30;
    private static String TOKEN_NAME = "token";

    @Autowired
    MiaoShaUserDao userDao;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    HttpServletRequest request;

    @Autowired(required = false)
    HttpServletResponse response;

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    @Override
    public boolean register(MiaoShaUser user) {
        String salt = MD5Utils.randomString();
        String saltPassword = MD5Utils.inputPassToDBPass(user.getPassword(), salt);
        user.setPassword(saltPassword);
        user.setSalt(salt);
        userDao.insert(user);
        return true;
    }

    /**
     * 用户登录
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean login(MiaoShaUser loginUser) {
        String password = loginUser.getPassword();
        MiaoShaUser user = userDao.selectByPrimaryKey(loginUser.getId());
        if (user == null) {
            throw new GlobalException(CodeMsg.UNREGISTER_USER);
        }
        String salt = user.getSalt();
        String dbPass = MD5Utils.inputPassToDBPass(password, salt);
        if (dbPass.equals(user.getPassword())) {
            if (!isLogin()) {
                String token = UUID.randomUUID().toString().replace("-", "");
                addUserTORedisAndSetCookie(token, user);
            }
            return true;
        } else {
            throw new GlobalException(CodeMsg.WRONG_PASSWORD);
        }
    }

    /**
     * 检查redis是否已经保存了登录信息,可用于避免多次向redis存入
     *
     * @return
     */
    @Override
    public boolean isLogin() {
        MiaoShaUser loginUser = getLoginUser();
        if (loginUser != null) {
            return true;
        }
        return false;
    }

    /**
     * 获得已登陆用户的信息
     * @return
     */
    @Override
    public MiaoShaUser getLoginUser() {
        MiaoShaUser miaoShaUser = null;
        // 从request域中获取cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_NAME.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    miaoShaUser = redisUtils.get(UserKey.TOKEN.getFullkey(token), MiaoShaUser.class);
                    // 登录信息已经存在 更新过期时间
                    if (miaoShaUser != null) {
                        addUserTORedisAndSetCookie(token, miaoShaUser);
                    }
                    break;
                }
            }
        }
        return miaoShaUser;
    }

    private void addUserTORedisAndSetCookie(String token, MiaoShaUser user) {
        // 将User存入redis
        redisUtils.set(UserKey.TOKEN.getFullkey(token), user, TOKEN_TIMEOUT);
        // 将token加入cookie
        Cookie cookie = new Cookie(TOKEN_NAME, token);
        cookie.setMaxAge(TOKEN_TIMEOUT);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
