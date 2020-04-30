package com.topjia.music.grasp.controller.rank;

import com.topjia.music.grasp.entity.top.TopGroup;
import com.topjia.music.grasp.service.rank.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author wjh
 * @date 2019-12-11 19:58
 */
@CrossOrigin
@RestController
@RequestMapping("/rank")
public class RankController {
    @Autowired
    private RankService rankService;

    @PostMapping("/toplistinfo")
    public Object TopListInfo() throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        List<TopGroup> topGroups = rankService.getTopListInfo();
        result.put("code", 0);
        result.put("toplist", topGroups);
        return result;
    }

    @PostMapping("/topdetail")
    public Object TopDetail(Integer topId, String period) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> topDetail = rankService.getTopDetail(topId, period);
        result.put("code", 0);
        result.put("topDetail", topDetail);
        return result;
    }
}
