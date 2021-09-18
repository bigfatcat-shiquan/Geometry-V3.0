package com.bigfatcat.geometry3.core.structure.elements;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import static java.lang.Math.min;

/**
 * 类：ComplexElement 复合元素
 * 该类继承于Element类，为SumUnits加和、MultiplyUnits乘积的基础抽象类
 * 在这里定义了有且只有复合元素才共同拥有的属性和方法
 * */
public abstract class ComplexElement extends Element{

    /**获取复合元素的内含元素个数表
     * 为哈希表结构，key为内含元素对象（可以是复合元素，但不能是同一类复合元素），value为个数
     * */
    public abstract HashMap<Element, Integer> getUnit_counts();

    /**获取复合元素的内含元素对象的集合*/
    public abstract Set<Element> getSubUnits();

    /**获取复合元素和另一个相同类型的复合元素所重合的部分，如果有多个元素重合，则复合为一个复合元素，如果仅有一个元素重合，则返回该元素，没有则返回空*/
    public Element getInnerOf(ComplexElement another_complex_unit) throws Exception {
        if (!this.isSameClassOf(another_complex_unit)) {
            throw new Exception("只能求两个相同类型的复合元素之间的交集！");
        }
        HashMap<Element, Integer> inner_unit_counts = new HashMap<>();
        HashMap<Element, Integer> this_unit_counts = this.getUnit_counts();
        HashMap<Element, Integer> another_unit_counts = another_complex_unit.getUnit_counts();
        for (Entry<Element, Integer> this_entry : this_unit_counts.entrySet()) {
            Element this_sub = this_entry.getKey();
            Integer this_sub_in_another_count = another_unit_counts.get(this_sub);
            if (this_sub_in_another_count != null) {
                inner_unit_counts.put(this_sub, min(this_entry.getValue(), this_sub_in_another_count));
            }
        }
        if (inner_unit_counts.keySet().size() == 0) {
            return null;
        } else if (inner_unit_counts.keySet().size() == 1 && Collections.max(inner_unit_counts.values()) == 1) {
            return inner_unit_counts.keySet().iterator().next();
        } else {
            if (this instanceof SumUnits) {
                return SumUnits.sumUnits(inner_unit_counts);
            } else if (this instanceof MultiplyUnits) {
                return MultiplyUnits.multiplyUnits(inner_unit_counts);
            } else {
                throw new Exception("未定义非加和或乘积的复合类型求重合交集！");
            }
        }
    }

    /**获取复合元素减去另一个相同类型的下属元素剩下的部分，如果剩下的有多个元素，则复合为一个复合元素，如果剩下的仅含一个元素，则返回该元素*/
    public Element getExceptOf(Element another_unit) throws Exception {
        if (!another_unit.isSubOf(this)) {
            throw new Exception("要减去的另一个元素必须可以是其下属元素！");
        }
        HashMap<Element, Integer> except_unit_counts = new HashMap<>();
        HashMap<Element, Integer> this_unit_counts = this.getUnit_counts();
        if (another_unit instanceof ComplexElement && another_unit.isSameClassOf(this)) {
            HashMap<Element, Integer> another_unit_counts = ((ComplexElement) another_unit).getUnit_counts();
            for (Entry<Element, Integer> this_entry : this_unit_counts.entrySet()) {
                Element this_sub = this_entry.getKey();
                Integer this_sub_in_this_count = this_entry.getValue();
                Integer this_sub_in_another_count = another_unit_counts.get(this_sub);
                if (this_sub_in_another_count == null) {
                    except_unit_counts.put(this_sub, this_sub_in_this_count);
                } else if (this_sub_in_this_count > this_sub_in_another_count) {
                    except_unit_counts.put(this_sub, this_sub_in_this_count - this_sub_in_another_count);
                }
            }
        } else {
            except_unit_counts.putAll(this_unit_counts);
            Integer another_unit_in_this_count = this_unit_counts.get(another_unit);
            if (another_unit_in_this_count == 1) {
                except_unit_counts.remove(another_unit);
            } else {
                except_unit_counts.put(another_unit, another_unit_in_this_count - 1);
            }
        }
        if (except_unit_counts.keySet().size() == 1 && Collections.max(except_unit_counts.values()) == 1) {
            return except_unit_counts.keySet().iterator().next();
        } else {
            if (this instanceof SumUnits) {
                return SumUnits.sumUnits(except_unit_counts);
            } else if (this instanceof MultiplyUnits) {
                return MultiplyUnits.multiplyUnits(except_unit_counts);
            } else {
                throw new Exception("未定义非加和或乘积的复合类型求与另一个元素差！");
            }
        }
    }

}
