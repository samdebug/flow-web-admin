package com.yzx.flow.core.util.licence;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoleilu.hutool.date.DateUtil;
import com.yzx.flow.core.util.zk.ZookeeperUtil;

public class LicenceFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(LicenceFilter.class);
	//上次更新时间
	private static String last_update_time = DateUtil.today();
	//计数
	private static int count = 0;
    
    private ZookeeperUtil zookeeperUtil;
    
    
    public LicenceFilter() {
		super();
	}


	public LicenceFilter(ZookeeperUtil baseEnvInit) {
		super();
		this.zookeeperUtil = baseEnvInit;
	}


	FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    		throws IOException, ServletException {
    	try{
    		//一天只检测一次,启动时执行一次
    		if(!DateUtil.today().equals(last_update_time) || count==0){
    			LicenceVo licence = LicenceUtil.verifyLicence(zookeeperUtil.publickey, zookeeperUtil.licencesecret);
    			if(licence!=null){
    				count ++ ;
    				//如果不是同一天则更新zk日期，通知其他组件验证licence有效性
    				last_update_time = DateUtil.today();
					zookeeperUtil.setDate(zookeeperUtil.LICENCESECRET_NODE, zookeeperUtil.licencesecret,false);
					logger.info("更新zk密文日期成功！"+DateUtil.today());
    				chain.doFilter(request, response);
    			}else{
    		        PrintWriter out = response.getWriter();
    		        out.print("licence unavailable!");
    		        //释放资源
    		        out.flush();
    		        out.close();
//    				response.getWriter().write("licence unavailable!");
    			}
    		}else{
    			chain.doFilter(request, response);
    		}
    	}catch(Exception e){
    		logger.error("licence使用受限:",e);
//    		response.getWriter().write("licence unavailable!");
	        PrintWriter out = response.getWriter();
	        out.print("licence unavailable!");
	        //释放资源
	        out.flush();
	        out.close();
    	}
    }

}
