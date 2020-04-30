package com.topjia.music.grasp.controller.recommend;

import com.topjia.music.grasp.service.recommend.RecommendBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 处理进入推荐页发的请求
 *
 * @author wjh
 * @date 2019-12-08 14:24
 */
@CrossOrigin
@RestController
@RequestMapping("/recommend")
public class RecommendBaseController {
    @Autowired
    private RecommendBaseService recommendBaseService;

    @PostMapping("/getRecommendBase")
    public Object GetRecommendBase() throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Object res = recommendBaseService.GetRecommendBase();
        result.put("code", 0);
        result.put("data", res);
        return result;
    }
}
