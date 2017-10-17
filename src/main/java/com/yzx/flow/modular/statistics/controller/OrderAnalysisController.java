package com.yzx.flow.modular.statistics.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.OrderAnalysisInfo;
import com.yzx.flow.modular.statistics.service.IOrderAnalysisService;

/**
 * 订单分析报表控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 12:08:37
 */
@Controller
@RequestMapping("/orderAnalysis")
public class OrderAnalysisController extends BaseController {

    private static Logger logger = Logger.getLogger(OrderAnalysisController.class);
    
    @Autowired
    private IOrderAnalysisService orderAnalysisService;
    
    private String PREFIX = "/orderAnalysis/";

    /**
     * 跳转到订单分析报表首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "orderAnalysis.html";
    }

    /**
     * 跳转到添加订单分析报表
     */
    @RequestMapping("/orderAnalysis_add")
    public String orderAnalysisAdd() {
        return PREFIX + "orderAnalysis_add.html";
    }

    /**
     * 跳转到修改订单分析报表
     */
    @RequestMapping("/orderAnalysis_update/{orderAnalysisId}")
    public String orderAnalysisUpdate(@PathVariable Integer orderAnalysisId, Model model) {
        return PREFIX + "orderAnalysis_edit.html";
    }

    /**
     * 获取订单分析报表列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<OrderAnalysisInfo> page) {

		logger.info("记录分页查询:" + page.toString());
		PageInfoBT<OrderAnalysisInfo> resultPage = null;
		try {
			resultPage = orderAnalysisService.pageQuery(page);
			
			Map<String, Object> map = orderAnalysisService.queryTotal(page);
			float totalPrice = Float.parseFloat(map.get("total_price").toString());
			
			for (OrderAnalysisInfo o : resultPage.getRows() ) {
				if(totalPrice > 0){
					o.setRate(String.format("%.2f%%", Double.parseDouble(o.getTotal_price()) * 100 / totalPrice));
				}else{
					o.setRate(0 + "%");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return resultPage;
    }

    /**
     * 新增订单分析报表
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return SUCCESS_TIP;
    }
    
    @RequestMapping(value="/chart")
	@ResponseBody
	 public Map<String, Object> chart(HttpServletResponse response, Page<OrderAnalysisInfo> page) throws Exception {
		Map<String, Object> map = orderAnalysisService.queryTotal(page);
		logger.info("【获取总金额】,totalPrice={}"+map.get("total_price"));
		List<OrderAnalysisInfo> returnList=returnList(page, map);
		List<String> sAreaName=new ArrayList<>();
		List<String> sCountry=new ArrayList<>();
		List<String> sProvince=new ArrayList<>();
		List<String> sCity=new ArrayList<>();
		List<String> sRate=new ArrayList<>();
		//按格式拼接所需数据
		for (OrderAnalysisInfo o : returnList) {
			sAreaName.add(o.getArea_name());
			sCountry.add(o.getCountry_price());
			sProvince.add(o.getProvincep());
			sCity.add(o.getCity_price());
			sRate.add(o.getRate().substring(0,o.getRate().indexOf("%")));
		}
		
		//返回map
		Map<String,Object> returnMap=new HashMap<String, Object>();
		returnMap.put("areaName", sAreaName);
		returnMap.put("country", sCountry);
		returnMap.put("province", sProvince);
		returnMap.put("city", sCity);
		returnMap.put("rate", sRate);
		
/*		Map<String,Object> map1 = new HashMap<>();
		map1.put("rate", Arrays.asList("14.29","7.14","78.57"));
		map1.put("areaName", Arrays.asList("山西","广东1","广东河北16"));
		map1.put("province", Arrays.asList("0.0","3.0","0.0"));
		map1.put("city", Arrays.asList("0.0","0.0","0.0"));
		map1.put("country", Arrays.asList("6.0","0.0","33.0"));
		*/
		
		
		
		return returnMap;
	}

    
	public List<OrderAnalysisInfo> returnList(Page<OrderAnalysisInfo> page,Map<String, Object> map) throws Exception{
		page.setRows(65530);
		page = orderAnalysisService.export(page);
		//获取数据
		List<OrderAnalysisInfo> returnList=new ArrayList<OrderAnalysisInfo>();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		
		float totalPrice=Float.parseFloat(map.get("total_price").toString());
		//遍历   获取金额订单占比
		for (OrderAnalysisInfo o :  page.getDatas()) {
			if(totalPrice>0){
				o.setRate(decimalFormat.format(Double.parseDouble(o.getTotal_price())*100/totalPrice)+"%");
			}else{
				o.setRate(0+"%");
			}
			returnList.add(o);
		}
		return returnList;
	}
    /**
     * 删除订单分析报表
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改订单分析报表
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 订单分析报表详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
