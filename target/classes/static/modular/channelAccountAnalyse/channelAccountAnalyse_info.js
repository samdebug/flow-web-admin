/**
 * 初始化通道结算账单详情对话框
 */
var ChannelAccountAnalyseInfoDlg = {
    channelAccountAnalyseInfoData : {}
};

/**
 * 清除数据
 */
ChannelAccountAnalyseInfoDlg.clearData = function() {
    this.channelAccountAnalyseInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelAccountAnalyseInfoDlg.set = function(key, val) {
    this.channelAccountAnalyseInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelAccountAnalyseInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelAccountAnalyseInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelAccountAnalyse.layerIndex);
}

/**
 * 收集数据
 */
ChannelAccountAnalyseInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
ChannelAccountAnalyseInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelAccountAnalyse/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelAccountAnalyse.table.refresh();
        ChannelAccountAnalyseInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelAccountAnalyseInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelAccountAnalyseInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelAccountAnalyse/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelAccountAnalyse.table.refresh();
        ChannelAccountAnalyseInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelAccountAnalyseInfoData);
    ajax.start();
}

$(function() {

});
