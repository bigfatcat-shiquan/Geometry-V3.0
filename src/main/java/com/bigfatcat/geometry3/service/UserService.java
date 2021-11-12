package com.bigfatcat.geometry3.service;

import com.bigfatcat.geometry3.entity.User;

/**
 * 接口：UserService 用户相关服务
 * 创建新用户
 * 查找一个用户
 * 修改一个用户
 * 删除一个用户
 * */
public interface UserService {

    /**保存一个新的用户*/
    Integer saveNewUser(String user_name,
                        String user_password,
                        String user_nickname,
                        String user_picture);

    /**查找一个用户*/
    User getOneUser(String user_name, String user_password);

    /**修改一个用户*/
    Integer changeOneUser(Integer user_id,
                          String user_password,
                          String user_nickname,
                          String user_picture);

}
