/**
 * 通道适配器管理初始化
 */
var ChannelAdapter = {
    id: "ChannelAdapterTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelAdapter.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '适配器ID', field: 'adapterId', visible: false, align: 'center', valign: 'middle'},
        {title: '适配器名称', field: 'adapterName', align: 'center', align: 'middle',formatter:ChannelAdapter.formatName},
        {title: '适配器类名', field: 'clazzName', align: 'center', align: 'middle'},
        //{title: '可重发错误代码', field: 'resendErrorCode', align: 'center', valign: 'middle'},
        //{title: '不可用错误代码', field: 'notavailableErrorCode', align: 'center', valign: 'middle'},
        {title: '创建者', field: 'creator',align: 'center',align: 'middle'},
        {title: '创建时间', field: 'createTime',  align: 'center',sortable : true,sortName : 'create_time',align: 'middle'},
       //{title : '操作',	field : 'opt',	width : 120,fixed : true,formatter : ChannelAdapter.actionButtons,align: 'center'}
    ];
};


ChannelAdapter.actionButtons=function(cellvalue, rowObject, index) {
	return '<div >'
			+ '<button onclick=\"ChannelAdapter.openChannelAdapterDetailById('
			+ rowObject.adapterId
			+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >'
			+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
			+ '</button>'
			+ '<button onclick=\"ChannelAdapter.deleteById('
			+ rowObject.adapterId
			+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >'
			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
			+ '</button>' + '</div>';
}


ChannelAdapter.formatName=function(cellvalue, rowObject, index) {
	return '<a href="javascript:;" onclick="ChannelAdapter.viewEvent(\''
			+ rowObject.adapterId + '\')">' + cellvalue + '</a>';
}

ChannelAdapter.viewEvent=function(id) {
	var index = layer.open({
        type: 2,
        title: '通道适配器详情',
        area: ['800px', '350px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelAdapter/detail/' + id,
    });
    this.layerIndex = index;
   // layer.full(index);
};

/**
 * 检查是否选中
 */
ChannelAdapter.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelAdapter.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道适配器
 */
ChannelAdapter.openAddChannelAdapter = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道适配器',
        area: ['800px', '350px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelAdapter/channelAdapter_add'
    });
    this.layerIndex = index;
   // layer.full(index);
};

/**
 * 打开查看通道适配器详情
 */
ChannelAdapter.openChannelAdapterDetail = function (id) {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '通道适配器详情',
            area: ['800px', '350px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelAdapter/channelAdapter_update/' + (this.seItem.adapterId!=""?this.seItem.adapterId:id)
        });
        this.layerIndex = index;
      //  layer.full(index);
    }
};

/**
 * 删除通道适配器
 */
ChannelAdapter.delete = function () {
    if (this.check()) {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
	        var ajax = new $ax(Feng.ctxPath + "/channelAdapter/delete", function (data) {
	            Feng.success("删除成功!");
	            ChannelAdapter.table.refresh();
	        }, function (data) {
	            Feng.error("删除失败!");
	        });
	        ajax.set("channelAdapterId",ChannelAdapter.seItem.adapterId);
	        ajax.start();
    	});
    }
};

/**
 * 打开查看通道适配器详情
 */
ChannelAdapter.openChannelAdapterDetailById = function (id) {
    if (id!=undefined&&id!="") {
        var index = layer.open({
            type: 2,
            title: '通道适配器详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelAdapter/channelAdapter_update/'+id
        });
        this.layerIndex = index;
      //  layer.full(index);
    }else{
    	 Feng.error("请选择需要查看的数据");
    }
};

/**
 * 删除通道适配器
 */
ChannelAdapter.deleteById = function (id) {
	 if (id!=undefined&&id!="") {
		 layer.confirm('是否要删除？', function(index){
			 layer.close(index);
	        var ajax = new $ax(Feng.ctxPath + "/channelAdapter/delete", function (data) {
	            Feng.success("删除成功!");
	            ChannelAdapter.table.refresh();
	        }, function (data) {
	            Feng.error("删除失败!");
	        });
	        ajax.set("channelAdapterId",id);
	        ajax.start();
		 });
    }else{
    	Feng.error("未选择任何数据!");
    }
};

/**
 * 查询通道适配器列表
 */
ChannelAdapter.search = function () {
    var queryData = {
    	params:{}
    };
    queryData.params['adapterName'] = $("#adapterName").val();
    queryData.params['clazzName'] = $("#clazzName").val();
    ChannelAdapter.table.refresh({query: queryData});
};

ChannelAdapter.reSet = function(){
	 $("#adapterName").val("");
	 $("#clazzName").val("");
}

$(function () {
    var defaultColunms = ChannelAdapter.initColumn();
    var table = new BSTable(ChannelAdapter.id, "/channelAdapter/list", defaultColunms);
  //  table.setPaginationType("client");
    ChannelAdapter.table = table.init();
});
