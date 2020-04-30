package com.topjia.music.grasp.controller.singer;

import com.topjia.music.grasp.service.singer.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author wjh
 * @date 2019-12-10 20:47
 */
@CrossOrigin
@RestController
@RequestMapping("/singer")
public class SingerController {
    @Autowired
    private SingerService singerService;

    /**
     * "area":-100,"sex":-100,"genre":-100,"index":-100,"sin":240,"cur_page":4
     */
    @PostMapping("/getSingerList")
    public Object getSingerList(Integer area, Integer sex, Integer genre, Integer index, Integer sin, Integer cur_page) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, Object> singerList = singerService.getSingerList(area, sex, genre, index, sin, cur_page);
        result.put("code", 0);
        result.put("data", singerList);
        return result;
    }

    @PostMapping("/singerdetail")
    public Object getSingerDetail(String singerMid) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, Object> data = singerService.getSingerDetail(singerMid);
        result.put("code", 0);
        result.put("data", data);
        return result;
    }
}
