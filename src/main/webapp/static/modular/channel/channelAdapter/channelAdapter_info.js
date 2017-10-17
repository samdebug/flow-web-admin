/**
 * 初始化通道适配器详情对话框
 */
var ChannelAdapterInfoDlg = {
    channelAdapterInfoData : {}
};

/**
 * 清除数据
 */
ChannelAdapterInfoDlg.clearData = function() {
    this.channelAdapterInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelAdapterInfoDlg.set = function(key, val) {
    this.channelAdapterInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelAdapterInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelAdapterInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelAdapter.layerIndex);
}

/**
 * 收集数据
 */
ChannelAdapterInfoDlg.collectData = function() {
    this.set('adapterId').set('updId').set('adapterName').set('remark').set('clazzName').set('resendErrorCode').set('userErrorCode').set('notavailableErrorCode');
}


/**
 * 提交添加
 */
ChannelAdapterInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();
    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelAdapter/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelAdapter.table.refresh();
        ChannelAdapterInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelAdapterInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelAdapterInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelAdapter/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelAdapter.table.refresh();
        ChannelAdapterInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelAdapterInfoData);
    ajax.start();
}

$(function() {

});
