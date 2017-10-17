/**
 * 初始化流量告警配置详情对话框
 */
var FlowWarnConfInfoDlg = {
    flowWarnConfInfoData : {}
};

/**
 * 清除数据
 */
FlowWarnConfInfoDlg.clearData = function() {
    this.flowWarnConfInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowWarnConfInfoDlg.set = function(key, val) {
    this.flowWarnConfInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowWarnConfInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowWarnConfInfoDlg.close = function() {
    parent.layer.close(window.parent.FlowWarnConf.layerIndex);
}

/**
 * 收集数据
 */
FlowWarnConfInfoDlg.collectData = function() {
	this.set('warnConfId').set('updId').set('warnType').
	set('failIntervalMin').set('notifyLlist').set('notifyIntervalMin').
	set('isMonthLastTwoDayNotify').set('failNum').set('isValid').
	set('notifyTemplate').set('remark').set('notifyTimeQuantum').
	set('creator').set('createTime');
}

/**
 * 提交添加
 */
FlowWarnConfInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowWarnConf/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowWarnConf.table.refresh();
        FlowWarnConfInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowWarnConfInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowWarnConfInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowWarnConf/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowWarnConf.table.refresh();
        FlowWarnConfInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowWarnConfInfoData);
    ajax.start();
}

$(function() {
	
	//初始化下拉选择框数据
//	alert($("#warnTypeValue").val());
    $("#warnType").val($("#warnTypeValue").val());
    $("#isMonthLastTwoDayNotify").val($("#isMonthLastTwoDayNotifyValue").val());
    $("#isValid").val($("#isValidValue").val());

});
