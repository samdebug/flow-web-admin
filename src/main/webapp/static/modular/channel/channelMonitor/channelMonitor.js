/**
 * 通道监控管理初始化
 */
var ChannelMonitor = {
    id: "ChannelMonitorTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelMonitor.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '通道ID', field: 'channelId',  align: 'center', valign: 'middle'},
        {title: '通道名称', field: 'channelName', align: 'center', valign: 'middle'},
        {title: '下发总计', field: 'totalCount', align: 'center', valign: 'middle'},
        {title: '下发金额', field: 'totalFee', align: 'center', valign: 'middle'},
        {title: '成功总计', field: 'successCount', align: 'center', valign: 'middle'},
        {title: '成功金额', field: 'successMoney', align: 'center', valign: 'middle'},
        {title: '失败总计', field: 'failCount', align: 'center', valign: 'middle'},
        {title: '失败金额', field: 'failMoney', align: 'center', valign: 'middle'},
        {title: '已发总计', field: 'sendCount', align: 'center', valign: 'middle'},
        {title: '已发金额', field: 'sendMoney', align: 'center', valign: 'middle'},
        {title: '利润', field: 'profit', align: 'center', valign: 'middle'},
        {title: '毛利率', field: 'profitRatio', align: 'center', valign: 'middle'},
        {title: '充值成功率(%)', field: 'succRatio', align: 'center', valign: 'middle'},
    ];
};

/**
 * 检查是否选中
 */
ChannelMonitor.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelMonitor.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道监控
 */
ChannelMonitor.openAddChannelMonitor = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道监控',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelMonitor/channelMonitor_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看通道监控详情
 */
ChannelMonitor.openChannelMonitorDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '通道监控详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelMonitor/channelMonitor_update/' + ChannelMonitor.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除通道监控
 */
ChannelMonitor.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelMonitor/delete", function (data) {
            Feng.success("删除成功!");
            ChannelMonitor.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelMonitorId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询通道监控列表
 */
ChannelMonitor.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['channelId'] = $("#channelId").val();
    queryData.params['channelName'] = $("#channelName").val();
    queryData.params['timeDec'] = $("#timeDec").val();
    queryData.params['createStartTime'] = $("#createStartTime").val();
    queryData.params['createEndTime'] = $("#createEndTime").val();
    ChannelMonitor.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = ChannelMonitor.initColumn();
    var table = new BSTable(ChannelMonitor.id, "/channelMonitor/list", defaultColunms);
    //table.setPaginationType("client");
    ChannelMonitor.table = table.init();
});
