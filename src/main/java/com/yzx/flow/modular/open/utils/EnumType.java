package com.yzx.flow.modular.open.utils;


public class EnumType {

	public static enum busiEnum{
		
		 busi_000000("000000","请求成功"),
		 busi_000001("000001","鉴权失败"),
		 busi_000002("000002","当前已是最新版本，无需更新"),
		 busi_000003("000003","userName不能为空"),
		 busi_000004("000004","passWord不能为空"),
		 busi_000005("000005","curVersion不能为空"),
		 busi_000006("000006","sign不能为空");
		
		private String code;
		
		private String message;

		private busiEnum(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	
	
}
