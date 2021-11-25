package com.bigfatcat.geometry3.controller.interceptor;

import com.bigfatcat.geometry3.dao.UserDao;
import com.bigfatcat.geometry3.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器：未登录拦截器
 * */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource(name = "userDao")
    private UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        // 拦截并检查请求cookie
        Cookie[] cookies = request.getCookies();
        // 没有cookie，则跳转到登陆页面
        if (cookies == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        // 如果有cookie，检查其是否包含用户名信息
        String cookie_username = null;
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("cookie_username")) {
                cookie_username = cookie.getValue();
                break;
            }
        }
        // 用户名信息可查得，则将用户保存至session，并放行
        if (!StringUtils.isEmpty(cookie_username)) {
            User this_user = userDao.selectOneByName(cookie_username);
            if (this_user != null) {
                HttpSession session = request.getSession();
                if(session.getAttribute("session_user") == null) {
                    session.setAttribute("session_user", this_user);
                }
                return true;
            }
        }
        // 其他不合规情况，跳转到登陆页面
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

}
