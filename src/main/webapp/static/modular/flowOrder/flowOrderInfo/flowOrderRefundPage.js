/**
 * 字典管理初始化
 */
var flowRefundPageInfo = {
    id: "flowRefundPageInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
flowRefundPageInfo.initColumn = function () {
    return [
        {field: 'selectItem', checkBox: true},
        {title: '分发订单号', field: 'orderIdStr', align: 'center', valign: 'middle'},
        {title: '分发订单号字符串', field: 'orderIdStr', align: 'center', valign: 'middle'},
        {title: '应用ID', field: 'appId', align: 'center', valign: 'middle'},
        {title: '流量包ID', field: 'packageId', align: 'center', valign: 'middle'},
	    {title: '价格', field: 'price', align: 'center', valign: 'middle'},
	    {title: '手机号码', field: 'usedMobile', align: 'center', valign: 'middle'},
	    {title: '运营商', field: 'mobileOperator', align: 'center', valign: 'middle'},
	    {title: '归属地', field: 'mobileHome', align: 'center', valign: 'middle'},
	    {title: '发送日期', field: 'applyDate', align: 'center', valign: 'middle'},
	    {title: '回调日期', field: 'checkTime', align: 'center', valign: 'middle'},
	    {title: '订单状态', field: 'statusDesc', align: 'center', valign: 'middle'},
	    {title: '网关状态', field: 'gwStatus', align: 'center', valign: 'middle'},
	    {title: '用时(秒)', field: 'usedTime', align: 'center', valign: 'middle'},
	    {title: '通道名称', field: 'channelName', align: 'center', valign: 'middle'},
	    {title: '成本', field: 'operatorBalancePrice', align: 'center', valign: 'middle'}
	    ];
};

/**
 * 检查是否选中
 */
flowRefundPageInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info("请先选中表格中的某一记录！");
        return false;
    } else {
        flowRefundPageInfo.seItem = selected[0];
        return true;
    }
};



$(function () {
    var defaultColunms = flowRefundPageInfo.initColumn();
    var table = new BSTable(flowRefundPageInfo.id, "/flowOrderInfo/queryForRefund?params['orderId']="+$("#orderId").val(), defaultColunms);
   // table.setPaginationType("client");
    flowRefundPageInfo.table = table.init();
});
