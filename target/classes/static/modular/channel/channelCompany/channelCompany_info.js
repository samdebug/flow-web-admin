/**
 * 初始化上游供应商详情对话框
 */
var ChannelCompanyInfoDlg = {
    channelCompanyInfoData : {}
};

/**
 * 清除数据
 */
ChannelCompanyInfoDlg.clearData = function() {
    this.channelCompanyInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelCompanyInfoDlg.set = function(key, val) {
    this.channelCompanyInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelCompanyInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelCompanyInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelCompany.layerIndex);
}


/**
 * 收集数据
 */
ChannelCompanyInfoDlg.collectData = function() {
	var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		ChannelCompanyInfoDlg.channelCompanyInfoData[this.name] = this.value;
    });
}


//defaultValue = function (id,value) {
//	$("#"+id+"").focus(function(){
//        if($(this).val()==value){
//            $(this).val("");
//        }
//    });
//
//    $("#b1").blur(function(){
//        if($(this).val()==""){
//            $(this).val(value);
//        }
//    });
//}


/**
 * 提交添加
 */
ChannelCompanyInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		tabValidate.validate();
		return;
	}
	this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelCompany/add", function(data){
    	if(data.code==200){
    		  Feng.success("添加成功!");
    	      window.parent.ChannelCompany.table.refresh();
    	      ChannelCompanyInfoDlg.close();
    	}else{
    		 Feng.error(data.message);
    	}
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelCompanyInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelCompanyInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		tabValidate.validate();
		return;
	}
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelCompany/update", function(data){
    	if(data.code==200){
  		  Feng.success("修改成功!");
  	      window.parent.ChannelCompany.table.refresh();
  	      ChannelCompanyInfoDlg.close();
  	}else{
  		 Feng.error(data.message);
  	}
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelCompanyInfoData);
    ajax.start();
}
