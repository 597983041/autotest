package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author lianghaijun
 * @date 2019-10-23
 */
public class DbUtil {
    
    public static int execute(DataSource dataSource, String sql, Object... params) throws SQLException {
        return new QueryRunner(dataSource).execute(sql, params);
    }
    
    public static <T> T queryBean(DataSource dataSource, String sql, Class<T> clazz) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new BeanHandler<>(clazz));
    }
    
    public static Map<String, Object> queryMap(DataSource dataSource, String sql) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new MapHandler());
    }
    
    public static ObjectNode queryJson(DataSource dataSource, String sql) throws SQLException {
        Map<String, Object> query = new QueryRunner(dataSource).query(sql, new MapHandler());
        return new ObjectMapper().valueToTree(query);
    }
    
    
    public static <T> List<T> queryBeanList(DataSource dataSource, String sql, Class<T> clazz) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new BeanListHandler<>(clazz));
    }
    
    public static List<Map<String, Object>> queryMapList(DataSource dataSource, String sql) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new MapListHandler());
    }
    
    public static ArrayNode queryJsonList(DataSource dataSource, String sql) throws SQLException {
        List<Map<String, Object>> query = new QueryRunner(dataSource).query(sql, new MapListHandler());
        return new ObjectMapper().valueToTree(query);
    }




    //byId
    public static <T> T queryBeanById(DataSource dataSource, String table, long id, Class<T> clazz) throws SQLException {
        String sql = "select * from " + table + " where id = ? ";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<>(clazz), id);
    }

    public static Map<String, Object> queryMapById(DataSource dataSource, String table, long id) throws SQLException {
        String sql = "select * from " + table + " where id = ? ";
        return new QueryRunner(dataSource).query(sql, new MapHandler());
    }

    public static ObjectNode queryJsonById(DataSource dataSource, String table, long id) throws SQLException {
        String sql = "select * from " + table + " where id = ? ";
        Map<String, Object> query = new QueryRunner(dataSource).query(sql, new MapHandler());
        return new ObjectMapper().valueToTree(query);
    }



    //byId
    public static boolean existById(DataSource dataSource, String table, long id) throws SQLException {
        String sql = "select id from " + table + " where id = ? ";
        Object idValue = new QueryRunner(dataSource).query(sql, new ScalarHandler("id"), id);
        return idValue != null;
    }

    //update
    public static int update(DataSource dataSource,String sql) throws SQLException{
        return new QueryRunner(dataSource).update(sql);
    }
}
