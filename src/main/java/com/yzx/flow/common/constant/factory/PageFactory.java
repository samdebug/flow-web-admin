package com.yzx.flow.common.constant.factory;

import com.baomidou.mybatisplus.plugins.Page;
import com.yzx.flow.core.support.HttpUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * BootStrap Table默认的分页参数创建
 *
 * @author liuyufeng
 * @date 2017-04-05 22:25
 */
public class PageFactory<T> {

    public Page<T> defaultPage() {
        HttpServletRequest request = HttpUtil.getRequest();
        int limit = Integer.valueOf(request.getParameter("limit"));
        int offset = Integer.valueOf(request.getParameter("offset"));
        return new Page<>((offset/limit + 1), limit);
    }
}
