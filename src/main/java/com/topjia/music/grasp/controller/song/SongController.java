package com.topjia.music.grasp.controller.song;

import com.topjia.music.grasp.entity.Lyric;
import com.topjia.music.grasp.service.song.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author wjh
 * @date 2020-01-04 14:12
 */
@CrossOrigin
@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    private SongService songService;

    /**
     * 获取歌曲播放链接
     *
     * @param songmid 歌曲的mid
     */
    @PostMapping("/getsongpurl")
    public Object getSongPurl(String songmid) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        String songPlayUrl = songService.getSongPurl(songmid);
        result.put("code", 0);
        result.put("songPlayUrl", songPlayUrl);
        return result;
    }

    /**
     * 获取qq音乐歌词
     *
     * @return 返回结果为json数据
     */
    @PostMapping(value = "/getLyric")
    public Object getLyric(String songmid) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Lyric lyric = songService.getLyric(songmid);
        result.put("code", 0);
        result.put("data", lyric);
        return result;
    }

    /**
     * 获取歌曲详情信息
     *
     * @param songId  歌曲id
     * @param songMid 歌曲mid
     * @return
     */
    @PostMapping("/songdetail")
    public Object getSongDetail(String songId, String songMid) throws Exception {
        Map<String, Object> result = new HashMap<>();
        HashMap<String, Object> songDetail = songService.getSongDetail(songId, songMid);
        result.put("code", 0);
        result.put("songInfo", songDetail);
        return result;
    }

    /**
     * 获取歌词
     */
    @PostMapping("/songdetaillyric")
    public Object getSongDetailLyric(String songId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<String> lyricList = songService.getSongDetailLyric(songId);
        result.put("code", 0);
        result.put("lyric", lyricList);
        return result;
    }
}
