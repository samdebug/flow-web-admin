
var MobileRegExp = {
	regs : {
		YD : {name : "移动", reg : /^((13[456789])|(14[7])|(15[012789])|(17[8])|(18[23478]))\d{8}$/},
		LT : {name : "联通", reg : /^((13[012])|(15[56])|(17[6])|(18[56]))\d{8}$/},
		DX : {name : "电信", reg : /^((13[3])|(15[3])|(17[73])|(18[190]))\d{8}$/}
	}
};


/**
 * 是否是一个合法的手机号码
 */
MobileRegExp.test = function(mobileStr) {
	for (opr in this.regs) {
		if ( this.regs[opr].reg.test(mobileStr) ) {
			return true;
		}
	}
	return false;
}

/**
 * 是否是一个移动号码
 */
MobileRegExp.isYD = function(mobileStr) {
	return this.regs.YD.reg.test(mobileStr);
}

/**
 * 是否是联通号码
 */
MobileRegExp.isLT = function(mobileStr) {
	return this.regs.LT.reg.test(mobileStr);
}

/**
 * 是否是一个电信号码
 */
MobileRegExp.isDX = function(mobileStr) {
	return this.regs.DX.reg.test(mobileStr);
}


/**
 * 获取运营商编码 - YD/LT/DX
 */
MobileRegExp.getOperator = function(mobileStr) {
	for (opr in this.regs) {
		if ( this.regs[opr].reg.test(mobileStr) ) {
			return "" + opr;
		}
	}
	return "-";
}

/**
 * 获取运营商名称 - 移动/联通/电信
 */
MobileRegExp.getOperatorName = function(mobileStr) {
	for (opr in this.regs) {
		if ( this.regs[opr].reg.test(mobileStr) ) {
			return this.regs[opr].name;
		}
	}
	return "-";
}
