CREATE TABLE problem (
    `problem_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `problem_name` varchar(256) NOT NULL,
    `problem_picture` MEDIUMTEXT NOT NULL,
    `problem_author_id` bigint(20) unsigned NOT NULL,
    `create_date` date NOT NULL,
    `initial_points_set` varchar(512) NOT NULL,
    `points_location_x` varchar(512) NOT NULL,
    `points_location_y` varchar(512) NOT NULL,
    `initial_equals_str_set` varchar(512) NOT NULL,
    `need_prove_equal_str` varchar(128) NOT NULL,
    `hard_level` int NOT NULL,
    PRIMARY KEY (`problem_id`),
    CONSTRAINT uniq_problem_name_problem_author_id UNIQUE(`problem_name`, `problem_author_id`),
    CONSTRAINT fk_user FOREIGN KEY (`problem_author_id`) REFERENCES user(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;