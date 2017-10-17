/**

 * 合作伙伴账单管理管理初始化
 */
var CustomerBillMonth = {
    id: "CustomerBillMonthTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
CustomerBillMonth.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '客户名称', field: 'customerInfo.customerName', align: 'center', valign: 'middle'},
        {title: '结算月份', field: 'month', align: 'center', valign: 'middle', formatter: formatMonth, sortable: true, sortName :'month'},
        {title: '账单金额（元）', field: 'payMoney', align: 'center', valign: 'middle', sortable: true, sortName :'pay_money'},
        {title: '状态', field: 'balanceStatus', align: 'center', valign: 'middle', formatter : formatBalanceStatus, sortable: true, sortName :'balance_status'},
        {title: '调整金额', field: 'adjustMoney', align: 'center', valign: 'middle', formatter: formatAdjustMoney, sortable: true, sortName :'adjust_money'},
        {title: '应结算金额', field: 'balanceMoney', align: 'center', valign: 'middle', sortable: true, sortName :'balance_money'},
    ];
};

function formatMonth(cellvalue, rowObject, options) {
    return cellvalue.substr(0,4) + "-" + cellvalue.substr(4,2);
}

function formatBalanceStatus(cellvalue, rowObject, options) {
    if (cellvalue == 0) {
        return "待确认";
    } else if (cellvalue == 1) {
        return "已确认";
    } else {
        return "";
    }
}

function formatAdjustMoney(cellvalue, rowObject, options) {
    if (cellvalue > 0) {
        return "+" + cellvalue;
    } else {
        return cellvalue;
    }
}


//下载当月结算单
function cSOLDownloadBtnClickHandler(balanceMonthId) {
	$('#queryForm').exportData({
        url: Feng.ctxPath + "/customerBalanceMonth/downLoadSettlementOrder",
        data: {
            balanceMonthId: balanceMonthId
        },
        callback: function(data) {},
        failure: function(form, action) {}
    });
}

/**
 * 下载指定月份的账单
 */
CustomerBillMonth.export = function() {
	if (!this.check()) {
		return;
	}
	cSOLDownloadBtnClickHandler(this.seItem.balanceMonthId);
}

/**
 * 检查是否选中
 */
CustomerBillMonth.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
    	CustomerBillMonth.seItem = selected[0];
        return true;
    }
};


/**
 * 收集表单数据
 */
CustomerBillMonth.collectQueryData = function() {
	var array = $("#queryForm").serializeArray();
	CustomerBillMonth.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			var _v = this.value;
			if ( /^\d{4}-{2}$/.test(this.value) ) {
				_v = this.value.replace("-", "");
			} 
			CustomerBillMonth.queryData[this.name] = _v;
	    });
	}
}

/**
 * 查询合作伙伴账单管理列表
 */
CustomerBillMonth.search = function () {
	
	this.collectQueryData();
    CustomerBillMonth.table.refresh({query: CustomerBillMonth.queryData});
};

/**
 * 重置表单
 * @returns
 */
CustomerBillMonth.reset = function() {
	CustomerBillMonth.queryData = {};
	document.getElementById("queryForm").reset();
}


$(function () {
    var defaultColunms = CustomerBillMonth.initColumn();
    var table = new BSTable(CustomerBillMonth.id, "/customerBalanceMonth/query?params['customerId']=" + $("#customerId").val(), defaultColunms);
//    table.setPaginationType("client");
    CustomerBillMonth.table = table.init();
    
});
