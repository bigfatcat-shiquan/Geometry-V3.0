package core.structure.elements;

import java.util.Comparator;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * 类：SumUnits 加和元素
 * 该类继承于ComplexElement类
 * 意义为多个边元素之间的加和，或多个角元素或角度元素之间的加和所生成的复合元素，写作sumUnits(元素1, 元素2)或sumUnits(HashMap<元素，个数>)
 * 一般使用第一种写法，即边AB+边AC写作sumUnits(segment("A", "B"), segment("A", "C"))
 * */
public class SumUnits extends ComplexElement{

    /**定义全局所出现的所有加和元素的带索引集合*/
    private final static HashMap<String, SumUnits> global_all_sumUnits = new HashMap<>();

    /**定义加和元素的唯一字符串编码*/
    private final String geometry_key;

    /**定义加和元素的内容属性：内含元素个数表*/
    private final HashMap<Element, Integer> unit_counts;

    /**定义生成加和元素的唯一字符串编码的方式*/
    public static String makeGeometry_key(HashMap<Element, Integer> this_unit_counts) {
        StringBuilder this_geometry_key_builder = new StringBuilder();
        ArrayList<String> units_key_list = new ArrayList<>();
        for (Entry<Element, Integer> this_entry : this_unit_counts.entrySet()) {
            Integer count = this_entry.getValue();
            if (count == 1) {
                units_key_list.add(this_entry.getKey().getGeometry_key());
            } else {
                units_key_list.add(count + this_entry.getKey().getGeometry_key());
            }
        }
        units_key_list.sort(Comparator.naturalOrder());
        for (String this_unit_key : units_key_list) {
            this_geometry_key_builder.append(this_unit_key);
            this_geometry_key_builder.append("+");
        }
        String this_geometry_key = this_geometry_key_builder.toString();
        return this_geometry_key.substring(0, this_geometry_key.length()-1);
    }

    /**
     * 定义获取一个加和元素对象的代码表达式
     * 两种方式：sumUnits(元素1, 元素2) 或 sumUnits(HashMap<元素，个数>)，后一种写法所有元素个数总和必须大于等于2
     * */
    public static SumUnits sumUnits(Element unit_1, Element unit_2) {
        HashMap<Element, Integer> this_unit_counts = new HashMap<>();
        if (unit_1 instanceof SumUnits) {
            HashMap<Element, Integer> unit1_unit_counts = ((SumUnits) unit_1).getUnit_counts();
            this_unit_counts.putAll(unit1_unit_counts);
            if (unit_2 instanceof SumUnits) {
                HashMap<Element, Integer> unit2_unit_counts = ((SumUnits) unit_2).getUnit_counts();
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
        } else if (unit_2 instanceof SumUnits) {
            return sumUnits(unit_2, unit_1);
        } else {
            if (unit_1.equals(unit_2)) {
                this_unit_counts.put(unit_1, 2);
            } else {
                this_unit_counts.put(unit_1, 1);
                this_unit_counts.put(unit_2, 1);
            }
        }
        String this_geometry_key = makeGeometry_key(this_unit_counts);
        if (global_all_sumUnits.containsKey(this_geometry_key)) {
            return global_all_sumUnits.get(this_geometry_key);
        } else {
            return new SumUnits(this_geometry_key, this_unit_counts);
        }
    }

    public static SumUnits sumUnits(HashMap<Element, Integer> this_unit_counts) {
        String this_geometry_key = makeGeometry_key(this_unit_counts);
        if (global_all_sumUnits.containsKey(this_geometry_key)) {
            return global_all_sumUnits.get(this_geometry_key);
        } else {
            return new SumUnits(this_geometry_key, this_unit_counts);
        }
    }

    /**生成一个加和元素对象的构造函数*/
    private SumUnits(String this_geometry_key, HashMap<Element, Integer> this_unit_counts) {
        this.geometry_key = this_geometry_key;
        this.unit_counts = this_unit_counts;
        global_all_sumUnits.put(this.geometry_key, this);
    }

    /**获取一个加和元素对象的唯一字符串编码*/
    @Override
    public String getGeometry_key() {
        return this.geometry_key;
    }

    /**获取一个加和元素对象的内容属性: 内含元素个数表*/
    @Override
    public HashMap<Element, Integer> getUnit_counts() {
        return this.unit_counts;
    }

    /**获取一个加和元素对象的内含元素对象的集合*/
    @Override
    public Set<Element> getSubUnits() {
        return this.getUnit_counts().keySet();
    }

}
