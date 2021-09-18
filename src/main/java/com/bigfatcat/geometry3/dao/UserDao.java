package com.bigfatcat.geometry3.dao;

import com.bigfatcat.geometry3.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**实体类User的ORM接口*/
@Mapper
public interface UserDao {

    Integer insertOne(@Param("user") User user);

    Integer deleteOne(@Param("user_id") Integer user_id);

}
