package com.bigfatcat.geometry3.celltest;

import com.bigfatcat.geometry3.core.structure.elements.*;
import com.bigfatcat.geometry3.core.structure.relations.Equal;

import static com.bigfatcat.geometry3.core.structure.elements.Segment.segment;
import static com.bigfatcat.geometry3.core.structure.elements.Angle.angle;
import static com.bigfatcat.geometry3.core.structure.elements.SumUnits.sumUnits;
import static com.bigfatcat.geometry3.core.structure.elements.MultiplyUnits.multiplyUnits;
import static com.bigfatcat.geometry3.core.structure.relations.Equal.equal;

/**
 * 数据结构功能定义层测试篇
 * */
public class StructureTest {

    public static void main(String[] args) throws Exception {

        /*测试用例1，验证边、加和、乘积元素的使用正确*/
        Segment x1 = segment("A", "B");
        Segment x2 = segment("A", "C");
        Segment x3 = segment("C", "A");

        SumUnits z1 = sumUnits(sumUnits(x1, x3), sumUnits(x2, x3));
        SumUnits z2 = sumUnits(z1, z1);
        SumUnits z3 = sumUnits(sumUnits(x1, z1), sumUnits(x2, sumUnits(x3, x2)));
        MultiplyUnits z4 = multiplyUnits(z3, x3);
        MultiplyUnits z5 = multiplyUnits(x2, z2);
        MultiplyUnits z6 = multiplyUnits(z4, z3);
        MultiplyUnits z7 = multiplyUnits(z5, z2);

        System.out.print(z1.getGeometry_key() + "\n");
        System.out.print(z1.getUnit_counts() + "\n");
        System.out.print(z1.getSubUnits() + "\n");
        System.out.print(z3.getGeometry_key() + "\n");
        System.out.print(z3.getUnit_counts() + "\n");
        System.out.print(z3.getSubUnits() + "\n");
        System.out.print(z2.equals(z3) + "\n");
        System.out.print(x1 + "\n");
        System.out.print(x2 + "\n");
        System.out.print(x3 + "\n");
        System.out.print(z6.getGeometry_key() + "\n");
        System.out.print(z6.getUnit_counts() + "\n");
        System.out.print(z6.getSubUnits() + "\n");
        System.out.print(z7.getGeometry_key() + "\n");
        System.out.print(z7.getUnit_counts() + "\n");
        System.out.print(z7.getSubUnits() + "\n");
        System.out.print(z6.equals(z7) + "\n");
        System.out.print(z4 + "\n");
        System.out.print(z5 + "\n");
        System.out.print("\n");
//        3AC+AB
//        {com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6=3, com.bigfatcat.geometry3.core.structure.elements.Segment@1540e19d=1}
//        [com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6, com.bigfatcat.geometry3.core.structure.elements.Segment@1540e19d]
//        2AB+6AC
//        {com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6=6, com.bigfatcat.geometry3.core.structure.elements.Segment@1540e19d=2}
//        [com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6, com.bigfatcat.geometry3.core.structure.elements.Segment@1540e19d]
//        true
//        com.bigfatcat.geometry3.core.structure.elements.Segment@1540e19d
//        com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6
//        com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6
//        (2AB+6AC)^2*AC
//        {com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6=1, com.bigfatcat.geometry3.core.structure.elements.SumUnits@14ae5a5=2}
//        [com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6, com.bigfatcat.geometry3.core.structure.elements.SumUnits@14ae5a5]
//        (2AB+6AC)^2*AC
//        {com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6=1, com.bigfatcat.geometry3.core.structure.elements.SumUnits@14ae5a5=2}
//        [com.bigfatcat.geometry3.core.structure.elements.Segment@677327b6, com.bigfatcat.geometry3.core.structure.elements.SumUnits@14ae5a5]
//        true
//        com.bigfatcat.geometry3.core.structure.elements.MultiplyUnits@7f31245a
//        com.bigfatcat.geometry3.core.structure.elements.MultiplyUnits@7f31245a

        /*测试用例2，验证角、角度、加和元素的使用正确*/
        Angle a1 = angle("C", "B", "A");
        Angle a2 = angle("D", "B", "A");
        Angle a3 = angle("A", "B", "D");
        Degree a4 = Degree.degree(180);
        Degree a5 = Degree.degree(180);

        SumUnits s1 = sumUnits(sumUnits(a1, a2), sumUnits(a2, a3));
        SumUnits s2 = sumUnits(s1, s1);
        SumUnits s3 = sumUnits(sumUnits(a1, s1), sumUnits(a2, sumUnits(a3, a2)));
        SumUnits s4 = sumUnits(s2, a5);
        SumUnits s5 = sumUnits(s3, a4);

        System.out.print(s1.getGeometry_key() + "\n");
        System.out.print(s1.getUnit_counts() + "\n");
        System.out.print(s1.getSubUnits() + "\n");
        System.out.print(s3.getGeometry_key() + "\n");
        System.out.print(s3.getUnit_counts() + "\n");
        System.out.print(s3.getSubUnits() + "\n");
        System.out.print(s2.equals(s3) + "\n");
        System.out.print(a1 + "\n");
        System.out.print(a2 + "\n");
        System.out.print(a3 + "\n");
        System.out.print(a4 + "\n");
        System.out.print(a5 + "\n");
        System.out.print(a2.equals(a3) + "\n");
        System.out.print(a4.equals(a5) + "\n");
        System.out.print(s4.getGeometry_key() + "\n");
        System.out.print(s4.getUnit_counts() + "\n");
        System.out.print(s4.getSubUnits() + "\n");
        System.out.print(s5.getGeometry_key() + "\n");
        System.out.print(s5.getUnit_counts() + "\n");
        System.out.print(s5.getSubUnits() + "\n");
        System.out.print(s4.equals(s5) + "\n");
        System.out.print("\n");
//        3∠ABD+∠ABC
//        {com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28=1, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4=3}
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        2∠ABC+6∠ABD
//        {com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28=2, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4=6}
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        true
//        com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28
//        com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4
//        com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4
//        com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7
//        com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7
//        true
//        true
//        180°+2∠ABC+6∠ABD
//        {com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7=1, com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28=2, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4=6}
//        [com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7, com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        180°+2∠ABC+6∠ABD
//        {com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7=1, com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28=2, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4=6}
//        [com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7, com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        true

        /*测试用例3，验证元素类附属方法的正确*/
        System.out.print(x1.isComplex() + "\n");
        System.out.print(z1.isComplex() + "\n");
        System.out.print(x1.getLength() + "\n");
        System.out.print(z1.getLength() + "\n");
        System.out.print(z3.getLength() + "\n");
        System.out.print(z4.getLength() + "\n");
        System.out.print(z7.getLength() + "\n");
        System.out.print(x1.getPoints() + "\n");
        System.out.print(a1.getVertex() + "\n");
        System.out.print(a1.getAside_points() + "\n");
        System.out.print(a4.getValue() + "\n");
        System.out.print("\n");
//        false
//        true
//        1
//        4
//        8
//        2
//        3
//        [A, B]
//        B
//        [A, C]
//        180

        /*测试用例4，验证复合元素类相关附属方法的正确*/
        System.out.print(x1.isSubOf(z1) + "\n");
        System.out.print(z1.isSubOf(z2) + "\n");
        System.out.print(z2.isSubOf(z4) + "\n");
        System.out.print(a1.isSubOf(s5) + "\n");
        System.out.print(s3.isSubOf(s5) + "\n");
        System.out.print(a5.isSubOf(s5) + "\n");
        System.out.print(z4.isSameClassOf(z5) + "\n");

        System.out.print(a5.isSubOf(a5) + "\n");
        System.out.print(x1.isSubOf(x2) + "\n");
        System.out.print(z2.isSubOf(z1) + "\n");
        System.out.print(z2.isSubOf(z3) + "\n");
        System.out.print(z1.isSubOf(z4) + "\n");
        System.out.print(a5.isSubOf(s3) + "\n");
        System.out.print("\n");
//        7*true
//        6*false
        x1 = segment("A", "B");
        x2 = segment("C", "B");
        x3 = segment("C", "D");
        Segment x4 = segment("D", "C");
        s1 = sumUnits(sumUnits(x3, x4), sumUnits(x1, x2));
        s2 = sumUnits(x1, sumUnits(x3, x4));
        s3 = sumUnits(x1, x2);
        System.out.print(s2.getInnerOf(s3).getGeometry_key() + "\n");
        System.out.print(s1.getInnerOf(s2).getGeometry_key() + "\n");
        System.out.print(s1.getInnerOf(s1).getGeometry_key() + "\n");
        System.out.print(s1.getInnerOf(s5) + "\n");
        System.out.print(s1.getInnerOf(s2).equals(s2.getInnerOf(s1)) + "\n");
        System.out.print(s1.getExceptOf(s3).getGeometry_key() + "\n");
        System.out.print(s1.getExceptOf(s2).getGeometry_key() + "\n");
        System.out.print(s1.getExceptOf(x1).getGeometry_key() + "\n");
        System.out.print(s1.getExceptOf(x3).getGeometry_key() + "\n");
        System.out.print(s1.getExceptOf(s2).equals(x2) + "\n");
        System.out.print(s1.getExceptOf(x3).equals(s1.getExceptOf(x4)) + "\n");
        System.out.print("\n");
//        AB
//        2CD+AB
//        2CD+AB+BC
//        null
//        true
//        2CD
//        BC
//        2CD+BC
//        AB+BC+CD
//        true
//        true

        /*测试用例5，验证等量关系类相关附属方法的正确*/
        Equal e1 = equal(a1, a2);
        Equal e2 = equal(a1, a4);
        Equal e3 = equal(a1, a5);
        System.out.print(e1.getGeometry_key() + "\n");
        System.out.print(e2.getGeometry_key() + "\n");
        System.out.print(e3.getGeometry_key() + "\n");
        System.out.print(e1.getUnits_set() + "\n");
        System.out.print(e2.getUnits_set() + "\n");
        System.out.print(e3.getUnits_set() + "\n");
        System.out.print(e1.getExceptSetOf(a1) + "\n");
        System.out.print(e1.getExceptSetOf(e2) + "\n");
        System.out.print(e1.getInnerSetOf(e2) + "\n");
        System.out.print(e2.equals(e3) + "\n");
        System.out.print(e2.getInnerSetOf(e3) + "\n");
        System.out.print(e2.getExceptSetOf(e3) + "\n");
        System.out.print(e1.isContainedOf(e1) + "\n");
        System.out.print(e2.isContainedOf(equal(e2.getInnerSetOf(e3))) + "\n");
//        ∠ABC=∠ABD
//        180°=∠ABC
//        180°=∠ABC
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7]
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7]
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@135fbaa4]
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28]
//        true
//        [com.bigfatcat.geometry3.core.structure.elements.Angle@6d6f6e28, com.bigfatcat.geometry3.core.structure.elements.Degree@45ee12a7]
//        []
//        2*true

    }

}
