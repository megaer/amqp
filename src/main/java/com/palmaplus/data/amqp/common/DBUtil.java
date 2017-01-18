package com.palmaplus.data.amqp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by jiabing.zhu on 2016/9/29.
 */
public class DBUtil {
    private static final String DRIVER_CLASS_NAME = PropertiesUtil.getProperty("db.driver");
    private static final String URL = PropertiesUtil.getProperty("db.url");
    private static final String USERNAME = PropertiesUtil.getProperty("db.username");
    private static final String PASSWORD = PropertiesUtil.getProperty("db.password");

    private static Logger logger = LoggerFactory.getLogger(DBUtil.class);

    // 注册数据库驱动
    static {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            logger.debug("registered driver class failed");
            e.printStackTrace();
        }
    }

    // 获取连接
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // 关闭连接
    public static void closeConn(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.debug("close dbconnect failed");
                e.printStackTrace();
            }
        }
    }
/*    //测试
    public static void main(String[] args) throws SQLException {
        System.out.println(DBUtil.getConn());
    }*/
}
