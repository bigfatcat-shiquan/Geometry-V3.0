package com.bigfatcat.geometry3.core.structure.elements;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 类：Angle 角元素
 * 该类继承于Element类
 * 意义即为欧氏几何中的角，其由一个顶点和两个旁点组成，点统一使用A-Z大写字符来表示，如∠ABC，则写作angle("A", "B", "C")，顶点写在中间
 * */
public class Angle extends Element {

    /**定义全局所出现的所有角元素的带索引集合*/
    private final static HashMap<String, Angle> global_all_angles = new HashMap<>();

    /**定义角元素的唯一字符串编码*/
    private final String geometry_key;

    /**定义角元素的内容属性：顶点*/
    private final String vertex;

    /**定义角元素的内容属性：两侧点集*/
    private final HashSet<String> aside_points;

    /**定义生成角元素的唯一字符串编码的方式*/
    public static String makeGeometry_key(@NotNull String point_aside_1,
                                          @NotNull String point_vertex,
                                          @NotNull String point_aside_2) {
        if (point_aside_1.compareTo(point_aside_2) < 0) {
            return "∠" + point_aside_1 + point_vertex + point_aside_2;
        } else {
            return "∠" + point_aside_2 + point_vertex + point_aside_1;
        }
    }

    /**定义获取一个角元素对象的代码表达式*/
    public static Angle angle(String point_aside_1, String point_vertex, String point_aside_2) {
        String this_geometry_key = makeGeometry_key(point_aside_1, point_vertex, point_aside_2);
        if (global_all_angles.containsKey(this_geometry_key)) {
            return global_all_angles.get(this_geometry_key);
        } else {
            return new Angle(point_aside_1, point_vertex, point_aside_2, this_geometry_key);
        }
    }

    /**生成一个角元素对象的构造函数*/
    private Angle(String point_aside_1, String point_vertex, String point_aside_2, String this_geometry_key) {
        // 生成该角元素的唯一字符串编码
        this.geometry_key = this_geometry_key;
        // 生成该角元素的内容属性：顶点
        this.vertex = point_vertex;
        // 生成该角元素的内容属性：两侧点集
        HashSet<String> aside_points = new HashSet<>();
        aside_points.add(point_aside_1);
        aside_points.add(point_aside_2);
        this.aside_points = aside_points;
        // 全局集合中添加记录该角元素
        global_all_angles.put(this.geometry_key, this);
    }

    /**获取一个角元素对象的唯一字符串编码*/
    @Override
    public String getGeometry_key() {
        return this.geometry_key;
    }

    /**获取一个角元素对象的内容属性：顶点*/
    public String getVertex() {
        return this.vertex;
    }

    /**获取一个角元素对象的内容属性：两侧点集*/
    public HashSet<String> getAside_points() {
        return this.aside_points;
    }

}
