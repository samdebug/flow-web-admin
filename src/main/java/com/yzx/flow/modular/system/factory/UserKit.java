package com.yzx.flow.modular.system.factory;

import com.yzx.flow.common.constant.state.UserType;
import com.yzx.flow.common.persistence.model.User;
/**
 * 
 * @author Liulei
 *
 */
public class UserKit {
	
	
	
	/**
     * 指定用户是否是 <b>客户</b> 类型
     * @return
     */
    public static boolean isCustomerType(User user) {
    	return user != null && UserType.CUSTOMER.getCode().equals(user.getType()) 
    			&& user.getTargetId() != null && user.getTargetId().compareTo(0L) > 0;
    }
    
    /**
     * 指定用户是否是 <b>管理员</b> 类型
     * @return
     */
    public static boolean isAdminTypeOfUser(User user) {
    	return user != null && UserType.ADMIN.getCode().equals(user.getType())
    			&& ( user.getTargetId() == null || user.getTargetId().equals(0L) );
    }
	

}
