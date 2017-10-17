/**
 * 通道维护信息管理初始化
 */
var ChannelMaintainInfo = {
    id: "ChannelMaintainInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelMaintainInfo.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '维护自增序列', field: 'maintainSeqId', visible: false, align: 'center', valign: 'middle'},
        {title: '维护信息名称', field: 'maintainName', align: 'center', valign: 'middle'},
        {title: '通道类别', field: 'channelTypeDesc', align: 'center', valign: 'middle'},
        {title: '适用区域', field: 'zoneDesc', align: 'center', valign: 'middle'},
        {title: '运营商', field: 'operatorCodeDesc', align: 'center', valign: 'middle'},
        {title: '维护公告', field: 'notice', align: 'center', valign: 'middle'},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle'},
        {title: '开始时间', field: 'startTime', align: 'center', valign: 'middle'},
        {title: '结束时间', field: 'endTime', align: 'center', valign: 'middle'},
        {title: '是否有效', field: 'isValidDesc', align: 'center', valign: 'middle'},
        {title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle'},
        {title: '修改者', field: 'updator', align: 'center', valign: 'middle'},
        {title: '修改时间', field: 'updateTime', align: 'center', valign: 'middle'},
        {title: '创建者IP', field: 'ip', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
ChannelMaintainInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelMaintainInfo.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道维护信息
 */
ChannelMaintainInfo.openAddChannelMaintainInfo = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道维护信息',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelMaintainInfo/channelMaintainInfo_add'
    });
    this.layerIndex = index;
    layer.full(index);
};

/**
 * 打开查看通道维护信息详情
 */
ChannelMaintainInfo.openChannelMaintainInfoDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '通道维护信息详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelMaintainInfo/channelMaintainInfo_update/' + ChannelMaintainInfo.seItem.maintainSeqId
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 删除通道维护信息
 */
ChannelMaintainInfo.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/channelMaintainInfo/delete", function (data) {
            Feng.success("删除成功!");
            ChannelMaintainInfo.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("channelMaintainInfoId",this.seItem.maintainSeqId);
        ajax.start();
    }
};

/**
 * 查询通道维护信息列表
 */
ChannelMaintainInfo.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['maintainName'] = $("#maintainName").val();
    queryData.params['zone'] = $("#zone").val();
    queryData.params['operatorCode'] = $("#operatorCode").val();
    queryData.params['condition'] = $("#condition").val();
    queryData.params['condition'] = $("#condition").val();
    ChannelMaintainInfo.table.refresh({query: queryData});
};

ChannelMaintainInfo.showAreaCode= function () {
	// 全国区域展示
	$.ajax({
		url : Feng.ctxPath + '/channelMaintainInfo/selectAreaCodeAll',
		type : 'get',
		dataType : 'json',
		success : function(data) {
			var html = '<option value="">请选择区域</option>';
			for (var i = 0; i < data.length; i++) {
				html += '<option value="'+data[i].areaCode+'">'
						+ data[i].areaName + '</option>';
			}
			$("#zone").html(html);
		}
	});
}

$(function () {
    var defaultColunms = ChannelMaintainInfo.initColumn();
    var table = new BSTable(ChannelMaintainInfo.id, "/channelMaintainInfo/list", defaultColunms);
   // table.setPaginationType("client");
    ChannelMaintainInfo.table = table.init();
    ChannelMaintainInfo.showAreaCode();
});

