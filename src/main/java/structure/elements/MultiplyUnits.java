package structure.elements;

import java.util.Comparator;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * 类：MultiplyUnits 乘积元素
 * 该类继承于ComplexElement类
 * 意义为多个边元素之间的乘积（在欧氏几何中一般不出现角或角度的乘积），写作multiplyUnits(元素1, 元素2)或multiplyUnits(HashMap<元素，个数>)
 * 一般使用第一种写法，即边AB*边AC写作multiplyUnits(segment("A", "B"), segment("A", "C"))
 * */
public class MultiplyUnits extends ComplexElement{

    /**定义全局所出现的所有乘积元素的带索引集合*/
    private final static HashMap<String, MultiplyUnits> global_all_multiplyUnits = new HashMap<>();

    /**定义乘积元素的唯一字符串编码*/
    private final String geometry_key;

    /**定义乘积元素的内容属性：内含元素个数表*/
    private final HashMap<Element, Integer> unit_counts;

    /**定义生成乘积元素的唯一字符串编码的方式*/
    public static String makeGeometry_key(HashMap<Element, Integer> this_unit_counts) {
        StringBuilder this_geometry_key_builder = new StringBuilder();
        ArrayList<String> units_key_list = new ArrayList<>();
        for (Entry<Element, Integer> this_entry : this_unit_counts.entrySet()) {
            Integer count = this_entry.getValue();
            Element this_sub_unit = this_entry.getKey();
            String this_sub_unit_key;
            if (this_sub_unit instanceof SumUnits) {
                this_sub_unit_key = "(" + this_sub_unit.getGeometry_key() + ")";
            } else {
                this_sub_unit_key = this_sub_unit.getGeometry_key();
            }
            if (count == 1) {
                units_key_list.add(this_sub_unit_key);
            } else {
                units_key_list.add(this_sub_unit_key + "^" + count);
            }
        }
        units_key_list.sort(Comparator.naturalOrder());
        for (String this_unit_key : units_key_list) {
            this_geometry_key_builder.append(this_unit_key);
            this_geometry_key_builder.append("*");
        }
        String this_geometry_key = this_geometry_key_builder.toString();
        return this_geometry_key.substring(0, this_geometry_key.length()-1);
    }

    /**
     * 定义获取一个乘积元素对象的代码表达式
     * 两种方式：multiplyUnits(元素1, 元素2) 或 multiplyUnits(HashMap<元素，个数>)，后一种写法所有元素个数总和必须大于等于2
     * */
    public static MultiplyUnits multiplyUnits(Element unit_1, Element unit_2) {
        HashMap<Element, Integer> this_unit_counts = new HashMap<>();
        if (unit_1 instanceof MultiplyUnits) {
            HashMap<Element, Integer> unit1_unit_counts = ((MultiplyUnits) unit_1).getUnit_counts();
            this_unit_counts.putAll(unit1_unit_counts);
            if (unit_2 instanceof MultiplyUnits) {
                HashMap<Element, Integer> unit2_unit_counts = ((MultiplyUnits) unit_2).getUnit_counts();
                for (Entry<Element, Integer> unit_2_entry : unit2_unit_counts.entrySet()) {
                    Element unit_2_sub = unit_2_entry.getKey();
                    Integer unit_2_sub_count = unit_2_entry.getValue();
                    Integer count = this_unit_counts.get(unit_2_sub);
                    this_unit_counts.put(unit_2_sub, (count == null) ? unit_2_sub_count : count + unit_2_sub_count);
                }
            } else {
                Integer count = this_unit_counts.get(unit_2);
                this_unit_counts.put(unit_2, (count == null) ? 1 : count + 1);
            }
        } else if (unit_2 instanceof MultiplyUnits) {
            return multiplyUnits(unit_2, unit_1);
        } else {
            if (unit_1.equals(unit_2)) {
                this_unit_counts.put(unit_1, 2);
            } else {
                this_unit_counts.put(unit_1, 1);
                this_unit_counts.put(unit_2, 1);
            }
        }
        String this_geometry_key = makeGeometry_key(this_unit_counts);
        if (global_all_multiplyUnits.containsKey(this_geometry_key)) {
            return global_all_multiplyUnits.get(this_geometry_key);
        } else {
            return new MultiplyUnits(this_geometry_key, this_unit_counts);
        }
    }

    public static MultiplyUnits multiplyUnits(HashMap<Element, Integer> this_unit_counts) {
        String this_geometry_key = makeGeometry_key(this_unit_counts);
        if (global_all_multiplyUnits.containsKey(this_geometry_key)) {
            return global_all_multiplyUnits.get(this_geometry_key);
        } else {
            return new MultiplyUnits(this_geometry_key, this_unit_counts);
        }
    }

    /**生成一个乘积元素对象的构造函数*/
    private MultiplyUnits (String this_geometry_key, HashMap<Element, Integer> this_unit_counts) {
        this.geometry_key = this_geometry_key;
        this.unit_counts = this_unit_counts;
        global_all_multiplyUnits.put(this.geometry_key, this);
    }

    /**获取一个乘积元素对象的唯一字符串编码*/
    @Override
    public String getGeometry_key() {
        return this.geometry_key;
    }

    /**获取一个乘积元素对象的内容属性: 内含元素个数表*/
    @Override
    public HashMap<Element, Integer> getUnit_counts() {
        return this.unit_counts;
    }

    /**获取一个乘积元素对象的内含元素对象的集合*/
    @Override
    public Set<Element> getSubUnits() {
        return this.getUnit_counts().keySet();
    }

}
