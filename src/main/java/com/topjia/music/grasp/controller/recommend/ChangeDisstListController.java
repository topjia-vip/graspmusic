package com.topjia.music.grasp.controller.recommend;

import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.service.recommend.ChangeDisstListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 切换歌单分类
 *
 * @author wjh
 * @date 2019-12-08 18:37
 */
@CrossOrigin
@RestController
@RequestMapping("/recommend")
public class ChangeDisstListController {
    @Autowired
    private ChangeDisstListService changeDisstListService;

    @PostMapping("/changeDisstList")
    public Object changeDisstList(Integer categoryId) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        List<Disst> dissts = changeDisstListService.changeDisstList(categoryId);
        result.put("code", 0);
        result.put("data", dissts);
        return result;
    }
}
