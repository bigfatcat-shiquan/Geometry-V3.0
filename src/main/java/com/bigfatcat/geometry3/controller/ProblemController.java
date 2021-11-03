package com.bigfatcat.geometry3.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bigfatcat.geometry3.service.ProblemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 题目相关交互控制器
 * 包括：新增题目，题目详情，开启解答题目并返回解答结果，查询题目列表
 * */
@Controller
public class ProblemController {

    @Resource(name = "problemService")
    private ProblemService problemService;

    /**
     * POST请求
     * 提交题目信息，创建并保存新题目至数据库
     * */
    @RequestMapping(value = "/startNewProblem", method = RequestMethod.POST)
    @ResponseBody
    public String startNewProblem(@RequestBody Map<String, String> map) {
        // 解析请求载荷，得到题目信息, 调用保存新题目服务
        Integer the_new_problem_id = problemService.saveNewProblem(
                map.get("problem_name"),
                map.get("problem_picture"),
                233333,
                JSONObject.parseObject(map.get("points_set"), new TypeReference<HashSet<String>>(){}),
                JSONObject.parseObject(map.get("points_location_x"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("points_location_y"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("initial_equals_str_set"), new TypeReference<HashSet<String>>(){}),
                map.get("need_prove_equal_str"));
        // 返回回执信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("problem_id", the_new_problem_id);
        return jsonObject.toJSONString();
    }

    /**
     * GET请求
     * 请求对应ID的题目详细信息
     * */
    @RequestMapping(value = "/getProblem", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblem(@RequestParam Integer id) {
        return JSON.toJSONString(problemService.getOneProblem(id));
    }

    /**
     * GET请求
     * 请求对应ID的题目详细信息，并打开相应题目详情页
     * */
    @RequestMapping(value = "/getProblemPage", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getProblemPage(@RequestParam Integer id, Model model) {
        model.addAttribute("the_problem", problemService.getOneProblem(id));
        return new ModelAndView("problem_detail", "info", model);
    }

    /**
     * POST请求
     * 开启解答题目，进行题目自动推理，并在推理完成后返回推理日志结果
     * */
    @RequestMapping(value = "/solveProblem", method = RequestMethod.POST)
    @ResponseBody
    public String solveProblem(@RequestBody Map<String, String> map) throws Exception{
        // 解析请求载荷，得到题目信息以及推理参数高级设置信息, 调用题目解答服务，进行自动推理
        HashMap<String, Object> solve_result = problemService.solveOneProblem(
                map.get("problem_name"),
                JSONObject.parseObject(map.get("points_set"), new TypeReference<HashSet<String>>(){}),
                JSONObject.parseObject(map.get("points_location_x"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("points_location_y"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("initial_equals_str_set"), new TypeReference<HashSet<String>>(){}),
                map.get("need_prove_equal_str"),
                Integer.parseInt(map.get("max_deduce_times")),
                Integer.parseInt(map.get("max_complex_len"))
        );
        // 返回推理完成结果信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("has_proved", solve_result.get("has_proved"));
        jsonObject.put("problem_solve_log", solve_result.get("log"));
        return jsonObject.toJSONString();
    }

}
