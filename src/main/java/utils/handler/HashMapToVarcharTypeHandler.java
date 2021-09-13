package utils.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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
        preparedStatement.setString(i, remade_string.substring(0, remade_string.length()-1));
    }

    @Override
    public HashMap<String, Double> getResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public HashMap<String, Double> getResult(ResultSet resultSet, String s) throws SQLException {
        return null;
    }

    @Override
    public HashMap<String, Double> getResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }

}
