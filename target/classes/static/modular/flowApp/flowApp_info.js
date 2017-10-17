/**
 * 初始化合作伙伴详情对话框
 */
var FlowAppDlg = {
    FlowAppData : {}
};

/**
 * 清除数据
 */
FlowAppDlg.clearData = function() {
    this.FlowAppData = {};
}

/**
 * 关闭此对话框
 */
FlowAppDlg.close = function() {
    parent.layer.close(window.parent.FlowApp.layerIndex);
}

/**
 * 收集数据
 */
FlowAppDlg.collectData = function() {
//    this.set('id');
}

/**
 * 提交添加
 */
FlowAppDlg.submit = function() {

//    this.clearData();
//    this.collectData();
//
//    //提交信息
//    var ajax = new $ax(Feng.ctxPath + "/partner/add", function(data){
//        Feng.success("添加成功!");
//        window.parent.Partner.table.refresh();
//        FlowAppDlg.close();
//    },function(data){
//        Feng.error("添加失败!" + data.responseJSON.message + "!");
//    });
//    ajax.set(this.FlowAppData);
//    ajax.start();
}

/**
 * 提交修改
 */
FlowAppDlg.editSubmit = function() {

//    this.clearData();
//    this.collectData();
//
//    //提交信息
//    var ajax = new $ax(Feng.ctxPath + "/partner/update", function(data){
//        Feng.success("修改成功!");
//        window.parent.Partner.table.refresh();
//        FlowAppDlg.close();
//    },function(data){
//        Feng.error("修改失败!" + data.responseJSON.message + "!");
//    });
//    ajax.set(this.FlowAppData);
//    ajax.start();
}

$(function() {

});
