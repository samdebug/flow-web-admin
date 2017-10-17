package com.yzx.flow.modular.system.warpper;

import java.util.Map;

import com.yzx.flow.common.warpper.BaseControllerWarpper;

/**
 *
 * @author liuyufeng
 * @date 2017年4月25日 18:10:31
 */
public class ChannelCompanyWrapper extends BaseControllerWarpper {

    public ChannelCompanyWrapper(Object list) {
        super(list);
    }

    @Override
    public void warpTheMap(Map<String, Object> map) {
//        Integer creater = (Integer) map.get("creater");
//        map.put("createrName", ConstantFactory.me().getUserNameById(creater));
    }

}
