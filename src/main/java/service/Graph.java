package service;

import structure.elements.Element;
import structure.relations.Equal;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 类：Graph 业务主功能层：图对象
 * 一个欧式几何问题，可以看作是一个图对象，这个图对象由structure结构层中的elements元素和relations等量关系所组成
 * 解决一个几何问题的过程，可以看作是利用这个图对象实例中的初始等量关系推导出更多等量关系的过程
 * */
public class Graph {

    /**
     * 定义一个图对象初始化时所拥有的变量
     * 图对象名称：这个图对象的名字，一般以几何题目的编号命名
     * 点集合：这个图所包含的所有点，点以A-Z大写字母表示
     * 点横坐标数值表：记录每个点的横坐标的HashMap
     * 点纵坐标数值表：记录每个点的纵坐标的HashMap
     * 边相等关系集合：这个图所已知的所有边的相等关系的集合，用包含边元素的equal的集合来表示
     * 角或角度相等关系集合：这个图所已知的所有角或角度的相等关系的集合，用包含角或角度元素的equal的集合来表示
     * */
    private final String graph_name;
    private final HashSet<String> points_set;
    private final HashMap<String, Float> points_location_x;
    private final HashMap<String, Float> points_location_y;
    private final HashSet<Equal> segment_equals_set;
    private final HashSet<Equal> angle_equals_set;

    /**定义图对象的构造函数*/
    public Graph(String graph_name) {
        this.graph_name = graph_name;
        this.points_set = new HashSet<>();
        this.points_location_x = new HashMap<>();
        this.points_location_y = new HashMap<>();
        this.segment_equals_set = new HashSet<>();
        this.angle_equals_set = new HashSet<>();
    }

    /**
     * 在图对象中添加一个点，例如添加点A，横坐标1.5，纵坐标2.5，则写作this_graph.addPoint("A", 1.5, 2.5)
     * 该方法一般在初始化一个几何问题图对象时给定初始点的时候调用，或者在后期添加辅助点的时候调用
     * */
    public void addPoint(String point, Float location_x, Float location_y) {
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
    public Float getPoint_location_x(String point) {
        return this.points_location_x.get(point);
    }

    /**获取该图对象的某个点的纵坐标*/
    public Float getPoint_location_y(String point) {
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
     * 1，若存在三个点A、B、C依次在一条直线上（共线），则有AB+BC=AC，
     * 2，
     * 3，
     * */
    public void deduceByBasicSpace() {

    }

}
