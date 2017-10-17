package com.yzx.flow.common.constant.tips;

/**
 * 返回给前台的成功提示
 *
 * @author liuyufeng
 * @date 2016年11月12日 下午5:05:22
 */
public class SuccessTip extends Tip{
	
	public SuccessTip(){
		super.code = 200;
		super.message = "操作成功";
	}
	
	public SuccessTip(String message){
		super.code = 200;
		super.message = message == null ? "操作成功" : message;
	}
	
	
	public static SuccessTip buildTip(String message) {
		return new SuccessTip(message);
	}
	
}
