package com.bigfatcat.geometry3.util;

import java.util.HashSet;

/**
 * 工具：Common 常用数据结构处理等高频刚需操作
 * */
public class Common{

    /**获取一个集合除去集合内的某一个元素之后剩下的部分组成的新集合*/
    public static <T> HashSet<T> getSetExcept(HashSet<T> old_set, T unit) {
        HashSet<T> new_set = new HashSet<>(old_set);
        new_set.remove(unit);
        return new_set;
    }

    /**获取一个集合除去另一个集合内所有元素之后剩下的部分组成的新集合*/
    public static <T> HashSet<T> getSetExcept(HashSet<T> old_set, HashSet<T> another_set) {
        HashSet<T> new_set = new HashSet<>(old_set);
        new_set.removeAll(another_set);
        return new_set;
    }

}
