package com.yzx.flow.modular.system.factory;

import com.yzx.flow.modular.system.transfer.UserDto;
import com.yzx.flow.common.constant.state.UserType;
import com.yzx.flow.common.persistence.model.User;
import org.springframework.beans.BeanUtils;

/**
 * 用户创建工厂
 *
 * @author liuyufeng
 * @date 2017-05-05 22:43
 */
public class UserFactory {

    public static User createUser(UserDto userDto){
        if(userDto == null){
            return null;
        }else{
            User user = new User();
            BeanUtils.copyProperties(userDto,user);
            // 默认创建 管理员类型
            user.setType(UserType.ADMIN.getCode());
            user.setTargetId(User.TARGET_ID_ADMIN);
            return user;
        }
    }
}
