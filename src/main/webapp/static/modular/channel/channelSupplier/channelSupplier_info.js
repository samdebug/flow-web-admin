/**
 * 初始化通道供应商详情对话框
 */
var ChannelSupplierInfoDlg = {
    channelSupplierInfoData : {}
};

/**
 * 清除数据
 */
ChannelSupplierInfoDlg.clearData = function() {
    this.channelSupplierInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelSupplierInfoDlg.set = function(key, val) {
    this.channelSupplierInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
ChannelSupplierInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
ChannelSupplierInfoDlg.close = function() {
    parent.layer.close(window.parent.ChannelSupplier.layerIndex);
}

/**
 * 收集数据
 */
ChannelSupplierInfoDlg.collectData = function() {
	var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		ChannelSupplierInfoDlg.channelSupplierInfoData[this.name] = this.value;
    });
}

/**
 * 提交添加
 */
ChannelSupplierInfoDlg.addSubmit = function() {
	if (!$("form").valid()) {
		tabValidate.validate();
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelSupplier/add", function(data){
    	if(data.code==200){
    		 Feng.success(data.message);
    		 window.parent.ChannelSupplier.table.refresh();
    		 ChannelSupplierInfoDlg.close();
    	}else{
   		 Feng.error(data.message);
    	}
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelSupplierInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
ChannelSupplierInfoDlg.editSubmit = function() {
	if (!$("form").valid()) {
		tabValidate.validate();
		return;
	}
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/channelSupplier/update", function(data){
    	if(data.code==200){
   		 Feng.success(data.message);
   		window.parent.ChannelSupplier.table.refresh();
   		 ChannelSupplierInfoDlg.close();
    	}else{
	  		 Feng.error(data.message);
	   	}
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.channelSupplierInfoData);
    ajax.start();
}

/**
 * 初始化select2组件
 * @param data
 */
ChannelSupplierInfoDlg.initSelect2 = function (id) {
    $("#"+id).removeClass().css("width", "100%").select2({
        minimumInputLength: 1,
        ajax: {
            url: Feng.ctxPath + '/channelCompany/selectCompanyByName',
            dataType: 'json',
            data: function(term) {
                return {
                    "companyName": term
                };
            },
            results: function(data) {
                return {
                    results: $.map(data.channelCompanyList, function(item) {
                        return {
                            id: item.companyId,
                            text: item.companyName
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
ChannelSupplierInfoDlg.initModifyViewStatus = function () {
    var obj = {};
    obj["id"] = $("#companyId").val();
    obj["text"] =$("#companyName").val();
    if(obj["text"]!=null && obj["text"]!=''){
    $("#company-select").select2("data", obj);
    }
}

$(function() {
	ChannelSupplierInfoDlg.initSelect2("company-select");
	ChannelSupplierInfoDlg.initModifyViewStatus();
    $("#editBtn", window.parent.document).attr("id","channelSupplierEditBtn");

    function initButton(){
        var supplierCode = $("#supplierCode").text();
        if (window.parent.document != null){
           $(".layui-layer", window.parent.document).last().click(function(e){
                if (e.target.parentElement.id == "channelSupplierEditBtn"){
                    $(".layui-layer-shade", window.parent.document).hide();
                    $(".layui-layer", window.parent.document).hide();
                     window.parent.ChannelSupplier.editEvent(supplierCode);
                }
            })
        }
    }
    initButton();
});
