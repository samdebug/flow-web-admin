/**
 * 初始化流量告警信息详情对话框
 */
var FlowWarnMsgInfoDlg = {
    flowWarnMsgInfoData : {}
};

/**
 * 清除数据
 */
FlowWarnMsgInfoDlg.clearData = function() {
    this.flowWarnMsgInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowWarnMsgInfoDlg.set = function(key, val) {
    this.flowWarnMsgInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowWarnMsgInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowWarnMsgInfoDlg.close = function() {
    parent.layer.close(window.parent.FlowWarnMsg.layerIndex);
}

/**
 * 收集数据
 */
FlowWarnMsgInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
FlowWarnMsgInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowWarnMsg/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowWarnMsg.table.refresh();
        FlowWarnMsgInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowWarnMsgInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowWarnMsgInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowWarnMsg/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowWarnMsg.table.refresh();
        FlowWarnMsgInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowWarnMsgInfoData);
    ajax.start();
}

$(function() {

});
