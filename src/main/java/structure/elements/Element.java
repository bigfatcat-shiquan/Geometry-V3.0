package structure.elements;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 类：Element 元素
 * 该类为一切几何单元的基础抽象类
 * 几何问题的描述与推理基于的几何元素包括：Segment 边， Angle 角， Degree 角度， SumUnits 加和， MultiplyUnits 乘积
 * 几何问题即为关于上述元素的等量关系的推导发现，上述元素类继承于Element这一基础抽象类
 * 边、角、角度属于单元素，加和、乘积属于复合元素，复合元素对象由至少2个及以上的单元素对象或复合元素对象才能生成
 * */
public abstract class Element {

    /**获取元素对应的唯一字符串识别编码
     * 唯一字符串识别编码：为保证每次书写相同的数学表达式时所指向的对象均是同一个，故使用唯一对应的字符串编码来作为key
     * 不同类型的元素的字符串编码生成规则是不同的，生成规则方法在各个类中予以实现
     * */
    public abstract String getGeometry_key();

    /**判断元素是否为一个复合元素*/
    public Boolean isComplex() {
        return this instanceof SumUnits || this instanceof MultiplyUnits;
    }

    /**获取元素的“长度”：如果为非复合元素，返回1；如果为复合元素，返回下属进行复合运算的元素的数量*/
    public Integer getLength() {
        Integer length = 0;
        if (this.isComplex()) {
            HashMap<Element, Integer> this_unit_counts = ((ComplexElement) this).getUnit_counts();
            for (Integer count : this_unit_counts.values()) {
                length += count;
            }
            return length;
        } else {
            return 1;
        }
    }

    /**判断元素是否和另一个元素是同一类元素*/
    public Boolean isSameClassOf (Element another_unit) {
        return this.getClass() == another_unit.getClass();
    }

    /**判断元素是否是另一个元素的下属元素，或者在某种组合中可以是另一个元素的下属元素*/
    public Boolean isSubOf (Element another_unit) {
        // 如果两个元素相同，返回否
        if (this.equals(another_unit)) {
            return false;
        }
        // 必须目标元素为复合元素,方可继续判断
        if (another_unit instanceof ComplexElement) {
            // 如果两个元素为同一类复合元素，则详细比较内含元素及其个数；如不同类，则直接判断是否为目标元素的内含元素
            if (this.isSameClassOf(another_unit)) {
                HashMap<Element, Integer> this_unit_counts = ((ComplexElement) this).getUnit_counts();
                HashMap<Element, Integer> another_unit_counts = ((ComplexElement) another_unit).getUnit_counts();
                for (Entry<Element, Integer> this_entry : this_unit_counts.entrySet()) {
                    Integer this_sub_in_another_count = another_unit_counts.get(this_entry.getKey());
                    if (this_sub_in_another_count == null || this_sub_in_another_count < this_entry.getValue()) {
                        return false;
                    }
                }
                return true;
            } else {
                return ((ComplexElement) another_unit).getSubUnits().contains(this);
            }
        } else {
            return false;
        }
    }

}
