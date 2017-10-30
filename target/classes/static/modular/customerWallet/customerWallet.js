/**
 * customerWallet管理初始化
 */
var CustomerWallet = {
    id: "CustomerWalletTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    queryData:{},
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
CustomerWallet.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '流水号', field: 'tradeNo', align: 'center', valign: 'middle'},
        {title: '合作伙伴名称', field: 'partnerName', align: 'center', valign: 'middle'},
        {title: '客户名称', field: 'customerName', align: 'center', valign: 'middle'},
        {title: '交易类型', field: 'tradeDesc', align: 'center', valign: 'middle',formatter:formatStateDesc},
        {title: '交易金额(元)', field: 'price', align: 'center', valign: 'middle'},
        {title: '账户余额(元)', field: 'balance', align: 'center', valign: 'middle'},
        {title: '订单号', field: 'orderId', align: 'center', valign: 'middle',formatter : formatName},
        {title: '充值手机号', field: 'usedMobile', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'},
        {title: '交易时间', field: 'applyDate', align: 'center', valign: 'middle'},
    ];
};

/**
 * 检查是否选中
 */
CustomerWallet.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        CustomerWallet.seItem = selected[0];
        return true;
    }
};
function formatStateDesc(cellvalue, rowObject,index) {
    if(cellvalue=='扣款'){
        return '<div style=\"background:#2ea967;color:#ffffff;padding: 5px;border-radius: 5px;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
    }else if(cellvalue=='退款'){
        return '<div style=\"background:#F44336;color:#ffffff;padding: 5px;border-radius: 5px;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
    }else {
        return '<div style=\"background:#9E9E9E;color:#ffffff;padding: 5px;border-radius: 5px;display:inline-block !important;display:inline;\">'+cellvalue+'</div>';
    }
}
function formatName(cellvalue, rowObject,index) {
	return '<a href="javascript:;" onclick="viewEvent(\''
			+ rowObject['orderId'] + '\')">' + cellvalue + '</a>';
}
//////操作事件
function viewEvent(orderId){
	var index = layer.open({
        type: 2,
        title: '订单详情',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/customerWallet/detail?orderId='+orderId
    });
	customerWallet.layerIndex = index;
	 
   // layer.full(index);// 全屏
	
   //window.location.href=Feng.ctxPath + '/flowOrderInfo/list?orderId='+orderId;
}
/**
 * 条件查询customerWallet列表
 */
CustomerWallet.search = function () {
/*    var queryData = {};
    queryData["params['tradeNo']"] = $("#tradeNo").val();
    queryData["params['partnerName']"] = $("#partnerName").val();
    queryData["params['customerName']"] = $("#customerName").val();
    queryData["params['orderId']"] = $("#orderId").val();
    queryData["params['createStartTime']"] = $("#createStartTime").val();
    queryData["params['createEndTime']"] = $("#createEndTime").val();*/
	var queryData = this.getQueryData();
	CustomerWallet.table.refresh({query: queryData});
	// CustomerWallet.table.refresh();
};

CustomerWallet.getQueryData=function(){
	var array = $("#queryForm").serializeArray();
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerWallet.queryData[this.name] = this.value;
	    });
	}
	return CustomerWallet.queryData;
}


/**
 * 导出customerWallet列表
 */
CustomerWallet.export2Excel=function(){
	var array = $("#queryForm").serializeArray();
	var str = "";
	if (array && array.length > 0) {
		$.each(array, function() {
			str= str+this.name+"="+this.value+"&";
	    });
	}
	location.href=encodeURI(Feng.ctxPath+"/customerWallet/export?" + str);
}

$(function () {
    var defaultColunms = CustomerWallet.initColumn();
    var table = new BSTable(CustomerWallet.id, "/customerWallet/list", defaultColunms);
    //table.setPaginationType("client");
    CustomerWallet.table = table.init();
});
