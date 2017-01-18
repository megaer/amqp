package com.palmaplus.data.amqp.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * Created by jiabing.zhu on 2016/10/19.
 */
public class ByteUtil {
    /**
     * 十六进制 转换 byte[]
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexString2Bytes(String hexStr) {
        if (hexStr == null)
            return null;
        if (hexStr.length() % 2 != 0) {
            hexStr += "0";
        }
        byte[] data = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            char hc = hexStr.charAt(2 * i);
            char lc = hexStr.charAt(2 * i + 1);
            byte hb = hexChar2Byte(hc);
            byte lb = hexChar2Byte(lc);
            if (hb < 0 || lb < 0) {
                return null;
            }
            int n = hb << 4;
            data[i] = (byte) (n + lb);
        }
        return data;
    }

    public static byte hexChar2Byte(char c) {
        if (c >= '0' && c <= '9')
            return (byte) (c - '0');
        if (c >= 'a' && c <= 'f')
            return (byte) (c - 'a' + 10);
        if (c >= 'A' && c <= 'F')
            return (byte) (c - 'A' + 10);
        return -1;
    }

    /**
     * byte[] 转 16进制字符串
     *
     * @param arr
     * @return
     */
    public static String bytes2HexString(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        for (byte b : arr) {
            String tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() < 2)
                tmp = "0" + tmp;
            sbd.append(tmp);
        }
        return sbd.toString().toUpperCase();
    }

    public static String bytes2HexStringWithSpace(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        for (byte b : arr) {
            String tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() < 2)
                tmp = "0" + tmp;
            sbd.append(tmp);
            sbd.append(" ");
        }
        return sbd.toString();
    }

    static public String getBCDString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return ByteUtil.bytes2HexString(t);
    }

    static public String getHexString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return ByteUtil.bytes2HexStringWithSpace(t);
    }
    /**
     * int转byte数组[]
     *
     * @param source long值
     * @param len 数组长度
     * @return
     */
    public static byte[] toByteArray(long source, int len) {
        byte[] bLocalArr = new byte[len];
        for (int i = 0; (i < 8) && (i < len); i++) {
            bLocalArr[len - 1 - i] = (byte) (source >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }
    /**
     * byte[]转int
     *
     * @param bRefArr
     * @return
     */
    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    /**
     * String转为ASCII字符
     * 0  --  48
     * 1  --  49
     * 以此类推，如2016返回的则是 50484954
     * @param value
     * @return
     */
    public static String string2Ascii(String value){
        if (null == value) return null;
        StringBuffer sbu = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            sbu.append((int)value.toCharArray()[i]);
        }
        return sbu.toString();
    }

    /**
     * String转为ASCII字符
     * 0  --  48
     * 1  --  49
     * 以此类推，如2016返回的则是 50484954
     * @param value
     * @return
     */
    public static String string2AsciiHex(String value,int len){
        if (null == value) return null;
        StringBuffer sbu = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            int x =(int)value.toCharArray()[i];
            sbu.append(Integer.toHexString(x));
        }
        for (int i = 0; i < len-value.length(); i++) {
            sbu.append("00");
        }
        return sbu.toString();
    }

    public static byte[] ascii2Bcd(String ascii) {
        if (ascii == null)
            return null;
        if ((ascii.length() & 0x01) == 1)
            ascii = "0" + ascii;
        byte[] asc = ascii.getBytes();
        byte[] bcd = new byte[ascii.length() >> 1];
        for (int i = 0; i < bcd.length; i++) {
            bcd[i] = (byte)(hex2byte((char)asc[2 * i]) << 4 | hex2byte((char)asc[2 * i + 1]));
        }
        return bcd;
    }

    /**
     * 快速预授权，右靠
     * @param ascii
     * @return
     */
    public static byte[] ascii2BcdRight(String ascii) {
        if (ascii == null)
            return null;
        if ((ascii.length() & 0x01) == 1)
            ascii = ascii+"0";
        byte[] asc = ascii.getBytes();
        byte[] bcd = new byte[ascii.length() >> 1];
        for (int i = 0; i < bcd.length; i++) {
            bcd[i] = (byte)(hex2byte((char)asc[2 * i]) << 4 | hex2byte((char)asc[2 * i + 1]));
        }
        return bcd;
    }

    public static String bcd2Ascii(final byte[] bcd) {
        if (bcd == null)
            return "";
        StringBuilder sb = new StringBuilder(bcd.length << 1);
        for (byte ch : bcd) {
            // bcd码无负数，负数高位补1不正确，应该无符号拓展
            // >>>只对32bit和64bit有效？changed by eric 20160331
            byte half = (byte) ((ch >> 4) & 0x0f);
            sb.append((char)(half + ((half > 9) ? ('A' - 10) : '0')));
            half = (byte) (ch & 0x0f);
            sb.append((char)(half + ((half > 9) ? ('A' - 10) : '0')));
        }
        return sb.toString();
    }

    public static byte hex2byte(char hex) {
        if (hex <= 'f' && hex >= 'a') {
            return (byte) (hex - 'a' + 10);
        }

        if (hex <= 'F' && hex >= 'A') {
            return (byte) (hex - 'A' + 10);
        }

        if (hex <= '9' && hex >= '0') {
            return (byte) (hex - '0');
        }

        return 0;
    }

    public static int bytes2Int(byte[] data) {
        if (data == null || data.length == 0) {
            return 0;
        }

        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total += (data[i] & 0xff) << (data.length - i - 1) * 8;
        }
        return total;
    }
    public static byte[] toBytes(String data, String charsetName) {
        try {
            return data.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] toBytes(String data) {
        return toBytes(data, "ISO-8859-1");
    }

    public static byte[] toGBK(String data) {
        return toBytes(data, "GBK");
    }

    public static byte[] toGB2312(String data) {
        return toBytes(data, "GB2312");
    }

    public static byte[] toUtf8(String data) {
        return toBytes(data, "UTF-8");
    }

    public static String fromBytes(byte[] data, String charsetName) {
        try {
            if (data == null) {
                return "null";
            }
            return new String(data, charsetName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String fromBytes(byte[] data) {
        return fromBytes(data, "ISO-8859-1");
    }

    public static String fromGBK(byte[] data) {
        return fromBytes(data, "GBK");
    }

    public static String fromGB2312(byte[] data) {
        return fromBytes(data, "GB2312");
    }

    public static String fromUtf8(byte[] data) {
        return fromBytes(data, "UTF-8");
    }

    /**
     * 将一个2位字节数组转换为char字符。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b
     *            字节数组
     * @return char字符
     */
    public static char bytesToChar(byte[] b) {
        char c = (char) ((b[0] << 8) & 0xFF00L);
        c |= (char) (b[1] & 0xFFL);
        return c;
    }

    /**
     * 将一个8位字节数组转换为双精度浮点数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b
     *            字节数组
     * @return 双精度浮点数
     */
    public static double bytesToDouble(byte[] b) {
        return Double.longBitsToDouble(bytesToLong(b));
    }

    /**
     * 将一个4位字节数组转换为浮点数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b
     *            字节数组
     * @return 浮点数
     */
    public static float bytesToFloat(byte[] b) {
        return Float.intBitsToFloat(bytesToInt(b));
    }

    /**
     * 将一个4位字节数组转换为4整数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b
     *            字节数组
     * @return 整数
     */
    public static int bytesToInt(byte[] b) {
        int i = (b[0] << 24) & 0xFF000000;
        i |= (b[1] << 16) & 0xFF0000;
        i |= (b[2] << 8) & 0xFF00;
        i |= b[3] & 0xFF;
        return i;
    }

    /**
     * 将一个8位字节数组转换为长整数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b
     *            字节数组
     * @return 长整数
     */
    public static long bytesToLong(byte[] b) {
        long l = ((long) b[0] << 56) & 0xFF00000000000000L;
        // 如果不强制转换为long，那么默认会当作int，导致最高32位丢失
        l |= ((long) b[1] << 48) & 0xFF000000000000L;
        l |= ((long) b[2] << 40) & 0xFF0000000000L;
        l |= ((long) b[3] << 32) & 0xFF00000000L;
        l |= ((long) b[4] << 24) & 0xFF000000L;
        l |= ((long) b[5] << 16) & 0xFF0000L;
        l |= ((long) b[6] << 8) & 0xFF00L;
        l |= (long) b[7] & 0xFFL;
        return l;
    }

    /**
     * 将一个char字符转换位字节数组（2个字节），b[0]存储高位字符，大端
     *
     * @param c
     *            字符（java char 2个字节）
     * @return 代表字符的字节数组
     */
    public static byte[] charToBytes(char c) {
        byte[] b = new byte[8];
        b[0] = (byte) (c >>> 8);
        b[1] = (byte) c;
        return b;
    }

    /**
     * 将一个双精度浮点数转换位字节数组（8个字节），b[0]存储高位字符，大端
     *
     * @param d
     *            双精度浮点数
     * @return 代表双精度浮点数的字节数组
     */
    public static byte[] doubleToBytes(double d) {
        return longToBytes(Double.doubleToLongBits(d));
    }

    /**
     * 将一个浮点数转换为字节数组（4个字节），b[0]存储高位字符，大端
     *
     * @param f
     *            浮点数
     * @return 代表浮点数的字节数组
     */
    public static byte[] floatToBytes(float f) {
        return intToBytes(Float.floatToIntBits(f));
    }

    /**
     * 将一个整数转换位字节数组(4个字节)，b[0]存储高位字符，大端
     *
     * @param i
     *            整数
     * @return 代表整数的字节数组
     */
    public static byte[] intToBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i >>> 24);
        b[1] = (byte) (i >>> 16);
        b[2] = (byte) (i >>> 8);
        b[3] = (byte) i;
        return b;
    }
    /**
     * 将一个整数转换位字节数组(4个字节)，b[0]存储高位字符，大端
     *
     * @param i
     *            整数
     * @return 代表整数的字节数组
     */
    public static byte[] intToBytes(int i,int len) {
        byte[] b = new byte[4];
        b[0] = (byte) (i >>> 24);
        b[1] = (byte) (i >>> 16);
        b[2] = (byte) (i >>> 8);
        b[3] = (byte) i;

        byte[] bb = new byte[len];
        for (int ii = 0;ii<len;ii++)
            bb[ii]=b[ii];

        return bb;
    }

    /**
     * 将一个长整数转换位字节数组(8个字节)，b[0]存储高位字符，大端
     *
     * @param l
     *            长整数
     * @return 代表长整数的字节数组
     */
    public static byte[] longToBytes(long l) {
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) (l);
        return b;
    }
    /*
    连接两个byte数组
     */
    public  static byte[] arraycat(byte[] buf1,byte[] buf2)
    {
        byte[] bufret=null;
        int len1=0;
        int len2=0;
        if(buf1!=null)
            len1=buf1.length;
        if(buf2!=null)
            len2=buf2.length;
        if(len1+len2>0)
            bufret=new byte[len1+len2];
        if(len1>0)
            System.arraycopy(buf1,0,bufret,0,len1);
        if(len2>0)
            System.arraycopy(buf2,0,bufret,len1,len2);
        return bufret;
    }

    public static int crc32(byte[]... bytes) {
        CRC32 crc32 = new CRC32();
        for (byte[] aByte : bytes){
            crc32.update(aByte);
        }
        return (int)crc32.getValue();
    }

    public static byte[] fromBytebuffer(ByteBuffer buffer){
        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        buffer.limit(buffer.capacity());
        return bytes;
    }
}
