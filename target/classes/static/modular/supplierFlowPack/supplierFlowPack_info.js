/**
 * 初始化供应商流量包对账明细详情对话框
 */
var SupplierFlowPackInfoDlg = {
    supplierFlowPackInfoData : {}
};

/**
 * 清除数据
 */
SupplierFlowPackInfoDlg.clearData = function() {
    this.supplierFlowPackInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
SupplierFlowPackInfoDlg.set = function(key, val) {
    this.supplierFlowPackInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
SupplierFlowPackInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
SupplierFlowPackInfoDlg.close = function() {
    parent.layer.close(window.parent.SupplierFlowPack.layerIndex);
}

/**
 * 收集数据
 */
SupplierFlowPackInfoDlg.collectData = function() {
    this.set('id').set('supplierName').set('channelName');
}

/**
 * 提交添加
 */
SupplierFlowPackInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/supplierFlowPack/add", function(data){
        Feng.success("添加成功!");
        window.parent.SupplierFlowPack.table.refresh();
        SupplierFlowPackInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.supplierFlowPackInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
SupplierFlowPackInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/supplierFlowPack/update", function(data){
        Feng.success("修改成功!");
        window.parent.SupplierFlowPack.table.refresh();
        SupplierFlowPackInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.supplierFlowPackInfoData);
    ajax.start();
}

$(function() {

});
