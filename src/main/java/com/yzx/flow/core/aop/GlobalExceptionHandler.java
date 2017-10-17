package com.yzx.flow.core.aop;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.InvalidKaptchaException;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.core.log.LogManager;
import com.yzx.flow.core.log.factory.LogTaskFactory;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.system.service.IUpgradeService;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.DisabledAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;

import static com.yzx.flow.core.support.HttpUtil.getIp;
import static com.yzx.flow.core.support.HttpUtil.getRequest;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author liuyufeng
 * @date 2016年11月12日 下午3:19:56
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    private IUpgradeService upgradeService;
    
    //页面标题栏
    @Value("${serverTitle}")
    private String serverTitle;
    
    //页面logo
    @Value("${logoName}")
    private String logoName;
    
    //headLogo
    @Value("${headLogoName}")
    private String headLogoName;
    
    //服务访问地址
    @Value("${logoUrl}")
    private String logoUrl;
    
    /**
     * 拦截业务异常
     *
     * @author liuyufeng
     */
    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorTip notFount(BussinessException e) {
        LogManager.me().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getId(), e));
        getRequest().setAttribute("tip", e.getMessage());
        log.error("业务异常:", e);
        return new ErrorTip(e.getCode(), e.getMessage());
    }

    /**
     * 用户未登录
     *
     * @author liuyufeng
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unAuth(AuthenticationException e ,Model model) {
        log.error("用户未登陆：", e);
        SystemVersion curVersion = this.upgradeService.selectCurVersion();
    	model.addAttribute("curVersion", curVersion);
    	//页面title
    	model.addAttribute("serverTitle", getServerTitle());
    	//页面logo地址
    	String logoSrc = logoUrl + "static/" + logoName;
    	model.addAttribute("logoSrc", logoSrc);
    	//页面head地址
    	String headLogo = logoUrl + "static/" + headLogoName;
    	model.addAttribute("headLogo", headLogo);
        return "/login.html";
    }
    
    /**
     * 账号被冻结
     *
     * @author liuyufeng
     */
    @ExceptionHandler(DisabledAccountException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountLocked(DisabledAccountException e, Model model) {
        String username = getRequest().getParameter("username");
        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "账号被冻结", getIp()));
        model.addAttribute("tips", "账号被冻结");
        SystemVersion curVersion = this.upgradeService.selectCurVersion();
    	model.addAttribute("curVersion", curVersion);
    	//页面title
    	model.addAttribute("serverTitle", getServerTitle());
    	//页面logo地址
    	String logoSrc = logoUrl + "static/" + logoName;
    	model.addAttribute("logoSrc", logoSrc);
    	//页面head地址
    	String headLogo = logoUrl + "static/" + headLogoName;
    	model.addAttribute("headLogo", headLogo);
        return "/login.html";
    }

    /**
     * 账号密码错误
     *
     * @author liuyufeng
     */
    @ExceptionHandler(CredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String credentials(CredentialsException e, Model model) {
        String username = getRequest().getParameter("username");
        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "账号密码错误", getIp()));
        model.addAttribute("tips", "账号密码错误");
        SystemVersion curVersion = this.upgradeService.selectCurVersion();
    	model.addAttribute("curVersion", curVersion);
    	//页面title
    	model.addAttribute("serverTitle", getServerTitle());
    	//页面logo地址
    	String logoSrc = logoUrl + "static/" + logoName;
    	model.addAttribute("logoSrc", logoSrc);
    	//页面head地址
    	String headLogo = logoUrl + "static/" + headLogoName;
    	model.addAttribute("headLogo", headLogo);
        return "/login.html";
    }

    /**
     * 验证码错误
     *
     * @author liuyufeng
     */
    @ExceptionHandler(InvalidKaptchaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String credentials(InvalidKaptchaException e, Model model) {
        String username = getRequest().getParameter("username");
        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "验证码错误", getIp()));
        model.addAttribute("tips", "验证码错误");
        SystemVersion curVersion = this.upgradeService.selectCurVersion();
    	model.addAttribute("curVersion", curVersion);
    	//页面title
    	model.addAttribute("serverTitle", getServerTitle());
    	//页面logo地址
    	String logoSrc = logoUrl + "static/" + logoName;
    	model.addAttribute("logoSrc", logoSrc);
    	//页面head地址
    	String headLogo = logoUrl + "static/" + headLogoName;
    	model.addAttribute("headLogo", headLogo);
        return "/login.html";
    }

    /**
     * 无权访问该资源
     *
     * @author liuyufeng
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorTip credentials(UndeclaredThrowableException e) {
        getRequest().setAttribute("tip", "权限异常");
        log.error("权限异常!", e);
        return new ErrorTip(BizExceptionEnum.NO_PERMITION);
    }

    /**
     * 拦截未知的运行时异常
     *
     * @author liuyufeng
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorTip notFount(RuntimeException e) {
        LogManager.me().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getId(), e));
        getRequest().setAttribute("tip", "服务器未知运行时异常");
        log.error("运行时异常:", e);
        return new ErrorTip(BizExceptionEnum.SERVER_ERROR);
    }
    
    /**
     * 获取html页面title
     * @return
     */
    private String getServerTitle() {
    	//页面title
    	String htmlTitle = null;
    	try {
    		htmlTitle = new String(serverTitle.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return htmlTitle;
	}
}
