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
        //{title: '分发订单号字符串', field: 'orderIdStr', visible: false, align: 'center', valign: 'middle'},
        {title: '应用ID', field: 'appId', align: 'center', valign: 'middle'},
        //{title: '订单类型', field: 'orderType', align: 'center', valign: 'middle'},
        {title: '流量包ID', field: 'packageId', align: 'center', valign: 'middle'},
        {title: '价格', field: 'price', align: 'center', valign: 'middle'},
        {title: '成本', field: 'operatorBalancePrice', align: 'center', valign: 'middle'},
        {title: '手机号码', field: 'usedMobile', align: 'center', valign: 'middle'},
        {title: '运营商', field: 'mobileOperator', align: 'center', valign: 'middle',formatter:formatOperatorName},
        {title: '归属地', field: 'mobileHome', align: 'center', valign: 'middle',formatter:formatMobileHome},
        {title: '发送时间', field: 'applyDate', align: 'center', valign: 'middle'},
        {title: '回调时间', field: 'checkTime', align: 'center', valign: 'middle'},
        {title: '订单状态', field: 'statusDesc', align: 'center', valign: 'middle',formatter:formatStateDesc},
        {title: '网关状态', field: 'gwStatus', align: 'center', valign: 'middle'},
        {title: '用时(秒)', field: 'usedTime', align: 'center', valign: 'middle'},
        {title: '回调状态', field: 'dealFlagDesc', align: 'center', valign: 'middle'},
        {title: '回调次数', field: 'callNum', align: 'center', valign: 'middle'},
        {title: '重发次数', field: 'resendTimes', align: 'center', valign: 'middle',formatter : formatResend},
        {title: '通道名称', field: 'channelName', visible: FlowOrderInfo.isShow, align: 'center', valign: 'middle'},
        {title: '流量类型', field: 'flowRangeDesc', align: 'center', valign: 'middle'},
       //{title: '操作', field: '', align: 'center', valign: 'middle',formatter : actionButtons}
    ];
};



///列表formatter
function actionButtons(cellvalue, rowObject,index) {
	var dealFlag = rowObject['dealFlag'],status = rowObject['status'],html = [];
	html.push('<div>');
	if (dealFlag == '2'){
		html.push('<button onclick=\"reCallBack(\''
		+ rowObject['orderId']+'\')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"重回调\"  permCheck=\"auth_flow_orderinfo_list,reCallBack\">'
		+ '<i class=\"ace-icon fa fa-hand-o-right bigger-120\"></i>'
		+ '</button>');
	}
	if (status == '1'){
		html.push('<button onclick=\"reFailBack(\''
		+ rowObject['orderId']+'\')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"置失败\"  permCheck=\"auth_flow_orderinfo_list,reFailBack\">'
		+ '<i class=\"ace-icon fa fa-thumbs-down bigger-120\"></i>'
		+ '</button>');
	}
	if (status == '4' && dealFlag == '2'){
		html.push('<button onclick=\"reSend(\''
		+ rowObject['orderId']+'\')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"重发\"  permCheck=\"auth_flow_orderinfo_list,reSend\">'
		+ '<i class=\"ace-icon fa fa-refresh bigger-120\"></i>'
		+ '</button>');
	}
	html.push('</div>');
	return html.join('');
}


function formatName(cellvalue, rowObject,index) {
	return '<a href="javascript:;" onclick="viewEvent(\''
			+ rowObject['orderId'] + '\')">' + cellvalue + '</a>';
}
function formatResend(cellvalue, rowObject,index) {
	if(cellvalue>0){
	return '<a href="javascript:;" onclick="viewResend(\''
	+ rowObject['orderId'] + '\')">' + cellvalue + '</a>';
	}else{
		return cellvalue;
	}
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
		return '<div style=\"background:#2ea967;color:#ffffff;padding: 5px;border-radius: 5px;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
	}else if(cellvalue=='失败'){
		return '<div style=\"background:#F44336;color:#ffffff;padding: 5px;border-radius: 5px;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
	}else {
		return '<div style=\"background:#9E9E9E;color:#ffffff;padding: 5px;border-radius: 5px;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
	}
}

function formatPackageName(cellvalue, rowObject,index) {
	var packageName = "",flowPackageInfo = rowObject['flowPackageInfo'];
	if (flowPackageInfo){
		packageName = flowPackageInfo['packageName'];
	}
	return packageName;
}

function IsSHowChannelName(){
	$.ajax({
		url:Feng.ctxPath+"/orderInfo/orderInfoViewInit.ajax",
		async:false,
		success: function (data) {
			if (data.isWYAdmin){
				FlowOrderInfo.isShow = true;
			}
		}

	});
}

function viewResend(id) {
	var index = layer.open({
        type: 2,
        title: '订单重发记录',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowOrderInfo/refundPage?orderId='+id
    });
	FlowOrderInfo.layerIndex = index;
    layer.full(index);// 全屏
};
//////操作事件

function viewEvent(orderId){
	var index = layer.open({
        type: 2,
        title: '订单详情',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowOrderInfo/detail?orderId='+orderId
    });
	FlowOrderInfo.layerIndex = index;
   // layer.full(index);// 全屏
}

function reCallBack(orderId){
	if (!orderId){
		orderId = FlowOrderInfo.getSelectRows();
		if (orderId == ''){
			Feng.error("请选择要操作的数据");
			return false;
		}
	}
	layer.confirm('请再次确认对所选订单进行置重回调', function(index){
		layer.close(index);
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
	});
}

function reFailBack(orderId){
    if (!orderId){
		orderId = FlowOrderInfo.getSelectRows();
		if (orderId == ''){
			Feng.error("请选择要操作的数据");
			return false;
		}
	}
	layer.confirm('请再次确认对所选订单进行置失败', function(index){
		layer.close(index);
		$.post( Feng.ctxPath + '/flowOrderInfo/reFailBack', {
			'orderId' : orderId
		}, function(rtn) {
			if (rtn.code==200) {
				Feng.success('操作成功');
				FlowOrderInfo.table.refresh();
			} else {
				Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
	});
}
function reSuccessBack(orderId){
    if (!orderId){
		orderId = FlowOrderInfo.getSelectRows();
		if (orderId == ''){
			Feng.error("请选择要操作的数据");
			return false;
		}
	}
    layer.confirm('请再次确认对所选订单进行置成功', function(index){
    	layer.close(index);
		$.post(Feng.ctxPath + '/flowOrderInfo/reFailBack', {
			'orderId' : orderId,'operate':'0'
		}, function(rtn) {
			if (rtn.code==200) {
				Feng.success('操作成功');
				FlowOrderInfo.table.refresh();
			} else {
				Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
    });
}
function reSend(orderId){
   if (!orderId){
		orderId = FlowOrderInfo.getSelectRows();
		if (orderId == ''){
			Feng.error("请选择要操作的数据");
			return false;
		}
	}
   layer.confirm('请再次确认对所选订单进行置重发', function(index){
	   layer.close(index);
		$.post(Feng.ctxPath + '/flowOrderInfo/reSend', {
			'orderId' : orderId
		}, function(rtn) {
			if (rtn.code==200) {
				Feng.success('操作成功');
				FlowOrderInfo.table.refresh();
			} else {
					Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
   });
}

////下载订单
   //订单天下载
   function initDownLoadByDay(){
	   var ds = $("#download");
	   	$.get(Feng.ctxPath +"/flowOrderInfo/listReports?type=day", function(result){
			 	if(result.success){
				    var data = result.data;
				    for(var i=0;i<data.length;i++){
				    	ds.append("<option value='"+data[i]+"'>"+data[i]+"</option>")
				    }
			 	}
			  });
   }
   function downLoadByDay(){
	   var dateStr = $("#download").val();
 	  window.location= Feng.ctxPath+"/flowOrderInfo/downloadReport?dateStr="+dateStr;

   }
   //订单月下载
   function initDownLoadByMonth(){
	   var ds = $("#downloadMonth");
	   	$.get(Feng.ctxPath +"/flowOrderInfo/listReports?type=month", function(result){
			 	if(result.success){
				    var data = result.data;
				    for(var i=0;i<data.length;i++){
				    	ds.append("<option value='"+data[i]+"'>"+data[i]+"</option>")
				    }
			 	}
			  });
   }
   function downLoadByMonth(){
	   var dateStr = $("#downloadMonth").val();
 	  window.location= Feng.ctxPath+"/flowOrderInfo/downloadReport.ajax?dateStr="+dateStr;
   }
////////初始化页面控件
   function initSelect2() {
		 $("#customer_select").removeClass().css("width","100%").css("margin-left","2px").select2({
	    	placeholder : "请输入客户名称关键字",
	        minimumInputLength: 1,
	        ajax: {
	            url: Feng.ctxPath + '/orderInfo/selectCustomerInfoByName',
	            dataType: 'json',
	            data: function (term) {
	                return {
	                    "customerName": term
	                };
	            },
	            results: function (data) {
	                return {
	                    results: $.map(data.customerList, function (item) {
	                        return {
	                            id: item.customerId,
	                            text: item.customerName
	                        }
	                    })
	                };
	            }
	        }
	    });
	    
	    $("#channelName_select").removeClass().css("width","100%").css("margin-left","2px").select2({
	    	placeholder : "请输入通道名称关键字",
	        minimumInputLength: 1,
	        ajax: {
	            url: Feng.ctxPath + '/accessChannelInfo/list',
	            dataType: 'json',
	            data: function (term) {
	                return {
	                    "params['keyword']": term,
	                    "rows" : 100
	                };
	            },
	            results: function (data) {
	                return {
	                    results: $.map(data.rows, function (item) {
	                        return {
	                            id: item.channelSeqId,
	                            text: item.channelName
	                        }
	                    })
	                };
	            }
	        }
	    });
	    
	    $("#partner_select").removeClass().css("width","100%").css("margin-left","2px").select2({
	    	placeholder : "请输入合作伙伴名称关键字",
	        minimumInputLength: 1,
	        ajax: {
	            url: Feng.ctxPath + '/orderInfo/selectPartnerInfoByName',
	            dataType: 'json',
	            data: function (term) {
	                return {
	                    "partnerName": term
	                };
	            },
	            results: function (data) {
	                return {
	                    results: $.map(data.partnerList, function (item) {
	                        return {
	                            id: item.partnerId,
	                            text: item.partnerName
	                        }
	                    })
	                };
	            }
	        }
	    }); 

	    $("#app_select").removeClass().css("width","100%").css("margin-left","2px").select2({
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
   
///checkBox事件  
function inputHis(){
	var inputHis= $("#id-his");
	 if(inputHis.prop("checked")==true){
		   $("#id-his-value").val("on");
	   }else{
		   $("#id-his-value").val("");
	   }
}
//统计汇总行
function initTotalPrice(){
	$.post(Feng.ctxPath +'/flowOrderInfo/getTotalPrice',  FlowOrderInfo.getQueryData(), function(rtn) {
		if (rtn.success) {
			if(rtn.data){
				$('#customerTotalPrice').html(rtn.data['sumPrice']);
				$('#partnerTotalPrice').html(rtn.data['partnerPrice']);
				$('#operatorTotalPrice').html(rtn.data['operatorPrice']);
			}else{
				$('#customerTotalPrice').html("");
				$('#partnerTotalPrice').html("");
				$('#operatorTotalPrice').html("");
			}
		} else {
			 Feng.error(rtn.message?rtn.message:"发生未知错误!");
		}
	});
}

////////////////////////////

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
    initTotalPrice();
    FlowOrderInfo.table.refresh({query: queryData});
};

/**
 * 清空查询条件
 */
FlowOrderInfo.reset = function() {
	document.getElementById("queryForm").reset();
	 $("#app_select").select2("val", "");
	 $("#partner_select").select2("val", "");
	 $("#customer_select").select2("val", "");
	 $("#channelName_select").select2("val", "");
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

FlowOrderInfo.export2Excel=function(){
	var array = $("#queryForm").serializeArray();
	var str = "";
	if (array && array.length > 0) {
		$.each(array, function() {
			str= str+this.name+"="+this.value+"&";
	    });
	}
	location.href=encodeURI(Feng.ctxPath + "/flowOrderInfo/export?"+str);
}

$(function () {
	IsSHowChannelName();
    var defaultColunms = FlowOrderInfo.initColumn();
    var table = new BSTable(FlowOrderInfo.id, "/flowOrderInfo/list", defaultColunms);
    var exportOptions = {};
    exportOptions.ignoreColumn="[0]";
    exportOptions.fileName="订单列表";
    exportOptions.tableName="订单列表";
    exportOptions.worksheetName="订单列表";
    table.setExport(false,"['excel']","all",exportOptions);
    FlowOrderInfo.table = table.init();
    initDownLoadByDay();
    initDownLoadByMonth();
    initSelect2();
    initTotalPrice()
});
