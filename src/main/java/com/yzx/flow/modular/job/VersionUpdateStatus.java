package com.yzx.flow.modular.job;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/checkServiceStatus")
public class VersionUpdateStatus {
	
	@RequestMapping("")
	@ResponseBody
	public void ckeckServiceStatus(){
		
	}
	
}
