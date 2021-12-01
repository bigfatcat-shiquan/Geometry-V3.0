package com.bigfatcat.geometry3.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bigfatcat.geometry3.entity.Problem;
import com.bigfatcat.geometry3.entity.User;
import com.bigfatcat.geometry3.service.ProblemService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 题目相关交互控制器
 * 包括：新增题目，题目详情，题目修改，开启解答题目并返回解答结果，打开公共或个人题库
 * */
@Controller
public class ProblemController {

    private final static int PROBLEMS_PAGE_SIZE = 3;

    @Resource(name = "problemService")
    private ProblemService problemService;

    /**
     * POST请求
     * 提交题目信息，创建并保存新题目至数据库
     * */
    @RequestMapping(value = "/startNewProblem", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String startNewProblem(@RequestBody Map<String, String> map, HttpSession session) {
        // 尝试获取当前页面会话所保存用户
        User current_user = (User) session.getAttribute("session_user");
        boolean success = current_user != null;
        String message = success ? "成功保存新题目!" : "用户会话已过期，请刷新页面!";
        // 解析请求载荷，得到题目信息, 调用保存新题目服务
        Integer the_new_problem_id = null;
        if (success) {
            the_new_problem_id = problemService.saveNewProblem(
                map.get("problem_name"),
                map.get("problem_picture"),
                current_user.getUser_id(),
                JSONObject.parseObject(map.get("points_set"), new TypeReference<HashSet<String>>(){}),
                JSONObject.parseObject(map.get("points_location_x"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("points_location_y"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("initial_equals_str_set"), new TypeReference<HashSet<String>>(){}),
                map.get("need_prove_equal_str"));
        }
        // 返回回执信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", message);
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
    public ModelAndView getProblemPage(@RequestParam Integer id, Model model) {
        model.addAttribute("the_problem", problemService.getOneProblem(id));
        return new ModelAndView("problem_detail", "info", model);
    }

    /**
     * GET请求
     * 打开相对应ID的题目修改页
     * */
    @RequestMapping(value = "/changeProblemPage", method = RequestMethod.GET)
    public ModelAndView changeProblemPage(@RequestParam Integer id, Model model) {
        model.addAttribute("the_problem", problemService.getOneProblem(id));
        return new ModelAndView("problem_change", "info", model);
    }

    /**
     * POST请求
     * 提交题目信息，修改数据库中题目信息并保存
     * */
    @RequestMapping(value = "/changeProblem", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeProblem(@RequestBody Map<String, String> map, HttpSession session) {
        // 初始化回执信息
        boolean success = true;
        String message = "题目信息修改成功";
        Integer matched_num = 0;
        // 尝试获取当前页面会话所保存用户
        User current_user = (User) session.getAttribute("session_user");
        if (current_user == null) {
            success = false;
            message = "用户会话已过期，请刷新页面!";
        } else if (!current_user.getUser_id().equals(Integer.valueOf(map.get("problem_author_id")))) {
            success = false;
            message = "您只能修改您自己创建的题目!";
        }
        // 解析请求载荷，得到题目信息, 调用修改题目服务
        if (success) {
            matched_num = problemService.changeOneProblem(
                Integer.valueOf(map.get("problem_id")),
                map.get("problem_name"),
                map.get("problem_picture"),
                JSONObject.parseObject(map.get("points_set"), new TypeReference<HashSet<String>>(){}),
                JSONObject.parseObject(map.get("points_location_x"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("points_location_y"), new TypeReference<HashMap<String, Double>>(){}),
                JSONObject.parseObject(map.get("initial_equals_str_set"), new TypeReference<HashSet<String>>(){}),
                map.get("need_prove_equal_str"));
            // 判断是否修改成功
            if (matched_num != 1) {
                success = false;
                message = "修改发生异常!";
            }
        }
        // 返回回执信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", message);
        jsonObject.put("matched_num", matched_num);
        return jsonObject.toJSONString();
    }

    /**
     * POST请求
     * 开启解答题目，进行题目自动推理，并在推理完成后返回推理日志结果
     * */
    @RequestMapping(value = "/solveProblem", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
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

    /**
     * GET请求
     * 请求所有用户创建的所有题目信息，并打开公共题库页
     * */
    @RequestMapping(value = "/problemBankPublic", method = RequestMethod.GET)
    public ModelAndView getProblemBankPublic(Model model) {
        PageInfo<Problem> page_info = problemService.getProblemList(
                null,
                null,
                null,
                null,
                1,
                PROBLEMS_PAGE_SIZE
        );
        model.addAttribute("session_user_id", -1);
        model.addAttribute("title", "公共题库");
        model.addAttribute("problems", page_info.getList());
        model.addAttribute("problems_count", page_info.getTotal());
        return new ModelAndView("problem_bank", "info", model);
    }

    /**
     * GET请求
     * 请求当前会话用户创建的所有题目信息，并打开个人题库页
     * */
    @RequestMapping(value = "/problemBankPrivate", method = RequestMethod.GET)
    public ModelAndView getProblemBankPrivate(HttpSession session, Model model) {
        User current_user = (User) session.getAttribute("session_user");
        Integer current_user_id = current_user.getUser_id();
        PageInfo<Problem> page_info = problemService.getProblemList(
                current_user_id,
                null,
                null,
                null,
                1,
                PROBLEMS_PAGE_SIZE
        );
        model.addAttribute("session_user_id", current_user_id);
        model.addAttribute("title", "我的题库");
        model.addAttribute("problems", page_info.getList());
        model.addAttribute("problems_count", page_info.getTotal());
        return new ModelAndView("problem_bank", "info", model);
    }

    /**
     * POST请求
     * 请求筛选查询题库中的题目信息
     * */
    @RequestMapping(value = "/filterProblems", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String filterProblems(@RequestBody Map<String, String> map) throws ParseException {
        // 解析请求载荷，得到查询参数信息
        int problem_author_id = Integer.parseInt(map.get("problem_author_id"));
        String problem_name = map.get("problem_name");
        String start_dt_str = map.get("start_dt");
        String end_dt = map.get("end_dt");
        SimpleDateFormat dt_format = new SimpleDateFormat("yyyy-MM-dd");
        // 调用查询题目列表服务
        PageInfo<Problem> page_info = problemService.getProblemList(
                problem_author_id == -1 ? null : problem_author_id,
                StringUtils.isEmpty(problem_name) ? null : problem_name,
                StringUtils.isEmpty(start_dt_str) ? null : dt_format.parse(start_dt_str),
                StringUtils.isEmpty(end_dt) ? null : dt_format.parse(end_dt),
                Integer.valueOf(map.get("page_num")),
                PROBLEMS_PAGE_SIZE
        );
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("problems", page_info.getList());
        jsonObject.put("problems_count", page_info.getTotal());
        return jsonObject.toJSONString();
    }

}
