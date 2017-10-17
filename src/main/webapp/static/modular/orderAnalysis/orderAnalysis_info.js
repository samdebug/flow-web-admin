/**
 * 初始化订单分析报表详情对话框
 */
var OrderAnalysisInfoDlg = {
    orderAnalysisInfoData : {}
};

/**
 * 清除数据
 */
OrderAnalysisInfoDlg.clearData = function() {
    this.orderAnalysisInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
OrderAnalysisInfoDlg.set = function(key, val) {
    this.orderAnalysisInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
OrderAnalysisInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
OrderAnalysisInfoDlg.close = function() {
    parent.layer.close(window.parent.OrderAnalysis.layerIndex);
}

/**
 * 收集数据
 */
OrderAnalysisInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
OrderAnalysisInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/orderAnalysis/add", function(data){
        Feng.success("添加成功!");
        window.parent.OrderAnalysis.table.refresh();
        OrderAnalysisInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.orderAnalysisInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
OrderAnalysisInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/orderAnalysis/update", function(data){
        Feng.success("修改成功!");
        window.parent.OrderAnalysis.table.refresh();
        OrderAnalysisInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.orderAnalysisInfoData);
    ajax.start();
}

$(function() {

});
