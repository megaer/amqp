package com.palmaplus.data.amqp.model;

import java.math.BigDecimal;

/**
 * Created by jiabing.zhu on 2016/9/28.
 */
public class LocationModel {
    private String idType;
    private BigDecimal timestamp;
    private String dataType;
    private Double x;
    private Double y;
    private BigDecimal z;
    private String userID;

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getX() {

        return x;
    }

    public Double getY() {
        return y;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setTimestamp(BigDecimal timestamp) {
        this.timestamp = timestamp;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setZ(BigDecimal z) {
        this.z = z;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIdType() {

        return idType;
    }

    public BigDecimal getTimestamp() {
        return timestamp;
    }

    public String getDataType() {
        return dataType;
    }

    public BigDecimal getZ() {
        return z;
    }

    public String getUserID() {
        return userID;
    }
}
