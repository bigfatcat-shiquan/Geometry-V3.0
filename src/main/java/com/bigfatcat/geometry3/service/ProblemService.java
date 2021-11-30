package com.bigfatcat.geometry3.service;

import com.bigfatcat.geometry3.entity.Problem;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 接口：ProblemService 题目相关服务
 * 保存新题目
 * 获取一个题目
 * 修改一个题目
 * 解答一个题目
 * 查找多个题目
 * */
public interface ProblemService {

    /**保存一个新的题目*/
    Integer saveNewProblem(String problem_name,
                           String problem_picture,
                           Integer problem_author_id,
                           HashSet<String> initial_points_set,
                           HashMap<String, Double> points_location_x,
                           HashMap<String, Double> points_location_y,
                           HashSet<String> initial_equals_str_set,
                           String need_prove_equal_str);

    /**获取一个题目*/
    Problem getOneProblem(Integer problem_id);

    /**修改一个题目*/
    Integer changeOneProblem(Integer problem_id,
                             String problem_name,
                             String problem_picture,
                             HashSet<String> initial_points_set,
                             HashMap<String, Double> points_location_x,
                             HashMap<String, Double> points_location_y,
                             HashSet<String> initial_equals_str_set,
                             String need_prove_equal_str);

    /**解答一个题目*/
    HashMap<String, Object> solveOneProblem(String problem_name,
                                            HashSet<String> initial_points_set,
                                            HashMap<String, Double> points_location_x,
                                            HashMap<String, Double> points_location_y,
                                            HashSet<String> initial_equals_str_set,
                                            String need_prove_equal_str,
                                            Integer max_deduce_times,
                                            Integer max_complex_len) throws Exception;

    /**查找多个题目*/
    List<Problem> getProblemList(Integer problem_author_id,
                                 String problem_name,
                                 Date start_dt,
                                 Date end_dt);

}
