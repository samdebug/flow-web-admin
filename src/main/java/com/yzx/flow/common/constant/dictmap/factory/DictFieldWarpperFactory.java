package com.yzx.flow.common.constant.dictmap.factory;

import com.yzx.flow.common.constant.factory.ConstantFactory;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;

import java.lang.reflect.Method;

/**
 * 字段的包装创建工厂
 *
 * @author liuyufeng
 * @date 2017-05-06 15:12
 */
public class DictFieldWarpperFactory {

    public static Object createFieldWarpper(Object field, String methodName) {
        ConstantFactory me = ConstantFactory.me();
        try {
            Method method = ConstantFactory.class.getMethod(methodName, field.getClass());
            Object result = method.invoke(me, field);
            return result;
        } catch (Exception e) {
            try {
                Method method = ConstantFactory.class.getMethod(methodName, Integer.class);
                Object result = method.invoke(me, Integer.parseInt(field.toString()));
                return result;
            } catch (Exception e1) {
                throw new BussinessException(BizExceptionEnum.ERROR_WRAPPER_FIELD);
            }
        }
    }

}
