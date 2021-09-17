package entity;

import java.util.Date;
import java.util.List;

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

    private List<Problem> problem_list;

    public User(String user_name,
                String user_password,
                String user_nickname,
                String user_picture) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_nickname = user_nickname;
        this.user_picture = user_picture;
    }

}
