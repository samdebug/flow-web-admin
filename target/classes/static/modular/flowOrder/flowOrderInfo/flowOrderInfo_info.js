/**
 * 初始化流量订单详情对话框
 */
var FlowOrderInfoInfoDlg = {
    flowOrderInfoInfoData : {}
};

/**
 * 清除数据
 */
FlowOrderInfoInfoDlg.clearData = function() {
    this.flowOrderInfoInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowOrderInfoInfoDlg.set = function(key, val) {
    this.flowOrderInfoInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowOrderInfoInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowOrderInfoInfoDlg.close = function() {
    parent.layer.close(window.parent.FlowOrderInfo.layerIndex);
}

/**
 * 收集数据
 */
FlowOrderInfoInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
FlowOrderInfoInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowOrderInfo/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowOrderInfo.table.refresh();
        FlowOrderInfoInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowOrderInfoInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowOrderInfoInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowOrderInfo/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowOrderInfo.table.refresh();
        FlowOrderInfoInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowOrderInfoInfoData);
    ajax.start();
}

$(function() {

});
