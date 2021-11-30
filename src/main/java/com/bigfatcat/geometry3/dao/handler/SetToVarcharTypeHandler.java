package com.bigfatcat.geometry3.dao.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;

/**HashSet类存储至数据库VARCHAR类型的转换器*/
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Set.class)
public class SetToVarcharTypeHandler implements TypeHandler<Set<String>> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, Set<String> strings, JdbcType jdbcType)
            throws SQLException {
        // 插入sql语句时进行的类型转换操作
        StringBuilder stringBuilder = new StringBuilder();
        for (String unit : strings) {
            stringBuilder.append(unit);
            stringBuilder.append(",");
        }
        String remade_string = stringBuilder.toString();
        preparedStatement.setString(i, remade_string.substring(0, max(0, remade_string.length()-1)));
    }

    @Override
    public Set<String> getResult(ResultSet resultSet, int i) throws SQLException {
        // 从结果集获取结果时进行的类型转换操作
        String resultString = resultSet.getString(i);
        if (!StringUtils.isEmpty(resultString)) {
            return new HashSet<>(Arrays.asList(resultString.split(",")));
        }
        return new HashSet<>();
    }

    @Override
    public Set<String> getResult(ResultSet resultSet, String s) throws SQLException {
        // 从结果集获取结果时进行的类型转换操作
        String resultString = resultSet.getString(s);
        if (!StringUtils.isEmpty(resultString)) {
            return new HashSet<>(Arrays.asList(resultString.split(",")));
        }
        return new HashSet<>();
    }

    @Override
    public Set<String> getResult(CallableStatement callableStatement, int i) throws SQLException {
        // 从结果集获取结果时进行的类型转换操作
        String resultString = callableStatement.getString(i);
        if (!StringUtils.isEmpty(resultString)) {
            return new HashSet<>(Arrays.asList(resultString.split(",")));
        }
        return new HashSet<>();
    }

}
