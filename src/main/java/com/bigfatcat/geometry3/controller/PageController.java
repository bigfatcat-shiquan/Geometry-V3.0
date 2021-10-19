package com.bigfatcat.geometry3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 页面交互控制器
 * 网站基础页面点击切换响应
 * */
@Controller
public class PageController {

    /**
     * GET请求
     * 打开主页
     * */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    /**
     * GET请求
     * 打开创建新题目页
     * */
    @RequestMapping(value = "/newProblem", method = RequestMethod.GET)
    public String newProblem() {
        return "new_problem";
    }

}
