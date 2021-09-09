package service.core;

import structure.elements.*;
import structure.relations.Equal;
import utils.Common;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.Math.*;

/**
 * 类：Graph 业务功能核心层：图对象
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
    private final HashSet<HashSet<String>> triangles_set;

    /**可以组成的三点共线的集合：这个图上的任意共线的三个点纳入该集合*/
    private final HashSet<String[]> collinear_set;

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
     * 打印展示该图对象的当前时刻信息，包括
     * 1.当前图对象的名称，所有点坐标信息
     * 2.当前图对象的所有共线三点组、不共线三点组（三角形）
     * 3.当前图对象的所有边等量关系式、所有角等量关系式
     * */
    public void displayGraphInfo() {
        System.out.print("图对象名称： " + this.graph_name + "\n");
        System.out.print("点个数： " + this.points_set.size() + "\n");
        for (String point : this.points_set) {
            System.out.print("    " + point + "  (" + this.getPoint_location_x(point) + ", "
                                                    + this.getPoint_location_y(point) + ")\n");
        }
        System.out.print("共线三点组个数:  " + this.collinear_set.size() + "\n");
        for (String[] three_point_combination : this.collinear_set) {
            System.out.print("    " + Arrays.toString(three_point_combination) + "\n");
        }
        System.out.print("不共线三点组个数:  " + this.triangles_set.size() + "\n");
        for (HashSet<String> three_point_combination : this.triangles_set) {
            System.out.print("    " + three_point_combination + "\n");
        }
        System.out.print("已发现的边相等关系式个数:  " + this.segment_equals_set.size() + "\n");
        for (Equal equal : this.segment_equals_set) {
            System.out.print("    " + equal.getGeometry_key() + "\n");
        }
        System.out.print("已发现的角相等关系式个数:  " + this.angle_equals_set.size() + "\n");
        for (Equal equal : this.angle_equals_set) {
            System.out.print("    " + equal.getGeometry_key() + "\n");
        }
    }

    /**
     * 进行推导：
     * 参数1，deduce_no，意义为指示本次推导是第几批次，如deduce_no=1，说明是该图对象进行第一次推导，如=2，说明是第二次推导
     * 参数2，max_complex_len，限制推导涉及的复合元素的最大长度，机器自动推导经常会耗费时间推导冗长表达式，诸如∠1+∠2+∠3+∠4+...=180°
     * */
    public void deduceByAll(Integer deduce_no, Integer max_complex_len) throws Exception {
        if (deduce_no == 1) {
            this.deduceByBasicSpace();
        }
        this.deduceByEquivalenceTransform("seg", max_complex_len);
        this.deduceByEquivalenceTransform("ang", max_complex_len);
        this.deduceByTheorem();
        this.deduceByCustomTheorem();
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
        // 先计算确定哪些三点组是共线的，哪些三点组是不共线可以组成三角形的
        HashSet<HashSet<String>> all_three_point_combinations = this.getThreePointCombinations();
        this.triangles_set.addAll(all_three_point_combinations);
        for (HashSet<String> combination_1 : all_three_point_combinations) {
            String[] collinear_point_array = this.isCollinear(combination_1);
            if (collinear_point_array != null) {
                this.triangles_set.remove(combination_1);
                this.collinear_set.add(collinear_point_array);
            }
        }
        // 遍历任意共线的三点组，运用规则1和规则2
        HashSet<String> out_points;
        for (String[] collinear_array : this.collinear_set) {
            // 规则1
            this.addEqual("seg", Segment.segment(collinear_array[0], collinear_array[2]),
                    SumUnits.sumUnits(Segment.segment(collinear_array[0], collinear_array[1]),
                            Segment.segment(collinear_array[1], collinear_array[2])));
            // 规则2
            out_points = new HashSet<>(this.points_set);
            Arrays.asList(collinear_array).forEach(out_points::remove);
            for (String out_point : out_points) {
                if (this.isCollinear(new HashSet<>(Arrays.asList(out_point,
                        collinear_array[0], collinear_array[1]))) == null) {
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
        // 遍历任意不共线的三点组，运用规则3
        String[] points_aside;
        for (HashSet<String> combination_1 : this.triangles_set) {
            out_points = Common.getSetExcept(this.points_set, combination_1);
            for (String point_vertex : combination_1) {
                points_aside = Common.getSetExcept(combination_1, point_vertex).toArray(new String[2]);
                for (String out_point : out_points) {
                    double degree_value_1 = this.getDegreeValue(out_point, point_vertex, points_aside[0]);
                    double degree_value_2 = this.getDegreeValue(out_point, point_vertex, points_aside[1]);
                    double degree_sum = this.getDegreeValue(points_aside[0], point_vertex, points_aside[1]);
                    if (degree_value_1 > 5 && degree_value_2 > 5 &&
                            abs(degree_sum - degree_value_1 - degree_value_2) < 2){
                        this.addEqual("ang", Angle.angle(points_aside[0], point_vertex, points_aside[1]),
                                SumUnits.sumUnits(Angle.angle(out_point, point_vertex, points_aside[0]),
                                                  Angle.angle(out_point, point_vertex, points_aside[1])));
                    }
                }
            }
        }
    }

    /**
     * 推导方法二：依据数学等式的等价转化规则
     * 主要规则内容如下：
     * 1，等价替换：假设有边AB+BM=MN，BM=BC，则可以得出AB+BM=AB+BC=MN
     * 2，等价抵消：假设有边AB+BM=AB+BC，则可以得出BM=BC
     * 3，乘法分配律：假设有(AE+EB)*AC=AB*AC，则可以得出AE*AC+EB*AC=AB*AC
     * 4，乘法结合律：假设有AE*AC+EB*AC=AB*AC，则可以得出(AE+EB)*AC=AB*AC
     * 该方法属于通过基本的代数等式运算发掘新的等量关系，虽然它们看似非常简单，属于常识性的东西，但计算机是不知道的，需要遍历寻找并挖掘
     * 备注：该方法是整个程序中最耗时、也最容易遗漏情况的环节，目前虽然尽可能考虑到了所有情况，但仍可能存在遍历未覆盖到的情况
     * 目前采取的思路是，分为两种嵌套的情况，即加法套乘法和乘法套加法，遍历寻找这两种嵌套形式下满足上述规则的情况，即可递归得出尽可能多的等价式
     * */
    public void deduceByEquivalenceTransform(String equal_type, Integer max_complex_len) throws Exception {
        // 判断是等价发掘边类型的等量关系，还是角类型的
        HashSet<Equal> the_equals_set;
        switch (equal_type) {
            case "seg":
                the_equals_set = new HashSet<>(this.segment_equals_set);
                break;
            case "ang":
                the_equals_set = new HashSet<>(this.angle_equals_set);
                break;
            default:
                throw new IllegalStateException("输入的等量关系类型既不是边类型也不是角类型！" + equal_type);
        }
        // 推导过程中最复杂耗时的模块，最难优化的地方：遍历所有已知的等量关系内的所有元素，寻找满足上述4条等价转化规则的情况
        // 遍历所有的等式中的所有复合元素
        for (Equal equal_1 : the_equals_set) {
            for (Element complex_unit_1 : equal_1.getUnits_set()) {
                if (!complex_unit_1.isComplex()) continue;
                // 寻找满足规则1，可以进行等价替换的情况
                // 比如有一个AB*MN+CD*EF，循环选取它的下属元素，比如第一个选取AB*MN
                for (Element complex_unit_1_sub_1 : ((ComplexElement) complex_unit_1).getSubUnits()) {
                    // 遍历所有的等式，寻找哪个等式里面含有AB*MN
                    for (Equal equal_2 : the_equals_set) {
                        if (equal_2.getUnits_set().contains(complex_unit_1_sub_1)) {
                            // 比如找到了一个等式为AB*MN=BC*PQ，那么这里面的BC*PQ可以替换掉上面AB*MN+CD*EF中的AB*MN，组成新的加和元素
                            // 然后将新的加和元素和旧的加和元素的相等关系，即AB*MN+CD*EF=BC*PQ+CD*EF添加进图对象中
                            for (Element equal_2_unit_1 : equal_2.getExceptSetOf(complex_unit_1_sub_1)) {
                                ComplexElement new_complex_unit;
                                if (complex_unit_1 instanceof SumUnits) {
                                    new_complex_unit = SumUnits.sumUnits(equal_2_unit_1,
                                            ((SumUnits) complex_unit_1).getExceptOf(complex_unit_1_sub_1));
                                } else {
                                    new_complex_unit = MultiplyUnits.multiplyUnits(equal_2_unit_1,
                                            ((ComplexElement) complex_unit_1).getExceptOf(complex_unit_1_sub_1));
                                }
                                // 判断新复合元素的长度有无超过限制参数的要求
                                if (new_complex_unit.getLength() > max_complex_len) continue;
                                this.addEqual(equal_type, complex_unit_1, new_complex_unit);
                            }
                        }
                    }
                    // 还可以寻找哪个等式里含有AB*MN的下属元素AB或MN，也就是AB*MN+CD*EF的下下属元素
                    if (!complex_unit_1_sub_1.isComplex()) continue;
                    for (Element complex_unit_1_sub_1_sub_1 : ((ComplexElement) complex_unit_1_sub_1).getSubUnits()) {
                        // 遍历所有的等式，寻找哪个等式里面含有AB或MN，后续过程与前面相同
                        for (Equal equal_2 : the_equals_set) {
                            if (equal_2.getUnits_set().contains(complex_unit_1_sub_1_sub_1)) {
                                for (Element equal_2_unit_1 : equal_2.getExceptSetOf(complex_unit_1_sub_1_sub_1)) {
                                    ComplexElement new_complex_unit;
                                    Element excepted_unit = ((ComplexElement) complex_unit_1_sub_1).getExceptOf(
                                                                                        complex_unit_1_sub_1_sub_1);
                                    if (complex_unit_1 instanceof SumUnits) {
                                        new_complex_unit = SumUnits.sumUnits(
                                                ((SumUnits) complex_unit_1).getExceptOf(complex_unit_1_sub_1),
                                                MultiplyUnits.multiplyUnits(equal_2_unit_1, excepted_unit)
                                        );
                                    } else {
                                        new_complex_unit = MultiplyUnits.multiplyUnits(
                                                ((ComplexElement) complex_unit_1).getExceptOf(complex_unit_1_sub_1),
                                                SumUnits.sumUnits(equal_2_unit_1, excepted_unit)
                                        );
                                    }
                                    if (new_complex_unit.getLength() > max_complex_len) continue;
                                    this.addEqual(equal_type, complex_unit_1, new_complex_unit);
                                }
                            }
                        }
                    }
                }
                // 寻找满足规则2，可以进行等价抵消的情况
                // 遍历当前这个复合元素所在的等式，寻找另一个相同类型的复合元素，如果两个之间有重合部分则可适用等价抵消规则
                for (Element complex_unit_2 : equal_1.getExceptSetOf(complex_unit_1)) {
                    if (!complex_unit_2.isSameClassOf(complex_unit_1)) continue;
                    Element inner_unit = ((ComplexElement) complex_unit_1).getInnerOf((ComplexElement) complex_unit_2);
                    if (inner_unit != null && !complex_unit_1.equals(complex_unit_2)) {
                        this.addEqual(equal_type, ((ComplexElement) complex_unit_1).getExceptOf(inner_unit),
                                                  ((ComplexElement) complex_unit_2).getExceptOf(inner_unit));
                    }
                }
                // 规则3和规则4一般情况只有边等量关系会出现，角或角度一般不会出现乘积
                if (equal_type.equals("ang")) continue;
                // 寻找满足规则3，可以进行乘法分配律的情况
                // 寻找类似AC*(AE+EB)或AC*(AE+EB)+...这样的复合元素结构
                if (complex_unit_1 instanceof MultiplyUnits) {
                    for (Element complex_unit_1_sub_1 : ((MultiplyUnits) complex_unit_1).getSubUnits()) {
                        if (complex_unit_1_sub_1 instanceof SumUnits) {
                            Element complex_unit_1_sub_2 = ((MultiplyUnits) complex_unit_1).getExceptOf(
                                                                                        complex_unit_1_sub_1);
                            HashMap<Element, Integer> temp = ((SumUnits) complex_unit_1_sub_1).getUnit_counts();
                            HashMap<Element, Integer> result_unit_counts = new HashMap<>();
                            for (Entry<Element, Integer> temp_entry : temp.entrySet()) {
                                result_unit_counts.put(MultiplyUnits.multiplyUnits(complex_unit_1_sub_2,
                                                                    temp_entry.getKey()), temp_entry.getValue());
                            }
                            SumUnits new_complex_unit = SumUnits.sumUnits(result_unit_counts);
                            this.addEqual(equal_type, complex_unit_1, new_complex_unit);
                        }
                    }
                } else if (complex_unit_1 instanceof SumUnits) {
                    // 如果是AC*(AE+EB)+...这样的结构，需要把符合规则3的乘积的那部分单独拎出来
                    for (Element complex_unit_1_sub : ((SumUnits) complex_unit_1).getSubUnits()) {
                        if (!(complex_unit_1_sub instanceof MultiplyUnits)) continue;
                        for (Element complex_unit_1_sub_sub_1 : ((MultiplyUnits) complex_unit_1_sub).getSubUnits()) {
                            if (complex_unit_1_sub_sub_1 instanceof SumUnits) {
                                Element complex_unit_1_sub_sub_2 = ((MultiplyUnits) complex_unit_1_sub).getExceptOf(
                                                                                            complex_unit_1_sub_sub_1);
                                HashMap<Element, Integer> temp = ((SumUnits) complex_unit_1_sub_sub_1).getUnit_counts();
                                HashMap<Element, Integer> result_unit_counts = new HashMap<>();
                                for (Entry<Element, Integer> temp_entry : temp.entrySet()) {
                                    result_unit_counts.put(MultiplyUnits.multiplyUnits(complex_unit_1_sub_sub_2,
                                            temp_entry.getKey()), temp_entry.getValue());
                                }
                                SumUnits new_complex_unit = SumUnits.sumUnits(result_unit_counts);
                                Element excepted_unit = ((SumUnits) complex_unit_1).getExceptOf(complex_unit_1_sub);
                                this.addEqual(equal_type, complex_unit_1_sub, new_complex_unit);
                                this.addEqual(equal_type, complex_unit_1, SumUnits.sumUnits(excepted_unit,
                                                                                            new_complex_unit));
                            }
                        }
                    }
                }
                // 寻找满足规则4，可以进行乘法结合律的情况
                // 寻找AE*AC+EB*AC+...这样的复合元素结构
                if (complex_unit_1 instanceof SumUnits) {
                    for (Element complex_unit_1_sub_1 : ((SumUnits) complex_unit_1).getSubUnits()) {
                        // 遍历这个复合元素的下属元素，寻找有无乘积元素
                        if (!(complex_unit_1_sub_1 instanceof MultiplyUnits)) continue;
                        // 比如找到了这个复合元素其中有一项为一个乘积元素AE*AC，那么求剩下的部分
                        Element complex_unit_1_sub_2 = ((SumUnits) complex_unit_1).getExceptOf(complex_unit_1_sub_1);
                        // 检测剩下的部分的结构，是否同样含有AE或AC可以满足乘法结合律的情况
                        if (complex_unit_1_sub_2 instanceof SumUnits) {
                            for (Element complex_unit_1_sub_2_sub : ((SumUnits) complex_unit_1_sub_2).getSubUnits()) {
                                if (complex_unit_1_sub_2_sub instanceof MultiplyUnits
                                        && !complex_unit_1_sub_2_sub.equals(complex_unit_1_sub_1)) {
                                    Element inner_unit = ((MultiplyUnits) complex_unit_1_sub_1).getInnerOf(
                                            (MultiplyUnits) complex_unit_1_sub_2_sub);
                                    if (inner_unit == null) continue;
                                    Element excepted_unit = ((SumUnits) complex_unit_1_sub_2).getExceptOf(
                                            complex_unit_1_sub_2_sub);
                                    Element new_complex_unit = SumUnits.sumUnits(excepted_unit,
                                            MultiplyUnits.multiplyUnits(inner_unit, SumUnits.sumUnits(
                                                    ((MultiplyUnits) complex_unit_1_sub_1).getExceptOf(inner_unit),
                                                    ((MultiplyUnits) complex_unit_1_sub_2_sub).getExceptOf(inner_unit)
                                            )));
                                    this.addEqual(equal_type, complex_unit_1, new_complex_unit);
                                }
                            }
                        } else if (complex_unit_1_sub_2 instanceof MultiplyUnits) {
                            Element inner_unit = ((MultiplyUnits) complex_unit_1_sub_1).getInnerOf(
                                    (MultiplyUnits) complex_unit_1_sub_2);
                            if (complex_unit_1_sub_1.equals(complex_unit_1_sub_2) || inner_unit == null) continue;
                            Element new_complex_unit = MultiplyUnits.multiplyUnits(inner_unit,
                                    SumUnits.sumUnits(((MultiplyUnits) complex_unit_1_sub_1).getExceptOf(inner_unit),
                                            ((MultiplyUnits) complex_unit_1_sub_2).getExceptOf(inner_unit)));
                            this.addEqual(equal_type, complex_unit_1, new_complex_unit);
                        }
                    }
                }
            }
        }
    }

    /**
     * 推导方法三：依据经典欧式几何中的定理
     * 主要规则内容如下：
     * 1，三角形内角和定理：内角和为180°
     * 2，等腰三角形定理：三角形内部，等角所对为等边，等边所对为等角
     * 3，全等三角形定理：SSS、SAS、AAS、ASA
     * 4，相似三角形定理：SS、SA、AA
     * 5，共圆四边形定理（四点共圆）：共圆四边形内对角互补，圆周角相等，满足其一，则其他亦成立
     * 该方法即为利用经典的欧式几何定理来发掘新的等量关系，致力于以高性能、不重不漏的方式遍历每组点集合，排查任何可能适用上述定理规则的情况
     * */
    public void deduceByTheorem() throws Exception {
        String[] triangle_1_array;
        HashSet<HashSet<String>> rest_triangles_set = new HashSet<>(this.triangles_set);
        HashSet<String> out_points_set;
        String[] points_aside;
        // 以三角形点组为遍历单元，进行遍历
        for (HashSet<String> triangle_1 : this.triangles_set) {
            // 对每个三角形应用规则1
            triangle_1_array = triangle_1.toArray(new String[3]);
            this.addEqual("ang",
                    SumUnits.sumUnits(
                            Angle.angle(triangle_1_array[1], triangle_1_array[0], triangle_1_array[2]),
                            SumUnits.sumUnits(
                                    Angle.angle(triangle_1_array[0], triangle_1_array[1], triangle_1_array[2]),
                                    Angle.angle(triangle_1_array[0], triangle_1_array[2], triangle_1_array[1]))
                    ), Degree.degree(180));
            // 检查每个三角形，是否满足规则2，为等腰三角形
            String[] isosceles_result = this.isIsosceles(triangle_1);
            if (isosceles_result != null) {
                this.addEqual("seg",
                        Segment.segment(isosceles_result[0], isosceles_result[1]),
                        Segment.segment(isosceles_result[0], isosceles_result[2]));
                this.addEqual("ang",
                        Angle.angle(isosceles_result[0], isosceles_result[1], isosceles_result[2]),
                        Angle.angle(isosceles_result[0], isosceles_result[2], isosceles_result[1]));
            }
            // 再遍历另一个三角形，组成三角形对
            rest_triangles_set.remove(triangle_1);
            for (HashSet<String> triangle_2 : rest_triangles_set) {
                // 判断这对三角形是否满足规则3，全等三角形定理
                String[][] congruent_result = this.isCongruent(triangle_1, triangle_2);
                if (congruent_result != null) {
                    this.addEqual("seg",
                            Segment.segment(congruent_result[0][0], congruent_result[0][1]),
                            Segment.segment(congruent_result[1][0], congruent_result[1][1]));
                    this.addEqual("seg",
                            Segment.segment(congruent_result[0][0], congruent_result[0][2]),
                            Segment.segment(congruent_result[1][0], congruent_result[1][2]));
                    this.addEqual("seg",
                            Segment.segment(congruent_result[0][1], congruent_result[0][2]),
                            Segment.segment(congruent_result[1][1], congruent_result[1][2]));
                    this.addEqual("ang",
                            Angle.angle(congruent_result[0][1], congruent_result[0][0], congruent_result[0][2]),
                            Angle.angle(congruent_result[1][1], congruent_result[1][0], congruent_result[1][2]));
                    this.addEqual("ang",
                            Angle.angle(congruent_result[0][0], congruent_result[0][1], congruent_result[0][2]),
                            Angle.angle(congruent_result[1][0], congruent_result[1][1], congruent_result[1][2]));
                    this.addEqual("ang",
                            Angle.angle(congruent_result[0][0], congruent_result[0][2], congruent_result[0][1]),
                            Angle.angle(congruent_result[1][0], congruent_result[1][2], congruent_result[1][1]));
                }
                // 判断这对三角形是否满足规则4，相似三角形定理
                String[][] similar_result = this.isSimilar(triangle_1, triangle_2);
                if (similar_result != null) {
                    this.addEqual("seg",
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(similar_result[0][0], similar_result[0][1]),
                                    Segment.segment(similar_result[1][1], similar_result[1][2])),
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(similar_result[0][1], similar_result[0][2]),
                                    Segment.segment(similar_result[1][0], similar_result[1][1])));
                    this.addEqual("seg",
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(similar_result[0][0], similar_result[0][1]),
                                    Segment.segment(similar_result[1][0], similar_result[1][2])),
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(similar_result[0][0], similar_result[0][2]),
                                    Segment.segment(similar_result[1][0], similar_result[1][1])));
                    this.addEqual("seg",
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(similar_result[0][0], similar_result[0][2]),
                                    Segment.segment(similar_result[1][1], similar_result[1][2])),
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(similar_result[0][1], similar_result[0][2]),
                                    Segment.segment(similar_result[1][0], similar_result[1][2])));
                    this.addEqual("ang",
                            Angle.angle(similar_result[0][1], similar_result[0][0], similar_result[0][2]),
                            Angle.angle(similar_result[1][1], similar_result[1][0], similar_result[1][2]));
                    this.addEqual("ang",
                            Angle.angle(similar_result[0][0], similar_result[0][1], similar_result[0][2]),
                            Angle.angle(similar_result[1][0], similar_result[1][1], similar_result[1][2]));
                    this.addEqual("ang",
                            Angle.angle(similar_result[0][0], similar_result[0][2], similar_result[0][1]),
                            Angle.angle(similar_result[1][0], similar_result[1][2], similar_result[1][1]));
                }
            }
            // 再遍历这个三角形之外的点，检查是否可以满足规则5，组成四点共圆
            out_points_set = Common.getSetExcept(this.points_set, triangle_1);
            for (String out_point : out_points_set) {
                for (String point_vertex : triangle_1) {
                    points_aside = Common.getSetExcept(triangle_1, point_vertex).toArray(new String[2]);
                    // 假设一个点为三角形顶点，检查是否有一个外点在三角形顶角扇区内并与之组成四点共圆，即满足一对对角互补或一对圆周角相等
                    if (this.queryEqual("ang",
                            Angle.angle(points_aside[0], point_vertex, points_aside[1]),
                            SumUnits.sumUnits(
                                    Angle.angle(out_point, point_vertex, points_aside[0]),
                                    Angle.angle(out_point, point_vertex, points_aside[1])))
                    ) {
                        if (this.queryEqual("ang", Degree.degree(180),
                                SumUnits.sumUnits(
                                        Angle.angle(points_aside[0], point_vertex, points_aside[1]),
                                        Angle.angle(points_aside[0], out_point, points_aside[1]))) ||
                                this.queryEqual("ang",
                                        Angle.angle(out_point, point_vertex, points_aside[0]),
                                        Angle.angle(out_point, points_aside[1], points_aside[0])) ||
                                this.queryEqual("ang",
                                        Angle.angle(out_point, point_vertex, points_aside[1]),
                                        Angle.angle(out_point, points_aside[0], points_aside[1]))
                        ) {
                            this.addEqual("ang",
                                    Angle.angle(out_point, point_vertex, points_aside[0]),
                                    Angle.angle(out_point, points_aside[1], points_aside[0]));
                            this.addEqual("ang",
                                    Angle.angle(out_point, point_vertex, points_aside[1]),
                                    Angle.angle(out_point, points_aside[0], points_aside[1]));
                            this.addEqual("ang",
                                    Angle.angle(point_vertex, out_point, points_aside[0]),
                                    Angle.angle(point_vertex, points_aside[1], points_aside[0]));
                            this.addEqual("ang",
                                    Angle.angle(point_vertex, out_point, points_aside[1]),
                                    Angle.angle(point_vertex, points_aside[0], points_aside[1]));
                        }
                    }
                }
            }

        }
    }

    /**
     * 推导方法四：依据自定义补充定理
     * 主要规则内容如下：
     * 1，待补充
     * 2，待补充
     * 3，待补充
     * 上述三个推导方法所使用的规则为常见的几何代数基本公理以及经典定理，在此之外的补充定理可以通过重写该方法进行添加
     * 添加补充定理可以加快某些特定几何问题的推导速度，如内心定理、重心定理等，这些定理虽然可以通过基本定理去证明，但仍至关重要
     * */
    public void deduceByCustomTheorem() {}

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
        String[] points_aside;
        double dis1, dis2;
        // 遍历三个点，假设一个点作为中轴点，中轴点的意思为假设A、B、C依次在一条直线上，那么B即为中轴点
        for (String point_vertex : one_combination) {
            points_aside = Common.getSetExcept(one_combination, point_vertex).toArray(new String[2]);
            // 计算和判断两端的点之间距离是否等于两端的点到中轴点的距离之和，如果是，则说明共线，按顺序返回此三点数组
            dis1 = getDistance(points_aside[0], points_aside[1]);
            dis2 = getDistance(point_vertex, points_aside[0]) + getDistance(point_vertex, points_aside[1]);
            if (abs(dis1 - dis2) < 0.02) {
                return new String[]{points_aside[0], point_vertex, points_aside[1]};
            }
        }
        return null;
    }

    /**工具：判断指定的三角形（三点集合）在该图对象中是否为等腰三角形，若是，则依次返回一个三点数组，第一个点为顶点，若否，返回null*/
    public String[] isIsosceles(HashSet<String> one_triangle) throws Exception {
        String[] points_aside;
        // 遍历三个点，假设一个点为顶点
        for (String point_vertex : one_triangle) {
            points_aside = Common.getSetExcept(one_triangle, point_vertex).toArray(new String[2]);
            // 判断顶点到两个侧点的距离相等是否已知，或两个侧角相等是否已知
            if (this.queryEqual("seg", Segment.segment(points_aside[0], point_vertex),
                                                 Segment.segment(points_aside[1], point_vertex)) ||
                this.queryEqual("ang", Angle.angle(point_vertex, points_aside[0], points_aside[1]),
                                                 Angle.angle(point_vertex, points_aside[1], points_aside[0]))
            ) {
                return new String[]{point_vertex, points_aside[0], points_aside[1]};
            }
        }
        return null;
    }

    /**工具：判断指定的一对三角形（三点集合）在该图对象中是否全等，若是，则返回一个2×3数组，即两个三角形的各自三个对应点，若否，返回null*/
    public String[][] isCongruent(HashSet<String> triangle_1, HashSet<String> triangle_2) throws Exception {
        String[] points_aside_1;
        String[] points_aside_2;
        // 两个三角形各自遍历三个点，并各自假设一个点为顶点
        for (String point_vertex_1 : triangle_1) {
            points_aside_1 = Common.getSetExcept(triangle_1, point_vertex_1).toArray(new String[2]);
            for (String point_vertex_2 : triangle_2) {
                points_aside_2 = Common.getSetExcept(triangle_2, point_vertex_2).toArray(new String[2]);
                // 如果满足 A 或 S 顶角或顶角所对边相等
                if (this.queryEqual("ang", Angle.angle(points_aside_1[0], point_vertex_1, points_aside_1[1]),
                                                     Angle.angle(points_aside_2[0], point_vertex_2, points_aside_2[1]))
                ) {
                    // 假设另外两个点是正好对应，或者相反对应
                    for (int i : new int[]{0, 1}) {
                        String temp = points_aside_1[1-i];
                        points_aside_1[0] = points_aside_1[i];
                        points_aside_1[1] = temp;
                        // 如果满足 SAS 两侧边和一顶角对应相等
                        if (this.queryEqual("seg", Segment.segment(points_aside_1[0], point_vertex_1),
                                                             Segment.segment(points_aside_2[0], point_vertex_2)) &&
                            this.queryEqual("seg", Segment.segment(points_aside_1[1], point_vertex_1),
                                                             Segment.segment(points_aside_2[1], point_vertex_2))
                        ) {
                            return new String[][]{{points_aside_1[0], point_vertex_1, points_aside_1[1]},
                                                  {points_aside_2[0], point_vertex_2, points_aside_2[1]}};
                        }
                    }
                } else if (this.queryEqual("seg", Segment.segment(points_aside_1[0], points_aside_1[1]),
                                                            Segment.segment(points_aside_2[0], points_aside_2[1]))
                ) {
                    // 假设另外两个点是正好对应，或者相反对应
                    for (int i : new int[]{0, 1}) {
                        String temp = points_aside_1[1-i];
                        points_aside_1[0] = points_aside_1[i];
                        points_aside_1[1] = temp;
                        // 如果满足 SSS 三边对应相等
                        if (this.queryEqual("seg", Segment.segment(points_aside_1[0], point_vertex_1),
                                                             Segment.segment(points_aside_2[0], point_vertex_2)) &&
                            this.queryEqual("seg", Segment.segment(points_aside_1[1], point_vertex_1),
                                                             Segment.segment(points_aside_2[1], point_vertex_2))
                        ) {
                            return new String[][]{{points_aside_1[0], point_vertex_1, points_aside_1[1]},
                                                  {points_aside_2[0], point_vertex_2, points_aside_2[1]}};
                        }
                        // 如果满足 SAA 一边两角对应相等
                        if (this.queryEqual("ang",
                                    Angle.angle(point_vertex_1, points_aside_1[0], points_aside_1[1]),
                                    Angle.angle(point_vertex_2, points_aside_2[0], points_aside_2[1])) &&
                            this.queryEqual("ang",
                                    Angle.angle(point_vertex_1, points_aside_1[1], points_aside_1[0]),
                                    Angle.angle(point_vertex_2, points_aside_2[1], points_aside_2[0]))
                        ) {
                            return new String[][]{{points_aside_1[0], point_vertex_1, points_aside_1[1]},
                                                  {points_aside_2[0], point_vertex_2, points_aside_2[1]}};
                        }
                    }
                }
            }
        }
        return null;
    }

    /**工具：判断指定的一对三角形（三点集合）在该图对象中是否相似，若是，则返回一个2×3数组，即两个三角形的各自三个对应点，若否，返回null*/
    public String[][] isSimilar(HashSet<String> triangle_1, HashSet<String> triangle_2) throws Exception {
        String[] points_aside_1;
        String[] points_aside_2;
        // 两个三角形各自遍历三个点，并各自假设一个点为顶点
        for (String point_vertex_1 : triangle_1) {
            points_aside_1 = Common.getSetExcept(triangle_1, point_vertex_1).toArray(new String[2]);
            for (String point_vertex_2 : triangle_2) {
                points_aside_2 = Common.getSetExcept(triangle_2, point_vertex_2).toArray(new String[2]);
                // 假设另外两个点是正好对应，或者相反对应
                for (int i : new int[]{0, 1}) {
                    String temp = points_aside_1[1-i];
                    points_aside_1[0] = points_aside_1[i];
                    points_aside_1[1] = temp;
                    // 如果满足 A 顶角对应相等
                    if (this.queryEqual("ang",
                            Angle.angle(points_aside_1[0], point_vertex_1, points_aside_1[1]),
                            Angle.angle(points_aside_2[0], point_vertex_2, points_aside_2[1]))
                    ) {
                        // 如果满足 AA 顶角对应相等 且 一侧角对应相等
                        if (this.queryEqual("ang",
                                Angle.angle(point_vertex_1, points_aside_1[0], points_aside_1[1]),
                                Angle.angle(point_vertex_2, points_aside_2[0], points_aside_2[1]))
                        ) {
                            return new String[][]{{points_aside_1[0], point_vertex_1, points_aside_1[1]},
                                                  {points_aside_2[0], point_vertex_2, points_aside_2[1]}};
                        }
                        // 如果满足 AS 顶角对应相等 且 两侧边对应比例相等
                        if (this.queryEqual("seg",
                                MultiplyUnits.multiplyUnits(
                                        Segment.segment(points_aside_1[0], point_vertex_1),
                                        Segment.segment(points_aside_2[1], point_vertex_2)
                                ),
                                MultiplyUnits.multiplyUnits(
                                        Segment.segment(points_aside_1[1], point_vertex_1),
                                        Segment.segment(points_aside_2[0], point_vertex_2)
                                ))
                        ) {
                            return new String[][]{{points_aside_1[0], point_vertex_1, points_aside_1[1]},
                                                  {points_aside_2[0], point_vertex_2, points_aside_2[1]}};
                        }
                    }
                    // 如果满足 SSS 三边对应比例相等
                    else if (this.queryEqual("seg",
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(points_aside_1[0], point_vertex_1),
                                    Segment.segment(points_aside_2[1], point_vertex_2)
                            ),
                            MultiplyUnits.multiplyUnits(
                                    Segment.segment(points_aside_1[1], point_vertex_1),
                                    Segment.segment(points_aside_2[0], point_vertex_2)
                            )) &&
                            this.queryEqual("seg",
                                    MultiplyUnits.multiplyUnits(
                                            Segment.segment(points_aside_1[0], point_vertex_1),
                                            Segment.segment(points_aside_2[0], points_aside_2[1])
                                    ),
                                    MultiplyUnits.multiplyUnits(
                                            Segment.segment(points_aside_2[0], point_vertex_2),
                                            Segment.segment(points_aside_1[0], points_aside_1[1])
                                    ))
                    ) {
                        return new String[][]{{points_aside_1[0], point_vertex_1, points_aside_1[1]},
                                              {points_aside_2[0], point_vertex_2, points_aside_2[1]}};
                    }
                }
            }
        }
        return null;
    }

}
