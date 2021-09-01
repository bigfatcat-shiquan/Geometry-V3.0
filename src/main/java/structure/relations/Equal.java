package structure.relations;

import structure.elements.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 类：Equal 相等关系式
 * 意义即为多个元素之间的相等关系，写作equal(元素1, 元素2)，如边AB=边AC，则写作equal(segment("A", "B"), segment("A", "C"))
 * 也可以写作equal(HashSet<元素>)，其中HashSet中元素个数至少有2个，来表示这一个集合中的元素均是相等的
 * 在欧式几何中，很多要证明的问题和定理均可以转化为相等关系式，也就是equal对象的形式来描述
 * */
public class Equal {

    /**定义全局所出现的所有相等关系式的带索引集合*/
    private final static HashMap<String, Equal> global_all_equals = new HashMap<>();

    /**定义相等关系式的唯一字符串编码*/
    private final String geometry_key;

    /**定义相等关系式的内容属性：相等的这组元素的集合*/
    private final HashSet<Element> units_set;

    /**定义生成相等关系式的唯一字符串编码的方式*/
    public static String makeGeometry_key(HashSet<Element> this_units_set) {
        StringBuilder this_geometry_key_builder = new StringBuilder();
        ArrayList<String> units_key_list = new ArrayList<>();
        for (Element this_unit : this_units_set) {
                units_key_list.add(this_unit.getGeometry_key());
        }
        units_key_list.sort(Comparator.naturalOrder());
        for (String this_unit_key : units_key_list) {
            this_geometry_key_builder.append(this_unit_key);
            this_geometry_key_builder.append("=");
        }
        String this_geometry_key = this_geometry_key_builder.toString();
        return this_geometry_key.substring(0, this_geometry_key.length()-1);
    }

    /**
     * 定义获取一个相等关系式对象的代码表达式
     * 两种方式：equal(元素1, 元素2)，元素1和元素2不能本来就相同 或 equal(HashSet<元素>)，HashSet内元素个数必须大于等于2
     * */
    public static Equal equal(Element unit_1, Element unit_2) throws Exception{
        HashSet<Element> this_units_set = new HashSet<>();
        if (unit_1.equals(unit_2)) {
            throw new Exception("两个本来就相同的元素不能生成equal对象！");
        }
        this_units_set.add(unit_1);
        this_units_set.add(unit_2);
        String this_geometry_key = makeGeometry_key(this_units_set);
        if (global_all_equals.containsKey(this_geometry_key)) {
            return global_all_equals.get(this_geometry_key);
        } else {
            return new Equal(this_geometry_key, this_units_set);
        }
    }

    public static Equal equal(HashSet<Element> this_units_set) throws Exception{
        if (this_units_set.size() < 2) {
            throw new Exception("组成相等关系式的元素个数不能少于2个！");
        }
        String this_geometry_key = makeGeometry_key(this_units_set);
        if (global_all_equals.containsKey(this_geometry_key)) {
            return global_all_equals.get(this_geometry_key);
        } else {
            return new Equal(this_geometry_key, this_units_set);
        }
    }

    /**生成一个相等关系式对象的构造函数*/
    private Equal(String this_geometry_key, HashSet<Element> this_units_set) {
        this.geometry_key = this_geometry_key;
        this.units_set = this_units_set;
        global_all_equals.put(this.geometry_key, this);
    }

    /**获取一个相等关系式对象的唯一字符串编码*/
    public String getGeometry_key() {
        return this.geometry_key;
    }

    /**获取一个相等关系式的内容属性：相等的这组元素的集合*/
    public HashSet<Element> getUnits_set() {
        return this.units_set;
    }

    /**判断相等关系式所含元素是否被另一个相等关系式所完全包含，如AB=AC已经被AB=AC=CD所完全包含*/
    public Boolean isContainedOf(Equal another_equal){
        return another_equal.units_set.containsAll(this.units_set);
    }

    /**获取一个相等关系式的集合除去某个元素或另一个相等关系式集合之后的集合部分*/
    public HashSet<Element> getExceptSetOf(Element another_unit) {
        HashSet<Element> result_units_set = new HashSet<>(this.units_set);
        result_units_set.remove(another_unit);
        return result_units_set;
    }

    public HashSet<Element> getExceptSetOf(Equal another_equal) {
        HashSet<Element> result_units_set = new HashSet<>(this.units_set);
        result_units_set.removeAll(another_equal.units_set);
        return result_units_set;
    }

    /**获取一个相等关系式的集合和另一个相等关系式集合的交集部分*/
    public HashSet<Element> getInnerSetOf(Equal another_equal) {
        HashSet<Element> result_units_set = new HashSet<>(this.units_set);
        result_units_set.retainAll(another_equal.units_set);
        return result_units_set;
    }

}
