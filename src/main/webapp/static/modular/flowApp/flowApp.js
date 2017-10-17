/**
 * App接入
 */
var FlowApp = {
    id: "FlowAppTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
FlowApp.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '接入ID号', field: 'flowAppId', visible: false, align: 'center', valign: 'middle'},
        {title: '应用ID', field: 'appId', align: 'center', valign: 'middle', sortable: true, sortName: 'app_id', formatter: formatName},
        {title: 'APP名称', field: 'appName', align: 'center', valign: 'middle', sortable: true, sortName: 'app_name'},
        {title: '客户名称', field: 'customerId', align: 'center', valign: 'middle', formatter: formatCustomerName},
        {title: '订单ID号', field: 'orderIdStr', align: 'center', valign: 'middle', sortable: true, sortName: 'order_id'},
        {title: '应用Key', field: 'appKey', align: 'center', valign: 'middle' },
        {title: '网关回调URL', field: 'callbackUrl', align: 'center', valign: 'middle' },
        {title: '签权IP地址', field: 'ipAddress', align: 'center', valign: 'middle' },
        {title: '状态', field: 'statusDesc', align: 'center', valign: 'middle', sortable: true, sortName: 'status'},
        {title: '是否重发', field: 'isResendDesc', align: 'center', valign: 'middle', sortable: true, sortName: 'is_resend' },
        {title: '是否需要短信', field: 'needSmsDesc', align: 'center', valign: 'middle' },
        {title: '通道组名称', field: 'dispatchChannelName', align: 'center', valign: 'middle' },
//        {title: '操作', field: 'myac', align: 'center', valign: 'middle', formatter: actionButtons}
    ];
};



function formatName(cellvalue, rowObject, options) {
	return '<a href="javascript:;" onclick="FlowApp.openDetailView(\''
			+ rowObject['flowAppId'] + '\')">' + cellvalue + '</a>';
}

function formatCustomerName(cellvalue, rowObject, options) {
	var customerInfo = rowObject['customerInfo'],customerName = "";
	if (customerInfo && customerInfo["customerName"]){
		customerName = customerInfo["customerName"];
	}
	return customerName;
}

function actionButtons(cellvalue, rowObject, options) {
    var status = rowObject['status'];
    var btnTitle = "";
    var changeStatus = 1;
    if (status == "1") {
        btnTitle = "无效";
        changeStatus = 3;
    } else {
        btnTitle = "有效";
        changeStatus = 1;
    }
	return '<div >'
//			+ '<button onclick=\"editEvent('
//			+ rowObject['flowAppId']
//			+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" permCheck=\"auth_app_access,add,hidden\">'
//			+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
//			+ '</button>'
			+ '<button onclick=\"FlowApp.changeEvent('
			+ rowObject['flowAppId'] + ',' + changeStatus
			+ ')\" class=\"btn btn-xs btn-warning\" data-rel=\"tooltip\" title=\"'+ btnTitle +'\" permCheck=\"auth_app_access,changestatus,hidden\">'
			+ '<i class=\"ace-icon fa fa-flag bigger-120\"></i>'
			+ '</button>'
//			+ '<button onclick=\"deleteEvent('
//			+ rowObject['flowAppId']
//			+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" permCheck=\"auth_app_access,del,hidden\">'
//			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
//			+ '</button>' 
			+ '<button onclick=\"FlowApp.exportFlowApp('
			+ rowObject['flowAppId']
			+ ')\" class=\"btn btn-xs btn-primary\" data-rel=\"tooltip\" title=\"导出\" permCheck=\"auth_app_access,export,hidden\">'
			+ '<i class=\"ace-icon fa fa-share-square-o bigger-120\"></i>'
			+ '</button>'+ '</div>';
}


FlowApp.changeEvent = function() {
	if ( !FlowApp.check() ) {
		return;
	}
	
	var id = FlowApp.seItem.flowAppId;
	var status = FlowApp.seItem.status;
    var btnTitle = "";
    var changeStatus = 1;
    if (status == "1") {
        btnTitle = "无效";
        changeStatus = 3;
    } else {
        btnTitle = "有效";
        changeStatus = 1;
    }
	
	$.ajax({
        url: Feng.ctxPath + '/flowAppInfo/changeStatus?flowAppId=' + id + '&status=' + changeStatus,
        dataType: "json",
        success: function(data) {
            if (data.code == 200) {
            	Feng.success("操作成功！");
            	FlowApp.table.refresh();
            } else {
                Feng.error(data.message ? data.message : "操作失败！");
            }
        }
    });
};


/**
 * 检查是否选中
 */
FlowApp.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
    	FlowApp.seItem = selected[0];
        return true;
    }
};


/**
 * 查看接入详情
 */
FlowApp.openDetailView = function(flowAppId) {
	var index = layer.open({
        type: 2,
        title: '接入详情',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowAppInfo/detail?flowAppId=' + flowAppId
    });
    this.layerIndex = index;
    layer.full(index);// 全屏
}

/**
 * 进入 添加接入信息页
 */
FlowApp.openAddFlowApp = function() {
	var index = layer.open({
        type: 2,
        title: '接入详情',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowAppInfo/add'
    });
    this.layerIndex = index;
    layer.full(index);// 全屏
}


/**
 * 进入 编辑页面
 */
FlowApp.openEditFlowApp = function() {
	if (!FlowApp.check()) {
		return;
	}
	var index = layer.open({
        type: 2,
        title: '接入详情',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowAppInfo/edit?flowAppId=' + FlowApp.seItem.flowAppId
    });
    this.layerIndex = index;
    layer.full(index);// 全屏
}


FlowApp.delete = function () {
	if (!FlowApp.check()) {
		return;
	}
	layer.confirm('确认删除？', function(index){
		layer.close(index);
		var ajax = new $ax(Feng.ctxPath + '/flowAppInfo/delete', function (data) {
			if ( data && data.code == 200 ) {
				Feng.success("删除成功!");
		        FlowApp.table.refresh();
		        return;
			}
	        Feng.error(data.message ? data.message : "删除失败!");
	    }, function (data) {
	        Feng.error("删除失败!");
	    });
	    ajax.set({'flowAppId' : FlowApp.seItem.flowAppId});
	    ajax.start();
	});
};


FlowApp.exportFlowApp = function() {
	if ( !this.check() ) {
		return;
	}
	window.location.href = encodeURI(Feng.ctxPath + '/flowAppInfo/excel?flowAppId=' + FlowApp.seItem.flowAppId);
}


/**
 * 收集表单数据
 */
FlowApp.collectQueryData = function() {
	FlowApp.queryData = $("#queryForm").serializeJson();
}

/**
 * 查询合作伙伴账单管理列表
 */
FlowApp.search = function () {
	this.collectQueryData();
    FlowApp.table.refresh({query: FlowApp.queryData});
};

/**
 * 重置表单
 * @returns
 */
FlowApp.reset = function() {
	document.getElementById("queryForm").reset();
}



$(function () {
    var defaultColunms = FlowApp.initColumn();
    var table = new BSTable(FlowApp.id, "/flowAppInfo/query", defaultColunms);
//    table.setPaginationType("client");
    FlowApp.table = table.init();

});
