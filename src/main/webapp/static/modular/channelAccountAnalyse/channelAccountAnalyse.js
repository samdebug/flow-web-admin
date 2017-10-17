/**
 * 通道结算账单管理初始化
 */
var ChannelAccountAnalyse = {
    id: "ChannelAccountAnalyseTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelAccountAnalyse.initColumn = function () {
    return [
            [
             {
                title: "",
               colspan: 4,
                rowspan: 1
             },
             {
            	 title: "提交成功",
            	 colspan: 4,
            	 align: 'center', valign: 'middle',
            	 rowspan: 1
             },
             {
            	 title: "充值失败",
            	 colspan: 4,
            	 align: 'center', valign: 'middle',
            	 rowspan: 1
             },
             {
            	 title: "充值成功",
            	 colspan: 4,
            	 align: 'center', valign: 'middle',
            	 rowspan: 1
             },
             {
            	 title: "",
            	 colspan: 3,
            	 rowspan: 1
             }
          ],
    [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '日期', field: 'analyseDate',  align: 'center', valign: 'middle'},
        {title: '供应商名称', field: 'supplierName',  align: 'center', valign: 'middle'},
        {title: '通道名称', field: 'channelName',  align: 'center', valign: 'middle'},
        {title: '数量', field: 'sentCount',  align: 'center', valign: 'middle'},
        {title: '原价', field: 'sentOriginPrice',  align: 'center', valign: 'middle'},
        {title: '代理价', field: 'sentCustomerPrice',  align: 'center', valign: 'middle'},
        {title: '通道价', field: 'sentChannelPrice',  align: 'center', valign: 'middle'},
        {title: '数量', field: 'failCount',  align: 'center', valign: 'middle'},
        {title: '原价', field: 'failOriginPrice',  align: 'center', valign: 'middle'},
        {title: '代理价', field: 'failCustomerPrice',  align: 'center', valign: 'middle'},
        {title: '通道价', field: 'failChannelPrice',  align: 'center', valign: 'middle'},
        {title: '数量', field: 'succCount',  align: 'center', valign: 'middle'},
        {title: '原价', field: 'succOriginPrice',  align: 'center', valign: 'middle'},
        {title: '代理价', field: 'succCustomerPrice',  align: 'center', valign: 'middle'},
        {title: '通道价', field: 'succChannelPrice',  align: 'center', valign: 'middle'},
        {title: '毛利', field: 'profit',  align: 'center', valign: 'middle'}
    ]];
};

/**
 * 检查是否选中
 */
ChannelAccountAnalyse.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelAccountAnalyse.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道结算账单
 */
ChannelAccountAnalyse.openAddChannelAccountAnalyse = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道结算账单',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelAccountAnalyse/channelAccountAnalyse_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看通道结算账单详情
 */
ChannelAccountAnalyse.openChannelAccountAnalyseDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '通道结算账单详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelAccountAnalyse/channelAccountAnalyse_update/' + ChannelAccountAnalyse.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除通道结算账单
 */
ChannelAccountAnalyse.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelAccountAnalyse/delete", function (data) {
            Feng.success("删除成功!");
            ChannelAccountAnalyse.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelAccountAnalyseId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询通道结算账单列表
 */
ChannelAccountAnalyse.search = function () {
	var params_={};
    params_.params = {};
    params_.params.startTime = $("#createStartTime").val();
    params_.params.endTime = $("#createEndTime").val();
    params_.params.channelName = $("#channelName").val();
    params_.params.supplierName = $("#supplierName").val();
    ChannelAccountAnalyse.table.refresh({query: params_});
};

$(function () {
    var defaultColunms = ChannelAccountAnalyse.initColumn();
    var table = new BSTable(ChannelAccountAnalyse.id, "/channelAccountAnalyse/list", defaultColunms);
//    table.setPaginationType("client");
    var exportOptions = {};
    exportOptions.ignoreColumn="[0]";
    exportOptions.fileName="通道结算账单";
    exportOptions.tableName="通道结算账单";
    exportOptions.worksheetName="通道结算账单";
    table.setExport(true,"['excel']","all",exportOptions);
    ChannelAccountAnalyse.table = table.init();
});
