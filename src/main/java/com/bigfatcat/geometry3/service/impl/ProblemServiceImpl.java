package com.bigfatcat.geometry3.service.impl;

import com.bigfatcat.geometry3.dao.ProblemDao;
import com.bigfatcat.geometry3.entity.Problem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bigfatcat.geometry3.service.ProblemService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 接口实现类：ProblemServiceImpl 实现题目相关服务
 *
 *
 * */
@Service("problemService")
public class ProblemServiceImpl implements ProblemService {

    private ProblemDao problemDao;
    @Resource(name = "problemDao")
    public void setProblemDao(ProblemDao problemDao) {
        this.problemDao = problemDao;
    }

    /**保存一个新的题目*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Boolean saveNewProblem(String problem_name,
                                  String problem_picture,
                                  Integer problem_author_id,
                                  HashSet<String> initial_points_set,
                                  HashMap<String, Double> points_location_x,
                                  HashMap<String, Double> points_location_y,
                                  HashSet<String> initial_equals_str_set,
                                  String need_prove_equal_str) {
        Problem the_new_problem = new Problem(problem_name, problem_picture, problem_author_id, initial_points_set,
                points_location_x, points_location_y, initial_equals_str_set, need_prove_equal_str);
        return problemDao.insertOne(the_new_problem) > 0;
    }

}
