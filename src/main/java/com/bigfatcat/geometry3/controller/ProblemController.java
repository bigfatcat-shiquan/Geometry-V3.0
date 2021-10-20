package com.bigfatcat.geometry3.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
        Problem the_problem = new Problem(
                map.get("problem_name"),
                map.get("problem_picture"),
                233333,
                JSONObject.parseObject(map.get("points_set"), new TypeReference<HashSet<String>>(){}),
                JSONObject.parseObject(map.get("points_location_x"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("points_location_y"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("initial_equals_str_set"), new TypeReference<HashSet<String>>(){}),
                map.get("need_prove_equal_str"));
        the_problem.setCreate_date(new Date(System.currentTimeMillis()));
        problemDao.insertOne(the_problem);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("problem_id", the_problem.getProblem_id());
        return jsonObject.toJSONString();
    }

}
