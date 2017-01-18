package com.palmaplus.data.amqp.service.impl;

import com.palmaplus.data.amqp.common.ByteUtil;
import com.palmaplus.data.amqp.common.Constant;
import com.palmaplus.data.amqp.common.ConventXYUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by jiabing.zhu on 2016/10/19.
 */
public class SendUDPSocket {
    private Charset cs = Charset.forName("UTF-8");

    public void createUDPMess(String mess){
        JSONObject result = JSONObject.fromObject(mess);
        JSONArray list = result.getJSONArray("locationstream");
        ByteBuffer headerByte = getHeader(list);
        ByteBuffer payloadByte = getPayload(list);
        ByteBuffer secretKeyByte = getSecretKey();
        int signature = getSignature(headerByte,payloadByte,secretKeyByte);
        send(headerByte,payloadByte,signature);
    }
    private void send(ByteBuffer headerByte,ByteBuffer payloadByte,int sign){
        ByteBuffer packageBuffer = ByteBuffer.allocate(8192);
        packageBuffer.put(ByteUtil.fromBytebuffer(headerByte)).put(ByteUtil.fromBytebuffer(payloadByte)).putInt(sign);
        byte[] pByte = ByteUtil.fromBytebuffer(packageBuffer);
        int server_port = 30002;
        String ip = "10.0.23.25";
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
            InetAddress hostIP = null;
            hostIP = InetAddress.getByName(ip);
            DatagramPacket p = new DatagramPacket(pByte,pByte.length,hostIP,server_port);
            s.send(p);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ByteBuffer getHeader(JSONArray list){
        long timestamp = System.currentTimeMillis();
        ByteBuffer headerBuffer = ByteBuffer.allocate(2048);

        headerBuffer.putShort(Constant.MAGICNUMBER);
        headerBuffer.put(Constant.VERSION);
        headerBuffer.putLong(Constant.TOKEN);
        headerBuffer.putLong(timestamp);

        byte locationEngineLen = (byte)Constant.LOCATIONENGINE.length();
        headerBuffer.put(locationEngineLen);
        headerBuffer.put(Constant.LOCATIONENGINE.getBytes(cs));

        byte countByte = (byte)list.size();
        headerBuffer.put(countByte);

        return headerBuffer;
    }

    private ByteBuffer getPayload(JSONArray list){
        ByteBuffer payloadBuffer = ByteBuffer.allocate(4096);
        for(int i = 0; i < list.size(); i++){
            JSONObject loc = list.getJSONObject(i);
            JSONObject location = loc.getJSONObject("location");
            Double X = ConventXYUtil.ConventX(location.getDouble("x"),location.getDouble("y"));
            Double Y = ConventXYUtil.ConventY(location.getDouble("x"),location.getDouble("y"));
            JSONArray useridList = loc.getJSONArray("userid");
            byte clienDataLen = (byte)useridList.getString(0).length();
            payloadBuffer.put(Constant.CLIENTTYPE);
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
    }
}
