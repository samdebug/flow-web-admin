/**
 * 初始化通道质量标准详情对话框
 */
var ChannelQualityQuotaInfoDlg = {
    channelQualityQuotaInfoData : {}
};

/**
 * 清除数据
 */
ChannelQualityQuotaInfoDlg.clearData = function() {
    this.channelQualityQuotaInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelQualityQuotaInfoDlg.set = function(key, val) {
    this.channelQualityQuotaInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelQualityQuotaInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelQualityQuotaInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelQualityQuota.layerIndex);
}

/**
 * 收集数据
 */
ChannelQualityQuotaInfoDlg.collectData = function() {
    this.set('quotaId').set('quotaName').set('timlyRateWeight').set('manualRatioWeight').set('timlyBaseLine').set('succRatioWeight')
    .set('priceRatioWeight').set('succBaseLine').set('remark');
}

/**
 * 提交添加
 */
ChannelQualityQuotaInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelQualityQuota/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelQualityQuota.table.refresh();
        ChannelQualityQuotaInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelQualityQuotaInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelQualityQuotaInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelQualityQuota/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelQualityQuota.table.refresh();
        ChannelQualityQuotaInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelQualityQuotaInfoData);
    ajax.start();
}

$(function() {

});
