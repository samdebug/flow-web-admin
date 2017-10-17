package com.yzx.flow.modular.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.core.shiro.ShiroKit;
/**
 * 欢迎 controller
 * 
 * @author Liulei
 *
 */
@Controller
@RequestMapping("/wellcome")
public class WellcomeController extends BaseController {
	
	private String PREFIX = "/system/wellcome/";
	
	
	/**
	 * 进入欢迎页
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String wellcome(Model model) {
		model.addAttribute("user", ShiroKit.getUser());
		return String.format("%s%s", PREFIX, "user.html");
	}

}
