package com.bigfatcat.geometry3.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigfatcat.geometry3.entity.User;
import com.bigfatcat.geometry3.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户相关交互控制器
 * 包括：用户注册，用户登录，用户修改个人信息
 * */
@Controller
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    /**
     * POST请求
     * 提交用户注册信息，创建并保存用户至数据库
     * */
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String UserRegister(@RequestBody Map<String, String> map) {
        // 解析请求载荷，得到用户注册信息, 调用保存新用户服务
        Integer the_new_user_id = userService.saveNewUser(
                map.get("user_name"),
                map.get("user_password"),
                map.get("user_nickname"),
                map.get("user_picture"));
        // 判断是否注册成功
        boolean success = the_new_user_id != -999;
        String message = success ? "注册成功" : "用户名已被注册，请更换";
        // 返回回执信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }

    /**
     * POST请求
     * 提交用户登录信息，获取用户详细信息
     * */
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String userLogin(@RequestBody Map<String, String> map,
                            HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // 尝试使用用户提交的信息进行登录
        User the_user = userService.getOneUser(map.get("user_name"), map.get("user_password"));
        // 判断是否登录成功
        boolean success = the_user != null;
        String message = success ? "登录成功" : "用户名或密码错误";
        // 登陆成功写入cookie和session
        if (success) {
            session.setAttribute("session_user", the_user);
            Cookie cookie = new Cookie("cookie_username", the_user.getUser_name());
            cookie.setMaxAge(3 * 24 * 60 * 60);
            cookie.setPath(request.getContextPath());
            response.addCookie(cookie);
        }
        // 返回回执信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }

    /**
     * POST请求
     * 提交用户信息，修改数据库中用户信息并保存
     * */
    @RequestMapping(value = "/changeUser", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeUser(@RequestBody Map<String, String> map) {
        // 解析请求载荷，得到用户提交信息, 调用修改用户信息服务
        Integer matched_num = userService.changeOneUser(
                Integer.valueOf(map.get("user_id")),
                map.get("user_password"),
                map.get("user_nickname"),
                map.get("user_picture"));
        // 判断是否修改成功
        boolean success = matched_num == 1;
        String message = success ? "用户信息修改成功" : "修改发生异常！";
        // 返回回执信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", message);
        jsonObject.put("matched_num", matched_num);
        return jsonObject.toJSONString();
    }

}
