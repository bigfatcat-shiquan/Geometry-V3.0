import service.ProblemService;
import service.impl.ProblemServiceImpl;

/**
 * 业务封装实现层测试篇
 * */
public class ServiceTest {

    public static void main(String[] args) {

        ProblemService problem_1 = new ProblemServiceImpl();
        problem_1.openNewProblem();

    }

}
