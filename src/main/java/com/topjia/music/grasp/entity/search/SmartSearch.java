package com.topjia.music.grasp.entity.search;

import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.song.Song;
import lombok.Data;

import java.util.List;

/**
 * @author wjh
 * @date 2019-12-20 14:58
 */
@Data
public class SmartSearch {
    private List<Singer> singers;
    private List<Song> songs;
}
