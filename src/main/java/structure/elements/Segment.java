package structure.elements;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 类：Segment 边元素
 * 该类继承于Element类
 * 意义即为欧氏几何中的边，其由两个点组成，点统一使用A-Z大写字符来表示，如边AB，则写作segment("A", "B")
 * */
public class Segment extends Element{

    /**定义全局所出现的所有边元素的带索引集合*/
    private final static HashMap<String, Segment> global_all_segments = new HashMap<>();

    /**定义边元素的唯一字符串编码*/
    private final String geometry_key;

    /**定义边元素的内容属性：两点组成的集合*/
    private final HashSet<String> points;

    /**定义生成边元素的唯一字符串编码的方式*/
    public static String makeGeometry_key(@NotNull String point_1, @NotNull String point_2) {
        if (point_1.compareTo(point_2) < 0) {
            return point_1 + point_2;
        } else {
            return point_2 + point_1;
        }
    }

    /**定义获取一个边元素对象的代码表达式*/
    public static Segment segment(String point_1, String point_2) {
        String this_geometry_key = makeGeometry_key(point_1, point_2);
        if (global_all_segments.containsKey(this_geometry_key)) {
            return global_all_segments.get(this_geometry_key);
        } else {
            return new Segment(point_1, point_2, this_geometry_key);
        }
    }

    /**生成一个边元素对象的构造函数*/
    private Segment(String point_1, String point_2, String this_geometry_key) {
        // 生成该边元素的唯一字符串编码
        this.geometry_key = this_geometry_key;
        // 生成该边元素的内容属性：两点集合
        HashSet<String> points_set = new HashSet<>();
        points_set.add(point_1);
        points_set.add(point_2);
        this.points = points_set;
        // 全局集合中添加记录该边元素
        global_all_segments.put(this.geometry_key, this);
    }

    /**获取一个边元素对象的唯一字符串编码*/
    @Override
    public String getGeometry_key() {
        return this.geometry_key;
    }

    /**获取一个边元素对象的内容属性：两点集合*/
    public HashSet<String> getPoints() {
        return this.points;
    }

}
