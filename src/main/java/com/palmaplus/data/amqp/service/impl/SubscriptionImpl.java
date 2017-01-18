package com.palmaplus.data.amqp.service.impl;

import com.palmaplus.data.amqp.common.PropertiesUtil;
import com.palmaplus.data.amqp.service.Amqp;
import com.palmaplus.data.amqp.service.SubscriptService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @ClassName: SubscriptionService
 * @Description: 订阅服务
 * @author jiabing.zhu
 * @date 2016/9/28
 */
public class SubscriptionImpl extends HttpsService implements SubscriptService{
    /**
     * @Fields log ： 输出日志
     */
    private Logger logger = LoggerFactory.getLogger(HttpsService.class);
    private String host = PropertiesUtil.getProperty("server.host");
    private String port = PropertiesUtil.getProperty("server.port");
    private String username = PropertiesUtil.getProperty("server.username");
    private String password = PropertiesUtil.getProperty("server.password");

    @Override
    public void getLocationstreamreg(){
        String url = "https://" + host + ":"
                + port + "/v3/auth/tokens";
        logger.debug("url : " + url);
        // 获取token参数
        String content = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\": {\"user\": {\"domain\": \"Api\",\"name\": \""
                + username
                + "\",\"password\": \""
                + password + "\"}}}}}";
        logger.debug("get token content : " + content);
        String charset = "UTF-8";
        try {
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null);
            String token = tokenResult.get("token");
            logger.debug("token : " + token);
            //非匿名全量数据订阅
            url = "https://" + host + ":" + port + "/enabler/catalog/locationstreamreg/json/v1.0";
            logger.debug("locationstreamreg url : " + url);
            content = "{\"APPID\":\"" + username + "\"}";
            logger.debug("locationstreamreg content : " + content);
            // 获取订阅ID
            Map<String,String> subResult = this.httpsPost(url, content, charset,"POST", tokenResult.get("token"));
            logger.debug("subscription result:" + subResult.get("result"));
            //解析反馈json数据,并获取queue_id
            JSONObject jsonObj = JSONObject.fromObject(subResult.get("result"));
            JSONArray list = jsonObj.getJSONArray("Subscribe Information");
            JSONObject obj = (JSONObject) list.get(0);
            String queueId = obj.getString("QUEUE_ID");
            logger.debug("queueId:" + queueId);
            // 如果获取queueId，则进入数据对接逻辑
            Amqp at = new Amqp();
            at.amqpData(queueId);
        } catch (IOException e){
            e.printStackTrace();
            logger.debug(e.getMessage());
        } catch (NoSuchAlgorithmException e1){
            e1.printStackTrace();
            logger.debug(e1.getMessage());
        } catch (KeyManagementException e2){
            e2.printStackTrace();
            logger.debug(e2.getMessage());
        }
    }

    @Override
    public void cancelRequest(){
        String url = "https://" + host + ":"
                + port + "/v3/auth/tokens";
        logger.debug("get token(cancel request) url : " + url);
        // 获取token参数
        String content = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\": {\"user\": {\"domain\": \"Api\",\"name\": \""
                + username
                + "\",\"password\": \""
                + password + "\"}}}}}";
        logger.debug("get token(cancel request) content : " + content);
        String charset = "UTF-8";
        try {
            Map<String,String> tokenResult = this.httpsPost(url, content, charset,"POST", null);
            String token = tokenResult.get("token");
            logger.debug("cancel request token : " + token);
            url = "https://" + host + ":" + port
                    + "/enabler/catalog/locationstreamunreg/json/v1.0";
            logger.debug("cancel request url : " + url);
            content = "{\"APPID\":\"" + username + "\"}";
            logger.debug("cancel request content : " + content);
            Map<String,String> subResultAnonymous = this.httpsPost(url, content,charset, "DELETE", token);
            logger.debug("[unsubscribe]anonymous result:" + subResultAnonymous.get("result"));
            System.out.println("[unsubscribe]anonymous result:" + subResultAnonymous.get("result"));
        } catch (IOException e){
            e.printStackTrace();
            logger.debug(e.getMessage());
        } catch (NoSuchAlgorithmException e1){
            e1.printStackTrace();
            logger.debug(e1.getMessage());
        } catch (KeyManagementException e2){
            e2.printStackTrace();
            logger.debug(e2.getMessage());
        }

    }
}
