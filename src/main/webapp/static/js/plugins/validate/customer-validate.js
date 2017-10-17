$(function(){
	
	
	$.validator.addMethod("number_2",function(value,element){
		var lengthStr = value + "";
		var reg = /^\d+(\.\d{1,2})?$/;
		return this.optional(element) || reg.test(lengthStr);
	}, "请输入整数或小数（最多两位小数）");
	
	$.validator.addMethod("mobile", function(value, element) {
		var tel = /^1\d{10}$/;
		return this.optional(element) || (tel.test(value));
	}, "请输入有效的手机号码");
	
	$.validator.addMethod("password", function(value, element) {
		var reg = /^(?=.*\d)(?=.*[A-Za-z])[0-9a-zA-Z]/;
		return this.optional(element) || (reg.test(value));
	}, "必须同时包含数字和字母");
	
	$.validator.addMethod("maxlength", function(value, element,param) {
		var len = $.trim(value).replace(/[^\x00-\xff]/g, '..').length;
		return this.optional(element) || (len <=param);
	}, "不能超过{0}个字节");
	
	$.validator.addMethod("priceValid", function(value, element) {
		var reg = /^\d{1,3}(\.\d{1,2})?$/;   
		return this.optional(element) || (reg.test(value));
	}, "金额最大999.99(2位小数)");
	
	$.validator.addMethod("isLetterAndNum", function(value, element) {
		var reg = /^[A-Za-z0-9]+$/;   
		return this.optional(element) || (reg.test(value));
	}, "只能数字和字母组合");
	
	$.validator.addMethod("isNotCn", function(value, element) {
		var reg = /.*[\u4e00-\u9fa5]+.*$/;   
		return !this.optional(element) || (!reg.test(value));
	}, "只能数字和字母组合");
	
	$.validator.addMethod("checkCustomerName", function(value, element) {
		value = value.replaceAll("-","");
		var reg = /^[\u2E80-\u9FFF]+$/;
		return this.optional(element) || (reg.test(value));
	}, "只能汉字和-组合");
	
	//验证多个邮箱格式
	$.validator.addMethod("multipleEmailValid", function(value, element) {
		value = value.replaceAll("-","");
		var reg = /^(([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6}\,))*([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$$/;
		return this.optional(element) || (reg.test(value));
	}, "请输入正确的邮箱格式");
	
	
	
	jQuery.validator.addMethod("resendTimes", function(value, element) {
		var isResend=$("#isResend").val();
		var resendTimes=$("#resendTimes").val();
		if(isResend==1 && (resendTimes<1||resendTimes>4)){
			return false;
		}else{
			return true;
		}
	}, "大于等于1小于等于4的正整数");
	$.validator.addMethod("mutipTelValid", function(value, element) {
		if(value==null || $.trim(value)==""){
			return true;
		}
		var reg = /\d$/; 
		var mutipTel=$.trim(value);
		var tels=mutipTel.split(" ");
		var len=tels.length;
		for(var i=0;i<len;i++){
			if(!reg.test(tels[i])){
				return false;
			}
		}
		return true;  
	}, "请用空格分割多个电话号码");
	$.validator.addMethod("endTimeBigThanCurrentTimeValid", function(value, element) {
		if(value==null || $.trim(value)==""){
			return false;
		}
		var d1_arr = value.split(" ");
		var day1_arr = d1_arr[0].split("-");
		var hour1_arr = d1_arr[1].split(":");
		var year=parseInt(day1_arr[0]);
		var month=parseInt(day1_arr[1])-1;
		var day=parseInt(day1_arr[2]);
		var hour=parseInt(hour1_arr[0]);
		var minute=parseInt(hour1_arr[1]);
		var second=parseInt(hour1_arr[2]);
		var date1 = new Date(year,month,day,hour,minute,second);
		var nowTime=new Date();			
		if(date1.getTime()<nowTime.getTime()){
			return false;
		}
		return true;
	}, "结束时间不能早于当前系统时间");
	$.validator.addMethod("endDateTimeValid",function(value, element) { 
		var startDateID = $(element).attr('cmpDate'); 
		var d1_arr = $("#" + startDateID).val().split(" ");
		var d2_arr = value.split(" ");
		var day1_arr = d1_arr[0].split("-");
		var day2_arr = d2_arr[0].split("-");
		var hour1_arr = d1_arr[1].split(":");
		var hour2_arr = d2_arr[1].split(":");
		var date1 = new Date(parseInt(day1_arr[0]),parseInt(day1_arr[1]),parseInt(day1_arr[2]),
		                parseInt(hour1_arr[0]),parseInt(hour1_arr[1]),parseInt(hour1_arr[2]));
		var date2 = new Date(parseInt(day2_arr[0]),parseInt(day2_arr[1]),parseInt(day2_arr[2]),
						parseInt(hour2_arr[0]),parseInt(hour2_arr[1]),parseInt(hour2_arr[2]));
		var time1 = date1.getTime();
		var time2 = date2.getTime();			
		return time1>time2?false:true;
	}, "必须晚于开始时间!");
			
	$.validator.addMethod("endDateValid",function(value, element) { 
		var startDateID = $(element).attr('cmpDate'); 
		var d1_arr = $("#" + startDateID).val();
		var d2_arr = value;
		var day1_arr = d1_arr.split("-");
		var day2_arr = d2_arr.split("-"); 
		var date1 = new Date(parseInt(day1_arr[0]),parseInt(day1_arr[1]),parseInt(day1_arr[2]));
		var date2 = new Date(parseInt(day2_arr[0]),parseInt(day2_arr[1]),parseInt(day2_arr[2]));
		var time1 = date1.getTime();
		var time2 = date2.getTime();
		return time1>time2?false:true;			
		
	}, "必须晚于开始日期!"); 
	
});