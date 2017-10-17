/**
 * 充值明细管理初始化
 */
var ChannelCompanyRecharge = {
    id: "ChannelCompanyRechargeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelCompanyRecharge.initColumn = function () {
    return [
        {title: '充值记录ID', field: 'companyRechargeId', visible: false,align: 'center', valign: 'middle'},
        //{title: '交易类型', field: 'typeDesc', align: 'center', valign: 'middle'},
        {title: '充值时间', field: 'inputTime', sortable: true,sortName: 'input_time', align: 'center', valign: 'middle'},
        {title: '操作金额', field: 'money', sortable: true,sortName: 'money', align: 'center', valign: 'middle'},
        {title: '操作者', field: 'loginName', align: 'center', valign: 'middle'},
        //{title: '操作者IP', field: 'operateIp', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
ChannelCompanyRecharge.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelCompanyRecharge.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加充值明细
 */
ChannelCompanyRecharge.openAddChannelCompanyRecharge = function () {
    var index = layer.open({
        type: 2,
        title: '添加充值明细',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelCompanyRecharge/channelCompanyRecharge_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看充值明细详情
 */
ChannelCompanyRecharge.openChannelCompanyRechargeDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '充值明细详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelCompanyRecharge/channelCompanyRecharge_update/' + ChannelCompanyRecharge.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除充值明细
 */
ChannelCompanyRecharge.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelCompanyRecharge/delete", function (data) {
            Feng.success("删除成功!");
            ChannelCompanyRecharge.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelCompanyRechargeId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询充值明细列表
 */
ChannelCompanyRecharge.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['startTime'] = $("#startTime").val();
    queryData.params['endTime'] = $("#endTime").val();
    ChannelCompanyRecharge.table.refresh({query: queryData});
};

ChannelCompanyRecharge.export2Excel=function(){
	location.href=encodeURI(Feng.ctxPath + '/channelCompanyRecharge/export' + "?params['companyId']=" + $("#companyId").val());
/*			+ "&params['startTime']=" + $("#startTime").val()
			+ "&params['endTime']=" + $("#endTime").val());*/
}

$(function () {
    var defaultColunms = ChannelCompanyRecharge.initColumn();
    var table = new BSTable(ChannelCompanyRecharge.id, '/channelCompanyRecharge/query?companyId=' +$("#companyId").val(), defaultColunms);
    table.setHeight(260);
    // table.setPaginationType("client");
    ChannelCompanyRecharge.table = table.init();
});
