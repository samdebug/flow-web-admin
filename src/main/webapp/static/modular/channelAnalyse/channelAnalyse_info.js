/**
 * 初始化上游通道分析详情对话框
 */
var ChannelAnalyseInfoDlg = {
    channelAnalyseInfoData : {}
};

/**
 * 清除数据
 */
ChannelAnalyseInfoDlg.clearData = function() {
    this.channelAnalyseInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelAnalyseInfoDlg.set = function(key, val) {
    this.channelAnalyseInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelAnalyseInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelAnalyseInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelAnalyse.layerIndex);
}

/**
 * 收集数据
 */
ChannelAnalyseInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
ChannelAnalyseInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelAnalyse/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelAnalyse.table.refresh();
        ChannelAnalyseInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelAnalyseInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelAnalyseInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelAnalyse/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelAnalyse.table.refresh();
        ChannelAnalyseInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelAnalyseInfoData);
    ajax.start();
}

$(function() {

});
