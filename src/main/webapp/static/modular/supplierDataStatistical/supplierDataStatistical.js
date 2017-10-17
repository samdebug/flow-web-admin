/**
 * 供应商出款数据统计管理初始化
 */
var SupplierDataStatistical = {
    id: "SupplierDataStatisticalTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
SupplierDataStatistical.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle',sortable:true},
        {title: '日期', field: 'statisticsDate',  align: 'center', valign: 'middle',sortname : 's.statistics_date',sortable:true},
        {title: '供应商名称', field: 'companyName',  align: 'center', valign: 'middle',sortname : 'c.supplier_name',sortable:true},
        {title: '期初结余', field: 'beginBalance',  align: 'center', valign: 'middle',sortname : 's.begin_balance',sortable:true},
        {title: '消耗', field: 'consumeAmount', align: 'center', valign: 'middle',sortname : 's.consume_amount',sortable:true},
        {title: '消耗占比', field: 'consumeRatio',  align: 'center', valign: 'middle',sortable:true},
        {title: '充值', field: 'rechargeAmount',  align: 'center', valign: 'middle',sortname : 's.recharge_amount',sortable:true},
        {title: '余额', field: 'balance',  align: 'center', valign: 'middle',sortname : 's.balance',sortable:true},
        {title: '利润', field: 'profit',  align: 'center', valign: 'middle',sortable:true}
    ];
};

/**
 * 检查是否选中
 */
SupplierDataStatistical.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        SupplierDataStatistical.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加供应商出款数据统计
 */
SupplierDataStatistical.openAddSupplierDataStatistical = function () {
    var index = layer.open({
        type: 2,
        title: '添加供应商出款数据统计',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/supplierDataStatistical/supplierDataStatistical_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看供应商出款数据统计详情
 */
SupplierDataStatistical.openSupplierDataStatisticalDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '供应商出款数据统计详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/supplierDataStatistical/supplierDataStatistical_update/' + SupplierDataStatistical.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除供应商出款数据统计
 */
SupplierDataStatistical.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/supplierDataStatistical/delete", function (data) {
            Feng.success("删除成功!");
            SupplierDataStatistical.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("supplierDataStatisticalId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询供应商出款数据统计列表
 */
SupplierDataStatistical.search = function () {
	var params_={};
    params_.params = {};
    params_.params.startTime = $("#startTime").val();
    params_.params.endTime = $("#endTime").val();
    params_.params.companyName = $("#companyName").val();
    SupplierDataStatistical.table.refresh({query: params_});
};

$(function () {
    var defaultColunms = SupplierDataStatistical.initColumn();
    var table = new BSTable(SupplierDataStatistical.id, "/supplierDataStatistical/list", defaultColunms);
//    table.setPaginationType("client");
    SupplierDataStatistical.table = table.init();
});
