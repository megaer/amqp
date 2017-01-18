package com.palmaplus.data.amqp.service.impl;

import com.palmaplus.data.amqp.common.ByteUtil;
import com.palmaplus.data.amqp.common.Constant;
import com.palmaplus.data.amqp.common.ConventXYUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * Created by jiabing.zhu on 2016/8/29.
 */
public class SeekerServer {
   /* private int port = 30002;
    private String ip = "10.0.23.25";
    private Charset cs = Charset.forName("UTF-8");
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);

    private Logger logger = LoggerFactory.getLogger(SeekerServer.class);
    private static Selector selector;

    public SeekerServer(String ip,int port){
        this.port = port;
        this.ip = ip;
        System.out.println(ip + ":" + port);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void init() throws IOException{
        // 打开服务器套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 服务器配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 检索与此通道关联的服务器套接字
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 进行服务的绑定
        serverSocket.bind(new InetSocketAddress(ip,port));
        serverSocket.bind(new InetSocketAddress(ip,port));

        // 通过open()方法找到Selector
        selector = Selector.open();
        // 注册到selector，等待连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("socketServer start on port:" + port);
    }

    public void listen(String mess){
        while (true) {
            try {
                // 选择一组键，并且相应的通道已经打开
                selector.select();
                // 返回此选择器的已选择键集。
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for(SelectionKey key : selectionKeys){
                    handle(key,mess);
                }
                selectionKeys.clear();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
    }
    private void handle(SelectionKey selectionKey,String mess) throws IOException, InterruptedException {
        // 接受请求
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count=0;
        // 测试此键的通道是否已准备好接受新的套接字连接。
        if (selectionKey.isAcceptable()) {
            // 返回为之创建此键的通道。
            server = (ServerSocketChannel) selectionKey.channel();
            // 接受到此通道套接字的连接。
            // 此方法返回的套接字通道（如果有）将处于阻塞模式。
            client = server.accept();
            // 配置为非阻塞
            client.configureBlocking(false);
            // 注册到selector，等待连接
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            // 返回为之创建此键的通道。
            client = (SocketChannel) selectionKey.channel();
            //将缓冲区清空以备下次读取
            rBuffer.clear();
            //读取服务器发送来的数据到缓冲区中
            count = client.read(rBuffer);
            if (count > 0) {
                receiveText = new String( rBuffer.array(),0,count);
                System.out.println("服务器端接受客户端数据--:"+receiveText);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            //将缓冲区清空以备下次写入
            sBuffer.clear();
            // 返回为之创建此键的通道。
            client = (SocketChannel) selectionKey.channel();
//            sendText="message from server--";
            JSONObject result = JSONObject.fromObject(mess);
            JSONArray list = result.getJSONArray("locationstream");
            ByteBuffer headerByte = getHeader(list);
            ByteBuffer payloadByte = getPayload(list);
            ByteBuffer secretKeyByte = getSecretKey();
            int signature = getSignature(headerByte,payloadByte,secretKeyByte);
            //向缓冲区中输入数据
//            sBuffer.putInt(signature);
            sBuffer.putInt(111111);
//            sBuffer.put((signature + "").getBytes(cs));
            //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
            sBuffer.flip();
            //输出到通道
            client.write(sBuffer);
//            System.out.println("服务器端向客户端发送数据--："+sendText);
            client.register(selector, SelectionKey.OP_READ);
        }
    }*/

    /*private ByteBuffer getHeader(JSONArray list){
        long timestamp = System.currentTimeMillis();
        ByteBuffer headerBuffer = ByteBuffer.allocate(2048);

        byte[] magicNumber = ByteUtil.hexString2Bytes(Constant.MAGICNUMBER);
        headerBuffer.put(magicNumber);

        byte[] version = ByteUtil.hexString2Bytes(Constant.VERSION);
        headerBuffer.put(version);
        headerBuffer.putLong(Constant.TOKEN);
        headerBuffer.putLong(timestamp);

        byte[] locationEngineLen = new byte[1];
        locationEngineLen[0] = ByteUtil.intToBytes(Constant.LOCATIONENGINE.length())[3];
        headerBuffer.put(locationEngineLen);
        headerBuffer.put(Constant.LOCATIONENGINE.getBytes(cs));

        byte[] countByte = new byte[1];
        countByte[0] = ByteUtil.intToBytes(list.size())[3];
        headerBuffer.put(countByte);

        return headerBuffer;
    }

    private ByteBuffer getPayload(JSONArray list){
        ByteBuffer payloadBuffer = ByteBuffer.allocate(3072);
        for(int i = 0; i < list.size(); i++){
            JSONObject loc = list.getJSONObject(i);
            JSONObject location = loc.getJSONObject("location");
            Double X = ConventXYUtil.ConventX(location.getDouble("x"),location.getDouble("y"));
            Double Y = ConventXYUtil.ConventY(location.getDouble("x"),location.getDouble("y"));
            JSONArray useridList = loc.getJSONArray("userid");
            byte[] clienDataLen = new byte[1];
            clienDataLen[0] = ByteUtil.intToBytes(useridList.getString(0).length())[3];
            payloadBuffer.put(Constant.CLIENT.getBytes(cs));
            payloadBuffer.put(clienDataLen);
            payloadBuffer.put(useridList.getString(0).getBytes(cs));
            payloadBuffer.putDouble(X);
            payloadBuffer.putDouble(Y);
            payloadBuffer.putInt(location.getInt("z"));
        }
        return payloadBuffer;
    }

    private ByteBuffer getSecretKey(){
        ByteBuffer secretKeyBuffer = ByteBuffer.allocate(1024);
        byte[] secretKeyByte = Constant.SECRET_KEY.getBytes(cs);
        secretKeyBuffer.put(secretKeyByte);
        return secretKeyBuffer;
    }

    private int getSignature(ByteBuffer hBuffer,ByteBuffer pBuffer,ByteBuffer secretKeyBuffer){
        byte[] hByte = ByteUtil.fromBytebuffer(hBuffer);
        byte[] pByte = ByteUtil.fromBytebuffer(pBuffer);
        byte[] sByte = ByteUtil.fromBytebuffer(secretKeyBuffer);
        return ByteUtil.crc32(hByte,pByte,sByte);
    }*/

/*    public static void main(String[] args) throws IOException {
        SeekerServer server = new SeekerServer(8080);
        server.listen("test");
    }*/
}
