/**
 * 初始化供应商出款数据统计详情对话框
 */
var SupplierDataStatisticalInfoDlg = {
    supplierDataStatisticalInfoData : {}
};

/**
 * 清除数据
 */
SupplierDataStatisticalInfoDlg.clearData = function() {
    this.supplierDataStatisticalInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
SupplierDataStatisticalInfoDlg.set = function(key, val) {
    this.supplierDataStatisticalInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
SupplierDataStatisticalInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
SupplierDataStatisticalInfoDlg.close = function() {
    parent.layer.close(window.parent.SupplierDataStatistical.layerIndex);
}

/**
 * 收集数据
 */
SupplierDataStatisticalInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
SupplierDataStatisticalInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/supplierDataStatistical/add", function(data){
        Feng.success("添加成功!");
        window.parent.SupplierDataStatistical.table.refresh();
        SupplierDataStatisticalInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.supplierDataStatisticalInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
SupplierDataStatisticalInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/supplierDataStatistical/update", function(data){
        Feng.success("修改成功!");
        window.parent.SupplierDataStatistical.table.refresh();
        SupplierDataStatisticalInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.supplierDataStatisticalInfoData);
    ajax.start();
}

$(function() {

});
