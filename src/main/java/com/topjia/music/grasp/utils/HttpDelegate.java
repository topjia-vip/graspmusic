package com.topjia.music.grasp.utils;

import com.alibaba.fastjson.JSONObject;
import com.topjia.music.grasp.entity.RequestHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author wjh
 * @date 2019-11-19 19:12
 */
@Slf4j
public class HttpDelegate {
    /**
     * 返回成功状态码
     */
    private static final int SUCCESS_CODE = 200;

    /**
     * 发送GET请求
     *
     * @param url               请求url
     * @param nameValuePairList 请求参数
     * @return JSON或者字符串
     * @throws Exception
     */
    public static Object sendGet(String url, List<NameValuePair> nameValuePairList, RequestHeader header) throws Exception {
        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            /**
             * 创建HttpClient对象
             */
            client = HttpClients.createDefault();
            /**
             * 创建URIBuilder
             */
            URIBuilder uriBuilder = new URIBuilder(url);
            /**
             * 设置参数
             */
            uriBuilder.addParameters(nameValuePairList);
            /**
             * 创建HttpGet
             */
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            /**
             * 设置请求头部编码
             */
            httpGet.addHeader("Accept", "*/*");
            httpGet.addHeader("Cache-Control", "no-cache");
            /**
             * 是否需要设置请求头
             */
            if (header != null) {
                httpGet.addHeader("Referer", header.getReferer());
                httpGet.addHeader("Host", header.getHost());
            }
            /**
             * 设置返回编码
             */
            /**
             * 请求服务
             */
            response = client.execute(httpGet);
            /**
             * 获取响应吗
             */
            int statusCode = response.getStatusLine().getStatusCode();

            if (SUCCESS_CODE == statusCode) {
                /**
                 * 获取返回对象
                 */
                HttpEntity entity = response.getEntity();
                /**
                 * 通过EntityUitls获取返回内容
                 */
                String result = EntityUtils.toString(entity, "UTF-8");
                /**
                 * 转换成json,根据合法性返回json或者字符串
                 */
                try {
                    jsonObject = JSONObject.parseObject(result);
                    return jsonObject;
                } catch (Exception e) {
                    return result;
                }
            } else {
                log.error("HttpClientService-line: {}, errorMsg{}", 97, "GET请求失败！");
            }
        } catch (Exception e) {
            log.error("HttpClientService-line: {}, Exception: {}", 100, e);
        } finally {
            response.close();
            client.close();
        }
        return null;
    }

    /**
     * 组织请求参数{参数名和参数值下标保持一致}
     *
     * @param params 参数名数组
     * @param values 参数值数组
     * @return 参数对象
     */
    public static List<NameValuePair> getParams(Object[] params, Object[] values) {
        /**
         * 校验参数合法性
         */
        boolean flag = params.length > 0 && values.length > 0 && params.length == values.length;
        if (flag) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            for (int i = 0; i < params.length; i++) {
                nameValuePairList.add(new BasicNameValuePair(params[i].toString(), values[i].toString()));
            }
            return nameValuePairList;
        } else {
            log.error("HttpClientService-line: {}, errorMsg：{}", 197, "请求参数为空且参数长度不一致");
        }
        return null;
    }

    public static String getHTML(String url) throws IOException {
        // 获取http客户端
        CloseableHttpClient client = HttpClients.createDefault();
        // 通过httpget方式来实现我们的get请求
        HttpGet httpGet = new HttpGet(url);
        // 通过client调用execute方法，得到我们的执行结果就是一个response，所有的数据都封装在response里面了
        CloseableHttpResponse Response = client.execute(httpGet);
        // HttpEntity
        // 是一个中间的桥梁，在httpClient里面，是连接我们的请求与响应的一个中间桥梁，所有的请求参数都是通过HttpEntity携带过去的
        // 所有的响应的数据，也全部都是封装在HttpEntity里面
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String HTML = EntityUtils.toString(entity, "UTF-8");
        // 关闭
        Response.close();
        return HTML;
    }
}
