/**
 * 初始化接入通道组详情对话框
 */
var AccessChannelGroupInfoDlg = {
    accessChannelGroupInfoData : {}
};

/**
 * 清除数据
 */
AccessChannelGroupInfoDlg.clearData = function() {
    this.accessChannelGroupInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
AccessChannelGroupInfoDlg.set = function(key, val) {
    this.accessChannelGroupInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
AccessChannelGroupInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
AccessChannelGroupInfoDlg.close = function() {
    parent.layer.close(window.parent.AccessChannelGroup.layerIndex);
}

/**
 * 收集数据
 */
AccessChannelGroupInfoDlg.collectData = function() {
	var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		AccessChannelGroupInfoDlg.accessChannelGroupInfoData[this.name] = this.value;
    });
}



/**
 * 提交添加
 */
AccessChannelGroupInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
	if (!checkInputValue()) {
		return;
	}
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/accessChannelGroup/add", function(data){
    	if (data.code==200) {
    		 Feng.success("添加成功!");
    	     window.parent.AccessChannelGroup.table.refresh();
    	     AccessChannelGroupInfoDlg.close();
		}else{
   		 Feng.error(data.message);
		}
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.accessChannelGroupInfoData);
    ajax.start();
}

/**
 * 提交添加
 */
AccessChannelGroupInfoDlg.addGroupSubmit = function() {
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/accessChannelGroup/groupAddToGroup", function(data){
    	if (data.success==true) {
    		 Feng.success("添加成功!");
    	     window.parent.AccessChannelGroup.table.refresh();
    	     AccessChannelGroupInfoDlg.close();
		}else{
   		 Feng.error(data.message);
		}
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.accessChannelGroupInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
AccessChannelGroupInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
	if(!checkInputValue()) {
		return;
	}
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/accessChannelGroup/update", function(data){
    	if (data.code==200) {
   		 Feng.success("修改成功!");
   	     window.parent.AccessChannelGroup.table.refresh();
   	     AccessChannelGroupInfoDlg.close();
		}else{
  		 Feng.error(data.message);
		}
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.accessChannelGroupInfoData);
    ajax.start();
}

$(function() {

});
