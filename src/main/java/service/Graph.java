package service;

import structure.elements.*;
import structure.relations.Equal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Math.*;

/**
 * 类：Graph 业务主功能层：图对象
 * 一个欧式几何问题，可以看作是一个图对象，这个图对象由structure结构层中的elements元素和relations等量关系所组成
 * 解决一个几何问题的过程，可以看作是利用这个图对象实例中的初始等量关系推导出更多等量关系的过程
 * */
public class Graph {

    /**图对象名称：这个图对象的名字，一般以几何题目的编号命名*/
    private final String graph_name;

    /**点集合：这个图所包含的所有点，点以A-Z大写字母表示*/
    private final HashSet<String> points_set;

    /**点横坐标数值表：记录每个点的横坐标的HashMap*/
    private final HashMap<String, Double> points_location_x;

    /**点纵坐标数值表：记录每个点的纵坐标的HashMap*/
    private final HashMap<String, Double> points_location_y;

    /**边相等关系集合：这个图所已知的所有边的相等关系的集合，用包含边元素的equal的集合来表示*/
    private final HashSet<Equal> segment_equals_set;

    /**角或角度相等关系集合：这个图所已知的所有角或角度的相等关系的集合，用包含角或角度元素的equal的集合来表示*/
    private final HashSet<Equal> angle_equals_set;

    /**可以组成的三角形的集合：这个图上的任意不共线的三个点纳入该集合*/
    private HashSet<HashSet<String>> triangles_set;

    /**可以组成的三点共线的集合：这个图上的任意共线的三个点纳入该集合*/
    private HashSet<String[]> collinear_set;

    /**定义图对象的构造函数*/
    public Graph(String graph_name) {
        this.graph_name = graph_name;
        this.points_set = new HashSet<>();
        this.points_location_x = new HashMap<>();
        this.points_location_y = new HashMap<>();
        this.segment_equals_set = new HashSet<>();
        this.angle_equals_set = new HashSet<>();
        this.triangles_set = new HashSet<>();
        this.collinear_set = new HashSet<>();
    }

    /**
     * 在图对象中添加一个点，例如添加点A，横坐标1.5，纵坐标2.5，则写作this_graph.addPoint("A", 1.5, 2.5)
     * 该方法一般在初始化一个几何问题图对象时给定初始点的时候调用，或者在后期添加辅助点的时候调用
     * */
    public void addPoint(String point, double location_x, double location_y) throws Exception{
        if (this.points_set.contains(point)) {
            throw new Exception("添加过的点不允许重复添加和修改！如需要请重建新的图对象！");
        }
        this.points_set.add(point);
        this.points_location_x.put(point, location_x);
        this.points_location_y.put(point, location_y);
    }

    /**获取该图对象的名称*/
    public String getGraph_name() {
        return this.graph_name;
    }

    /**获取该图对象的点的集合*/
    public HashSet<String> getPoints_set() {
        return this.points_set;
    }

    /**获取该图对象的某个点的横坐标*/
    public Double getPoint_location_x(String point) {
        return this.points_location_x.get(point);
    }

    /**获取该图对象的某个点的纵坐标*/
    public Double getPoint_location_y(String point) {
        return this.points_location_y.get(point);
    }

    /**
     * 在图对象中添加一个相等关系
     * 例如添加一个条件：边AB=边AC，则写作this_graph.addEqual("seg", segment("A", "B"), segment("A", "C"))
     * 例如添加一个条件：∠ABC=60°，则写作this_graph.addEqual("ang", angle("A", "B", "C"), Degree(60))
     * 该方法一般用在三个地方：
     * 一，给定几何题目的初始等量关系时，需要调用此方法添加一波
     * 二，在解决题目过程中通过定理推导出了更多的等量关系，补充入图对象中时
     * 三，需要添加新的辅助点时，需要给定所添加辅助点的初始等量关系
     * */
    public synchronized void addEqual(String equal_type, Element unit_1, Element unit_2) throws Exception {
        // 如果两个元素本来就是同一元素，则取消添加相等关系；如果不同，方才创建equal对象
        if (unit_1.equals(unit_2)) {
            return;
        }
        Equal the_equal = Equal.equal(unit_1, unit_2);
        HashSet<Equal> the_set;
        // 判断是添加边元素相等关系，还是添加角或角度元素相等关系
        switch (equal_type) {
            case "seg":
                the_set = this.segment_equals_set;
                break;
            case "ang":
                the_set = this.angle_equals_set;
                break;
            default:
                throw new IllegalStateException("不可以添加非边类型或角类型的相等关系！" + equal_type);
        }
        // 先遍历寻找要添加的相等关系是否已被已知的相等关系包含，如果已被包含，则取消添加
        for (Equal equal_1 : the_set) {
            if (the_equal.isContainedOf(equal_1)) {
                return;
            }
        }
        /*再遍历寻找是否有可以合并的相等关系，比如已知条件中已经有一个BC=CD，那么又要添加一个AB=BC，则这两个equal可以
        合并为一个equal，即AB=BC=CD*/
        HashSet<Equal> need_merged_equals = new HashSet<>();
        for (Equal equal_1 : the_set) {
            if (the_equal.getInnerSetOf(equal_1).size() >= 1) {
                need_merged_equals.add(equal_1);
            }
        }
        if (need_merged_equals.size() >= 1) {
            HashSet<Element> merged_equal_units_set = new HashSet<>(the_equal.getUnits_set());
            for (Equal equal_1 : need_merged_equals) {
                merged_equal_units_set.addAll(equal_1.getUnits_set());
            }
            Equal merged_equal = Equal.equal(merged_equal_units_set);
            the_set.removeAll(need_merged_equals);
            the_set.add(merged_equal);
            return;
        }
        // 如果上述情况都没有，说明当前已知条件集合中没有任何一个equal与要添加的equal有重叠，因此可直接添加进集合中
        the_set.add(the_equal);
    }

    /**
     * 查询该图对象中是否存在某个等量关系
     * 该方法一般用在查询一个等量关系是否已经被推导出来：
     * 例如查询边CD=边CM是否已经被证明，则写作this_graph.queryEqual("seg", segment("C", "D"), segment("C", "M"))
     * */
    public Boolean queryEqual(String equal_type, Element unit_1, Element unit_2) throws Exception {
        // 如果两个元素本来就是同一元素，则自然而然是相等的，无需再进行下一步
        if (unit_1.equals(unit_2)) {
            return true;
        }
        Equal the_equal = Equal.equal(unit_1, unit_2);
        HashSet<Equal> the_set;
        // 判断是查询边元素相等关系，还是查询角或角度元素相等关系
        switch (equal_type) {
            case "seg":
                the_set = this.segment_equals_set;
                break;
            case "ang":
                the_set = this.angle_equals_set;
                break;
            default:
                throw new IllegalStateException("不可以查询非边类型或角类型的相等关系！" + equal_type);
        }
        // 遍历寻找要查询的相等关系是否已被已知的相等关系包含，如果已被包含，则说明已被证明
        for (Equal equal_1 : the_set) {
            if (the_equal.isContainedOf(equal_1)) {
                return true;
            }
        }
        // 如果上述情况都没有，说明要查询的相等关系还没有被证明
        return false;
    }

    /**
     * 进行推导：
     * */
    public void deduceByAll(Integer deduce_no) {

    }

    /**
     * 推导方法一：依据平面空间的基础规则
     * 主要规则内容如下：
     * 1，若存在三个点A、B、C依次在一条直线上（共线），则有AB+BC=AC
     * 2，对于上面A、B、C共线的这条直线外一点D，有∠DAB=∠DAC，∠DCB=∠DCA，∠DBA+∠DBC=180°
     * 3，对于不存在三点共线的四个点A、B、C、D，若点D在∠ABC的扇区内，则有∠DBA+∠DBC=∠ABC
     * 该方法即为通过性能最佳的遍历方式来依据上述规则推导出所有可以被发现的等量关系
     * 该方法在图中所有点位置确定之后，仅调用一次即可推导出所有的结论，因此如果没有新增辅助点，该方法仅需调用一次
     * */
    public void deduceByBasicSpace() throws Exception {
        // 先计算确定哪些三点集合是共线的，哪些三点集合是不共线可以组成三角形的
        HashSet<HashSet<String>> all_three_point_combinations = this.getThreePointCombinations();
        this.triangles_set.addAll(all_three_point_combinations);
        for (HashSet<String> combination_1 : all_three_point_combinations) {
            String[] collinear_point_array = this.isCollinear(combination_1);
            if (collinear_point_array[0] != null) {
                this.triangles_set.remove(combination_1);
                this.collinear_set.add(collinear_point_array);
            }
        }
        // 遍历任意共线的三点组，运用规则1和规则2
        for (String[] collinear_array : this.collinear_set) {
            // 规则1
            this.addEqual("seg", Segment.segment(collinear_array[0], collinear_array[2]),
                    SumUnits.sumUnits(Segment.segment(collinear_array[0], collinear_array[1]),
                            Segment.segment(collinear_array[1], collinear_array[2])));
            // 规则2
            HashSet<String> out_points = new HashSet<>(this.points_set);
            Arrays.asList(collinear_array).forEach(out_points::remove);
            for (String out_point : out_points) {
                if (this.isCollinear(new HashSet<>(Arrays.asList(out_point,
                        collinear_array[0], collinear_array[1])))[0] == null) {
                    this.addEqual("ang", Degree.degree(180),
                            SumUnits.sumUnits(Angle.angle(out_point, collinear_array[1], collinear_array[0]),
                                              Angle.angle(out_point, collinear_array[1], collinear_array[2])));
                    this.addEqual("ang",
                            Angle.angle(out_point, collinear_array[0], collinear_array[1]),
                            Angle.angle(out_point, collinear_array[0], collinear_array[2]));
                    this.addEqual("ang",
                            Angle.angle(out_point, collinear_array[2], collinear_array[1]),
                            Angle.angle(out_point, collinear_array[2], collinear_array[0]));
                }
            }
        }
        //

    }

    /**工具：获取该图对象中所有的三点组合，例如该图中有4个点，A、B、C、D，则返回一个集合，包含ABC、ABD、ACD、BCD*/
    public HashSet<HashSet<String>> getThreePointCombinations() {
        HashSet<HashSet<String>> threePointCombinations = new HashSet<>();
        getNPointCombination(threePointCombinations,
                             new ArrayList<>(this.points_set),
                             new String[3],
                             0, this.points_set.size()-1, 0, 3);
        return threePointCombinations;
    }

    private static void getNPointCombination(HashSet<HashSet<String>> combinations_set,
                                             ArrayList<String> points_array,
                                             String[] one_combination,
                                             int start, int end, int has_n, int n) {
        if (has_n == n) {
            combinations_set.add(new HashSet<>(Arrays.asList(one_combination)));
            return;
        }
        for (int i=start; i<=end && end-i+1>=n-has_n; i++) {
            one_combination[has_n] = points_array.get(i);
            getNPointCombination(combinations_set, points_array, one_combination,
                                i+1, end, has_n+1, n);
        }
    }

    /**工具：获取计算指定的两个点在该图对象中的距离数值*/
    public Double getDistance(String point_1, String point_2) {
        double point_1_x = this.getPoint_location_x(point_1);
        double point_1_y = this.getPoint_location_y(point_1);
        double point_2_x = this.getPoint_location_x(point_2);
        double point_2_y = this.getPoint_location_y(point_2);
        return pow(pow(point_2_x - point_1_x, 2) + pow(point_2_y - point_1_y, 2), 0.5);
    }

    /**工具：获取计算指定的三个点所组成的角在该图对象中的角度数值*/
    public double getDegreeValue(String point_aside_1, String point_vertex, String point_aside_2) {
        double point_aside_1_x = this.getPoint_location_x(point_aside_1);
        double point_aside_1_y = this.getPoint_location_y(point_aside_1);
        double point_aside_2_x = this.getPoint_location_x(point_aside_2);
        double point_aside_2_y = this.getPoint_location_y(point_aside_2);
        double point_vertex_x = this.getPoint_location_x(point_vertex);
        double point_vertex_y = this.getPoint_location_y(point_vertex);
        double vec_aside_1_x = point_aside_1_x - point_vertex_x;
        double vec_aside_1_y = point_aside_1_y - point_vertex_y;
        double vec_aside_2_x = point_aside_2_x - point_vertex_x;
        double vec_aside_2_y = point_aside_2_y - point_vertex_y;
        double vec_aside_1_len = pow(pow(vec_aside_1_x, 2) + pow(vec_aside_1_y, 2), 0.5);
        double vec_aside_2_len = pow(pow(vec_aside_2_x, 2) + pow(vec_aside_2_y, 2), 0.5);
        double cos_degree = (vec_aside_1_x * vec_aside_2_x + vec_aside_1_y * vec_aside_2_y) /
                (vec_aside_1_len * vec_aside_2_len);
        return acos(cos_degree) / PI * 180;
    }

    /**工具：判断指定的三点集合在该图对象中是否共线，若共线，则依次返回三个点组成的数组，若不共线，则返回三个null组成的数组*/
    public String[] isCollinear(HashSet<String> one_combination) {
        // 遍历三个点，假设一个点作为中轴点，中轴点的意思为假设A、B、C依次在一条直线上，那么B即为中轴点
        for (String point_vertex : one_combination) {
            HashSet<String> points_aside = new HashSet<>(one_combination);
            points_aside.remove(point_vertex);
            String[] point_aside = points_aside.toArray(new String[2]);
            // 计算和判断两端的点之间距离是否等于两端的点到中轴点的距离之和，如果是，则说明共线，按顺序返回此三点数组
            double dis1 = getDistance(point_aside[0], point_aside[1]);
            double dis2 = getDistance(point_vertex, point_aside[0]) + getDistance(point_vertex, point_aside[1]);
            if (abs(dis1 - dis2) < 0.02) {
                return new String[]{point_aside[0], point_vertex, point_aside[1]};
            }
        }
        return new String[]{null, null, null};
    }

}
