package entity;

import java.util.HashMap;
import java.util.HashSet;

public class Problem {

    private Integer problem_id;

    private String problem_name;

    private String problem_author_id;

    private String graph_name;

    private HashSet<String> initial_points_set;

    private HashSet<String> auxiliary_points_set;

    private HashMap<String, Double> points_location_x;

    private HashMap<String, Double> points_location_y;

    private HashSet<String> segment_equals_str_set;

    private HashSet<String> angle_equals_str_set;

    private HashSet<String> auxiliary_equals_str_set;


}
