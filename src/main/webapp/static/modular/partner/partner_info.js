/**
 * 初始化合作伙伴详情对话框
 */
var PartnerInfoDlg = {
    partnerInfoData : {}
};

/**
 * 清除数据
 */
PartnerInfoDlg.clearData = function() {
    this.partnerInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
PartnerInfoDlg.set = function(key, val) {
    this.partnerInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
PartnerInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
PartnerInfoDlg.close = function() {
    parent.layer.close(window.parent.Partner.layerIndex);
}

/**
 * 收集数据
 */
PartnerInfoDlg.collectData = function() {
//    this.set('id');
	var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		PartnerInfoDlg.partnerInfoData[this.name] = this.value;
    });
}

/**
 * 提交添加
 */
PartnerInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();
    
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/partner/add", function(data){
        Feng.success("添加成功!");
        window.parent.Partner.table.refresh();
        PartnerInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.partnerInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
PartnerInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/partner/update", function(data){
        Feng.success("修改成功!");
        window.parent.Partner.table.refresh();
        PartnerInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.partnerInfoData);
    ajax.start();
}

$(function() {

});
