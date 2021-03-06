package com.bigfatcat.geometry3.celltest;

import com.bigfatcat.geometry3.core.Graph;
import com.bigfatcat.geometry3.core.structure.elements.Element;

import java.util.Arrays;
import java.util.HashSet;

import static com.bigfatcat.geometry3.core.structure.elements.Segment.segment;
import static com.bigfatcat.geometry3.core.structure.elements.SumUnits.sumUnits;
import static com.bigfatcat.geometry3.core.structure.elements.MultiplyUnits.multiplyUnits;

/**
 * 业务功能核心层测试篇
 * */
public class CoreTest {

    public static void main(String[] args) throws Exception {

        /*测试用例1，测试Graph图类基本功能，点、等量关系添加与查询功能执行正确*/
        Graph t1 = new Graph("中国联赛第一题");
        t1.addPoint("A", 2.7, 3.5);
        t1.addEqual("seg", segment("A", "B"), segment("B", "C"));
        System.out.print(t1 + "\n");
        System.out.print(t1.hashCode() + "\n");
        System.out.print(t1.getGraph_name() + "\n");
        System.out.print(t1.getPoints_set() + "\n");
        System.out.print(t1.getPoint_location_x("A") + "\n");
        System.out.print(t1.getPoint_location_y(t1.getPoints_set().iterator().next()) + "\n");
        System.out.print(t1.queryEqual("seg", segment("B", "A"), segment("C", "B")) + "\n");
        System.out.print(t1.queryEqual("seg", segment("C", "B"), segment("C", "D")) + "\n");

        t1.addEqual("seg", segment("D", "C"), segment("B", "C"));
        System.out.print(t1.queryEqual("seg", segment("C", "B"), segment("C", "D")) + "\n");
        System.out.print(t1.queryEqual("seg", segment("B", "A"), segment("C", "D")) + "\n");
        System.out.print(t1.queryEqual("seg", segment("D", "C"), segment("B", "A")) + "\n");

        t1.addEqual("seg", segment("D", "C"), segment("M", "N"));
        System.out.print(t1.queryEqual("seg", segment("N", "M"), segment("B", "A")) + "\n");
        System.out.print("\n");
//        com.bigfatcat.geometry3.core.Graph@7f31245a
//        2133927002
//        中国联赛第一题
//        [A]
//        2.7
//        3.5
//        true
//        false
//        4*true

        /*测试用例2，测试Graph图类通用计算功能模块，如计算点距离、判断三角形全等相似等功能正确*/
        t1.addPoint("B", 1.7, 4.5);
        t1.addPoint("C", -1.7, -4.5);
        t1.addPoint("D", -3.7, -4.5);
        t1.addPoint("E", 10.7, -4.5);
        System.out.print(t1.getThreePointCombinations() + "\n");
        System.out.print(t1.getDistance("C", "D") + "\n");
        System.out.print(Arrays.toString(t1.isCollinear(new HashSet<>(Arrays.asList("C", "D", "E")))) + "\n");
        System.out.print(Arrays.toString(t1.isCollinear(new HashSet<>(Arrays.asList("B", "D", "E")))) + "\n");
        System.out.print("\n");
//        10个三点集合
//        2.0
//        [D, C, E]
//        null
        
        /*测试用例3，测试Graph图类推导功能模块，通过已知等量关系发现更多等量关系的功能正确*/
        // 测试基本空间推导功能
        Graph t2 = new Graph("中国联赛第二题");
        t2.addPoint("A", 0.0, 0.0);
        t2.addPoint("B", 3.5, -1.5);
        t2.addPoint("C", 3.5, 2.5);
        t2.addPoint("D", 3.5, 0.0);
        t2.deduceByBasicSpace();
        t2.displayGraphInfo();
        System.out.print("\n");
        // 测试等价转化推导功能, AB*(CD+MN)+PQ*(CD+MN)+BQ=BQ+AQ, MN=NP
        Element x1 = segment("A", "B");
        Element x2 = segment("C", "D");
        Element x3 = segment("M", "N");
        Element x4 = segment("P", "Q");
        Element x5 = segment("B", "Q");
        Element x6 = segment("A", "Q");
        Element x7 = segment("N", "P");
        Element s1 = sumUnits(multiplyUnits(x1, sumUnits(x2, x3)),
                                       multiplyUnits(x4, sumUnits(x2, x3)));
        t2 = new Graph("中国联赛第二题");
        t2.addEqual("seg", sumUnits(x5, s1), sumUnits(x5, x6));
        t2.addEqual("seg", x3, x7);
        t2.deduceByEquivalenceTransform("seg", 4);
        t2.deduceByEquivalenceTransform("seg", 4);
        t2.deduceByEquivalenceTransform("seg", 4);
        t2.displayGraphInfo();
        System.out.print("\n");
        // 测试经典定理推导功能
        // 测试自定义定理推导功能（开发者自行测试）

        /*测试用例4，测试工具函数方法正确*/
        Element a1 = Graph.unitStrToElement("(AB+BC)*CD+DE");
        Element a2 = sumUnits(segment("A", "B"), segment("B", "C"));
        Element a3 = sumUnits(multiplyUnits(a2, segment("C", "D")),
                segment("D", "E"));
        System.out.print(a1 + "\n");
        System.out.print(a3 + "\n");
        System.out.print(a1.equals(a3) + "\n");
    }

}
