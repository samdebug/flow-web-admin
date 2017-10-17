package com.yzx.flow.modular.flowOrder.card;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalCfg {

	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PortalCfg.class);
	/**
	 * 配置文件接口.
	 */
//	private static Configuration configuration = null;

	/**
	 * 门户配置对象.
	 */
	private static PortalCfg config;

	/**
	 * 缺省配置文件名称.
	 */
	private static final String DEFAULT_CONFIGURATION_FILENAME = "portal-config.xml";

	/**
	 * 配置文件名.
	 */
	private static String configurationFileName = DEFAULT_CONFIGURATION_FILENAME;

	private static Map<String, Boolean> ipMap=new HashMap<String, Boolean>();

	/**
	 * 私有构造方法.
	 * 
	 * @param configurationFileName
	 *            配置文件相对路径
	 */
	private PortalCfg() {
//		if (configuration == null) {
//			refresh();
//		}
	}

	/*
	 * * 获取AJAX服务端配置对象.
	 * 
	 * @return AJAX服务端配置对象
	 */
	public static PortalCfg getInstance() {
		if (config == null) {
			config = new PortalCfg();
		}
		return config;
	}

	/**
	 * 刷新配置文件.
	 */
	private static void refresh() {
		// TODO 
//		configuration = ConfigurationHelper.getConfiguration(configurationFileName, 50000);
//		if (configuration == null) {
//			LOGGER.error("读portal配置文件失败, 配置文件：" + configurationFileName);
//		}
	}

	public String getString(String arg) {
		// TODO 
//		if (configuration == null) {
//			return null;
//		}
//		return configuration.getString(arg);
		return "";
	}

	public Map<String, Boolean> getIpMap() {
		if (ipMap.isEmpty()) {
			String ipStr = getString("ip-address");
			if (ipStr == null) {
				LOGGER.error("读取portal-config.xml配置文件中ip-address失败");
				ipMap.put("*", true);
				return ipMap;
			}
			ipMap = new HashMap<String, Boolean>();
			String[] ipList = ipStr.split(",");
			for (int i = 0; i < ipList.length; i++) {
				ipMap.put(ipList[i], true);
			}
		}
		return ipMap;
	}
	public String get(String arg) {
		// TODO 
//		if (configuration == null) {
//			return null;
//		}
//		return configuration.getString(arg);
		return "";
	}
	
	public String getBurl(){
		return get("b_url");
	}
	
	public String getCurl(){
		return get("c_url");
	}

}
