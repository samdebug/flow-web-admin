package com.yzx.flow.config.properties;

import static com.yzx.flow.core.util.ToolUtil.getTempPath;
import static com.yzx.flow.core.util.ToolUtil.isEmpty;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目配置
 *
 * @author liuyufeng
 * @Date 2017/5/23 22:31
 */
@Component
@ConfigurationProperties(prefix = FlowProperties.PREFIX)
public class FlowProperties {

    public static final String PREFIX = "flow";

    private Boolean kaptchaOpen;

    private String fileUploadPath;

    private Boolean haveCreatePath = false;

    public String getFileUploadPath() {
        //如果没有写文件上传路径,保存到临时目录
        if(isEmpty(fileUploadPath)){
            return getTempPath();
        }else{
            //判断有没有结尾符,没有得加上
            if(!fileUploadPath.endsWith(File.separator)){
                fileUploadPath = fileUploadPath + File.separator;
            }
            //判断目录存不存在,不存在得加上
            if(haveCreatePath == false){
                File file = new File(fileUploadPath);
                file.mkdirs();
                haveCreatePath = true;
            }
            return fileUploadPath;
        }
    }

    public void setFileUploadPath(String fileUploadPath) {
        this.fileUploadPath = fileUploadPath;
    }

    public Boolean getKaptchaOpen() {
        return kaptchaOpen;
    }

    public void setKaptchaOpen(Boolean kaptchaOpen) {
        this.kaptchaOpen = kaptchaOpen;
    }
}
