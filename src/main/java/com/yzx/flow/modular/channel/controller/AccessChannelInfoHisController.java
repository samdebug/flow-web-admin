package com.yzx.flow.modular.channel.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IAccessChannelInfoHisService;

@Controller
@RequestMapping("/accessChannelInfoHis")
public class AccessChannelInfoHisController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(AccessChannelInfoHisController.class);
    @Autowired
    @Qualifier("accessChannelInfoHisService")
    private IAccessChannelInfoHisService accessChannelInfoHisService;
    
    private String PREFIX = "/channel/accessChannelInfoHistory/";
    
    @RequestMapping("/list/{channelInfoId}")
    public String list(@PathVariable String channelInfoId,Model model){
    	model.addAttribute("channelSeqId", channelInfoId);
    	return PREFIX+"accessChannelInfoHistory.html";
    }
    
    @RequestMapping(value = "/query")
    @ResponseBody
    public  PageInfoBT<AccessChannelInfo> pageQuery(Page<AccessChannelInfo> page,@RequestParam(name="channelSeqId") String channelSeqId) {
    	if (ToolUtil.isEmpty(channelSeqId)) {
    		throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
    	page.getParams().put("channelSeqId", channelSeqId);
    	LOG.debug("分页查询，parms={}",page.getParams());
        PageInfoBT<AccessChannelInfo> resPage = accessChannelInfoHisService.pageQuery(page);
        return resPage;
    }
}
