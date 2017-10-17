package com.yzx.flow.config.evn;

public interface IAvatorRepository {
	
	/**
	 * 获取 系统配置的avator 目录
	 * @return
	 */
	String getRepository();
	
	
	
	
	public static class WindowAvatorRepository implements IAvatorRepository {
		
		private String repository;
		
		public WindowAvatorRepository(String repository) {
			super();
			this.repository = repository;
		}

		@Override
		public String getRepository() {
			return repository;
		}
		
	}
	
	
	public static class LinuxAvatorRepository implements IAvatorRepository {
		
		private String repository;

		public LinuxAvatorRepository(String repository) {
			super();
			this.repository = repository;
		}

		@Override
		public String getRepository() {
			return repository;
		}
		
	}

}
