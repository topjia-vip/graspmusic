package com.topjia.music.grasp.entity.song;

import com.topjia.music.grasp.entity.singer.Singer;
import lombok.Data;

import java.util.List;

/**
 * @author wjh
 * @date 2019-11-30 20:21
 */
@Data
public class Song {
    private String id;
    private String mid;
    private List<Singer> singers;
    private String name;
    private String album;
    private String duration;
    private String image = "https://y.gtimg.cn/mediastyle/global/img/playlist_300.png?max_age=31536000";
    private String playUrl;
    private String title;
    private String subTitle;
    private List<SongComment> comments;
}
