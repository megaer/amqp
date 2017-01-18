import com.palmaplus.data.amqp.common.DBUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jiabing.zhu on 2016/9/29.
 */
public class ImageOperator {
    // 将图片插入数据库
     public static void readImage2DB() {
         String path = "D:/ICSData/H2&ICS_Data/161020内容需求清单/image/image4_8/office@2x.png";
         Connection conn = null;
         PreparedStatement ps = null;
         FileInputStream in = null;

         try {
             in = ImageUtil.readImage(path);
             conn = com.palmaplus.data.amqp.common.DBUtil.getConn();
             String sql = "insert into webchat values (?,?,?,?)";
//             String sql = "update poiinfo set viceImg_3 = ?,viceImg_4 = ?,viceImg_5 = ?,viceImg_6 = ?,viceImg_7 = ? where location = ?";
             ps = conn.prepareStatement(sql);

             ps.setString(1,"华为ICS包括交付、开发、MKT、销售、产品等各领域在内一支经验丰富的专家队伍，总人数超过300人。在全球标志性建筑的网络建设中，积累并固化了丰富的规划设计以及工程实施经验，为着一个共同的目标“构建全联接的室内生活”而奋斗");
             ps.setBinaryStream(2,in,in.available());
             ps.setString(3,"ICS办公区");
             ps.setString(4,"ICS办公区");

             int count = ps.executeUpdate();
             if (count > 0) {
                 System.out.println("插入成功！");
             } else {
                 System.out.println("插入失败！");
             }
         } catch (IOException e) {
            e.printStackTrace();
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             DBUtil.closeConn(conn);
             if (null != ps) {
                 try {
                     ps.close();
                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
             }
         }

     }

    // 读取数据库中图片
    public static void readDB2Image() {
        String targetPath = "D:/ICSData/H2&ICS_Data/image/kingStrong.png";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConn();
            String sql = "select * from photoinfo where updtime = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1,1477021290000L);
//            ps.setString(1,"kingStrong");
            rs = ps.executeQuery();
            while (rs.next()) {
                InputStream in = rs.getBinaryStream("photo");
                ImageUtil.readBin2Image(in, targetPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConn(conn);
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
     //测试
     public static void main(String[] args) {
         readImage2DB();
//         readDB2Image();
     }
}
