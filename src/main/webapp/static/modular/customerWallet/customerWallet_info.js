/**
 * 初始化customerWallet详情对话框
 */
var CustomerWalletInfoDlg = {
    customerWalletInfoData : {}
};

/**
 * 清除数据
 */
CustomerWalletInfoDlg.clearData = function() {
    this.customerWalletInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerWalletInfoDlg.set = function(key, val) {
    this.customerWalletInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerWalletInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
CustomerWalletInfoDlg.close = function() {
    parent.layer.close(window.parent.CustomerWallet.layerIndex);
}

/**
 * 收集数据
 */
CustomerWalletInfoDlg.collectData = function() {
    this.set('id');
}

/**
 * 提交添加
 */
CustomerWalletInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customerWallet/add", function(data){
        Feng.success("添加成功!");
        window.parent.CustomerWallet.table.refresh();
        CustomerWalletInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerWalletInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
CustomerWalletInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customerWallet/update", function(data){
        Feng.success("修改成功!");
        window.parent.CustomerWallet.table.refresh();
        CustomerWalletInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerWalletInfoData);
    ajax.start();
}

$(function() {

});
