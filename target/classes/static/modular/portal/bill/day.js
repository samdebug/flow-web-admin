/**
 * 合作伙伴账单管理管理初始化
 */
var CustomerBillDay = {
    id: "CustomerBillDayTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
CustomerBillDay.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '客户名称', field: 'customerInfo.customerName', align: 'center', valign: 'middle'},
        {title: '产品名称', field: 'productName', align: 'center', valign: 'middle', sortable: true, sortName: 'product_name'},
        {title: '运营商', field: 'mobileOperator', align: 'center', valign: 'middle', formatter: formatMobileOperator},
        {title: '流量大小(M)', field: 'flowAmount', align: 'center', valign: 'middle', sortable: true, sortName: 'flow_amount'},
        {title: '账单日期', field: 'balanceDay', align: 'center', valign: 'middle', formatter: formatBalanceDay, sortable: true, sortName: 'balance_day'},
        {title: '结算价格（元）', field: 'customerBalancePrice', align: 'center', valign: 'middle', sortable: true, sortName: 'customer_balance_price'},
        {title: '结算数量（个）', field: 'sendNum', align: 'center', valign: 'middle', sortable: true, sortName: 'send_num'},
        {title: '合计金额（元）', field: 'customerAmount', align: 'center', valign: 'middle', sortable: true, sortName: 'customer_amount'}
    ];
};


function formatMobileOperator(cellvalue, rowObject, options) {
    switch (cellvalue) {
        case "YD":
            return "移动";
            break;
        case "LT":
            return "联通";
            break;
        case "DX":
            return "电信";
            break;
        case "YD/LT/DX":
            return "移动/联通/电信";
            break;
        default:
            return cellvalue;
            break;
    }
}

function formatBalanceDay(cellvalue, rowObject, options) {
    return cellvalue.substr(0, 4) + "-" + cellvalue.substr(4, 2) + "-" + cellvalue.substr(6, 2);
}


CustomerBillDay.exportCustomerBillDay = function() {
	$('#queryForm').exportData({
        url: Feng.ctxPath + "/customerBalanceDay/downLoadCustomerBill?params['customerId']="+$("#customerId").val(),
        callback: function(data) {},
        failure: function(form, action) {}
    });
}

/**
 * 收集表单数据
 */
CustomerBillDay.collectQueryData = function() {
	var array = $("#queryForm").serializeArray();
	CustomerBillDay.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerBillDay.queryData[this.name] = this.value;
	    });
	}
}

/**
 * 查询合作伙伴账单管理列表
 */
CustomerBillDay.search = function () {
	this.collectQueryData();
    CustomerBillDay.table.refresh({query: CustomerBillDay.queryData});
};

/**
 * 重置表单
 * @returns
 */
CustomerBillDay.reset = function() {
	CustomerBillDay.queryData = {};
	document.getElementById("queryForm").reset();
}


$(function () {
    var defaultColunms = CustomerBillDay.initColumn();
    var table = new BSTable(CustomerBillDay.id, "/customerBalanceDay/query?params['customerId']=" + $("#customerId").val(), defaultColunms);
    CustomerBillDay.table = table.init();
});
