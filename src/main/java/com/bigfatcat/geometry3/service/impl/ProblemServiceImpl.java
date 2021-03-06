package com.bigfatcat.geometry3.service.impl;

import com.bigfatcat.geometry3.core.Graph;
import com.bigfatcat.geometry3.core.structure.elements.Element;
import com.bigfatcat.geometry3.service.ProblemService;
import com.bigfatcat.geometry3.dao.ProblemDao;
import com.bigfatcat.geometry3.entity.Problem;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 接口实现类：ProblemServiceImpl 实现题目相关服务
 * 保存新题目
 * 获取一个题目
 * 修改一个题目
 * 解答一个题目
 * 查找多个题目
 * 统计题目数量
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
                                  String need_prove_equal_str,
                                  Integer hard_level) {
        Problem the_new_problem = new Problem(problem_name, problem_picture, problem_author_id, initial_points_set,
                points_location_x, points_location_y, initial_equals_str_set, need_prove_equal_str, hard_level);
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

    /**修改一个题目，并返回数据库中匹配到的记录行数*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer changeOneProblem(Integer problem_id,
                                    String problem_name,
                                    String problem_picture,
                                    HashSet<String> initial_points_set,
                                    HashMap<String, Double> points_location_x,
                                    HashMap<String, Double> points_location_y,
                                    HashSet<String> initial_equals_str_set,
                                    String need_prove_equal_str,
                                    Integer hard_level) {
        Problem the_problem = new Problem(problem_name, problem_picture, null, initial_points_set,
                points_location_x, points_location_y, initial_equals_str_set, need_prove_equal_str, hard_level);
        the_problem.setProblem_id(problem_id);
        return problemDao.updateOne(the_problem);
    }

    /**解答一个题目，返回解答该题目的最终结果与过程日志*/
    @Override
    public HashMap<String, Object> solveOneProblem(String problem_name,
                                                   HashSet<String> initial_points_set,
                                                   HashMap<String, Double> points_location_x,
                                                   HashMap<String, Double> points_location_y,
                                                   HashSet<String> initial_equals_str_set,
                                                   String need_prove_equal_str,
                                                   Integer max_deduce_times,
                                                   Integer max_complex_len) throws Exception{
        // 创建题目图对象并解析题目初始信息
        Graph the_problem_graph = new Graph(problem_name);
        for (String point : initial_points_set) {
            the_problem_graph.addPoint(point, points_location_x.get(point), points_location_y.get(point));
        }
        for (String equation_str : initial_equals_str_set) {
            Element[] equal_element_array = Graph.equationStrToElement(equation_str);
            the_problem_graph.addEqual(Graph.getMathStrType(equation_str), equal_element_array[0], equal_element_array[1]);
        }
        System.out.print("[ProblemService SolveOneProblem] Problem Information Has Received \n");
        // 初始化推理参数设置
        if (max_deduce_times == 0) {
            max_deduce_times = 5;
        }
        if (max_complex_len == 0) {
            max_complex_len = 3;
        }
        // 进行推理
        for (int deduce_no=1; deduce_no<=max_deduce_times; deduce_no++) {
            System.out.print("[ProblemService SolveOneProblem] Start Deduce No " + deduce_no + " ...... \n");
            the_problem_graph.deduceByAll(deduce_no, max_complex_len);
        }
        System.out.print("[ProblemService SolveOneProblem] Deduction Has Finished! \n");
        the_problem_graph.displayGraphInfo();
        // 推理完成，检查待求证结论是否已被证明
        Boolean has_proved;
        Element[] need_prove_equal_element_array = Graph.equationStrToElement(need_prove_equal_str);
        has_proved = the_problem_graph.queryEqual(Graph.getMathStrType(need_prove_equal_str),
                                                need_prove_equal_element_array[0], need_prove_equal_element_array[1]);
        // 返回结果与过程日志
        HashMap<String, Object> result_map = new HashMap<>();
        result_map.put("has_proved", has_proved);
        result_map.put("log", the_problem_graph.getGraph_log());
        return result_map;
    }

    /**查找多个题目，返回题目对象列表或分页对象*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Problem> getProblemList(Integer problem_author_id,
                                        String problem_name,
                                        Date start_dt,
                                        Date end_dt) {
        return problemDao.select(problem_author_id, problem_name, start_dt, end_dt);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public PageInfo<Problem> getProblemList(Integer problem_author_id,
                                            String problem_name,
                                            Date start_dt,
                                            Date end_dt,
                                            Integer page_num,
                                            Integer page_size) {
        PageHelper.startPage(page_num, page_size);
        return new PageInfo<>(problemDao.select(problem_author_id, problem_name, start_dt, end_dt));
    }

    /**统计题目数量，返回符合筛选条件的题目记录数*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer countProblem(Integer problem_author_id,
                                String problem_name,
                                Date start_dt,
                                Date end_dt) {
        return problemDao.count(problem_author_id, problem_name, start_dt, end_dt);
    }

}
