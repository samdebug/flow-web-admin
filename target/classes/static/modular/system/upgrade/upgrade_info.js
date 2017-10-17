/**
 * 初始化客户端自动升级详情对话框
 */
var UpgradeInfoDlg = {
    upgradeInfoData : {}
};

/**
 * 清除数据
 */
UpgradeInfoDlg.clearData = function() {
    this.upgradeInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
UpgradeInfoDlg.set = function(key, val) {
    this.upgradeInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
UpgradeInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
UpgradeInfoDlg.close = function() {
    parent.layer.close(window.parent.Upgrade.layerIndex);
}

/**
 * 收集数据
 */
UpgradeInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
UpgradeInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/upgrade/add", function(data){
        Feng.success("添加成功!");
        window.parent.Upgrade.table.refresh();
        UpgradeInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.upgradeInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
UpgradeInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/upgrade/update", function(data){
        Feng.success("修改成功!");
        window.parent.Upgrade.table.refresh();
        UpgradeInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.upgradeInfoData);
    ajax.start();
}

$(function() {

});
