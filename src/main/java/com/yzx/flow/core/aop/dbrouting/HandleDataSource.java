package com.yzx.flow.core.aop.dbrouting;

public class HandleDataSource {
	public static final ThreadLocal<String> holder = new ThreadLocal<String>();

	public static void putDataSource(String datasource) {
		holder.set(datasource);
	}

	public static String getDataSource() {
		return holder.get();
	}

	public static void setDefault(String datasource) {
		holder.set(datasource);
	}
}
