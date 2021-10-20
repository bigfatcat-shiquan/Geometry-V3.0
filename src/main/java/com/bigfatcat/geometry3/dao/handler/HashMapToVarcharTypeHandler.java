package com.bigfatcat.geometry3.dao.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static java.lang.Math.max;

/**HashMap类存储至数据库VARCHAR类型的转换器*/
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(HashMap.class)
public class HashMapToVarcharTypeHandler implements TypeHandler<HashMap<String, Double>> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i,
                             HashMap<String, Double> stringDoubleHashMap, JdbcType jdbcType) throws SQLException {
        // 插入sql语句时进行的类型转换操作
        StringBuilder stringBuilder = new StringBuilder();
        stringDoubleHashMap.forEach((key, value) -> {
            stringBuilder.append(key);
            stringBuilder.append(":");
            stringBuilder.append(value);
            stringBuilder.append(",");
        });
        String remade_string = stringBuilder.toString();
        preparedStatement.setString(i, remade_string.substring(0, max(0, remade_string.length()-1)));
    }

    @Override
    public HashMap<String, Double> getResult(ResultSet resultSet, int i) throws SQLException {
        // 从结果集获取结果时进行的类型转换操作
        String resultString = resultSet.getString(i);
        HashMap<String, Double> result_hashmap = new HashMap<>();
        if (!StringUtils.isEmpty(resultString)) {
            for (String entry_str : resultString.split(",")) {
                String[] entry = entry_str.split(":");
                result_hashmap.put(entry[0], Double.valueOf(entry[1]));
            }
        }
        return result_hashmap;
    }

    @Override
    public HashMap<String, Double> getResult(ResultSet resultSet, String s) throws SQLException {
        // 从结果集获取结果时进行的类型转换操作
        String resultString = resultSet.getString(s);
        HashMap<String, Double> result_hashmap = new HashMap<>();
        if (!StringUtils.isEmpty(resultString)) {
            for (String entry_str : resultString.split(",")) {
                String[] entry = entry_str.split(":");
                result_hashmap.put(entry[0], Double.valueOf(entry[1]));
            }
        }
        return result_hashmap;
    }

    @Override
    public HashMap<String, Double> getResult(CallableStatement callableStatement, int i) throws SQLException {
        // 从结果集获取结果时进行的类型转换操作
        String resultString = callableStatement.getString(i);
        HashMap<String, Double> result_hashmap = new HashMap<>();
        if (!StringUtils.isEmpty(resultString)) {
            for (String entry_str : resultString.split(",")) {
                String[] entry = entry_str.split(":");
                result_hashmap.put(entry[0], Double.valueOf(entry[1]));
            }
        }
        return result_hashmap;
    }

}
