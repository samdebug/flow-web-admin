/**
 * 初始化产品设置详情对话框
 */
var FlowProductInfoInfoDlg = {
    flowProductInfoInfoData : {}
};

/**
 * 清除数据
 */
FlowProductInfoInfoDlg.clearData = function() {
    this.flowProductInfoInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowProductInfoInfoDlg.set = function(key, val) {
    this.flowProductInfoInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
FlowProductInfoInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
FlowProductInfoInfoDlg.close = function() {
    parent.layer.close(window.parent.FlowProductInfo.layerIndex);
}

/**
 * 收集数据
 */
FlowProductInfoInfoDlg.collectData = function() {
    this.set('productId').set('productCode').set('productType').set('productDesc').set('productName').
    set('productPrice').set('operatorCode').set('zone');
    var pid=$("#packageId").val();
    this.set('packageId',pid);
    var pt=$("#productType").val();
    if(pt==3)　{
    	this.set('cardType').set('channelType');
    }
}

/**
 * 提交添加
 */
FlowProductInfoInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowProductInfo/add", function(data){
        Feng.success("添加成功!");
        window.parent.FlowProductInfo.table.refresh();
        FlowProductInfoInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowProductInfoInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
FlowProductInfoInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/flowProductInfo/update", function(data){
        Feng.success("修改成功!");
        window.parent.FlowProductInfo.table.refresh();
        FlowProductInfoInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.flowProductInfoInfoData);
    ajax.start();
}


var map={};
/**
 * 初始化select2组件
 * @param data
 */
FlowProductInfoInfoDlg.initSelect2 = function (id) {
	
	$("#"+id).removeClass().css("width", "260px").select2({
        minimumInputLength: 1,
        ajax: {
            url: Feng.ctxPath + '/flowProductInfo/selectPackageByName',
            dataType: 'json',
            data: function(term) {
            	var zone = $("#zone").val();
                return {
                	"params['zone']": zone,
                	"params['packageName']": term,
                };
            },
            results: function(data) {
                return {
                    results: $.map(data.packageList, function(item) {
                    	map[item.packageId]=item.operatorName;
                        return {
                            id: item.packageId,
                            text: item.packageName
                        }
                    })
                };
            }
        }
    });
}

/**
 * 初始化修改页面的值状态
 */
FlowProductInfoInfoDlg.initModifyViewStatus = function () {
    var obj = {};
    obj["id"] = $("#packageId").val();
    obj["text"] =$("#packageName").val();
    if(obj["text"]!=null && obj["text"]!=''){
    	$("#packageId").select2("data", obj);
    }
}

$(function() {
	FlowProductInfoInfoDlg.initSelect2("packageId");
	FlowProductInfoInfoDlg.initModifyViewStatus();
	
	$("#packageId").change(function(){
		var st=$("#packageId").val();
		$("#operatorCode").val(map[st]);
	});
	
	$("#productType").change(function(){
		var pt=$("#productType").val();
		if(pt==3)　{
			$("#plusProduct").show();
		} else {
			$("#plusProduct").hide();
		}
	})
});
