package com.yzx.flow.common.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.baomidou.mybatisplus.plugins.Page;
import com.yzx.flow.common.constant.tips.SuccessTip;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.warpper.BaseControllerWarpper;
import com.yzx.flow.core.support.HttpUtil;
import com.yzx.flow.core.util.FileUtil;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST)
public class BaseController {

    protected static String SUCCESS = "SUCCESS";
    protected static String ERROR = "ERROR";

    protected static String REDIRECT = "redirect:";
    protected static String FORWARD = "forward:";

    protected static SuccessTip SUCCESS_TIP = new SuccessTip();

    protected HttpServletRequest getHttpServletRequest() {
        return HttpUtil.getRequest();
    }

    protected HttpServletResponse getHttpServletResponse() {
        return HttpUtil.getResponse();
    }

    protected HttpSession getSession() {
        return HttpUtil.getRequest().getSession();
    }

    protected String getPara(String name) {
        return HttpUtil.getRequest().getParameter(name);
    }

    protected void setAttr(String name, Object value) {
        HttpUtil.getRequest().setAttribute(name, value);
    }

    protected Integer getSystemInvokCount() {
        return (Integer) this.getHttpServletRequest().getServletContext().getAttribute("systemCount");
    }

    /**
     * 兼容旧系统分分页信息
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> com.yzx.flow.common.page.Page<T> getPageInfo(Map params) {
    	com.yzx.flow.common.page.Page<T> page = new com.yzx.flow.common.page.Page<T>();
    	page.setParams(params==null?new HashMap<String,Object>():params);
    	return page;
    }
    
    /**
     * 把service层的分页信息，封装为bootstrap table通用的分页封装
     */
    protected <T> PageInfoBT<T> packForBT(Page<T> page) {
    	return new PageInfoBT<T>(page);
    }

    /**
     * 包装一个list，让list增加额外属性
     */
    protected Object warpObject(BaseControllerWarpper warpper) {
        return warpper.warp();
    }

    /**
     * 删除cookie
     */
    protected void deleteCookieByName(String cookieName) {
        Cookie[] cookies = this.getHttpServletRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                Cookie temp = new Cookie(cookie.getName(), "");
                temp.setMaxAge(0);
                this.getHttpServletResponse().addCookie(temp);
            }
        }
    }

    /**
     * 返回前台文件流
     *
     * @author liuyufeng
     * @date 2017年2月28日 下午2:53:19
     */
    protected ResponseEntity<byte[]> renderFile(String fileName, String filePath) {
        byte[] bytes = FileUtil.toByteArray(filePath);
        return renderFile(fileName, bytes);
    }

    /**
     * 返回前台文件流
     *
     * @author liuyufeng
     * @date 2017年2月28日 下午2:53:19
     */
    protected ResponseEntity<byte[]> renderFile(String fileName, byte[] fileBytes) {
        String dfileName = null;
        try {
            dfileName = new String(fileName.getBytes("gb2312"), "iso8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", dfileName);
        return new ResponseEntity<byte[]>(fileBytes, headers, HttpStatus.CREATED);
    }
}
