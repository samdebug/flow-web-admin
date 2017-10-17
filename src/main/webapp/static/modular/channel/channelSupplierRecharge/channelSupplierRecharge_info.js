/**
 * 初始化通道供应商充值明细详情对话框
 */
var ChannelSupplierRechargeInfoDlg = {
    channelSupplierRechargeInfoData : {}
};

/**
 * 清除数据
 */
ChannelSupplierRechargeInfoDlg.clearData = function() {
    this.channelSupplierRechargeInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelSupplierRechargeInfoDlg.set = function(key, val) {
    this.channelSupplierRechargeInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelSupplierRechargeInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelSupplierRechargeInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelSupplierRecharge.layerIndex);
}

/**
 * 收集数据
 */
ChannelSupplierRechargeInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
ChannelSupplierRechargeInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelSupplierRecharge/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelSupplierRecharge.table.refresh();
        ChannelSupplierRechargeInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelSupplierRechargeInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelSupplierRechargeInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelSupplierRecharge/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelSupplierRecharge.table.refresh();
        ChannelSupplierRechargeInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelSupplierRechargeInfoData);
    ajax.start();
}

$(function() {

});
