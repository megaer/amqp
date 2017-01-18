package com.palmaplus.data.amqp.mainThread;

import com.palmaplus.data.amqp.service.impl.SubscriptionImpl;

/**
 * Created by jiabing.zhu on 2016/9/28.
 */
public class MainThread {
    public static void main(String[] args) {
        SubscriptionImpl subscription = new SubscriptionImpl();
        subscription.getLocationstreamreg();
//        subscription.cancelRequest();
    }
}
