/**
 * 初始化充值明细详情对话框
 */
var ChannelCompanyRechargeInfoDlg = {
    channelCompanyRechargeInfoData : {}
};

/**
 * 清除数据
 */
ChannelCompanyRechargeInfoDlg.clearData = function() {
    this.channelCompanyRechargeInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelCompanyRechargeInfoDlg.set = function(key, val) {
    this.channelCompanyRechargeInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelCompanyRechargeInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelCompanyRechargeInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelCompanyRecharge.layerIndex);
}

/**
 * 收集数据
 */
ChannelCompanyRechargeInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
ChannelCompanyRechargeInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelCompanyRecharge/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelCompanyRecharge.table.refresh();
        ChannelCompanyRechargeInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelCompanyRechargeInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelCompanyRechargeInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelCompanyRecharge/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelCompanyRecharge.table.refresh();
        ChannelCompanyRechargeInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelCompanyRechargeInfoData);
    ajax.start();
}

$(function() {

});
