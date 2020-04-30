package com.topjia.music.grasp.entity.album;

import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.song.Song;
import lombok.Data;

import java.util.List;

/**
 * @author wjh
 * @date 2020-01-07 15:34
 */
@Data
public class Album {
    private Integer id;
    private String mid;
    private String name;
    private String company;
    private String lan;
    private String desc;
    private String pubTime;
    private String genre;
    private String image;
    private List<Song> songs;
    private List<Singer> singers;
    private Integer currentSongNum;
}
