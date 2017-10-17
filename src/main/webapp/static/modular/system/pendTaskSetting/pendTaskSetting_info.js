/**
 * 初始化设置个性化参数详情对话框
 */
var PendTaskSettingInfoDlg = {
    pendTaskSettingInfoData : {}
};

/**
 * 清除数据
 */
PendTaskSettingInfoDlg.clearData = function() {
    this.pendTaskSettingInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
PendTaskSettingInfoDlg.set = function(key, val) {
    this.pendTaskSettingInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
PendTaskSettingInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
PendTaskSettingInfoDlg.close = function() {
    parent.layer.close(window.parent.PendTaskSetting.layerIndex);
}

/**
 * 收集数据
 */
PendTaskSettingInfoDlg.collectData = function() {
    this.set('id').set('sendemail');
}

/**
 * 提交添加
 */
PendTaskSettingInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/pendTaskSetting/add", function(data){
        Feng.success("添加成功!");
        window.parent.PendTaskSetting.table.refresh();
        PendTaskSettingInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.pendTaskSettingInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
PendTaskSettingInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/pendTaskSetting/update", function(data){
        Feng.success("修改成功!");
        window.parent.PendTaskSetting.table.refresh();
        PendTaskSettingInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.pendTaskSettingInfoData);
    ajax.start();
}

$(function() {

});
