/**
 * 初始化合作伙伴账单管理详情对话框
 */
var PartnerBillInfoDlg = {
    partnerBillInfoData : {}
};

/**
 * 清除数据
 */
PartnerBillInfoDlg.clearData = function() {
    this.partnerBillInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
PartnerBillInfoDlg.set = function(key, val) {
    this.partnerBillInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
PartnerBillInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
PartnerBillInfoDlg.close = function() {
    parent.layer.close(window.parent.PartnerBill.layerIndex);
}

/**
 * 收集数据
 */
PartnerBillInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
PartnerBillInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/partnerBill/add", function(data){
        Feng.success("添加成功!");
        window.parent.PartnerBill.table.refresh();
        PartnerBillInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.partnerBillInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
PartnerBillInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/partnerBill/update", function(data){
        Feng.success("修改成功!");
        window.parent.PartnerBill.table.refresh();
        PartnerBillInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.partnerBillInfoData);
    ajax.start();
}


/**
 * 提交在线充值信息
 */
PartnerBillInfoDlg.toOnlinPay = function() {
	
	Feng.success("开发中......");
}

$(function() {

	
});
