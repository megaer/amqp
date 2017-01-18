package com.palmaplus.data.amqp.service;

import com.palmaplus.data.amqp.common.*;
import com.palmaplus.data.amqp.model.LocationModel;
import com.palmaplus.data.amqp.service.impl.SendUDPSocket;
import com.palmaplus.data.amqp.test.CreateCSV;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.qpid.QpidException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.jms.Session;
import org.apache.qpid.url.URLSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.*;
import java.text.DecimalFormat;

/**
 * @ClassName: AmqpThread
 * @Description: amqp对接线程
 * @author jiabing.zhu
 * @date 2016/9/28.
 *
 */
public class Amqp {
    private Logger logger = LoggerFactory.getLogger(Amqp.class);
    private AMQConnection conn;
    private String tableName = "locationMsg";
    private String host = PropertiesUtil.getProperty("server.host");
    private String port = PropertiesUtil.getProperty("amqp.port");
    private String username = PropertiesUtil.getProperty("amqp.username");
    private String password = PropertiesUtil.getProperty("amqp.password");
    private String keystorePath = PropertiesUtil.getProperty("keystore.path");

    public void amqpData(String queueId){
        logger.debug("amqp started:"
                + "userId:" + username
                + ",queueId:" + queueId
                + ",ip:" + host
                + ",port:" + port );
        // 获取keystore的路径
        logger.debug("get keystore path:" + keystorePath);
        // 设置系统环境jvm参数
        System.setProperty("javax.net.ssl.keyStore", keystorePath + "keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "importkey");
        System.setProperty("javax.net.ssl.trustStore", keystorePath + "mykeystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "importkey");

        // 地址变量
        String brokerOpts = "?brokerlist='tcp://"+host+":"+port+"?ssl='true'&ssl_verify_hostname='false''";
        String connectionString = "amqp://"+username+":"+ password + "/"+brokerOpts;
        logger.debug("connection string:" + connectionString);
        try {
            conn = new AMQConnection(connectionString);
            conn.start();
            logger.debug("connection started!");
            // 获取session
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("session created!");
            // 获取队列
            Destination queue = new AMQAnyDestination("ADDR:"+queueId+";{create:sender}");
            MessageConsumer consumer = session.createConsumer(queue);
            do
            {
                logger.debug("begin get data from " + queueId);
                Message m = consumer.receive(60000);
                if(m != null){
                    if(m instanceof BytesMessage){
                        BytesMessage tm = (BytesMessage)m;
                        byte[] keyBytes = new byte[(int) tm.getBodyLength()];
                        tm.readBytes(keyBytes);
                        String messages = new String(keyBytes);
                        logger.debug("SVA Data:"+messages);
                        try {
                            saveSvaData(messages);
                        } catch (IOException e){
                            e.printStackTrace();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
//                        System.out.println("SVA Data:"+messages);
                    }else{
                        logger.warn("Message is not in Byte format!");
//                        System.out.println("Get Message faliure");
                        return ;
                    }
                }
            } while(true);
        } catch (QpidException e){
            logger.debug(e.getMessage());
        } catch (URLSyntaxException e){
            logger.debug(e.getMessage());
        } catch (URISyntaxException e){
            logger.debug(e.getMessage());
        } catch (JMSException e) {
            logger.debug(e.getMessage());
        } finally{
            try {
                if(conn != null)
                {
                    conn.close();
                }
            } catch (JMSException e) {
                logger.debug(e.getMessage());
            }
            logger.error("[AMQP]No data from SVA,connection closed!");
        }
    }

    /**
     * @Title: saveSvaData
     * @Description: 将从sva获取的数据解析并保存到数据库
     * @param jsonStr：待解析的字符串
     */
    private void saveSvaData(String jsonStr) throws Exception{
        JSONObject result = JSONObject.fromObject(jsonStr);
        JSONArray list = result.getJSONArray("locationstream");
//        CreateCSV createCSV = new CreateCSV();
        for(int i = 0; i < list.size(); i++){
            LocationModel lm = new LocationModel();
            JSONObject loc = list.getJSONObject(i);
            // 设置LocationModel
            JSONObject location = loc.getJSONObject("location");
            lm.setIdType(loc.getString("IdType"));
            lm.setTimestamp(BigDecimal.valueOf(loc.getLong("Timestamp")));
            lm.setDataType(loc.getString("datatype"));
            Point p = ConventXYUtil.ConvertPoint(location.getDouble("x"),location.getDouble("y"));
//            createCSV.createCsvFile(location.getDouble("x") + "",location.getDouble("y") + "",p.X + "",p.Y + "");
            //Double X = ConventXYUtil.ConventX(location.getDouble("x"),location.getDouble("y"));
            //Double Y = ConventXYUtil.ConventY(location.getDouble("x"),location.getDouble("y"));
            DecimalFormat decimalFormat = new DecimalFormat("###########0.00000000");//格式化设置
            lm.setX(Double.parseDouble(decimalFormat.format(p.X)));
            lm.setY(Double.parseDouble(decimalFormat.format(p.Y)));
            lm.setZ(BigDecimal.valueOf(location.getInt("z")));
            JSONArray useridList = loc.getJSONArray("userid");
            logger.debug("convertXY : " + Double.parseDouble(decimalFormat.format(p.X)) + " ,"
                    + Double.parseDouble(decimalFormat.format(p.Y)) + "; huaweiXY:" + location.getDouble("x") + "," + location.getDouble("y")
                    + "userId :" + useridList.getString(0));
            // 用户存在多个的情况，目前只取第一个；若用户为空则不作处理
            if(useridList.size() > 0){
                lm.setUserID(useridList.getString(0));
            }else {
                logger.debug("user id is null");
            }
            //数据入库
            svaDataInsert(lm);
        }
    }
    /**
     * @Title: svaDataInsert
     * @Description: 将解析后的数据入库
     * @param lm
     * @return：boolean
     */
    private boolean svaDataInsert(LocationModel lm){
        java.sql.Connection conn = null;
        PreparedStatement prestm = null;
        String sql = null;
        if (queryUserByUserId(lm.getUserID())){
            sql = "update " + tableName +" set idType = ?,timestamp = ?,datatype = ?,x = ?,y = ?,z = ?,updTime = ? where userId = ?";
        } else {
            sql = "insert into " + tableName +" VALUES (?,?,?,?,?,?,?,?)";
        }
        try {
            logger.debug("connect database start (insert)");
            conn = DBUtil.getConn();
            logger.debug("connect database success (insert)");
            prestm  = conn.prepareStatement(sql);
            prestm.setString(1,lm.getIdType());
            prestm.setBigDecimal(2,lm.getTimestamp());
            prestm.setString(3,lm.getDataType());
            prestm.setDouble(4,lm.getX());
            prestm.setDouble(5,lm.getY());
            prestm.setBigDecimal(6,lm.getZ());
            prestm.setString(7,TimeToolsUtil.currrentTime());
            prestm.setString(8,lm.getUserID());
            prestm.executeUpdate();
            logger.debug("insert data success");
        } catch (SQLException e){
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            DBUtil.closeConn(conn);
            if (null != prestm) {
                try {
                    prestm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * @Title: queryUserByUserId
     * @Description: userId在库表中是否存在
     * @param userId
     * @return：boolean
     */
    private boolean queryUserByUserId(String userId){
        boolean flag = false;
        java.sql.Connection conn = null;
        PreparedStatement prestm = null;
        ResultSet rs = null;
        String sql = "select * from " + tableName + " where userId = ?";
        logger.debug("connect database start (select)");
        try {
            conn = DBUtil.getConn();
            logger.debug("connect database success (select)");
            prestm  = conn.prepareStatement(sql);
            prestm.setString(1,userId);
            rs = prestm.executeQuery();
            if (rs.next()){
                flag = true;
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        } finally {
            DBUtil.closeConn(conn);
            try {
                if (prestm != null){
                    prestm.close();
                }
            } catch (SQLException e){
                logger.debug("close dbconnect failed");
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        String json = "";
//        SeekerServer seekerServer = new SeekerServer("10.0.23.25",30002);
//        SeekerServer seekerServer = new SeekerServer("10.0.10.227",8080);
//        SendUDPSocket sendUDPSocket = new SendUDPSocket();
//        sendUDPSocket.createUDPMess("{\"locationstream\":[{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":1699.0,\"y\":816.0,\"z\":3},\"userid\":[\"645ecf24\"],\"map\":{\"mapid\":\"3\"}},{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":893.0,\"y\":1763.0,\"z\":3},\"userid\":[\"645db600\"],\"map\":{\"mapid\":\"3\"}},{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":1013.0,\"y\":1819.0,\"z\":3},\"userid\":[\"64459aff\"],\"map\":{\"mapid\":\"3\"}},{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":1579.0,\"y\":783.0,\"z\":3},\"userid\":[\"0ae09266\"],\"map\":{\"mapid\":\"3\"}},{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":1753.0,\"y\":1480.0,\"z\":3},\"userid\":[\"0adf12b1\"],\"map\":{\"mapid\":\"3\"}},{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":1469.0,\"y\":1253.0,\"z\":3},\"userid\":[\"0ad3dc16\"],\"map\":{\"mapid\":\"3\"}},{\"IdType\":\"IP\",\"Timestamp\":1475984682000,\"datatype\":\"coordinates\",\"location\":{\"x\":1523.0,\"y\":1253.0,\"z\":3},\"userid\":[\"0ad3ca91\",\"0ad2a7c7\"],\"map\":{\"mapid\":\"3\"}}]}");
    }
}

