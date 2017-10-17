/**
 * 初始化通道错误日志详情对话框
 */
var ChannelErrorLogInfoDlg = {
    channelErrorLogInfoData : {}
};

/**
 * 清除数据
 */
ChannelErrorLogInfoDlg.clearData = function() {
    this.channelErrorLogInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelErrorLogInfoDlg.set = function(key, val) {
    this.channelErrorLogInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelErrorLogInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelErrorLogInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelErrorLog.layerIndex);
}

/**
 * 收集数据
 */
ChannelErrorLogInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
ChannelErrorLogInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelErrorLog/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelErrorLog.table.refresh();
        ChannelErrorLogInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelErrorLogInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelErrorLogInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelErrorLog/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelErrorLog.table.refresh();
        ChannelErrorLogInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelErrorLogInfoData);
    ajax.start();
}

$(function() {

});
