/**
 * 上游通道每月消费额度管理初始化
 */
var MonthlyConsumptionQuota = {
    id: "MonthlyConsumptionQuotaTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
MonthlyConsumptionQuota.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '供应商名称', field: 'supplierName',  align: 'center', valign: 'middle'},
        {title: '统计月份', field: 'month',  align: 'center', valign: 'middle'},
        {title: '消费额度', field: 'profit',  align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
MonthlyConsumptionQuota.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        MonthlyConsumptionQuota.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加上游通道每月消费额度
 */
MonthlyConsumptionQuota.openAddMonthlyConsumptionQuota = function () {
    var index = layer.open({
        type: 2,
        title: '添加上游通道每月消费额度',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/monthlyConsumptionQuota/monthlyConsumptionQuota_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看上游通道每月消费额度详情
 */
MonthlyConsumptionQuota.openMonthlyConsumptionQuotaDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '上游通道每月消费额度详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/monthlyConsumptionQuota/monthlyConsumptionQuota_update/' + MonthlyConsumptionQuota.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除上游通道每月消费额度
 */
MonthlyConsumptionQuota.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/monthlyConsumptionQuota/delete", function (data) {
            Feng.success("删除成功!");
            MonthlyConsumptionQuota.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("monthlyConsumptionQuotaId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询上游通道每月消费额度列表
 */
MonthlyConsumptionQuota.search = function () {
    var queryData = {};
    queryData['supplierName'] = $("#supplierName").val();
    queryData['month'] = $("#month").val();
    MonthlyConsumptionQuota.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = MonthlyConsumptionQuota.initColumn();
    var table = new BSTable(MonthlyConsumptionQuota.id, "/monthlyConsumptionQuota/list", defaultColunms);
    table.setPaginationType("client");
    MonthlyConsumptionQuota.table = table.init();
});
