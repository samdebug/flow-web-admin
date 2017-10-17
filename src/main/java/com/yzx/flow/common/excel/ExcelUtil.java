package com.yzx.flow.common.excel;


import java.io.IOException;
import java.util.Map;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * <b>Title：</b>ExcelUtil.java<br/> <b>Description：</b> excel导入导出工具类<br/> <b>@author：
 * </b>zhuangruhai<br/> <b>@date：</b>2014年3月12日 下午8:23:45<br/> <b>Copyright (c) 2014 ASPire
 * Tech.</b>
 */
public class ExcelUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 根据模板生成Excel文件.
     * 
     * @param templateFileName
     *            模板文件.
     * @param list
     *            模板中存放的数据.
     * @param resultFileName
     *            生成的文件.
     */
    public static void createExcel(String templateSrcFilePath, Map<String, Object> beanParams,
                                   String destFilePath) {
        // 创建XLSTransformer对象
        XLSTransformer transformer = new XLSTransformer();
        try {
            // 生成Excel文件
            transformer.transformXLS(templateSrcFilePath, beanParams, destFilePath);
        } catch (ParsePropertyException e) {
            LOGGER.error(e.getMessage(),e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    
}
