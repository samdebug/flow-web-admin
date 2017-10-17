package com.yzx.flow.modular.system.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yzx.flow.common.annotion.Permission;
import com.yzx.flow.common.annotion.log.BussinessLog;
import com.yzx.flow.common.constant.Const;
import com.yzx.flow.common.constant.Dict;
import com.yzx.flow.common.constant.factory.ConstantFactory;
import com.yzx.flow.common.constant.state.ManagerStatus;
import com.yzx.flow.common.constant.state.UserType;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.dao.UserMapper;
import com.yzx.flow.common.persistence.model.User;
import com.yzx.flow.config.evn.IAvatorRepository;
import com.yzx.flow.config.properties.FlowProperties;
import com.yzx.flow.core.db.Db;
import com.yzx.flow.core.log.LogObjectHolder;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.support.HttpUtil;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.controller.AccessChannelGroupController;
import com.yzx.flow.modular.system.dao.UserMgrDao;
import com.yzx.flow.modular.system.factory.UserFactory;
import com.yzx.flow.modular.system.factory.UserKit;
import com.yzx.flow.modular.system.transfer.UserDto;
import com.yzx.flow.modular.system.warpper.UserWarpper;

/**
 * 系统管理员控制器
 *
 * @author liuyufeng
 * @Date 2017年1月11日 下午1:08:17
 */
@Controller
@RequestMapping("/mgr")
public class UserMgrController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMgrController.class);
	
	/**
     * 用户头像 格式
     */
    private static String AVATOR_FORMAT = "%s.yzx";

    private static String PREFIX = "/system/user/";

    @Resource
    private FlowProperties flowProperties;

    @Resource
    private UserMgrDao managerDao;

    @Resource
    private UserMapper userMapper;
    
    @Autowired
    private IAvatorRepository avatorRepository;
    
    
   
    

    /**
     * 跳转到查看管理员列表的页面
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "user.html";
    }

    /**
     * 跳转到查看管理员列表的页面
     */
    @RequestMapping("/user_add")
    public String addView() {
        return PREFIX + "user_add.html";
    }

    /**
     * 跳转到角色分配页面
     */
    @RequestMapping("/role_assign/{userId}")
    public String roleAssign(@PathVariable Integer userId, Model model) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        User user = (User) Db.create(UserMapper.class).selectOneByCon("id", userId);
        model.addAttribute("userId", userId);
        model.addAttribute("userAccount", user.getAccount());
        return PREFIX + "user_roleassign.html";
    }

    /**
     * 跳转到查看管理员列表的页面
     */
    @RequestMapping("/user_edit/{userId}")
    public String userEdit(@PathVariable Integer userId, Model model) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        User user = this.userMapper.selectById(userId);
        // 只允许查看 管理员类型的账户
        if ( !UserKit.isAdminTypeOfUser(user) ) {
        	throw new BussinessException(BizExceptionEnum.OPERATION_ILLEGALE);
        }
        model.addAttribute(user);
        model.addAttribute("roleName", ConstantFactory.me().getRoleName(user.getRoleid()));
        model.addAttribute("deptName", ConstantFactory.me().getDeptName(user.getDeptid()));
        LogObjectHolder.me().set(user);
        return PREFIX + "user_edit.html";
    }

    /**
     * 跳转到查看用户详情页面
     */
    @RequestMapping("/user_info/{userId}")
    public String userInfo(@PathVariable Integer userId, Model model) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        User user = this.userMapper.selectById(userId);
        model.addAttribute(user);
        model.addAttribute("roleName", ConstantFactory.me().getRoleName(user.getRoleid()));
        model.addAttribute("deptName", ConstantFactory.me().getDeptName(user.getDeptid()));
        LogObjectHolder.me().set(user);
        return PREFIX + "user_view.html";
    }

    /**
     * 跳转到修改密码界面
     */
    @RequestMapping("/user_chpwd")
    public String chPwd() {
        return PREFIX + "user_chpwd.html";
    }

    /**
     * 修改当前用户的密码
     */
    @RequestMapping("/changePwd")
    @ResponseBody
    public Object changePwd(@RequestParam String oldPwd, @RequestParam String newPwd, @RequestParam String rePwd) {
        if (!newPwd.equals(rePwd)) {
            throw new BussinessException(BizExceptionEnum.TWO_PWD_NOT_MATCH);
        }
        Integer userId = ShiroKit.getUser().getId();
        User user = userMapper.selectById(userId);
        String oldMd5 = ShiroKit.md5(oldPwd, user.getSalt());
        if (user.getPassword().equals(oldMd5)) {
            String newMd5 = ShiroKit.md5(newPwd, user.getSalt());
            user.setPassword(newMd5);
            user.updateById();
            return SUCCESS_TIP;
        } else {
            throw new BussinessException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
        }
    }

    /**
     * 查询管理员列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String name, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        // 只查看管理员类型的数据
    	List<Map<String, Object>> users = managerDao.selectUsers(name, beginTime, endTime, UserType.ADMIN.getCode());
        return new UserWarpper(users).warp();
    }

    /**
     * 添加管理员
     */
    @RequestMapping("/add")
    @BussinessLog(value = "添加管理员", key = "account", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip add(@Valid UserDto user, BindingResult result) {
        if (result.hasErrors()) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }

        // 判断账号是否重复
        User theUser = managerDao.getByAccount(user.getAccount());
        if (theUser != null) {
            throw new BussinessException(BizExceptionEnum.USER_ALREADY_REG);
        }

        // 完善账号信息
        user.setSalt(ShiroKit.getRandomSalt(5));
        user.setPassword(ShiroKit.md5(user.getPassword(), user.getSalt()));
        user.setStatus(ManagerStatus.OK.getCode());
        user.setCreatetime(new Date());
        // 只允许添加管理员类型
        user.setType(UserType.ADMIN.getCode());
        user.setTargetId(User.TARGET_ID_ADMIN);

        this.userMapper.insert(UserFactory.createUser(user));
        return SUCCESS_TIP;
    }

    /**
     * 修改管理员
     *
     * @throws NoPermissionException
     */
    @RequestMapping("/edit")
    @BussinessLog(value = "修改管理员", key = "account", dict = Dict.UserDict)
    @ResponseBody
    public Tip edit(@Valid UserDto user, BindingResult result) throws NoPermissionException {
        if (result.hasErrors()) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        if (ShiroKit.hasRole(Const.ADMIN_NAME)) {
            this.userMapper.updateById(UserFactory.createUser(user));
            return SUCCESS_TIP;
        } else {
            ShiroUser shiroUser = ShiroKit.getUser();
            if (shiroUser.getId().equals(user.getId())) {
                this.userMapper.updateById(UserFactory.createUser(user));
                return SUCCESS_TIP;
            } else {
                throw new BussinessException(BizExceptionEnum.NO_PERMITION);
            }
        }
    }

    /**
     * 删除管理员（逻辑删除）
     */
    @RequestMapping("/delete")
    @BussinessLog(value = "删除管理员", key = "userId", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip delete(@RequestParam Integer userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.managerDao.setStatus(userId, ManagerStatus.DELETED.getCode());
        return SUCCESS_TIP;
    }

    /**
     * 查看管理员详情
     */
    @RequestMapping("/view/{userId}")
    @ResponseBody
    public User view(@PathVariable Integer userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        return this.userMapper.selectById(userId);
    }
    
    
    /**
     * 跳转到用户更换头像页面
     */
    @RequestMapping("/view/avator")
    public String viewAvator(Model model) {
        User current = this.userMapper.selectById(ShiroKit.getUser().getId());
        if ( current == null ) 
        	return "/404.html";
        
    	model.addAttribute("avator", current.getAvatar());
    	return PREFIX + "user_avator.html";
    }
    
    /**
     * 保存用户头像
     */
    @RequestMapping("/saveAvator")
    @ResponseBody
    public Object saveAvator(@RequestParam MultipartFile avator) {
        
    	if ( avator == null ) 
    		return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
    	
    	if ( !avator.getOriginalFilename().toLowerCase().matches("^.*\\.(jpg|png|jpeg){1}$") ) 
    		return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_INVALIDATE); 
    	
    	if ( !avator.getContentType().matches("^(?i)image/.+$"))
    		return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
    	
    	try {
    		if ( avator.getBytes().length > 2 * 1024 * 1024 ) 
        		return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "文件大小超过限制（2MB）");
    		
    		String fileName = internalSaveAvator(avator);
    		User current = userMapper.selectById(ShiroKit.getUser().getId());
    		if ( current == null )
    			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL); 
    		
    		current.setAvatar(fileName);
    		userMapper.updateById(current);
    		return SUCCESS_TIP;
    	} catch (BussinessException e) {
    		return ErrorTip.buildErrorTip(e.getMessage());
    	} catch (Exception e) {
    		LOGGER.error("上传用户头像异常", e);
    	}
    	return ErrorTip.buildErrorTip(BizExceptionEnum.SERVER_ERROR);
    }
    
    /**
     * 获取当前登陆用户头像
     */
    @RequestMapping("/getAvator")
    public void getAvator() {
    	String name = String.format(AVATOR_FORMAT, ShiroKit.getUser().getId().toString());
    	BufferedInputStream is = null;
    	OutputStream os = null;
        try {
        	
        	File file = new File(avatorRepository.getRepository(), name);
        	if ( !file.exists() || !file.isFile() )
        		return;
        	
			is = new BufferedInputStream(new FileInputStream(file));
			HttpServletResponse response = HttpUtil.getResponse();
			response.reset();
			
			os = response.getOutputStream();
			IOUtils.copy(is, os);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
    	
    }
    
    
    
    /**
     * 保存文件到本地 - 文件名为（当前用户id.yzx）
     * @param avator
     */
    private String internalSaveAvator(MultipartFile avator) {
    	
    	String rootDir = avatorRepository.getRepository();
    	if ( StringUtils.isBlank(rootDir) ) 
    		throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "Don't find the avator's repository!");
    	
    	File dir = new File(rootDir);
    	if ( !dir.exists() || dir.isFile() ) 
    		dir.mkdirs();
    	
    	String name = String.format(AVATOR_FORMAT, ShiroKit.getUser().getId().toString());
    	File file = new File(dir, name);
    	
    	BufferedOutputStream bos = null;
    	BufferedInputStream bis = null;
    	
    	try {
    		bos = new BufferedOutputStream(new FileOutputStream(file, false));// override
    		bis = new BufferedInputStream(avator.getInputStream());
    		
    		byte[] buff = new byte[1024];
    		int red = -1;
    		
    		while ( (red = bis.read(buff, 0, buff.length)) != -1 ) {
    			bos.write(buff, 0, red);
    		}
    		bos.flush();
    		return name;
    	} catch(Exception e) {
    		LOGGER.error("保存上传的图片错误");
    		throw new BussinessException(BizExceptionEnum.SERVER_ERROR);
    	} finally {
    		IOUtils.closeQuietly(bos);
    		IOUtils.closeQuietly(bis);
    	}
    }
    

    /**
     * 重置管理员的密码
     */
    @RequestMapping("/reset")
    @BussinessLog(value = "重置管理员密码", key = "userId", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip reset(@RequestParam Integer userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        User user = this.userMapper.selectById(userId);
        user.setSalt(ShiroKit.getRandomSalt(5));
        user.setPassword(ShiroKit.md5(Const.DEFAULT_PWD, user.getSalt()));
        this.userMapper.updateById(user);
        return SUCCESS_TIP;
    }

    /**
     * 冻结用户
     */
    @RequestMapping("/freeze")
    @BussinessLog(value = "冻结用户", key = "userId", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip freeze(@RequestParam Integer userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.managerDao.setStatus(userId, ManagerStatus.FREEZED.getCode());
        return SUCCESS_TIP;
    }

    /**
     * 解除冻结用户
     */
    @RequestMapping("/unfreeze")
    @BussinessLog(value = "解除冻结用户", key = "userId", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip unfreeze(@RequestParam Integer userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.managerDao.setStatus(userId, ManagerStatus.OK.getCode());
        return SUCCESS_TIP;
    }

    /**
     * 分配角色
     */
    @RequestMapping("/setRole")
    @BussinessLog(value = "分配角色", key = "userId,roleIds", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip setRole(@RequestParam("userId") Integer userId, @RequestParam("roleIds") String roleIds) {
        if (ToolUtil.isOneEmpty(userId, roleIds)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.managerDao.setRoles(userId, roleIds);
        return SUCCESS_TIP;
    }

    /**
     * 上传图片(上传到项目的webapp/static/img)
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    public
    @ResponseBody
    String upload(@RequestPart("file") MultipartFile picture) {
        String pictureName = UUID.randomUUID().toString() + ".jpg";
        try {
            String fileSavePath = flowProperties.getFileUploadPath();
            picture.transferTo(new File(fileSavePath + pictureName));
        } catch (Exception e) {
            throw new BussinessException(BizExceptionEnum.UPLOAD_ERROR);
        }
        return pictureName;
    }
}
