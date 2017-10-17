/**
 * 流量告警信息管理初始化
 */
var FlowWarnMsg = {
    id: "FlowWarnMsgTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
FlowWarnMsg.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '告警信息ID', field: 'warnMsgId', align: 'center', valign: 'middle'},
        {title: '告警类别', field: 'warnType', align: 'center', valign: 'middle'},
        {title: '通知用户', field: 'notifyUser', align: 'center', valign: 'middle'},
        {title: '通知信息', field: 'notifyMsg', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
FlowWarnMsg.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        FlowWarnMsg.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加流量告警信息
 */
FlowWarnMsg.openAddFlowWarnMsg = function () {
    var index = layer.open({
        type: 2,
        title: '添加流量告警信息',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowWarnMsg/flowWarnMsg_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看流量告警信息详情
 */
FlowWarnMsg.openFlowWarnMsgDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '流量告警信息详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/flowWarnMsg/flowWarnMsg_update/' + FlowWarnMsg.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除流量告警信息
 */
FlowWarnMsg.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/flowWarnMsg/delete", function (data) {
            Feng.success("删除成功!");
            FlowWarnMsg.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("flowWarnMsgId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询流量告警信息列表
 */
FlowWarnMsg.search = function () {
    var queryData = {};
    queryData['warnType'] = $("#warnType").val();
    queryData['notifyUser'] = $("#notifyUser").val();
    FlowWarnMsg.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = FlowWarnMsg.initColumn();
    var table = new BSTable(FlowWarnMsg.id, "/flowWarnMsg/list", defaultColunms);
    table.setPaginationType("client");
    FlowWarnMsg.table = table.init();
});
