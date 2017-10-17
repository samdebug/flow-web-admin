/**
 * 流量分发异常订单记录管理初始化
 */
var FlowOrderInfoException = {
    id: "FlowOrderInfoExceptionTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData:{},
    isShow:true
};

/**
 * 初始化表格的列
 */
FlowOrderInfoException.initColumn = function () {
    return [
        {field: 'selectItem', checkBox: true},
        {title: '分发订单号', field: 'orderId', align: 'center', valign: 'middle',formatter : formatName},
       // {title: '分发订单号字符串', field: 'orderIdStr', visible: false, align: 'center', valign: 'middle'},
        {title: '应用ID', field: 'appId', align: 'center', valign: 'middle'},
       // {title: '订单类型', field: 'orderType', align: 'center', valign: 'middle',formatter : formatOrderType},
        {title: '流量包ID', field: 'packageId', align: 'center', valign: 'middle'},
        {title: '价格', field: 'price', align: 'center', valign: 'middle'},
        {title: '成本', field: 'operatorBalancePrice', align: 'center', valign: 'middle'},
        {title: '手机号码', field: 'usedMobile', align: 'center', valign: 'middle'},
        {title: '运营商', field: 'mobileOperator', align: 'center', valign: 'middle',formatter:formatOperatorName},
        {title: '归属地', field: 'mobileHome', align: 'center', valign: 'middle',formatter:formatMobileHome},
        {title: '发送时间', field: 'applyDate', align: 'center', valign: 'middle'},
        {title: '回调时间', field: 'checkTime', align: 'center', valign: 'middle'},
        {title: '订单状态', field: 'statusDesc', align: 'center', valign: 'middle',formatter:formatStateDesc},
        {title: '错误代码', field: 'gwErrorCode', align: 'center', valign: 'middle'},
        {title: '网关状态', field: 'gwStatus', align: 'center', valign: 'middle'},
        {title: '用时(秒)', field: 'usedTime', align: 'center', valign: 'middle'},
        {title: '回调状态', field: 'dealFlagDesc', align: 'center', valign: 'middle'},
        {title: '回调次数', field: 'callNum', align: 'center', valign: 'middle'},
        {title: '通道名称', field: 'channelName', visible: FlowOrderInfoException.isShow, align: 'center', valign: 'middle'},
        {title: '流量类型', field: 'flowRangeDesc', align: 'center', valign: 'middle'},
       // {title: '操作', field: '', align: 'center', valign: 'middle',formatter : actionButtons}
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



function viewEvent(orderId){
	var index = layer.open({
        type: 2,
        title: '异常订单详情表',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowOrderInfo/detail?orderId='+orderId
    });
	FlowOrderInfoException.layerIndex = index;
   // layer.full(index);// 全屏
}

function reCallBack(orderId){
	if (!orderId){
		orderId = FlowOrderInfoException.getSelectRows();
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
				FlowOrderInfoException.table.refresh();
			} else {
				Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
	});
}

function reFailBack(orderId){
    if (!orderId){
		orderId = FlowOrderInfoException.getSelectRows();
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
				FlowOrderInfoException.table.refresh();
			} else {
				Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
	});
}
function reSuccessBack(orderId){
    if (!orderId){
		orderId = FlowOrderInfoException.getSelectRows();
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
				FlowOrderInfoException.table.refresh();
			} else {
				Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
    });
}
function reSend(orderId){
   if (!orderId){
		orderId = FlowOrderInfoException.getSelectRows();
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
				FlowOrderInfoException.table.refresh();
			} else {
					Feng.error(rtn.message?rtn.message:"发生未知错误!");
			}
		});
   });
}

////获取勾选
FlowOrderInfoException.getSelectRows=function(){
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
///初始化页面控件

FlowOrderInfoException.initSelect=function(){
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
	    $("#partnerId_select").removeClass().css("width","100%").css("margin-left","2px").select2({
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

	    $("#flowAppId_select").removeClass().css("width","100%").css("margin-left","2px").select2({
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

/**
 * 检查是否选中
 */
FlowOrderInfoException.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        FlowOrderInfoException.seItem = selected[0];
        return true;
    }
};


/**
 * 打开查看流量分发异常订单记录详情
 */
FlowOrderInfoException.openFlowOrderInfoExceptionDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '流量分发异常订单记录详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/flowOrderInfoException/flowOrderInfoException_update/' + FlowOrderInfoException.seItem.orderId
        });
        this.layerIndex = index;
    }
};

/**
 * 删除流量分发异常订单记录
 */
FlowOrderInfoException.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/flowOrderInfoException/delete", function (data) {
            Feng.success("删除成功!");
            FlowOrderInfoException.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("flowOrderInfoExceptionId",this.seItem.orderId);
        ajax.start();
    }
};

/**
 * 查询流量分发异常订单记录列表
 */
FlowOrderInfoException.search = function () {
    var queryData = this.getQueryData();
    FlowOrderInfoException.table.refresh({query: queryData});
};

/**
 * 清空查询条件
 */
FlowOrderInfoException.reset = function() {
	document.getElementById("queryForm").reset();
	 $("#flowAppId_select").select2("val", "");
	 $("#partnerId_select").select2("val", "");
	 $("#customer_select").select2("val", "");
	FlowOrderInfoException.queryData = {};
}


FlowOrderInfoException.getQueryData=function(){
	var array = $("#queryForm").serializeArray();
	if (array && array.length > 0) {
		$.each(array, function() {
			FlowOrderInfoException.queryData[this.name] = this.value;
	    });
	}
	return FlowOrderInfoException.queryData;
}

FlowOrderInfoException.export2Excel=function(){
	var array = $("#queryForm").serializeArray();
	var str = "";
	if (array && array.length > 0) {
		$.each(array, function() {
			str= str+this.name+"="+this.value+"&";
	    });
	}
	location.href=encodeURI(Feng.ctxPath + "/flowOrderInfoException/export?"+str);
}


$(function () {
    var defaultColunms = FlowOrderInfoException.initColumn();
    var table = new BSTable(FlowOrderInfoException.id, "/flowOrderInfoException/list", defaultColunms);
    //table.setPaginationType("client");
    FlowOrderInfoException.table = table.init();
    FlowOrderInfoException.initSelect();
    
    //异步加载判断当前用户是否为管理员，控制通道名称显示和隐藏
    var ajax = new $ax(Feng.ctxPath + "/orderInfo/orderInfoViewInit", function (data) {
			if (data.isWYAdmin){
				isHiddenChannelName = true;
			}
		});
    ajax.start();
});
