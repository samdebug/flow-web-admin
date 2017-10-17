package com.yzx.flow.core.util.zk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.date.DateUtil;
import com.yzx.flow.config.properties.LicenceProperties;
import com.yzx.flow.core.util.licence.LicenceUtil;
import com.yzx.flow.core.util.licence.LicenceVo;
import com.yzx.flow.modular.job.VersionUpdateExecute;
import com.yzx.flow.modular.job.service.VersionUpdateService;

/**
 * 
 * ZK 链接工具
 * @author lyf
 */
@Component
public class ZookeeperUtil {
	
	@Value("${zk.host}")
	public  String ZK_HOST ;
	
	@Value("${zk.timeout}")
	public Integer ZK_TIMEOUT;
	
	@Autowired
	private LicenceProperties licenceProperties;
	
	public static LicenceVo licence;
	
	private static final Logger logger = LoggerFactory.getLogger(ZookeeperUtil.class);
	
	//密文
	public final String LICENCESECRET_NODE = "/licence/licencesecret";
	//公钥
	public final String PUBLICKEY_NODE = "/licence/publickey";
	
	public static String licencesecret ;
	
	public static String publickey ;
	
	private static ZooKeeper zk = null; 
	
	@Value("${war_download_url}")
	public  String war_download_url ;
	
	@Autowired
	private VersionUpdateService versionUpdateService;
	

	private Watcher dataWatcher = new Watcher() {

		@Override
		public void process(WatchedEvent event) {
			ZooKeeper zk = getZk(ZK_HOST,ZK_TIMEOUT);
			if (event.getType() == EventType.NodeDataChanged) {
				try {
					byte[] data = zk.getData(event.getPath(), dataWatcher, new Stat());
					if(event.getPath().equals(LICENCESECRET_NODE)){
						logger.info("数据变更前licencesecret:"+licencesecret);
						licencesecret = new String(data, "utf-8");
						logger.info("数据变更后licencesecret:"+licencesecret);
					}
					if(event.getPath().equals(PUBLICKEY_NODE)){
						logger.info("数据变更前publickey:"+publickey);
						publickey = new String(data, "utf-8");
						logger.info("数据变更后publickey:"+publickey);
					}
					VersionUpdateExecute versionUpdateExecute = new VersionUpdateExecute(
							versionUpdateService, war_download_url, publickey, licence.getCustomerName(), licence.getPassword());
					if(event.getPath().equals(VersionUpdateExecute.parentNode+VersionUpdateExecute.COMPONENT)
							|| event.getPath().equals(VersionUpdateExecute.parentNode+VersionUpdateExecute.orderNode)){
						//判断是否为当前组件升级
						byte[] data1 = zk.getData(VersionUpdateExecute.parentNode+VersionUpdateExecute.orderNode,
								dataWatcher, new Stat());
						String order= new String(data1, "utf-8");
						String status = new String(data, "utf-8");//之前存取的是否需要更新数据
						//可以升级,status,带升级 2，升级成功 3.升级失败
						if(order.startsWith(VersionUpdateExecute.COMPONENT) && "1".equals(status)){
							//执行更新
							versionUpdateExecute.updateExecute();
						}
					}
					//监听到回滚
					if(event.getPath().equals(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderNode)){
						byte[] data3 = zk.getData(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderNode,
								dataWatcher, new Stat());
						String roolbackOrder = new String(data3, "utf-8");//回滚顺序
						//符合回滚顺序并且失败状态，则回滚
						if(roolbackOrder.startsWith(VersionUpdateExecute.COMPONENT)){
							//执行回滚 
							versionUpdateExecute.executeRollback();
						}
					}
				} catch (Exception e) {
					logger.error("dataWatcher:",e);
				} 
			}
		}
	};
	
	/**
	 * 修改zk参数
	 * @param path
	 * @param data
	 * @param noTime 是否要在数据尾端拼接日期
	 */
	public void setDate(String path,String data,boolean noTime){
		try {
			ZooKeeper zk = getZk(ZK_HOST,ZK_TIMEOUT);
			if(!noTime){
				data = (data.split(",")[0])+","+DateUtil.today();
			}
			zk.setData(path, data.getBytes(), -1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建并且设置
	 * @param path
	 * @param data
	 * @param noTime
	 */
	public void createNodeAndsetDate(String path,String data){
		try {
			ZooKeeper zk = getZk(ZK_HOST,ZK_TIMEOUT);
			Stat exists = zk.exists(path, dataWatcher);
			if(exists==null){
				zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}else{
				String datev = getDate(path);
				datev = data+","+datev;
				zk.setData(path, datev.getBytes(), -1);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据
	 * @param path
	 * @param data
	 */
	public String getDate(String path){
		try {
			ZooKeeper zk = getZk(ZK_HOST,ZK_TIMEOUT);
			byte[] data = zk.getData(path, dataWatcher, new Stat());
			String dataStr = new String(data,"UTF-8");
			return dataStr;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public void deleteNote(String path){
		try {
			ZooKeeper zk = getZk(ZK_HOST,ZK_TIMEOUT);
			zk.delete(path, -1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 *  清空节点下全部参数
	 * @param path
	 */
	public void clearNote(String path){
		try {
			logger.info("所有组件更新完毕，清空zk！");
			String parentPath = path.substring(0, path.length()-1);
			ZooKeeper zk = getZk(ZK_HOST,ZK_TIMEOUT);
			List<String> children = zk.getChildren(parentPath, false);
			for(String child : children){
				zk.setData(path+child, "".getBytes(), -1);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@PostConstruct
	public void init() {
		try {
			ZooKeeper zk = getZk(ZK_HOST, ZK_TIMEOUT);
			logger.info("------------start------------------");
			byte[] data1 = zk.getData(LICENCESECRET_NODE, dataWatcher, new Stat());
			licencesecret = new String(data1,"UTF-8");
			byte[] data2 = zk.getData(PUBLICKEY_NODE, dataWatcher, new Stat());
			publickey = new String(data2,"UTF-8");
			if(StringUtils.isBlank(licencesecret)||StringUtils.isBlank(publickey)){
				licencesecret = licenceProperties.getLicencesecret();
				publickey = licenceProperties.getPublickey();
			}
			LicenceVo licence = LicenceUtil.verifyLicence(publickey, licencesecret);
			if(licence!=null){
				this.licence = licence;
			}
			logger.info("licencesecret:"+licencesecret);
			logger.info("publickey:"+publickey);
			logger.info("------------end------------------");
		} catch (Exception e) {
			logger.error("connectZookeeper:",e);
		}
	}

	public  ZooKeeper getZk(String zk_host,int zk_timeout) {  
		if(zk != null){
			return zk;
		}
		try {
			zk = new ZooKeeper(zk_host, zk_timeout, dataWatcher);
			logger.info("正在获取Zookeeper连接...");
            while(true){
            	if(zk.getState() == ZooKeeper.States.CONNECTED){
            		break;
            	}
            }
            logger.info("Zookeeper连接成功！");
		} catch (IOException e) {
			logger.error("获取Zookeeper连接失败");
		}
		return zk;
    }

	public String getLicencesecret() {
		return licencesecret;
	}

	public void setLicencesecret(String licencesecret) {
		this.licencesecret = licencesecret;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}

	public LicenceVo getLicence() {
		return licence;
	}

	public void setLicence(LicenceVo licence) {
		this.licence = licence;
	}

}
