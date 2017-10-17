package com.yzx.flow.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 全局的控制器
 *
 * @author liuyufeng
 * @date 2016年11月13日 下午11:04:45
 */
@Controller
@RequestMapping("/global")
public class GlobalController {

    /**
     * 跳转到404页面
     *
     * @author liuyufeng
     */
    @RequestMapping(path = "/error")
    public String errorPage() {
        return "/404.html";
    }
    
    
    /**
     * 跳转到400页面 - bad request
     *
     * @author Liulei
     */
    @RequestMapping(path = "/400")
    public String errorPage400() {
        return "/400.html";
    }
}
