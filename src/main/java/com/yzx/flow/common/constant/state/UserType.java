package com.yzx.flow.common.constant.state;

/**
 * 用户账户类型
 * @author Liulei
 *
 */
public enum UserType {
	
	
	/**
	 * 管理员类型
	 */
	ADMIN(0, "管理员"),
	
	/**
	 * 客户类型
	 */
	CUSTOMER(1, "客户"),
	
	;
	
	private Integer code;
    private String des;
    
	private UserType(Integer code, String des) {
		this.code = code;
		this.des = des;
	}

	public Integer getCode() {
		return code;
	}

	public String getDes() {
		return des;
	}
	
	/**
	 * find UserType by code
	 * @param code
	 * @return
	 */
	public static UserType valueOf(Integer code) {
		if ( code == null )
			return null;
		
		UserType[] uts = UserType.values();
		for ( UserType ut : uts ) {
			if ( code.equals(ut.getCode()) )
				return ut;
		}
		return null;
	}

}
