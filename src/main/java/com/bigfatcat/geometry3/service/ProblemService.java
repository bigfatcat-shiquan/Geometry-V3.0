package com.bigfatcat.geometry3.service;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 接口：ProblemService 题目相关服务
 *
 *
 * */
public interface ProblemService {

    /**保存一个新的题目*/
    Boolean saveNewProblem(String problem_name,
                           String problem_picture,
                           Integer problem_author_id,
                           HashSet<String> initial_points_set,
                           HashMap<String, Double> points_location_x,
                           HashMap<String, Double> points_location_y,
                           HashSet<String> initial_equals_str_set,
                           String need_prove_equal_str);

    /**读取一个题目*/

    /**修改一个题目*/

    /**解答一个题目*/

}
