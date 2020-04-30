package com.topjia.music.grasp.service.song;

import com.topjia.music.grasp.entity.Lyric;

import java.util.HashMap;
import java.util.List;

/**
 * @author wjh
 * @date 2020-01-04 14:14
 */
public interface SongService {
    String getSongPurl(String songMid) throws Exception;

    Lyric getLyric(String songmid) throws Exception;

    HashMap<String, Object> getSongDetail(String songId, String songMid) throws Exception;

    List<String> getSongDetailLyric(String songId) throws Exception;
}
