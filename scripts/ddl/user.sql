CREATE TABLE user (
    `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `user_name` varchar(256) NOT NULL,
    `user_password` varchar(256) NOT NULL,
    `user_nickname` varchar(256) NOT NULL,
    `user_picture` varchar(4096) NOT NULL,
    `register_date` date NOT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT uniq_user_name UNIQUE(`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;