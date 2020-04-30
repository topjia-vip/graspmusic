package com.topjia.music.grasp.entity.singer;

import lombok.Data;

/**
 * 歌手实体类
 *
 * @author wjh
 * @date 2019-11-30 19:51
 */
@Data
public class Singer {
    private Integer singerId;
    private String SingerMid;
    private String SingerName;
    private String SingerPic = "https://y.gtimg.cn/mediastyle/global/img/singer_300.png?max_age=2592000";
}
