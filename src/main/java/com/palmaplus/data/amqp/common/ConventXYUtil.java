package com.palmaplus.data.amqp.common;

/**
 * Created by jiabing.zhu on 2016/10/17.
 */

public class ConventXYUtil {
    //深圳坐标原点
//    private double X0 = 12697081.7666;
//    private double Y0 = 2588845.8546;
    //深圳坐标转化计算公式
    // X=X0+x;
    //Ya=Y0+y;

    //无锡坐标转化计算公式
    //X=X0+x*cos9.5897°-y*sin9.5897°
    //Y=Y0+y*cos9.5897°+x*sin9.5897°

    //无锡坐标原点
    private static double X0 = 13391951.611;
    private static double Y0 = 3696168.288;

    public static Point ConvertPoint(Double x,Double y){
        double len = Math.sqrt(x * x + y * y);
        double alpha = Math.asin(y / len);
        len = len / 10;
        double beta = Math.toRadians(9.5897);
        Point p = new Point();
        p.X = Math.cos(alpha + beta) * len + X0;
        p.Y = Math.sin(alpha + beta) * len + Y0;
        return p;
    }

    public static Double ConventX(Double x,Double y){
        double degrees = 9.5897;
        double radians = Math.toRadians(degrees);
        Double X = X0 + (x/10)*(Math.cos(radians)) - (y/10)*(Math.sin(radians));
        return X;
    }
    public static Double ConventY(Double x,Double y){
        double degrees = 9.5897;
        double radians = Math.toRadians(degrees);
        Double Y = Y0 + (y/10)*(Math.cos(radians)) + (x/10)*(Math.sin(degrees));
        return Y;
    }

   /* public static void main(String[] args) {
        Point p = ConvertPoint(1777.0D,1597.0D);
        System.out.println("X :" + p.X);
        System.out.println("Y :" + p.Y);
    }*/
}
