/**
 * 充值明细管理初始化
 */
var customerRecharge = {
    id: "customerRechargeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData:{}
};

/**
 * 初始化表格的列
 */
customerRecharge.initColumn = function () {
    return [
        {title: '交易流水ID', field: 'tradeFlowId', visible: false,align: 'center', valign: 'middle'},
        {title: '日期', field: 'tradeTime', align: 'center', valign: 'middle'},
        {title: '交易类型', field: 'tradeType', sortable: true,sortname: 'money', align: 'center', valign: 'middle',formatter: formatTradeType},
        {title: '操作金额', field: 'tradeAmount', align: 'center', valign: 'middle',formatter: formatTradeAmount},
        {title: '账户余额', field: 'balance', align: 'center', valign: 'middle'},
        {title: '信用额度', field: 'creditAmount', sortable: true,sortname: 'input_time', align: 'center', valign: 'middle'},
        {title: '操作者', field: 'loginName', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'}
    ];
};
//列表formatter

function formatTradeAmount(cellvalue, options, rowObject) {
    if (cellvalue > 0) {
        return "+" + cellvalue;
    } else {
        return cellvalue;
    }
}

function formatTradeType(cellvalue, options, rowObject) {
    if (cellvalue == 1) {
        return "结算";
    } else if (cellvalue == 2) {
        return "充值";
    } else if (cellvalue == 3) {
        return "授信";
    } else if (cellvalue == 4) {
        return "流量加激活";
    } else if (cellvalue == 5) {
        return "流量加作废";
    }else {
        return "";
    }
}

customerRecharge.getQueryData=function(){
	var array = $("#queryForm").serializeJson();
	customerRecharge.queryData=array;
	return customerRecharge.queryData;
}
/**
 * 查询充值明细列表
 */
customerRecharge.search = function () {
    var queryData =this.getQueryData();
    customerRecharge.table.refresh({query: queryData});
};



$(function () {
    var defaultColunms = customerRecharge.initColumn();
    var table = new BSTable(customerRecharge.id, '/customerTradeFlow/query?customerId=' +$("#customerId").val(), defaultColunms);
   // table.setPaginationType("client");
    customerRecharge.table = table.init();
});
