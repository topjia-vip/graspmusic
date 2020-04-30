package com.topjia.music.grasp.base;

/**
 * @author wjh
 * @date 2019-12-01 20:33
 */
public class RedisExpirationDate {
    /**
     * 推荐轮播图redis过期时间12小时
     */
    public static long RECOMMEND_PIC_TIME = 6;

    /**
     * 歌手列表redis过期时间24小时
     */
    public static long SINGER_LIST_TIME = 24;

    /**
     * 排行榜列表redis过期时间12小时
     */
    public static long TOP_LIST_TIME = 6;

    /**
     * 推荐歌单列表redis过期时间12小时
     */
    public static long DISC_LIST_TIME = 6;

    /**
     * 搜索热词redis过期时间1小时
     */
    public static long HOT_KEY_TIME = 1;

    /**
     * 歌曲redis过期时间30分钟，防止vkey过期歌曲无法播放
     */
    public static long SONG_TIME = 30;
}
