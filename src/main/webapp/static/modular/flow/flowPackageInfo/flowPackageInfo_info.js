/**
 * 初始化流量包详情对话框
 * 
 */
var FlowPackageInfoDlg = {
    flowPackageInfoData : {}
};

/**
 * 清除数据
 */
FlowPackageInfoDlg.clearData = function() {
    this.flowPackageInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowPackageInfoDlg.set = function(key, val) {
    this.flowPackageInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowPackageInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowPackageInfoDlg.close = function() {
    parent.layer.close(window.parent.FlowPackageInfo.layerIndex);
}

/**
 * 收集数据
 */
FlowPackageInfoDlg.collectData = function() {
	var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		FlowPackageInfoDlg.flowPackageInfoData[this.name] = this.value;
    });
}

/**
 * 提交添加
 */
FlowPackageInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowPackageInfo/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowPackageInfo.table.refresh();
        FlowPackageInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowPackageInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowPackageInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowPackageInfo/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowPackageInfo.table.refresh();
        FlowPackageInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowPackageInfoData);
    ajax.start();
}


$(function() {
	
});
