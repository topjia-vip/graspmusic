package com.topjia.music.grasp.service.disst;

import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.entity.disst_category_group.DisstCategoryGroup;
import com.topjia.music.grasp.entity.song.Song;

import java.util.List;
import java.util.Map;

/**
 * @author wjh
 * @date 2019-12-19 17:05
 */
public interface DisstService {
    List<DisstCategoryGroup> getDisstCategoryGroup() throws Exception;

    Map<String, Object> getDisstList(Integer categoryId, Integer sortId) throws Exception;

    List<Song> getDisstSongList(String disstId) throws Exception;

    Disst getDisstDetail(String disstId) throws Exception;
}
