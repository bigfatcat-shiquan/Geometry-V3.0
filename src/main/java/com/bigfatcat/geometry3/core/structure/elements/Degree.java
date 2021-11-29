package com.bigfatcat.geometry3.core.structure.elements;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 类：Degree 角度元素
 * 该类继承于Element类
 * 意义即为欧氏几何中的角的度数，如60°则写作degree(60)
 * 为什么将角度单独作为一个元素，而不是作为角的一个属性呢，原因为在经典欧氏几何中，经常会有∠ABC+∠ABD=180°等等这样的等量关系
 * 因此角度对象可以独立于角对象存在于等量关系中，不适合作为角对象的一个属性来编写
 * */
public class Degree extends Element{

    /**定义全局所出现的所有角度元素的带索引集合*/
    private final static ConcurrentHashMap<String, Degree> global_all_degrees = new ConcurrentHashMap<>();

    /**定义角度元素的唯一字符串编码*/
    private final String geometry_key;

    /**定义角度元素的内容属性：角度数值*/
    private final Integer value;

    /**定义生成角度元素的唯一字符串编码的方式*/
    public static String makeGeometry_key(Integer value) {
        return value + "°";
    }

    /**定义获取一个角度元素对象的代码表达式*/
    public static Degree degree(Integer value) {
        String this_geometry_key = makeGeometry_key(value);
        if (global_all_degrees.containsKey(this_geometry_key)) {
            return global_all_degrees.get(this_geometry_key);
        } else {
            return new Degree(value, this_geometry_key);
        }
    }

    /**生成一个角度元素对象的构造函数*/
    private Degree(Integer value, String this_geometry_key) {
        // 生成该角度元素的唯一字符串编码
        this.geometry_key = this_geometry_key;
        // 生成该角度元素的内容属性：角度数值
        this.value = value;
        // 全局集合中添加记录该角度元素
        global_all_degrees.put(this.geometry_key, this);
    }

    /**获取一个角度元素对象的唯一字符串编码*/
    @Override
    public String getGeometry_key() {
        return this.geometry_key;
    }

    /**获取一个角度元素对象的内容属性：角度数值*/
    public Integer getValue() {
        return this.value;
    }

}
