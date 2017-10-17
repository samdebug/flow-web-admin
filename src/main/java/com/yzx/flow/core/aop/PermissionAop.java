/**
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yzx.flow.core.aop;

import java.util.List;

import javax.naming.NoPermissionException;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yzx.flow.core.cache.CacheKit;
import com.yzx.flow.core.listener.ConfigListener;
import com.yzx.flow.core.shiro.check.PermissionCheckManager;
import com.yzx.flow.core.shiro.factory.IShiro;
import com.yzx.flow.core.shiro.factory.ShiroFactroy;
import com.yzx.flow.core.support.HttpUtil;

/**
 * 在所有controller中配置切面
 * 对每一个请求有注解@Controller进行切面配置，拦截所有表中（shiroFactory.getResUrls()）有的路径请求，并进行鉴权
 * AOP 权限自定义检查
 */
@Aspect
@Component
public class PermissionAop {

	//缓存菜单
	public static final String permissionControl = "menu";
	//缓存菜单路径
	public static final String permissionPath = "path";
	
	@Autowired
	ShiroFilterFactoryBean shiroFilterFactoryBean;
	
    @Pointcut(value = "within(@org.springframework.stereotype.Controller *)")
    private void cutPermission() {

    }

    @Around("cutPermission()")
    public Object doPermission(ProceedingJoinPoint point) throws Throwable {
    	String requestURI = HttpUtil.getRequest().getRequestURI().replace(ConfigListener.getConf().get("contextPath"), "");
    	//获取需要权限认证的列表
    	List<String> list =  (List) CacheKit.get(permissionControl, permissionPath);
    	if(list==null){
    		IShiro shiroFactory = ShiroFactroy.me();
    		list = shiroFactory.getResUrls();
    		if(list!=null && !list.isEmpty()){
    			CacheKit.put(permissionControl, permissionPath,list);
    		}
    	}
    	if(list == null){
    		HttpUtil.getResponse().sendRedirect("/400.html");
    		throw new NoPermissionException("无访问权限！");
    	}
    	if(list!=null && !list.isEmpty() && list.contains(requestURI)){
    		//检查全体角色
    		boolean result = PermissionCheckManager.checkAll();
    		if (result) {
    			return point.proceed();
    		} else {
    			HttpUtil.getResponse().sendRedirect("/400.html");
    			throw new NoPermissionException("无访问权限！");
    		}
    	}else{
    		//不在权限列表中的不拦截
    		return point.proceed();
    	}
    }
}
