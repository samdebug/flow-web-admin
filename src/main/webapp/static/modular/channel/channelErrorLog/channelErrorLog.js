/**
 * 通道错误日志管理初始化
 */
var ChannelErrorLog = {
    id: "ChannelErrorLogTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelErrorLog.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '通道流水ID', field: 'channelFlowId', visible: false, align: 'center', valign: 'middle'},
        {title: '通道ID', field: 'channelId', align: 'center', valign: 'middle'},
        {title: '通道名称', field: 'channelName', align: 'center', valign: 'middle'},
        {title: '流量下发适配器', field: 'adapterName', align: 'center', valign: 'middle'},
        {title: '错误信息', field: 'errorMsg', align: 'center', valign: 'middle'},
        {title: '录入时间', field: 'inputTime', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
ChannelErrorLog.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelErrorLog.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道错误日志
 */
ChannelErrorLog.openAddChannelErrorLog = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道错误日志',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelErrorLog/channelErrorLog_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看通道错误日志详情
 */
ChannelErrorLog.openChannelErrorLogDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '通道错误日志详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelErrorLog/channelErrorLog_update/' + this.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除通道错误日志
 */
ChannelErrorLog.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelErrorLog/delete", function (data) {
            Feng.success("删除成功!");
            ChannelErrorLog.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelErrorLogId",this.seItem.channelFlowId);
        ajax.start();
    }
};

/**
 * 查询通道错误日志列表
 */
ChannelErrorLog.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.param['channelId'] = $("#channelId").val();
    queryData.param['channelName'] = $("#channelName").val();
    queryData.param['adapterName'] = $("#adapterName").val();
    ChannelErrorLog.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = ChannelErrorLog.initColumn();
    var table = new BSTable(ChannelErrorLog.id, "/channelErrorLog/list", defaultColunms);
   // table.setPaginationType("client");
    ChannelErrorLog.table = table.init();
});
