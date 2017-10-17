/**
 * 初始化通道维护信息详情对话框
 */
var ChannelMaintainInfoInfoDlg = {
    channelMaintainInfoInfoData : {}
};

/**
 * 清除数据
 */
ChannelMaintainInfoInfoDlg.clearData = function() {
    this.channelMaintainInfoInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelMaintainInfoInfoDlg.set = function(key, val) {
    this.channelMaintainInfoInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelMaintainInfoInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelMaintainInfoInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelMaintainInfo.layerIndex);
}

/**
 * 收集数据
 */
ChannelMaintainInfoInfoDlg.collectData = function() {
	 this.set('maintainSeqId').set('maintainName').set('operatorCode').set('channelType').set('startTime').set('endTime')
	 .set('remark').set('zone').set('notice').set('isValid');
}

/**
 * 提交添加
 */
ChannelMaintainInfoInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelMaintainInfo/add", function(data){
        Feng.success("添加成功!");
        window.parent.ChannelMaintainInfo.table.refresh();
        ChannelMaintainInfoInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelMaintainInfoInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelMaintainInfoInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelMaintainInfo/update", function(data){
        Feng.success("修改成功!");
        window.parent.ChannelMaintainInfo.table.refresh();
        ChannelMaintainInfoInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelMaintainInfoInfoData);
    ajax.start();
}


ChannelMaintainInfoInfoDlg.showAreaCode= function () {
	// 全国区域展示
	$.ajax({
		url : Feng.ctxPath + '/channelMaintainInfo/selectAreaCodeAll',
		type : 'get',
		dataType : 'json',
		success : function(data) {
			var html = '<option value="">请选择区域</option>';
			for (var i = 0; i < data.length; i++) {
				html += '<option value="'+data[i].areaCode+'">'
						+ data[i].areaName + '</option>';
			}
			$("#zone").html(html);
		}
	});
}

ChannelMaintainInfoInfoDlg.dateFmt=function(pattern,value){
	if(value!=undefined&&value!=""){
		return $.formatDate(pattern, value)
	}
	return '';
}




$(function() {
});
