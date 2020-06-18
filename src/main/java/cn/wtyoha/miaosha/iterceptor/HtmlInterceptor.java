package cn.wtyoha.miaosha.iterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class HtmlInterceptor implements HandlerInterceptor {

    /**
     * 对于直接请求html页面的，返回相应的视图
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info(requestURI);
        if ("html".equals(requestURI.substring(requestURI.length() - 4))) {
            String[] url = requestURI.split("/");
            String page = url[url.length - 1];
            page = page.substring(0, page.length() - 5);
            log.info(requestURI + " redirect to +" + "/staticHtml?page=" + page);
            response.sendRedirect(request.getContextPath() + "/staticHtml?page=" + page);
            return false;
        }
        return true;
    }
}
