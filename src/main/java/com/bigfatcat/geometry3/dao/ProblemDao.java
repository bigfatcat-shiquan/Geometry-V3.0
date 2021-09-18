package com.bigfatcat.geometry3.dao;

import com.bigfatcat.geometry3.entity.Problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**实体类Problem的ORM接口*/
@Mapper
public interface ProblemDao {

    Integer insertOne(@Param("problem") Problem problem);

    Integer deleteOne(@Param("problem_id") Integer problem_id);

    Integer updateOne(@Param("problem") Problem problem);

    Problem selectOne(@Param("problem_id") Integer problem_id);

    List<Problem> select(@Param("problem_author_id") Integer problem_author_id,
                         @Param("problem_name") String problem_name,
                         @Param("start_dt") Date start_dt,
                         @Param("end_dt") Date end_dt);

}
