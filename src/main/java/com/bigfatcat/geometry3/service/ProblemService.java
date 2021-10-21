package com.bigfatcat.geometry3.service;

import com.bigfatcat.geometry3.entity.Problem;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 接口：ProblemService 题目相关服务
 * 保存新题目
 * 获取一个题目
 * 修改一个题目
 * 删除题目
 * 查找多个题目
 * 解答一个题目
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

    /**解答一个题目*/

}
