/**
 * 充值明细管理初始化
 */
var ChannelSupplierRecharge = {
    id: "ChannelSupplierRechargeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelSupplierRecharge.initColumn = function () {
    return [
        {title: '充值记录ID', field: 'companyRechargeId', visible: false,align: 'center', valign: 'middle'},
        {title: '充值日期', field: 'inputTime', sortable: true,sortName: 'input_time', align: 'center', valign: 'middle'},
        // {title: '交易类型', field: 'typeDesc', align: 'center', valign: 'middle'},
        {title: '操作金额', field: 'money', sortable: true,sortname: 'money', align: 'center', valign: 'middle'},
        {title: '操作者', field: 'loginName', align: 'center', valign: 'middle'},
        //{title: '操作者IP', field: 'operateIp', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
ChannelSupplierRecharge.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelSupplierRecharge.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加充值明细
 */
ChannelSupplierRecharge.openAddChannelSupplierRecharge = function () {
    var index = layer.open({
        type: 2,
        title: '添加充值明细',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelSupplierRecharge/ChannelSupplierRecharge_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看充值明细详情
 */
ChannelSupplierRecharge.openChannelSupplierRechargeDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '充值明细详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelSupplierRecharge/ChannelSupplierRecharge_update/' + ChannelSupplierRecharge.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除充值明细
 */
ChannelSupplierRecharge.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelSupplierRecharge/delete", function (data) {
            Feng.success("删除成功!");
            ChannelSupplierRecharge.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("ChannelSupplierRechargeId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询充值明细列表
 */
ChannelSupplierRecharge.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['startTime'] = $("#startTime").val();
    queryData.params['endTime'] = $("#endTime").val();
    ChannelSupplierRecharge.table.refresh({query: queryData});
};

ChannelSupplierRecharge.export2Excel=function(){
	location.href=encodeURI(Feng.ctxPath + "/channelSupplierRecharge/export?params['supplierCode']=" + $("#supplierCode").val() );
		/*	+ "&params['startTime']=" + $("#startTime").val() 
			+ "&params['endTime']=" + $("#endTime").val()*/
}




$(function () {
    var defaultColunms = ChannelSupplierRecharge.initColumn();
    var table = new BSTable(ChannelSupplierRecharge.id, '/channelSupplierRecharge/query?supplierCode=' +$("#supplierCode").val(), defaultColunms);
    table.setHeight(260);
    // table.setPaginationType("client");
    ChannelSupplierRecharge.table = table.init();
});
