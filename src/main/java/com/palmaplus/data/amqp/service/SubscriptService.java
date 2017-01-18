package com.palmaplus.data.amqp.service;

/**
 * Created by jiabing.zhu on 2016/10/8.
 */
public interface SubscriptService {
    /**
     * @Description: 订阅服务
     */
    void getLocationstreamreg();

    /**
     * @Description 取消订阅
     */
    void cancelRequest();
}
