package com.yzx.flow.modular.system.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.annotion.Permission;
import com.yzx.flow.common.annotion.log.BussinessLog;
import com.yzx.flow.common.constant.Const;
import com.yzx.flow.common.constant.Dict;
import com.yzx.flow.common.constant.cache.Cache;
import com.yzx.flow.common.constant.factory.ConstantFactory;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.node.ZTreeNode;
import com.yzx.flow.common.persistence.dao.RoleMapper;
import com.yzx.flow.common.persistence.dao.UserMapper;
import com.yzx.flow.common.persistence.model.Role;
import com.yzx.flow.common.persistence.model.User;
import com.yzx.flow.core.cache.CacheKit;
import com.yzx.flow.core.log.LogObjectHolder;
import com.yzx.flow.core.shiro.ShiroDbRealm;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.util.Convert;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.system.dao.RoleDao;
import com.yzx.flow.modular.system.service.IRoleService;
import com.yzx.flow.modular.system.warpper.RoleWarpper;

/**
 * 角色控制器
 *
 * @author liuyufeng
 * @Date 2017年2月12日21:59:14
 */
@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    private static String PREFIX = "/system/role";

    @Resource
    UserMapper userMapper;

    @Resource
    RoleMapper roleMapper;

    @Resource
    RoleDao roleDao;

    @Resource
    IRoleService roleService;
    
    @Autowired
    ShiroDbRealm shiroDbRealm;

    /**
     * 跳转到角色列表页面
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "/role.html";
    }

    /**
     * 跳转到添加角色
     */
    @RequestMapping(value = "/role_add")
    public String roleAdd() {
        return PREFIX + "/role_add.html";
    }

    /**
     * 跳转到修改角色
     */
    @RequestMapping(value = "/role_edit/{roleId}")
    public String roleEdit(@PathVariable Integer roleId, Model model) {
        if (ToolUtil.isEmpty(roleId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        Role role = this.roleMapper.selectById(roleId);
        model.addAttribute(role);
        model.addAttribute("pName", ConstantFactory.me().getSingleRoleName(role.getPid()));
        model.addAttribute("deptName", ConstantFactory.me().getDeptName(role.getDeptid()));
        LogObjectHolder.me().set(role);
        return PREFIX + "/role_edit.html";
    }

    /**
     * 跳转到添加角色
     */
    @RequestMapping(value = "/role_assign/{roleId}")
    public String roleAssign(@PathVariable("roleId") Integer roleId, Model model) {
        if (ToolUtil.isEmpty(roleId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        model.addAttribute("roleId", roleId);
        model.addAttribute("roleName", ConstantFactory.me().getSingleRoleName(roleId));
        return PREFIX + "/role_assign.html";
    }

    /**
     * 获取角色列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String roleName) {
        List<Map<String, Object>> roles = this.roleDao.selectRoles(super.getPara("roleName"));
        return super.warpObject(new RoleWarpper(roles));
    }

    /**
     * 角色新增
     */
    @RequestMapping(value = "/add")
    @BussinessLog(value = "添加角色", key = "name", dict = Dict.RoleDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip add(@Valid Role role, BindingResult result) {
        if (result.hasErrors()) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        role.setId(null);
        this.roleMapper.insert(role);
        return SUCCESS_TIP;
    }

    /**
     * 角色修改
     */
    @RequestMapping(value = "/edit")
    @BussinessLog(value = "修改角色", key = "name", dict = Dict.RoleDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip edit(@Valid Role role, BindingResult result) {
        if (result.hasErrors()) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.roleMapper.updateById(role);

        //删除缓存
        CacheKit.removeAll(Cache.CONSTANT);
        return SUCCESS_TIP;
    }

    /**
     * 删除角色
     */
    @RequestMapping(value = "/remove")
    @BussinessLog(value = "删除角色", key = "roleId", dict = Dict.DeleteDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip remove(@RequestParam Integer roleId) {
        if (ToolUtil.isEmpty(roleId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }

        //缓存被删除的角色名称
        LogObjectHolder.me().set(ConstantFactory.me().getSingleRoleName(roleId));

        this.roleService.delRoleById(roleId);

        //删除缓存
        CacheKit.removeAll(Cache.CONSTANT);
        return SUCCESS_TIP;
    }

    /**
     * 查看角色
     */
    @RequestMapping(value = "/view/{roleId}")
    @ResponseBody
    public Tip view(@PathVariable Integer roleId) {
        if (ToolUtil.isEmpty(roleId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.roleMapper.selectById(roleId);
        return SUCCESS_TIP;
    }

    /**
     * 配置权限
     */
    @RequestMapping("/setAuthority")
    @BussinessLog(value = "配置权限", key = "roleId,ids", dict = Dict.RoleDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip setAuthority(@RequestParam("roleId") Integer roleId, @RequestParam("ids") String ids) {
        if (ToolUtil.isOneEmpty(roleId)) {
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        this.roleService.setAuthority(roleId, ids);
        //权限配置以后更新更新 清空缓存
        shiroDbRealm.clearCachedAuthorizationInfo(ShiroKit.getSubject().getPrincipals());;
//        cacheManager.getCache(arg0);
        return SUCCESS_TIP;
    }

    /**
     * 获取角色列表
     */
    @RequestMapping(value = "/roleTreeList")
    @ResponseBody
    public List<ZTreeNode> roleTreeList() {
        List<ZTreeNode> roleTreeList = this.roleDao.roleTreeList();
        roleTreeList.add(ZTreeNode.createParent());
        return roleTreeList;
    }

    /**
     * 获取角色列表
     */
    @RequestMapping(value = "/roleTreeListByUserId/{userId}")
    @ResponseBody
    public List<ZTreeNode> roleTreeListByUserId(@PathVariable Integer userId) {
        User theUser = this.userMapper.selectById(userId);
        String roleid = theUser.getRoleid();
        if (ToolUtil.isEmpty(roleid)) {
            List<ZTreeNode> roleTreeList = this.roleDao.roleTreeList();
            return roleTreeList;
        } else {
            String[] strArray = Convert.toStrArray(",", roleid);
            List<ZTreeNode> roleTreeListByUserId = this.roleDao.roleTreeListByRoleId(strArray);
            return roleTreeListByUserId;
        }
    }

}
