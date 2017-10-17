/**
 * 初始化通道监控详情对话框
 */
var ChannelMonitorInfoDlg = {
    channelMonitorInfoData : {}
};

/**
 * 清除数据
 */
ChannelMonitorInfoDlg.clearData = function() {
    this.channelMonitorInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelMonitorInfoDlg.set = function(key, val) {
    this.channelMonitorInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelMonitorInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelMonitorInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelMonitor.layerIndex);
}

/**
 * 收集数据
 */
ChannelMonitorInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
ChannelMonitorInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelMonitor/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelMonitor.table.refresh();
        ChannelMonitorInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelMonitorInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelMonitorInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelMonitor/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelMonitor.table.refresh();
        ChannelMonitorInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelMonitorInfoData);
    ajax.start();
}

$(function() {

});
