/**
 * 供应商流量包对账明细管理初始化
 */
var SupplierFlowPack = {
    id: "SupplierFlowPackTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
SupplierFlowPack.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '日期', field: 'check_time',align: 'center', valign: 'middle',sortable:true},
        {title: '供应商名称', field: 'supplier_name',align: 'center', valign: 'middle',sortable:true},
        {title: '通道名称', field: 'channel_name',align: 'center', valign: 'middle',sortable:true},
        {title: '运营商', field: 'mobile_operator',align: 'center', valign: 'middle',sortable:true},
        {title: '区域', field: 'area_name',align: 'center', valign: 'middle',sortable:true},
        {title: '产品包名称', field: 'flow_name',align: 'center', valign: 'middle',sortable:true},
        {title: '规格', field: 'flow_amount',align: 'center', valign: 'middle',sortable:true},
        {title: '数量(个)', field: 'count',align: 'center', valign: 'middle',sortable:true},
        {title: '原价', field: 'cost_price',align: 'center', valign: 'middle',sortable:true},
        {title: '通道价', field: 'operator_balance_price',align: 'center', valign: 'middle',sortable:true},
        {title: '金额(元)', field: 'money',align: 'center', valign: 'middle',sortable:true}
    ];
};

/**
 * 检查是否选中
 */
SupplierFlowPack.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        SupplierFlowPack.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加供应商流量包对账明细
 */
SupplierFlowPack.openAddSupplierFlowPack = function () {
    var index = layer.open({
        type: 2,
        title: '添加供应商流量包对账明细',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/supplierFlowPack/supplierFlowPack_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看供应商流量包对账明细详情
 */
SupplierFlowPack.openSupplierFlowPackDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '供应商流量包对账明细详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/supplierFlowPack/supplierFlowPack_update/' + SupplierFlowPack.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除供应商流量包对账明细
 */
SupplierFlowPack.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/supplierFlowPack/delete", function (data) {
            Feng.success("删除成功!");
            SupplierFlowPack.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("supplierFlowPackId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询供应商流量包对账明细列表
 */
SupplierFlowPack.search = function () {
	var params_={};
    params_.params = {};
    params_.params.beginCheckTime = $("#beginCheckTime").val();
    params_.params.endCheckTime = $("#endCheckTime").val();
    params_.params.supplierName = $("#supplier_name").val();
    params_.params.channelName = $("#channel_name").val();
    params_.params.mobileOperator = $("#mobile_operator").val();
    
    SupplierFlowPack.table.refresh({query: params_});
};

$(function () {
    var defaultColunms = SupplierFlowPack.initColumn();
    var table = new BSTable(SupplierFlowPack.id, "/supplierFlowPack/list", defaultColunms);
//    table.setPaginationType("client");
    var exportOptions = {};
    exportOptions.ignoreColumn="[0]";
    exportOptions.fileName="供应商流量包对账明细";
    exportOptions.tableName="供应商流量包对账明细";
    exportOptions.worksheetName="供应商流量包对账明细";
    table.setExport(true,"['excel']","all",exportOptions);
    SupplierFlowPack.table = table.init();
});
