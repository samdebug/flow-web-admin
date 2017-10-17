package com.yzx.flow.core.template.config;

import com.yzx.flow.core.util.ToolUtil;

/**
 * 全局配置
 *
 * @author liuyufeng
 * @date 2017-05-08 20:21
 */
public class ContextConfig {

    private String projectPath = "D:";//模板输出的项目目录
    private String bizChName;   //业务名称
    private String bizEnName;   //业务英文名称
    private String bizEnBigName;//业务英文名称(大写)

    public String getBizEnBigName() {
        return bizEnBigName;
    }

    public void setBizEnBigName(String bizEnBigName) {
        this.bizEnBigName = bizEnBigName;
    }

    public String getBizChName() {
        return bizChName;
    }

    public void setBizChName(String bizChName) {
        this.bizChName = bizChName;
    }

    public String getBizEnName() {
        return bizEnName;
    }

    public void setBizEnName(String bizEnName) {
        this.bizEnName = bizEnName;
        this.bizEnBigName = ToolUtil.firstLetterToUpper(this.bizEnName);
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
