package entity;

import core.Graph;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Problem {

    private Integer problem_id;

    private String problem_name;

    private String problem_picture;

    private Integer problem_author_id;

    private Date create_date;

    private Graph graph;

    private HashSet<String> initial_points_set;

    private HashMap<String, Double> points_location_x;

    private HashMap<String, Double> points_location_y;

    private HashSet<String> initial_equals_str_set;

    private String need_prove_equal_str;

    public Problem(String problem_name,
                   String problem_picture,
                   Integer problem_author_id,
                   HashSet<String> initial_points_set,
                   HashMap<String, Double> points_location_x,
                   HashMap<String, Double> points_location_y,
                   HashSet<String> initial_equals_str_set,
                   String need_prove_equal_str) {
        this.problem_name = problem_name;
        this.problem_picture = problem_picture;
        this.problem_author_id = problem_author_id;
        this.initial_points_set = initial_points_set;
        this.points_location_x = points_location_x;
        this.points_location_y = points_location_y;
        this.initial_equals_str_set = initial_equals_str_set;
        this.need_prove_equal_str = need_prove_equal_str;
    }

    public void setProblem_id(Integer problem_id) {
        this.problem_id = problem_id;
    }

    public void setProblem_name(String problem_name) {
        this.problem_name = problem_name;
    }

    public void setProblem_picture(String problem_picture) {
        this.problem_picture = problem_picture;
    }

    public void setProblem_author_id(Integer problem_author_id) {
        this.problem_author_id = problem_author_id;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setInitial_points_set(HashSet<String> initial_points_set) {
        this.initial_points_set = initial_points_set;
    }

    public void setPoints_location_x(HashMap<String, Double> points_location_x) {
        this.points_location_x = points_location_x;
    }

    public void setPoints_location_y(HashMap<String, Double> points_location_y) {
        this.points_location_y = points_location_y;
    }

    public void setInitial_equals_str_set(HashSet<String> initial_equals_str_set) {
        this.initial_equals_str_set = initial_equals_str_set;
    }

    public void setNeed_prove_equal_str(String need_prove_equal_str) {
        this.need_prove_equal_str = need_prove_equal_str;
    }

    public Integer getProblem_id() {
        return this.problem_id;
    }

    public String getProblem_name() {
        return this.problem_name;
    }

    public String getProblem_picture() {
        return this.problem_picture;
    }

    public Integer getProblem_author_id() {
        return this.problem_author_id;
    }

    public Date getCreate_date() {
        return this.create_date;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public HashSet<String> getInitial_points_set() {
        return this.initial_points_set;
    }

    public HashMap<String, Double> getPoints_location_x() {
        return this.points_location_x;
    }

    public HashMap<String, Double> getPoints_location_y() {
        return this.points_location_y;
    }

    public HashSet<String> getInitial_equals_str_set() {
        return this.initial_equals_str_set;
    }

    public String getNeed_prove_equal_str() {
        return this.need_prove_equal_str;
    }

}
