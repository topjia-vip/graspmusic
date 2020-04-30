package com.topjia.music.grasp.entity.disst_category_group;

import lombok.Data;

import java.util.List;

/**
 * @author wjh
 * @date 2019-12-19 16:54
 */
@Data
public class DisstCategoryGroup {
    private String categoryGroupName;
    private List<DisstCategory> categories;
}
