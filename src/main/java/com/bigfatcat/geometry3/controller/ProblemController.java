package com.bigfatcat.geometry3.controller;

import com.bigfatcat.geometry3.dao.ProblemDao;
import com.bigfatcat.geometry3.entity.Problem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 题目相关交互控制器
 * 包括：新增题目，我的题目，题目详情，开启解答题目，查看解答结果
 * */
@Controller
public class ProblemController {

    @Resource(name = "problemDao")
    private ProblemDao problemDao;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Problem test() {
        HashSet<String> initial_points_set = new HashSet<>();
        initial_points_set.add("A");
        HashMap<String, Double> points_location_x = new HashMap<>();
        points_location_x.put("A", 1.2);
        HashMap<String, Double> points_location_y = new HashMap<>();
        points_location_y.put("A", 2.5);
        HashSet<String> initial_equals_str_set = new HashSet<>();
        initial_equals_str_set.add("AB=CD");
        initial_equals_str_set.add("∠ABC=∠BDE");

        Problem the_problem = new Problem("第一题", "1.jpg", 1212456,
                initial_points_set, points_location_x, points_location_y, initial_equals_str_set,
                "AB=DE");
        the_problem.setCreate_date(new Date(System.currentTimeMillis()));
        problemDao.insertOne(the_problem);
        return the_problem;
    }

    @RequestMapping(value = "/startNewProblem", method = RequestMethod.POST)
    public ModelAndView startNewProblem(@RequestBody Map<String, Object> map, Model model) {
        model.addAttribute("initial_points_set", map.get("points_set"));
        model.addAttribute("points_location_x", map.get("points_location_x"));
        return new ModelAndView("problem_detail", "problem_info", model);
    }

    @RequestMapping(value = "/newProblem", method = RequestMethod.GET)
    public String newProblem() {
        return "new_problem";
    }

}
