package com.bigfatcat.geometry3.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigfatcat.geometry3.dao.ProblemDao;
import com.bigfatcat.geometry3.entity.Problem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 题目相关交互控制器
 * 包括：新增题目，题目详情，开启解答题目，查看解答结果，查询题目列表
 * */
@Controller
public class ProblemController {

    @Resource(name = "problemDao")
    private ProblemDao problemDao;

    /**
     * POST请求
     * 提交题目信息，创建并保存新题目至数据库
     * */
    @RequestMapping(value = "/startNewProblem", method = RequestMethod.POST)
    @ResponseBody
    public String startNewProblem(@RequestBody Map<String, String> map) {
//        Problem the_problem = new Problem(
//                "第一题",
//                map.get("problem_picture"),
//                1212456,
//                JSON.toJavaObject(JSONObject.parseObject(map.get("points_set")), HashSet.class),
//                map.get("points_location_x"),
//                map.get("points_location_y"),
//                map.get("initial_equals_str_set"),
//                map.get("need_prove_equal_str"));
//        the_problem.setCreate_date(new Date(System.currentTimeMillis()));
//        problemDao.insertOne(the_problem);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("problem_id", 0);
        return jsonObject.toJSONString();
    }

}
