package com.yzx.flow.modular.channel.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAdapter;
import com.yzx.flow.common.persistence.model.ChannelCompanyRecharge;
import com.yzx.flow.common.persistence.model.ChannelSupplier;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelAdapterService;
import com.yzx.flow.modular.channel.service.IChannelSupplierService;

/**
 * 通道供应商控制器
 *
 * @author liuyufeng
 * @Date 2017-08-14 20:05:12
 */
@Controller
@RequestMapping("/channelSupplier")
public class ChannelSupplierController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelCompanyController.class);
	
    private String PREFIX = "/channel/channelSupplier/";

    @Autowired
    private IChannelSupplierService channelSupplierService;
    
    @Autowired
    private IChannelAdapterService channelAdapterService;
    /**
     * 跳转到通道供应商首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelSupplier.html";
    }

    /**
     * 跳转到添加通道供应商
     */
    @RequestMapping("/channelSupplier_add")
    public String channelSupplierAdd(Model model) {
    	model.addAttribute("adapters", channelAdapterService.find(new ChannelAdapter()));
        return PREFIX + "channelSupplier_add.html";
    }

    
    /**
     * 跳转到修改通道供应商
     */
    @RequestMapping("/channelSupplier_update/{channelSupplierId}")
    public String channelSupplierUpdate(@PathVariable String channelSupplierId, Model model) {
    	try {
			String supplierCodes = java.net.URLDecoder.decode(channelSupplierId,
					"UTF-8");
			ChannelSupplier data = channelSupplierService.get(supplierCodes);
			model.addAttribute("adapters", channelAdapterService.find(new ChannelAdapter()));
			if (ToolUtil.isEmpty(data.getCoopGroupId())) {
				//data.setAttachmentFileList();
			}
			model.addAttribute("info", data);
		} catch (Exception e) {
			throw new BussinessException(BizExceptionEnum.SERVER_ERROR);
		}
        return PREFIX + "channelSupplier_edit.html";
    }

    @RequestMapping("/detail/{channelSupplierId}")
    public String detailView(@PathVariable String channelSupplierId, Model model) {
    	try {
			String supplierCodes = java.net.URLDecoder.decode(channelSupplierId,
					"UTF-8");
			ChannelSupplier data = channelSupplierService.get(supplierCodes);
			model.addAttribute("adapters", channelAdapterService.find(new ChannelAdapter()));
			if (ToolUtil.isEmpty(data.getCoopGroupId())) {
				//data.setAttachmentFileList();
			}
			model.addAttribute("info", data);
		} catch (Exception e) {
			throw new BussinessException(BizExceptionEnum.SERVER_ERROR);
		}
        return PREFIX + "channelSupplier_view.html";
    }
    
    /**
     * 获取通道供应商列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<ChannelSupplier> list(Page<ChannelSupplier> page) {
    	if (page.getSort()==null) {
			page.setSort("create_time");
		}
        return channelSupplierService.pageQuery(page);
    }

    /**
     * 新增通道供应商
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(ChannelSupplier data,@RequestParam(value = "updId", required = false) String updaId ) {
    	try {
    		ShiroUser user =ShiroKit.getUser();
			if (null == user) {
				throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
			}
			Staff staff = new Staff();
			staff.setLoginName(user.getName());
			channelSupplierService.saveAndUpdate(data, staff,updaId);
			return  SUCCESS_TIP;
		} catch (MyException e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(0, e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(0, "添加失败");
		}
    }

    /**
     * 删除通道供应商
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam(name="channelSupplierId") String supplierCode) {
    	Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (ToolUtil.isEmpty(supplierCode)) {
				throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
			}
			// false表示无记录
			if (channelSupplierService.selectByInfo(supplierCode)) {
				 return new ErrorTip(0, "删除失败，接入通道已引用该记录");
			}
			channelSupplierService.delete(supplierCode);
			 return SUCCESS_TIP;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(0,"删除失败");
		}
       
    }


    /**
     * 修改通道供应商
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(ChannelSupplier data,@RequestParam(value = "updId", required = false) String updaId ) {
    	try {
    		ShiroUser user =ShiroKit.getUser();
			if (null == user) {
				throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
			}
			Staff staff = new Staff();
			staff.setLoginName(user.getName());
			channelSupplierService.saveAndUpdate(data, staff,updaId);
			return  SUCCESS_TIP;
		} catch (MyException e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(0, e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(0, "服务端出错");
		}
    }
   
    @RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(@RequestParam(name="supplierCode") String supplierCode) {
    	Map<String, Object> resMap = new HashMap<String, Object>(); 
    	if (ToolUtil.isEmpty(supplierCode)) {
			throw new  BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		try {
			String supplierCodes = java.net.URLDecoder.decode(supplierCode,"UTF-8");
			ChannelSupplier data = channelSupplierService.get(supplierCodes);
			/*AttachmentInterface attachmentInterface = SpringContextHolder
					.getBean("attachmentInterface");
			if (StringTools.isNotEmptyString(data.getCoopGroupId())) {
				attachmentInterface
						.formalAttachmentGroup(data.getCoopGroupId());
				List<AttachmentFile> smAttachmentFileList = attachmentInterface
						.listAttachmentFile(data.getCoopGroupId());
				data.setAttachmentFileList(smAttachmentFileList);
			}*/
			resMap.put("data", data);
			resMap.put("success", Boolean.valueOf(true));
		} catch (Exception e) {
			resMap.put("message", "参数错误");
			resMap.put("success", Boolean.valueOf(false));
		}
		return resMap;
	}
    
    /**
	 * 取出通道供应商信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/selectChannelSupplierByName")
	@ResponseBody
	public Map<String, Object> selectChannelSupplierByName(String supplierCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ChannelSupplier> list = new ArrayList<ChannelSupplier>();
		list = channelSupplierService.selectChannelSupplierByName(supplierCode);
		map.put("channelSupplierList", list);
		return map;
	}
	
	
	/**
	 * 供应商有效无效设置
	 * @param status
	 * @param supplierCode
	 * @return
	 */
	@RequestMapping(value = "/changeStatus")
    @ResponseBody
    public Object changeStatus(@RequestParam(name="status")Integer status,@RequestParam(name="channelSupplierId") String supplierCode) {
		ShiroUser user = ShiroKit.getUser();
        Staff staff = new Staff();
        staff.setLoginName(user.getName());
        ChannelSupplier data = channelSupplierService.get(supplierCode);
        if (status == null || data == null) {
            return new ErrorTip(0, "非法请求！");
        }
        if (status < 1 || status > 2) {
            return new ErrorTip(0, "非法请求！");
        }
        data.setIsValid(status);
        data.setUpdator(staff.getLoginName());
        data.setUpdateTime(new Date());
        channelSupplierService.update(data);
        return SUCCESS_TIP;
    }
	
	/**
	 * 供应商批量有效无效设置
	 * @param status
	 * @param supplierCode
	 * @return
	 */
	@RequestMapping(value = "/batchUpdateStatus", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Object batchUpdateStatus(HttpServletRequest request, HttpServletResponse response, ModelMap model)
    		throws Exception {
        Staff staff = ShiroKit.getStaff();
        response.setCharacterEncoding("utf-8");
		response.addHeader("pragma", "no-cache");
		String params = request.getParameter("params");
		JSONArray array = JSONArray.fromObject(params);
		List<ChannelSupplier> list = new ArrayList<ChannelSupplier>();
		for (int i = 0; i < array.size(); i++) {
			ChannelSupplier data = new ChannelSupplier();
			data.setIsValid(Integer.valueOf( array.getJSONObject(i).get("status").toString()));
			data.setSupplierCode(array.getJSONObject(i).get("supplierCode").toString());
	        data.setUpdator(staff.getLoginName());
	        data.setUpdateTime(new Date());
	        list.add(data);
		}
        channelSupplierService.batchUpdate(list);
        return SUCCESS_TIP;
    }
	
}
