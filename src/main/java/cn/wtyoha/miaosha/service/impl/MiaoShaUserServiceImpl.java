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
    public MiaoShaUser login(MiaoShaUser loginUser) {
        MiaoShaUser user = null;
        // 根据cookie检查是否已经登陆
        if ((user = getLoginUser()) != null) {
            return user;
        }
        if (redisUtils.get(UserKey.ID.getFullKey(String.valueOf(loginUser.getId())), Boolean.class) != null) {
            // 用户已经登陆
            throw new GlobalException(CodeMsg.REPEAT_LOGIN);
        }
        String password = loginUser.getPassword();
        // 从数据库获取用户信息
        user = userDao.selectByPrimaryKey(loginUser.getId());
        if (user == null) {
            throw new GlobalException(CodeMsg.UNREGISTER_USER);
        }
        String salt = user.getSalt();
        String dbPass = MD5Utils.inputPassToDBPass(password, salt);
        if (dbPass.equals(user.getPassword())) {
            // 设置cookie
            String token = UUID.randomUUID().toString().replace("-", "");
            addUserToRedisAndSetCookie(token, user);
            setUserLoginStatus(user);
            return user;
        } else {
            throw new GlobalException(CodeMsg.WRONG_PASSWORD);
        }
    }

    /**
     * 设置用户登陆状态
     *
     * @param user
     */
    private void setUserLoginStatus(MiaoShaUser user) {
        redisUtils.set(UserKey.ID.getFullKey(String.valueOf(user.getId())), true, RedisUtils.THIRTY_MINUTE);
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
     *
     * @return
     */
    @Override
    public MiaoShaUser getLoginUser() {
        MiaoShaUser miaoShaUser = null;
        // 从request域中获取cookie
        String token = getCookie(TOKEN_NAME);
        miaoShaUser = redisUtils.get(UserKey.TOKEN.getFullKey(token), MiaoShaUser.class);
        // 登录信息已经存在 更新过期时间
        if (miaoShaUser != null) {
            addUserToRedisAndSetCookie(token, miaoShaUser);
            setUserLoginStatus(miaoShaUser);
        }
        return miaoShaUser;
    }

    @Override
    public void logout() {
        // 用户登出
        // 删除cookie中thoken对应的user信息
        String token = getCookie(TOKEN_NAME);
        MiaoShaUser user = redisUtils.get(UserKey.TOKEN.getFullKey(token), MiaoShaUser.class);
        redisUtils.deleteKey(UserKey.TOKEN.getFullKey(token));
        // 删除user的登陆状态
        redisUtils.deleteKey(UserKey.ID.getFullKey(String.valueOf(user.getId())));
    }

    private void addUserToRedisAndSetCookie(String token, MiaoShaUser user) {
        // 将User存入redis
        redisUtils.set(UserKey.TOKEN.getFullKey(token), user, RedisUtils.THIRTY_MINUTE);
        // 将token加入cookie
        Cookie cookie = new Cookie(TOKEN_NAME, token);
        cookie.setMaxAge(RedisUtils.THIRTY_MINUTE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(String key) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_NAME.equals(cookie.getName())) {
                    value = cookie.getValue();
                    return value;
                }
            }
        }
        return value;
    }
}
