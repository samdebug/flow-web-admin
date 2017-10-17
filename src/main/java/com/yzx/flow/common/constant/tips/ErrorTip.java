package com.yzx.flow.common.constant.tips;

import com.yzx.flow.common.exception.BizExceptionEnum;

/**
 * 返回给前台的错误提示
 *
 * @author liuyufeng
 * @date 2016年11月12日 下午5:05:22
 */
public class ErrorTip extends Tip {

    public ErrorTip(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ErrorTip(BizExceptionEnum bizExceptionEnum) {
        this.code = bizExceptionEnum.getCode();
        this.message = bizExceptionEnum.getMessage();
    }
    
    /**
     * 自动格式化
     * @param bizExceptionEnum
     * @param params
     */
    public ErrorTip(BizExceptionEnum bizExceptionEnum, Object...params) {
        this.code = bizExceptionEnum.getCode();
        this.message = String.format(bizExceptionEnum.getMessage(), params);
    }
    
    /**
     * string format
     * @param bizExceptionEnum
     * @param params
     * @return
     */
    public static ErrorTip buildErrorTip(BizExceptionEnum bizExceptionEnum, Object...params) {
    	return new ErrorTip(bizExceptionEnum, params);
    }
    
    public static ErrorTip buildErrorTip(String errorTip) {
    	return buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, errorTip);
    }
    
    public static ErrorTip buildErrorTip(BizExceptionEnum bizExceptionEnum) {
    	return new ErrorTip(bizExceptionEnum);
    }
    
}
