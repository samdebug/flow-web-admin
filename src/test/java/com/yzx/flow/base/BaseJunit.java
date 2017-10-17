package com.yzx.flow.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.github.tobato.fastdfs.domain.StorePath;
import com.yzx.flow.Application;
import com.yzx.flow.common.fastDFS.ComponetImport;


/**
 * 基础测试类
 *
 * @author stylefeng
 * @Date 2017/5/21 16:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Transactional //测试之后数据可回滚
public class BaseJunit {
	@Autowired
	ComponetImport componetImport;
	
	@Test
	public void uploadFile(){
		StorePath uploadFile = componetImport.uploadFile("d:\\e.war");
		System.out.println(uploadFile.getGroup());
		System.out.println(uploadFile.getPath());
	}
	
	@Test
	public void downLoad(){
		componetImport.downLoad("group1", "M00/00/00/rBAGDlmv4NiANhKgAAAACpIItYQ959.war","d:\\a.war");
	}
}
