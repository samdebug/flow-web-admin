/**
 * 客户充值管理管理初始化
 */
var CustomerCharge = {
    id: "CustomerChargeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData:{}
};

/**
 * 初始化表格的列
 */
CustomerCharge.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '客户ID', field: 'customerId', visible: false, align: 'center', valign: 'middle'},
        {title: '客户名称', field: 'customerName',  align: 'center', valign: 'middle'},
        {title: '合作伙伴', field: 'partnerName',  align: 'center', valign: 'middle'},
        {title: '合作类型', field: 'partnerType', align: 'center', valign: 'middle',formatter: formatPartnerType},
        {title: '状态', field: 'status',  align: 'center', valign: 'middle',formatter:formatStatus},
        {title: '账户余额', field: 'balance',  align: 'center', valign: 'middle'},
        {title: '授信额度', field: 'creditAmount', align: 'center', valign: 'middle'},
        {title: '未确认金额', field: 'currentAmount', align: 'center', valign: 'middle'},
        {title: '可用额度', field: 'availableCredit', align: 'center', valign: 'middle'},
//        {title: '操作', field: 'opt', align: 'center', valign: 'middle',formatter : actionButtons}
    ];
};

//列表formatter

function formatPartnerType(cellvalue, rowObject,index ) {
    switch (parseInt(cellvalue)) {
        case 1:
            return "流量营销";
            break;
        case 2:
            return "渠道直充";
            break;
        default:
            return "";
            break;
    }
}
function formatStatus( cellvalue,rowObject,index){
    switch (cellvalue) {
        case 0:
            return "保存";
            break;
        case 1:
            return "商用";
            break;
        case 2:
            return "暂停";
            break;
        default:
            return "";
            break;
    }
}
function actionButtons(cellvalue, rowObject, index) {
	return '<div >'
	+ '<button onclick=\"detailEvent('
	+ rowObject['customerId']
	+ ')\" class=\"btn btn-xs btn-warning\" permCheck="auth_customer_bill,accountDetail,hidden" data-rel=\"tooltip\" title=\"账户明细\" >'
	+ '<i class=\"ace-icon fa fa-money bigger-120\"></i>'
	+ '</button>'
	+ '<button onclick=\"accountSettlementReport('
	+ rowObject['customerId']
	+ ')\" class=\"btn btn-xs btn-warning\" permCheck="auth_customer_bill,accountSettlement,hidden" data-rel=\"tooltip\" title=\"账单报表\" >'
	+ '<i class=\"ace-icon fa fa-download bigger-120\"></i>'
	+ '</button></div>';
}

function detailEvent() {
	if (!CustomerCharge.check()) {
		return;
	}
	
	var id = CustomerCharge.seItem.customerId;
	var index = layer.open({
        type: 2,
        title: '客户账号明细',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/customerCharge/customerChargeView?customerId=' + id
    });
	CustomerCharge.layerIndex = index;
	layer.full(index);
};


function accountSettlementReport() {
	if (!CustomerCharge.check()) {
		return;
	}
	
	var id = CustomerCharge.seItem.customerId;
	var index = layer.open({
        type: 2,
        title: '账单报表',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/customerAccountSettlement?customerId=' + id
    });
	CustomerCharge.layerIndex = index;
	layer.full(index);
}

CustomerCharge.restForm=function() {
	document.getElementById("queryForm").reset();
	CustomerCharge.queryData = {};
}
/**
 * 查询流量订单列表
 */
CustomerCharge.search = function () {
    var queryData = this.getQueryData();
    CustomerCharge.table.refresh({query: queryData});
};

CustomerCharge.getQueryData=function(){
	var array = $("#queryForm").serializeArray();
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerCharge.queryData[this.name] = this.value;
	    });
	}
	return CustomerCharge.queryData;
}

/**
 * 检查是否选中
 */
CustomerCharge.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        CustomerCharge.seItem = selected[0];
        return true;
    }
};



$(function () {
    var defaultColunms = CustomerCharge.initColumn();
    var table = new BSTable(CustomerCharge.id, "/customer/list", defaultColunms);
    //table.setPaginationType("client");
    CustomerCharge.table = table.init();
});
