/**
 * 初始化客户充值管理详情对话框
 */
var CustomerChargeInfoDlg = {
    customerChargeInfoData : {}
};

/**
 * 清除数据
 */
CustomerChargeInfoDlg.clearData = function() {
    this.customerChargeInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerChargeInfoDlg.set = function(key, val) {
    this.customerChargeInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerChargeInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
CustomerChargeInfoDlg.close = function() {
    parent.layer.close(window.parent.CustomerCharge.layerIndex);
}

/**
 * 收集数据
 */
CustomerChargeInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
CustomerChargeInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customerCharge/add", function(data){
        Feng.success("添加成功!");
        window.parent.CustomerCharge.table.refresh();
        CustomerChargeInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerChargeInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
CustomerChargeInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customerCharge/update", function(data){
        Feng.success("修改成功!");
        window.parent.CustomerCharge.table.refresh();
        CustomerChargeInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerChargeInfoData);
    ajax.start();
}

$(function() {

});
