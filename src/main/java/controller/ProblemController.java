package controller;

import dao.ProblemDao;
import entity.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 题目相关交互控制器
 * 包括：新增题目，我的题目，题目详情，开启解答题目，查看解答结果
 * */
@Controller
public class ProblemController {

    @Autowired
    private ProblemDao problemDao;

    @RequestMapping("/test")
    public Problem test(){
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

}
