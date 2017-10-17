package com.yzx.flow.modular.channel.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.modular.channel.service.IChannelMonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 通道监控控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 14:47:54
 */
@Controller
@RequestMapping("/channelMonitor")
public class ChannelMonitorController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelMonitorController.class);
	
	@Resource(name="channelMonitorService")
	private IChannelMonitorService channelMonitorService;
	
    private String PREFIX = "/channel/channelMonitor/";

    /**
     * 跳转到通道监控首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelMonitor.html";
    }

    /**
     * 跳转到添加通道监控
     */
    @RequestMapping("/channelMonitor_add")
    public String channelMonitorAdd() {
        return PREFIX + "channelMonitor_add.html";
    }

    /**
     * 跳转到修改通道监控
     */
    @RequestMapping("/channelMonitor_update/{channelMonitorId}")
    public String channelMonitorUpdate(@PathVariable Integer channelMonitorId, Model model) {
        return PREFIX + "channelMonitor_edit.html";
    }

    /**
     * 获取通道监控列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<Map<String, Object>> list(Page<Map<String, Object>> page) {
    	LOG.info("通道监控查询,params={}",page.getParams());
    	return channelMonitorService.pageQuery(page);
    }

    /**
	 * 通道监控按省分布详情
	 */
    @RequestMapping(value = "/queryDetail")
    @ResponseBody
	public List<Map<String, Object>> queryDetail(Page<Map<String, Object>> page){
		List<Map<String, Object>> list = channelMonitorService.queryDetail(page);
		return list;
	}

    /**
	 * 通道监控利润详情
	 */
	
	@RequestMapping(value="/queryProfitRatioDetail")
	@ResponseBody
	public List<Map<String, Object>>  queryProfitRatioDetai(Page<Map<String, Object>> page){
		List<Map<String, Object>> list = channelMonitorService.queryProfitRatioDetail(page);
		return list;
	}


}
