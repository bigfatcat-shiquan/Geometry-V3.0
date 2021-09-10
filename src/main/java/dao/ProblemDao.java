package dao;

import entity.Problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProblemDao {

    Integer insertOne(@Param("problem") Problem problem);

    Integer updateOne(@Param("problem") Problem problem);

    Problem selectOne(@Param("problem_id") Integer problem_id);

}
