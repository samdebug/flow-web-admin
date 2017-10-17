/**
 * 初始化接入通道详情对话框
 */
var AccessChannelInfoInfoDlg = {
	accessChannelInfoInfoData : {}
};

/**
 * 清除数据
 */
AccessChannelInfoInfoDlg.clearData = function() {
	this.accessChannelInfoInfoData = {};
}

/**
 * 设置对话框中的数据
 * 
 * @param key
 *            数据的名称
 * @param val
 *            数据的具体值
 */
AccessChannelInfoInfoDlg.set = function(key, val) {
	this.accessChannelInfoInfoData[key] = (typeof value == "undefined") ? $(
			"#" + key).val() : value;
	return this;
}

/**
 * 设置对话框中的数据
 * 
 * @param key
 *            数据的名称
 * @param val
 *            数据的具体值
 */
AccessChannelInfoInfoDlg.get = function(key) {
	return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
AccessChannelInfoInfoDlg.close = function() {
	parent.layer.close(window.parent.AccessChannelInfo.layerIndex);
}

/**
 * 收集数据
 */
AccessChannelInfoInfoDlg.collectData = function() {
	var _array = $("form").serializeArray();
	if (!_array || _array.length <= 0) {
		return;
	}
	$
			.each(
					_array,
					function() {
						AccessChannelInfoInfoDlg.accessChannelInfoInfoData[this.name] = this.value;
					});
}

/**
 * 提交添加
 */
AccessChannelInfoInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
	if(!checkInputValue()){
		return;
	}
	this.clearData();
	this.collectData();

	// 提交信息
	var ajax = new $ax(Feng.ctxPath + "/accessChannelInfo/add", function(data) {
		if (data.code == 200) {
			Feng.success("添加成功!");
			window.parent.AccessChannelInfo.table.refresh();
			AccessChannelInfoInfoDlg.close();
		} else {
			Feng.error(data.message);
		}
	}, function(data) {
		Feng.error("添加失败!" + data.responseJSON.message + "!");
	});
	ajax.set(this.accessChannelInfoInfoData);
	ajax.start();
}

AccessChannelInfoInfoDlg.addGroutSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
	this.clearData();
	this.collectData();
	// 提交信息
	var ajax = new $ax(Feng.ctxPath + "/accessChannelInfo/addToGroup",
			function(data) {
				if (data.success == true) {
					Feng.success("添加成功!");
					AccessChannelInfoInfoDlg.close();
					window.parent.AccessChannelInfo.table.refresh();
				} else {
					Feng.error(data.message);
				}
			}, function(data) {
				Feng.error("添加失败!" + data.responseJSON.message + "!");
			});
	ajax.set(this.accessChannelInfoInfoData);
	ajax.start();
}

/**
 * 提交修改
 */
AccessChannelInfoInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
	if(!checkInputValue()){
		return;
	}
	this.clearData();
	this.collectData();

	// 提交信息
	var ajax = new $ax(Feng.ctxPath + "/accessChannelInfo/update", function(
			data) {
		Feng.success("修改成功!");
		window.parent.AccessChannelInfo.table.refresh();
		AccessChannelInfoInfoDlg.close();
	}, function(data) {
		Feng.error("修改失败!" + data.responseJSON.message + "!");
	});
	ajax.set(this.accessChannelInfoInfoData);
	ajax.start();
}

$(function() {

});
