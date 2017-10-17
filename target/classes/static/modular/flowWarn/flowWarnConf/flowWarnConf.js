/**
 * 流量告警配置管理初始化
 */
var FlowWarnConf = {
    id: "FlowWarnConfTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
FlowWarnConf.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '告警配置ID', field: 'warnConfId', align: 'center', valign: 'middle'},
        {title: '告警类别', field: 'warnTypeDesc', align: 'center', valign: 'middle'},
        {title: '失败条数', field: 'failNum', align: 'center', valign: 'middle'},
        {title: '多久间隔时间内(分钟)', field: 'failIntervalMin', align: 'center', valign: 'middle'},
        {title: '是否有效', field: 'isValidDesc', align: 'center', valign: 'middle'},
        {title: '通知名单', field: 'notifyLlist', align: 'center', valign: 'middle'},
        {title: '通知间隔时间(分钟)', field: 'notifyIntervalMin', align: 'center', valign: 'middle'},
        {title: '是否每月最后两天不通知', field: 'isMonthLastTwoDayNotifyDesc', align: 'center', valign: 'middle'},
        {title: '通知时间段', field: 'notifyTimeQuantum', align: 'center', valign: 'middle'},
        {title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle'},
        {title: '更新者', field: 'updator', align: 'center', valign: 'middle'},
        {title: '更新时间', field: 'updateTime', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
FlowWarnConf.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        FlowWarnConf.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加流量告警配置
 */
FlowWarnConf.openAddFlowWarnConf = function () {
    var index = layer.open({
        type: 2,
        title: '添加流量告警配置',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowWarnConf/flowWarnConf_add'
    });
//    this.layerIndex = index;
    layer.full(index);
};

/**
 * 打开查看流量告警配置详情
 */
FlowWarnConf.openFlowWarnConfDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '流量告警配置详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/flowWarnConf/flowWarnConf_update/' + FlowWarnConf.seItem.warnConfId
        });
//        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除流量告警配置
 */
FlowWarnConf.delete = function () {
    if (this.check()) {
//    	alert(this.seItem.warnConfId);
        var ajax = new $ax(Feng.ctxPath + "/flowWarnConf/delete", function (data) {
            Feng.success("删除成功!");
            FlowWarnConf.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("warnConfId",this.seItem.warnConfId);
        ajax.start();
    }
};

/**
 * 查询流量告警配置列表
 */
FlowWarnConf.search = function () {
    var queryData = {};
    queryData['warnType'] = $("#warnType").val();
    FlowWarnConf.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = FlowWarnConf.initColumn();
    var table = new BSTable(FlowWarnConf.id, "/flowWarnConf/list", defaultColunms);
    table.setPaginationType("client");
    FlowWarnConf.table = table.init();
});
