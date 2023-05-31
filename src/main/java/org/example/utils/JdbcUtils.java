package org.example.utils;


import javax.sql.rowset.JdbcRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcUtils {
	public static Connection getConnect(){
		//1.加载驱动
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		//2.链接数据库
		String url = "jdbc:mysql://localhost:3306/test";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, "root", "12345678");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return conn;
	}

	public static void closeConn(Statement statement, Connection conn) {
		//释放资源：数据库连接使用使用结束后，需要关闭资源，不关闭资源会导致线程长时间被占用，造成资源浪费，
		//严重情况会宕机（服务器死机），这是很危险的
		//关闭资源时需要从小到大依次关闭
		if (statement != null) {
			try {
				statement.close();
				System.out.println("数据库的操作对象已关闭");
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			if (conn != null) {
				try {
					conn.close();
					System.out.println("数据库连接对象已关闭");
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
		}

	}


	public static void main(String[] args) {
		Connection conn = JdbcUtils.getConnect();
		Statement statement = null;
		try {
			//获取statement对象
			statement = conn.createStatement();
			int page =0;
			int size =5;
			String sql = "select count(*) from requirement";
			//获取结果集
			ResultSet resultSet = statement.executeQuery(sql);
			System.out.println("resultSet: " + ResultSetTool.resultSetToJsonArry(resultSet));
		} catch (Exception e) {
			e.printStackTrace();
		}
		JdbcUtils.closeConn(statement,conn);
	}
}