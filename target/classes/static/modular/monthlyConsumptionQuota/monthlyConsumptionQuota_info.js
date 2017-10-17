/**
 * 初始化上游通道每月消费额度详情对话框
 */
var MonthlyConsumptionQuotaInfoDlg = {
    monthlyConsumptionQuotaInfoData : {}
};

/**
 * 清除数据
 */
MonthlyConsumptionQuotaInfoDlg.clearData = function() {
    this.monthlyConsumptionQuotaInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
MonthlyConsumptionQuotaInfoDlg.set = function(key, val) {
    this.monthlyConsumptionQuotaInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
MonthlyConsumptionQuotaInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
MonthlyConsumptionQuotaInfoDlg.close = function() {
    parent.layer.close(window.parent.MonthlyConsumptionQuota.layerIndex);
}

/**
 * 收集数据
 */
MonthlyConsumptionQuotaInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
MonthlyConsumptionQuotaInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/monthlyConsumptionQuota/add", function(data){
        Feng.success("添加成功!");
        window.parent.MonthlyConsumptionQuota.table.refresh();
        MonthlyConsumptionQuotaInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.monthlyConsumptionQuotaInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
MonthlyConsumptionQuotaInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/monthlyConsumptionQuota/update", function(data){
        Feng.success("修改成功!");
        window.parent.MonthlyConsumptionQuota.table.refresh();
        MonthlyConsumptionQuotaInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.monthlyConsumptionQuotaInfoData);
    ajax.start();
}

$(function() {

});
