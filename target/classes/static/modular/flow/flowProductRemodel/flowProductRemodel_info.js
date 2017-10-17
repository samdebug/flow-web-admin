/**
 * 初始化产品设置详情对话框
 */
var FlowProductRemodelDlg = {
		FlowProductRemodelData : {}
};

/**
 * 清除数据
 */
FlowProductRemodelDlg.clearData = function() {
    this.FlowProductRemodelData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowProductRemodelDlg.set = function(key, val) {
    this.FlowProductRemodelData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowProductRemodelDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowProductRemodelDlg.close = function() {
    parent.layer.close(window.parent.FlowProductRemodel.layerIndex);
}

/**
 * 收集数据
 */
FlowProductRemodelDlg.collectData = function() {
	var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		FlowProductRemodelDlg.FlowProductRemodelData[this.name] = this.value;
    });
}

/**
 * 提交添加
 */
FlowProductRemodelDlg.addSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowProductRemodel/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowProductRemodel.table.refresh();
        FlowProductRemodelDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.FlowProductRemodelData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowProductRemodelDlg.editSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowProductRemodel/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowProductRemodel.table.refresh();
        FlowProductRemodelDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.FlowProductRemodelData);
    ajax.start();
}


$(function() {
	
});
