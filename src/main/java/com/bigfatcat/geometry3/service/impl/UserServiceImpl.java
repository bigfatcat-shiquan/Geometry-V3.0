package com.bigfatcat.geometry3.service.impl;

import com.bigfatcat.geometry3.dao.UserDao;
import com.bigfatcat.geometry3.entity.User;
import com.bigfatcat.geometry3.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 接口实现类：UserServiceImpl 实现用户相关服务
 * 创建新用户
 * 查找一个用户
 * 修改一个用户
 * 删除一个用户
 * */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDao")
    private UserDao userDao;

    /**保存一个新的用户，并返回该用户在数据库中的ID值*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer saveNewUser(String user_name,
                               String user_password,
                               String user_nickname,
                               String user_picture) {
        User the_new_user = new User(user_name, user_password, user_nickname, user_picture);
        the_new_user.setRegister_date(new Date(System.currentTimeMillis()));
        userDao.insertOne(the_new_user);
        return the_new_user.getUser_id();
    }

    /**查找一个用户，返回该用户详细信息*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public User getOneUser(String user_name, String user_password) {
        return userDao.selectOne(user_name, user_password);
    }

    /**修改一个用户，并返回数据库中匹配到的记录行数*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer changeOneUser(Integer user_id,
                                 String user_password,
                                 String user_nickname,
                                 String user_picture) {
        User the_user = new User(null, user_password, user_nickname, user_picture);
        the_user.setUser_id(user_id);
        return userDao.updateOne(the_user);
    }

}
