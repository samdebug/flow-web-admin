/**
 * 初始化通道变更记录详情对话框
 */
var AccessChannelInfoHistoryInfoDlg = {
    accessChannelInfoHistoryInfoData : {}
};

/**
 * 清除数据
 */
AccessChannelInfoHistoryInfoDlg.clearData = function() {
    this.accessChannelInfoHistoryInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
AccessChannelInfoHistoryInfoDlg.set = function(key, val) {
    this.accessChannelInfoHistoryInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
AccessChannelInfoHistoryInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
AccessChannelInfoHistoryInfoDlg.close = function() {
    parent.layer.close(window.parent.AccessChannelInfoHistory.layerIndex);
}

/**
 * 收集数据
 */
AccessChannelInfoHistoryInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
AccessChannelInfoHistoryInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/accessChannelInfoHistory/add", function(data){
        Feng.success("添加成功!");
        window.parent.AccessChannelInfoHistory.table.refresh();
        AccessChannelInfoHistoryInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.accessChannelInfoHistoryInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
AccessChannelInfoHistoryInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/accessChannelInfoHistory/update", function(data){
        Feng.success("修改成功!");
        window.parent.AccessChannelInfoHistory.table.refresh();
        AccessChannelInfoHistoryInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.accessChannelInfoHistoryInfoData);
    ajax.start();
}

$(function() {

});
