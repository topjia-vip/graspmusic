package com.topjia.music.grasp.utils;

import com.topjia.music.grasp.entity.song.Song;

import java.util.List;

/**
 * @author wjh
 * @date 2019-11-30 21:18
 */
public class SongMidUtil {
    public static String getSongMids(List<Song> songs) {
        String songmids = "[";
        for (int i = 0; i < songs.size(); i++) {
            if (i != songs.size() - 1) {
                songmids += "\"" + songs.get(i).getMid() + "\",";
            } else {
                songmids += "\"" + songs.get(i).getMid() + "\"]";
            }
        }
        return songmids;
    }
}
