package com.yzx.flow.modular.channel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelErrorLog;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelErrorLogService;

/**
 * 通道错误日志控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 12:23:26
 */
@Controller
@RequestMapping("/channelErrorLog")
public class ChannelErrorLogController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ChannelErrorLogController.class);
	
	@Autowired
	private IChannelErrorLogService channelErrorLogService;
	
    private String PREFIX = "/channel/channelErrorLog/";

    /**
     * 跳转到通道错误日志首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelErrorLog.html";
    }

    /**
     * 跳转到添加通道错误日志
     */
    @RequestMapping("/channelErrorLog_add")
    public String channelErrorLogAdd() {
        return PREFIX + "channelErrorLog_add.html";
    }

    /**
     * 跳转到修改通道错误日志
     */
    @RequestMapping("/channelErrorLog_update/{channelErrorLogId}")
    public String channelErrorLogUpdate(@PathVariable Integer channelErrorLogId, Model model) {
        return PREFIX + "channelErrorLog_edit.html";
    }

    /**
     * 获取通道错误日志列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<ChannelErrorLog> list(Page<ChannelErrorLog> page) {
    	 return  channelErrorLogService.pageQuery(page);
    }

    /**
     * 新增通道错误日志
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
    	
        return super.SUCCESS_TIP;
    }

    /**
     * 删除通道错误日志
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String channelErrorLogId) {
    	
    	if (ToolUtil.isEmpty(channelErrorLogId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
    	int i = channelErrorLogService.delete(Long.parseLong(channelErrorLogId));
    	if (i<0) {
			return new ErrorTip(200, "删除失败");
		}
        return SUCCESS_TIP;
    }


    /**
     * 修改通道错误日志
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 通道错误日志详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
