/**
 * 充值明细管理初始化
 */
var customerAccountSettlement = {
    id: "customerAccountSettlementTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData:{}
};

/**
 * 初始化表格的列
 */
customerAccountSettlement.initColumn = function () {
    return [
        {title: '客户ID', field: 'customerId', visible: false,align: 'center', valign: 'middle'},
        {title: '日期', field: 'settlementTime', align: 'center', valign: 'middle',formatter:formatDate},
        {title: '凌晨余额', field: 'morningBalance', sortable: true,sortname: 'morning_balance', align: 'center', valign: 'middle'},
        {title: '次日凌晨余额', field: 'nextMorningBalance', align: 'center', valign: 'middle'},
        {title: '加款金额', field: 'addMoney', align: 'center', valign: 'middle'},
        {title: '提现金额', field: 'withdrawalMoney', sortable: true,sortname: 'input_time', align: 'center', valign: 'middle'},
        {title: '扣款金额', field: 'chargebackMoney', align: 'center', valign: 'middle'},
        {title: '退款金额', field: 'refundMoney', align: 'center', valign: 'middle'},
        {title: '当天交易金额', field: 'consumeMoney', align: 'center', valign: 'middle'},
        {title: '操作', field: 'opt', align: 'center', valign: 'middle',formatter:formatActionButtons}
    ];
};
//列表formatter
function formatDate(cellvalue, rowObject, index) {
    return cellvalue.substr(0,10);
}
function formatActionButtons(cellvalue,rowObject, index) {
    var btnHtml = "<div>";
    btnHtml += '<button onclick=downloadReport(\"' + rowObject['settlementTime'].substr(0,10) + '\") class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"下载账单详情\" ><i class=\"ace-icon fa fa-download bigger-120\"></i></button>';
    btnHtml += "</div>";
    return btnHtml;
}

// 下载当月结算单
function downloadReport(dateStr) {
	dateStr = dateStr.replaceAll("-","");
    window.location= Feng.ctxPath+"/customerAccountSettlement/downloadReport?customerId="+$("#customerId").val()+"&dateStr="+dateStr;
}




customerAccountSettlement.getQueryData=function(){
	var array = $("#queryForm").serializeJson();
	customerAccountSettlement.queryData=array;
	return customerAccountSettlement.queryData;
}
customerAccountSettlement.reset=function(){
	document.getElementById("queryForm").reset();
	customerAccountSettlement.queryData = {};
}
/**
 * 查询充值明细列表
 */
customerAccountSettlement.search = function () {
    var queryData =this.getQueryData();
    customerAccountSettlement.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = customerAccountSettlement.initColumn();
    var table = new BSTable(customerAccountSettlement.id, '/customerAccountSettlement/query?customerId=' +$("#customerId").val(), defaultColunms);
   // table.setPaginationType("client");
    customerAccountSettlement.table = table.init();
});
