/**
 * 流量订单管理初始化
 */
var FlowOrderInfo = {
    id: "FlowOrderInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData:{},
    isShow:false
};

/**
 * 初始化表格的列
 */
FlowOrderInfo.initColumn = function () {
    return [
        {field: 'selectItem', checkBox: true},
        {title: '分发订单号', field: 'orderId', align: 'center', valign: 'middle',formatter : formatName},
        {title: '分发订单号字符串', field: 'orderIdStr', visible: false, align: 'center', valign: 'middle'},
        {title: 'CP订单号', field: 'extorderId', align: 'center', valign: 'middle'},
        {title: '应用ID', field: 'appId', align: 'center', valign: 'middle'},
        {title: '流量包ID', field: 'packageId', align: 'center', valign: 'middle'},
        {title: '手机号码', field: 'usedMobile', align: 'center', valign: 'middle'},
        {title: '运营商', field: 'mobileOperator', align: 'center', valign: 'middle',formatter:formatOperatorName},
        {title: '归属地', field: 'mobileHome', align: 'center', valign: 'middle',formatter:formatMobileHome},
        {title: '收单时间', field: 'applyDate', align: 'center', valign: 'middle'},
        {title: '发送时间', field: 'activeDate', align: 'center', valign: 'middle'},
        {title: '回调时间', field: 'checkTime', align: 'center', valign: 'middle'},
        {title: '订单状态', field: 'statusDesc', align: 'center', valign: 'middle',formatter:formatStateDesc},
        {title: '回调状态', field: 'dealFlagDesc', align: 'center', valign: 'middle'},
        {title: '价格', field: 'price', align: 'center', valign: 'middle'},
        {title: '流量类型', field: 'flowRangeDesc', align: 'center', valign: 'middle'}
    ];
};


function formatName(cellvalue, rowObject,index) {
	return '<a href="javascript:;" onclick="viewEvent(\''
			+ rowObject['orderId'] + '\')">' + cellvalue + '</a>';
}


function formatOrderType(cellvalue, rowObject,index) {
	if (cellvalue == '1'){
		return '流量包';
	}else if (cellvalue == '3'){
		return '话费';
	}
	return '';
}
function formatOperatorName(cellvalue, rowObject,index) {
	if(cellvalue=='YD'){
		return '移动';
	}else if(cellvalue=='LT'){
		return '联通';
	}else if(cellvalue=='DX'){
		return '电信'
	}
	return cellvalue;
}
function formatMobileHome(cellvalue, rowObject,index) {
	var cellarray=cellvalue.split('-');
	if(cellarray.length==3){
		return cellarray[0]+'-'+cellarray[1];
	}else{
		return cellvalue;
	}
}

function formatStateDesc(cellvalue, rowObject,index) {
	if(cellvalue=='成功'){
		return '<div style=\"background:#008000;color:#ffffff;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
	}else if(cellvalue=='失败'){
		return '<div style=\"background:#FF0000;color:#ffffff;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
	}else {
		return cellvalue;
	}
}

function formatPackageName(cellvalue, rowObject,index) {
	var packageName = "",flowPackageInfo = rowObject['flowPackageInfo'];
	if (flowPackageInfo){
		packageName = flowPackageInfo['packageName'];
	}
	return packageName;
}

function reCallBack(orderId){
	if (!orderId){
		orderId = FlowOrderInfo.getSelectRows();
		if (orderId == ''){
			Feng.error("请选择要操作的数据");
			return false;
		}
	}
	$.post(Feng.ctxPath + '/flowOrderInfo/reCallBack', {
		'orderId' : orderId
	}, function(rtn) {
		if (rtn.code==200) {
			Feng.success('操作成功');
			FlowOrderInfo.table.refresh();
		} else {
			Feng.error(rtn.message?rtn.message:"发生未知错误!");
		}
	});
}
////////初始化页面控件
   function initSelect2() {
	    $("#app_select").removeClass().css("width","220px").css("margin-left","2px").select2({
	    	placeholder : "请输入应用ID或应用名称",
	        minimumInputLength: 1,
	        ajax: {
	            url: Feng.ctxPath + '/flowAppInfo/selectInfoByIdOrName',
	            dataType: 'json',
	            data: function (term) {
	                return {
	                    "idOrName": term
	                };
	            },
	            results: function (data) {
	                return {
	                    results: $.map(data.flowAppInfoList, function (item) {
	                        return {
	                            id: item.flowAppId,
	                            text: item.appName
	                        }
	                    })
	                };
	            }
	        }
	    }); 
	 
	}
   

function viewEvent(orderId){
	var index = layer.open({
        type: 2,
        title: '订单详情',
        area: ['880px', '600px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/portal/order_view?orderId='+orderId
	});
	FlowOrderInfo.layerIndex = index;
//	layer.full(index);// 全屏
}
   
   
///checkBox事件  
function inputHis(){
	var inputHis= $("#id-his");
	 if(inputHis.prop("checked")==true){
		   $("#id-his-value").val("on");
	   }else{
		   $("#id-his-value").val("");
	   }
}

FlowOrderInfo.getSelectRows=function(){
	  var selected = $('#' + this.id).bootstrapTable('getSelections');
	    if(selected.length == 0){
	        return "";
	    }else{
	    	var ids="";
	    	selected.forEach(function(e){ 
	    		ids+=e.orderId+",";
	    	}) 
	    	return ids.substring(0,ids.lastIndexOf(","));;
	    }
}

/**
 * 检查是否选中
 */

FlowOrderInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        FlowOrderInfo.seItem = selected[0];
        return true;
    }
};

/**
 * 查询流量订单列表
 */
FlowOrderInfo.search = function () {
    var queryData = this.getQueryData();
    FlowOrderInfo.table.refresh({query: queryData});
};

/**
 * 清空查询条件
 */
FlowOrderInfo.reset = function() {
	document.getElementById("queryForm").reset();
	 $("#app_select").select2("val", "");
	FlowOrderInfo.queryData = {};
}

FlowOrderInfo.getQueryData=function(){
	var array = $("#queryForm").serializeArray();
	if (array && array.length > 0) {
		$.each(array, function() {
			FlowOrderInfo.queryData[this.name] = this.value;
	    });
	}
	return FlowOrderInfo.queryData;
}


$(function () {

    var defaultColunms = FlowOrderInfo.initColumn();
    var table = new BSTable(FlowOrderInfo.id, "/flowOrderInfo/list", defaultColunms);

    FlowOrderInfo.table = table.init();
    FlowOrderInfo.table.refresh({query:  FlowOrderInfo.getQueryData()});
    initSelect2();
});
