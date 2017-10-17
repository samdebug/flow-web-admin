package com.yzx.flow.modular.system.controller;

import com.yzx.flow.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 测试控制器
 *
 * @author liuyufeng
 * @Date 2017-05-26 15:12:46
 */
@Controller
@RequestMapping("/test")
public class TestController extends BaseController {

    private String PREFIX = "/system/test/";

    /**
     * 跳转到测试首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "test.html";
    }

    /**
     * 跳转到添加测试
     */
    @RequestMapping("/test_add")
    public String testAdd() {
        return PREFIX + "test_add.html";
    }

    /**
     * 跳转到修改测试
     */
    @RequestMapping("/test_update/{testId}")
    public String testUpdate(@PathVariable Integer testId, Model model) {
        return PREFIX + "test_edit.html";
    }

    /**
     * 获取测试列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        return null;
    }

    /**
     * 新增测试
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除测试
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改测试
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 测试详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
