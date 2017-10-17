package com.yzx.flow.modular.system.controller;

import static com.yzx.flow.core.support.HttpUtil.getIp;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.kaptcha.Constants;
import com.yzx.flow.common.constant.factory.ConstantFactory;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.SuccessTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.InvalidKaptchaException;
import com.yzx.flow.common.node.MenuNode;
import com.yzx.flow.common.persistence.dao.UserMapper;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.common.persistence.model.User;
import com.yzx.flow.core.log.LogManager;
import com.yzx.flow.core.log.factory.LogTaskFactory;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.system.dao.MenuDao;
import com.yzx.flow.modular.system.service.IUpgradeService;

/**
 * 登录控制器
 *
 * @author liuyufeng
 * @Date 2017年1月10日 下午8:25:24
 */
@Controller
public class LoginController extends BaseController {

    private static Logger logger = Logger.getLogger(LoginController.class);
    @Autowired
    MenuDao menuDao;

    @Autowired
    UserMapper userMapper;
    
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
     * 跳转到主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        //获取菜单列表
        List<Integer> roleList = ShiroKit.getUser().getRoleList();
        List<MenuNode> menus = menuDao.getMenusByRoleIds(roleList);
        List<MenuNode> titles = MenuNode.buildTitle(menus);
        model.addAttribute("titles", titles);

        //获取用户头像
        Integer id = ShiroKit.getUser().getId();
        User user = userMapper.selectById(id);
        model.addAttribute("user", user);
        model.addAttribute("roleName", ConstantFactory.me().getRoleName(user.getRoleid()));
        model.addAttribute("deptName", ConstantFactory.me().getDeptName(user.getDeptid()));
        
        //页面title
    	model.addAttribute("serverTitle", getServerTitle());
        
    	//页面logo地址
    	String logoSrc = logoUrl + "static/" + logoName;
    	model.addAttribute("logoSrc", logoSrc);
    	
    	//页面head地址
    	String headLogo = logoUrl + "static/" + headLogoName;
    	model.addAttribute("headLogo", headLogo);
    	
        return "/index.html";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
    	
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
    	
        if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
            return REDIRECT + "/";
        } else {
            return "/login.html";
        }
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

	/**
     * 点击登录执行的动作
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali() {

        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String autologin = super.getPara("autologin");

        //登陆错误次数
        Object errorCount = super.getSession().getAttribute("errorCount");
        //验证验证码是否正确
//        if(ToolUtil.getKaptchaOnOff()){
        //错误次数增加验证码验证
        if(errorCount!=null){
            String kaptcha = super.getPara("kaptcha").trim();
            String code = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if(ToolUtil.isEmpty(kaptcha) || !kaptcha.equals(code)){
                throw new InvalidKaptchaException();
            }
        }

        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());
        if("autologin".equals(autologin)){//自动登陆
        	token.setRememberMe(true);
        }
		try {
			currentUser.login(token);
		} catch (AuthenticationException e) {
			if(errorCount == null){
				super.getSession().setAttribute("errorCount",1);
			}
			logger.error("账号或密码错误！", e);
			throw e;
		}
		super.getSession().removeAttribute("errorCount");
		
        ShiroUser shiroUser = ShiroKit.getUser();
        //设置登陆超时时间为30min
        super.getSession().setMaxInactiveInterval(30 * 60);
        super.getSession().setAttribute("shiroUser", shiroUser);

        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));

        return REDIRECT + "/";
    }

    
    @RequestMapping(value="/ckeckCode",method=RequestMethod.POST)
    @ResponseBody
    public Object checkCode(){
    	 String kaptcha = super.getPara("kaptcha").trim();
    	 logger.info("页面传入的验证码："+kaptcha);
         String code = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
         logger.info("服务端验证码："+code);
         if(ToolUtil.isEmpty(kaptcha) || !kaptcha.equals(code)){
           return new ErrorTip(0, "验证码错误");
         }
         return new SuccessTip("验证通过");
    }
    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut() {
        LogManager.me().executeLog(LogTaskFactory.exitLog(ShiroKit.getUser().getId(), getIp()));
        ShiroKit.getSubject().logout();
        super.getSession().invalidate();
        return REDIRECT + "/login";
    }
}
