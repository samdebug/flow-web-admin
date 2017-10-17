/**
 * 初始化客户结算账单详情对话框
 */
var CustomerAccountAnalyseInfoDlg = {
    customerAccountAnalyseInfoData : {}
};

/**
 * 清除数据
 */
CustomerAccountAnalyseInfoDlg.clearData = function() {
    this.customerAccountAnalyseInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerAccountAnalyseInfoDlg.set = function(key, val) {
    this.customerAccountAnalyseInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerAccountAnalyseInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
CustomerAccountAnalyseInfoDlg.close = function() {
    parent.layer.close(window.parent.CustomerAccountAnalyse.layerIndex);
}

/**
 * 收集数据
 */
CustomerAccountAnalyseInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
CustomerAccountAnalyseInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customerAccountAnalyse/add", function(data){
        Feng.success("添加成功!");
        window.parent.CustomerAccountAnalyse.table.refresh();
        CustomerAccountAnalyseInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerAccountAnalyseInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
CustomerAccountAnalyseInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customerAccountAnalyse/update", function(data){
        Feng.success("修改成功!");
        window.parent.CustomerAccountAnalyse.table.refresh();
        CustomerAccountAnalyseInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerAccountAnalyseInfoData);
    ajax.start();
}

$(function() {

});
