package com.yzx.flow.modular.system.service.impl;

import com.yzx.flow.modular.system.dao.MenuDao;
import com.yzx.flow.modular.system.service.IMenuService;
import com.yzx.flow.common.persistence.dao.MenuMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 菜单服务
 *
 * @author liuyufeng
 * @date 2017-05-05 22:20
 */
@Service
public class MenuServiceImpl implements IMenuService {

    @Resource
    MenuMapper menuMapper;

    @Resource
    MenuDao menuDao;

    @Override
    public void delMenu(Integer menuId) {

        //删除菜单
        this.menuMapper.deleteById(menuId);

        //删除关联的relation
        this.menuDao.deleteRelationByMenu(menuId);
    }
}
