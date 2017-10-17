/**
 * 上游通道分析管理初始化
 */
var ChannelAnalyse = {
    id: "ChannelAnalyseTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelAnalyse.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '运营商编码', field: 'mobileOperator',  align: 'center', valign: 'middle',hidden : true},
        {title: '运营商名称', field: 'mobileOperatorName',  align: 'center', valign: 'middle'},
        {title: '省份', field: 'areaName',  align: 'center', valign: 'middle',sortable : true,sortname : 'area_name'},
        {title: '最近一小时平均回调时间(秒)', field: 'avgCallbackSec',  align: 'center', valign: 'middle',sortable : true,sortname : 'avg_callback_sec'},
        {title: '最近1小时成功率', field: 'succRatio',  align: 'center', valign: 'middle' ,sortable : true,sortname : 'succ_ratio'},
    ];
};

/**
 * 检查是否选中
 */
ChannelAnalyse.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelAnalyse.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加上游通道分析
 */
ChannelAnalyse.openAddChannelAnalyse = function () {
    var index = layer.open({
        type: 2,
        title: '添加上游通道分析',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelAnalyse/channelAnalyse_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看上游通道分析详情
 */
ChannelAnalyse.openChannelAnalyseDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '上游通道分析详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelAnalyse/channelAnalyse_update/' + ChannelAnalyse.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除上游通道分析
 */
ChannelAnalyse.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelAnalyse/delete", function (data) {
            Feng.success("删除成功!");
            ChannelAnalyse.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelAnalyseId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询上游通道分析列表
 */
ChannelAnalyse.search = function () {
	var params_={};
    params_.params = {};
    params_.params.mobileOperator = $("#mobileOperator").val();
    ChannelAnalyse.table.refresh({query: params_});
};

$(function () {
    var defaultColunms = ChannelAnalyse.initColumn();
    var table = new BSTable(ChannelAnalyse.id, "/channelAnalyse/list", defaultColunms);
//    table.setPaginationType("client");
    ChannelAnalyse.table = table.init();
});
