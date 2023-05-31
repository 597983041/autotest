package org.example.common;

import com.alibaba.fastjson.JSONArray;
import org.example.utils.JdbcUtils;
import org.example.utils.ResultSetTool;

import java.sql.*;

public class RequirementSql {

    public static JSONArray queryRequirement(int page, int size){
        Connection conn = JdbcUtils.getConnect();
        Statement statement = null;
        JSONArray jsonArray = null;
        try {
            statement = conn.createStatement();
            String sql = String.format("select * from requirement limit %s,%s", page,size);
            ResultSet resultSet = statement.executeQuery(sql);
            jsonArray = ResultSetTool.resultSetToJsonArry(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JdbcUtils.closeConn(statement,conn);
        return jsonArray;
    }

    public static JSONArray countRequirement(){
        Connection conn = JdbcUtils.getConnect();
        Statement statement = null;
        JSONArray jsonArray = null;
        try {
            statement = conn.createStatement();
            String sql = String.format("select count(*) from requirement");
            ResultSet resultSet = statement.executeQuery(sql);
            jsonArray = ResultSetTool.resultSetToJsonArry(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JdbcUtils.closeConn(statement,conn);
        return jsonArray;
    }
}
