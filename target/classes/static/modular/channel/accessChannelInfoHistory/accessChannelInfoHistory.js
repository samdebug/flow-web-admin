/**
 * 通道变更记录管理初始化
 */
var AccessChannelInfoHistory = {
    id: "AccessChannelInfoHistoryTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
AccessChannelInfoHistory.initColumn = function () {
    return [
        {field: 'selectItem', radio: true,visible: false},
        {title: '是否有效', field: 'isValidDesc', align: 'center', valign: 'middle'},
        {title: '折扣', field: 'discount', align: 'center', valign: 'middle'},
        {title: '产品', field: 'productInfos', align: 'center', valign: 'middle'},
        {title: '修改者', field: 'updator', align: 'center', valign: 'middle'},
        {title: '修改时间', field: 'updateTime', align: 'center', valign: 'middle'}
    ];
};

/**
 * 查询通道变更记录列表
 */
AccessChannelInfoHistory.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['createStartTime'] = $("#createStartTime").val();
    queryData.params['createEndTime'] = $("#createEndTime").val();
    AccessChannelInfoHistory.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = AccessChannelInfoHistory.initColumn();
    var table = new BSTable(AccessChannelInfoHistory.id, "/accessChannelInfoHis/query?channelSeqId="+$("#channelSeqId").val(), defaultColunms);
    //table.setPaginationType("client");
    AccessChannelInfoHistory.table = table.init();
});

$('#resetBtn').on('click', function() {
	$("#createStartTime").val("");
	$("#createEndTime").val("");
});
