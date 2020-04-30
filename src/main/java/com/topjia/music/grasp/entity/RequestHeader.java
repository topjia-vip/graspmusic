package com.topjia.music.grasp.entity;

import lombok.Data;

/**
 * 设置请求头的pojo
 *
 * @author wjh
 * @date 2019-11-20 17:48
 */
@Data
public class RequestHeader {
    private String Host;
    private String Referer;

    public RequestHeader(String host, String referer) {
        Host = host;
        Referer = referer;
    }

    public RequestHeader() {
    }
}
