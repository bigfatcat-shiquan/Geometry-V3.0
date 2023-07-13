package com.bigfatcat.geometry3;

import com.bigfatcat.geometry3.controller.PageController;
import com.bigfatcat.geometry3.controller.ProblemController;
import com.bigfatcat.geometry3.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes=com.bigfatcat.geometry3.Geometry3Application.class)
class Geometry3ApplicationTests {

    @Autowired
    private PageController pageController;

    @Autowired
    private UserController userController;

    @Autowired
    private ProblemController problemController;

    @Test
    void contextLoads() {
        assertThat(pageController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(problemController).isNotNull();
    }

}
