package com.bigfatcat.geometry3.entity;

import java.util.Date;

/**
 * 实体类：User
 * 用户，代表软件使用者单元
 * */
public class User {

    private Integer user_id;

    private String user_name;

    private String user_password;

    private String user_nickname;

    private String user_picture;

    private Date register_date;

    public User() {}

    public User(String user_name,
                String user_password,
                String user_nickname,
                String user_picture) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_nickname = user_nickname;
        this.user_picture = user_picture;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_id() {
        return this.user_id;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public String getUser_nickname() {
        return this.user_nickname;
    }

    public String getUser_picture() {
        return this.user_picture;
    }

    public void setRegister_date(Date register_date) {
        this.register_date = register_date;
    }

    public Date getRegister_date() {
        return this.register_date;
    }

}
