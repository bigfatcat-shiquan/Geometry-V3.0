package com.bigfatcat.geometry3.service.impl;

import com.bigfatcat.geometry3.dao.ProblemDao;
import com.bigfatcat.geometry3.entity.Problem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bigfatcat.geometry3.service.ProblemService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 接口实现类：ProblemServiceImpl 实现题目相关服务
 * 保存新题目
 * 获取一个题目
 * 修改一个题目
 * 删除题目
 * 查找多个题目
 * 解答一个题目
 * */
@Service("problemService")
public class ProblemServiceImpl implements ProblemService {

    @Resource(name = "problemDao")
    private ProblemDao problemDao;

    /**保存一个新的题目，并返回该题目在数据库中的ID值*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer saveNewProblem(String problem_name,
                                  String problem_picture,
                                  Integer problem_author_id,
                                  HashSet<String> initial_points_set,
                                  HashMap<String, Double> points_location_x,
                                  HashMap<String, Double> points_location_y,
                                  HashSet<String> initial_equals_str_set,
                                  String need_prove_equal_str) {
        Problem the_new_problem = new Problem(problem_name, problem_picture, problem_author_id, initial_points_set,
                points_location_x, points_location_y, initial_equals_str_set, need_prove_equal_str);
        the_new_problem.setCreate_date(new Date(System.currentTimeMillis()));
        problemDao.insertOne(the_new_problem);
        return the_new_problem.getProblem_id();
    }

    /**获取一个题目，返回该题目详细信息*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Problem getOneProblem(Integer problem_id) {
        return problemDao.selectOne(problem_id);
    }

}
