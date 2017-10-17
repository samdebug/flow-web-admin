/**
 * 初始化流量分发异常订单记录详情对话框
 */
var FlowOrderInfoExceptionInfoDlg = {
    flowOrderInfoExceptionInfoData : {}
};

/**
 * 清除数据
 */
FlowOrderInfoExceptionInfoDlg.clearData = function() {
    this.flowOrderInfoExceptionInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowOrderInfoExceptionInfoDlg.set = function(key, val) {
    this.flowOrderInfoExceptionInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowOrderInfoExceptionInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowOrderInfoExceptionInfoDlg.close = function() {
    parent.layer.close(window.parent.FlowOrderInfoException.layerIndex);
}

/**
 * 收集数据
 */
FlowOrderInfoExceptionInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
FlowOrderInfoExceptionInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowOrderInfoException/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowOrderInfoException.table.refresh();
        FlowOrderInfoExceptionInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowOrderInfoExceptionInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowOrderInfoExceptionInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowOrderInfoException/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowOrderInfoException.table.refresh();
        FlowOrderInfoExceptionInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowOrderInfoExceptionInfoData);
    ajax.start();
}

$(function() {

});
