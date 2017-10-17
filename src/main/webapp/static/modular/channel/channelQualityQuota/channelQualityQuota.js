/**
 * 通道质量标准管理初始化
 */
var ChannelQualityQuota = {
    id: "ChannelQualityQuotaTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelQualityQuota.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '指标ID', field: 'quotaId', visible: false, align: 'center', valign: 'middle'},
        {title: '指标名称', field: 'quotaName', align: 'center', valign: 'middle'},
        {title: '成功率比重', field: 'succRatioWeight', align: 'center', valign: 'middle'},
        {title: '及时率比重', field: 'timlyRateWeight', align: 'center', valign: 'middle'},
        {title: '价格率比重', field: 'priceRatioWeight', align: 'center', valign: 'middle'},
        {title: '人工权重率比重', field: 'manualRatioWeight', align: 'center', valign: 'middle'},
        {title: '成功率基线', field: 'succBaseLine', align: 'center', valign: 'middle'},
        {title: '及时率基线', field: 'timlyBaseLine', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle'},
        {title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
        {title: '修改时间', field: 'updateTime', align: 'center', valign: 'middle'},
        {title: '修改者', field: 'updator', align: 'center', valign: 'middle'},
    ];
};

/**
 * 检查是否选中
 */
ChannelQualityQuota.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelQualityQuota.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道质量标准
 */
ChannelQualityQuota.openAddChannelQualityQuota = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道质量标准',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelQualityQuota/channelQualityQuota_add'
    });
    this.layerIndex = index;
    layer.full(index);
};

/**
 * 打开查看通道质量标准详情
 */
ChannelQualityQuota.openChannelQualityQuotaDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '通道质量标准详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelQualityQuota/channelQualityQuota_update/' + this.seItem.quotaId
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除通道质量标准
 */
ChannelQualityQuota.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelQualityQuota/delete", function (data) {
            Feng.success("删除成功!");
            ChannelQualityQuota.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelQualityQuotaId",this.seItem.quotaId);
        ajax.start();
    }
};

/**
 * 查询通道质量标准列表
 */
ChannelQualityQuota.search = function () {
    var queryData = {
    		param:{}
    };
    queryData.param['quotaName'] = $("#quotaName").val();
    queryData.param['succRatioWeight'] = $("#succRatioWeight").val();
    queryData.param['timlyRateWeight'] = $("#timlyRateWeight").val();
    ChannelQualityQuota.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = ChannelQualityQuota.initColumn();
    var table = new BSTable(ChannelQualityQuota.id, "/channelQualityQuota/list", defaultColunms);
   // table.setPaginationType("client");
    ChannelQualityQuota.table = table.init();
});
