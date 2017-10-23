/**
 * 客户端自动升级管理初始化
 */
var Upgrade = {
    id: "UpgradeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Upgrade.initColumn = function () {
    return [
    	 {field: 'selectItem', radio: true},
        {title: '系统版本', field: 'version', align: 'center', valign: 'middle', sortable:true, sortName:'version', formatter:Upgrade.formatName},
        {title: '发布时间', field: 'createTime', align: 'center', valign: 'middle'},
        {title: '更新说明', field: 'versionNotes', align: 'center', valign: 'middle'},
        {title: '状态', field: 'statusName', align: 'center', valign: 'middle'},
        {title: '更新包大小(M)', field: 'componentSize', align: 'center', valign: 'middle'},
        {title: '升级时间', field: 'reserveTime', align: 'center', valign: 'middle'},
    ];
};

Upgrade.formatName=function(cellvalue, rowObject, index) {
    return '<a href="javascript:;" onclick="Upgrade.detail(\'' + rowObject.version + '\')">' + cellvalue + '</a>';
}

/**
 * 检查是否选中
 */
Upgrade.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Upgrade.seItem = selected[0];
        return true;
    }
};


/**
 * 打开查看客户端自动升级详情
 */
Upgrade.detail = function (version) {
    var index = layer.open({
        type: 2,
        title: '客户端自动升级详情',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/upgrade/detail/' + version + '/',
    });
    this.layerIndex = index;
//    layer.full(index);//全屏
};

/**
 * 时间修改
 */
Upgrade.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/upgrade/delete", function (data) {
            Feng.success("删除成功!");
            Upgrade.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("upgradeId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询客户端自动升级列表
 */
Upgrade.search = function () {
    var queryData = {
    		params:{}
    };
    Upgrade.table.refresh({query: queryData});
};

/**
 * 升级请求
 */
Upgrade.openChangeStatus = function () {
	var newVersion = $("#newVersion").val();
	 var ajax = new $ax(Feng.ctxPath + "/upgrade/autoUpgrade", function (data) {
	    	Feng.success("升级请求已发出！");
	        Upgrade.table.refresh();
	    }, function (data) {
	        Feng.error("请求失败!");
	    });
	    ajax.set("version",newVersion);
	    ajax.set("reserveTime",$("#reserveTime").val());
	    ajax.start();
       
};

$(function () {
    var defaultColunms = Upgrade.initColumn();
    var table = new BSTable(Upgrade.id, "/upgrade/list", defaultColunms);
//    table.setPaginationType("client");
    Upgrade.table = table.init();
    $(".hidden-xs").children().removeClass("button-margin");
});