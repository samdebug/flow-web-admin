/**
 * 产品设置管理初始化
 */
var FlowProductRemodel = {
    id: "FlowProductRemodelTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
FlowProductRemodel.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '流量包代码', field: 'productCode', align: 'center', valign: 'middle', sortable : true, sortName : 'product_code',formatter:FlowProductRemodel.formatName},
        {title: '流量包名称', field: 'productName', align: 'center', valign: 'middle'},
        {title: '流量大小(M)', field: 'flowAmount', align: 'center', valign: 'middle',sortable : true, sortName : 'flow_amount',},
        {title: '适用区域', field: 'zoneDesc', align: 'center', valign: 'middle'},
        {title: '运营商', field: 'operatorCodeDesc', align: 'center', valign: 'middle'},
        {title: '标准销售价格(元)', field: 'productPrice', align: 'center', valign: 'middle',sortable:true, sortName:'product_price'},
    ];
    
};


FlowProductRemodel.formatName=function(cellvalue, rowObject,index ) {
	return '<a href="javascript:;" onclick="FlowProductRemodel.detail(\''
			+ rowObject.productId + '\')">' + cellvalue
			+ '</a>';
}

//FlowProductInfo.actionButtons=function(cellvalue, rowObject, index) {
//	return '<div >'
//			+ '<button onclick=\"FlowProductInfo.openFlowProductInfoDetailById('
//			+ rowObject.productId
//			+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >'
//			+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
//			+ '</button>'
//			+ '<button onclick=\"FlowProductInfo.deleteById('
//			+ rowObject.productId
//			+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >'
//			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
//			+ '</button>' 
//			+ '<button onclick=\"FlowProductInfo.detail('
//			+ rowObject.productId
//			+ ')\" class=\"btn btn-xs btn-warning\" data-rel=\"tooltip\" title=\"详情\" >'
//			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
//			+ '</button>' 
//			+ '</div>';
//}

/**
 * 检查是否选中
 */
FlowProductRemodel.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
    	FlowProductRemodel.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加产品设置
 */
FlowProductRemodel.openAddFlowProductRemodel = function () {
    var index = layer.open({
        type: 2,
        title: '添加流量包',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowProductRemodel/flowProductRemodel_add'
    });
    this.layerIndex = index;
//    layer.full(index);//全屏
};

/**
 * 打开查看产品设置修改
 */
FlowProductRemodel.openUpdateFlowProductRemodel = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '修改流量包',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/flowProductRemodel/flowProductRemodel_update/' + FlowProductRemodel.seItem.productId
        });
        this.layerIndex = index;
//        layer.full(index);//全屏
    }
};

/**
 * 打开查看产品详细参数
 */
FlowProductRemodel.detail = function (productId) {
    var index = layer.open({
        type: 2,
        title: '流量包详情',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowProductRemodel/detail/' + productId
    });
    this.layerIndex = index;
//    layer.full(index);//全屏
}

/**
 * 删除产品设置
 */
FlowProductRemodel.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/flowProductRemodel/delete", function (data) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data && data.message ? data.message : "操作失败！");	
        		return;
        	}
            Feng.success("删除成功!");
            FlowProductRemodel.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("productId",this.seItem.productId);
        ajax.start();
    }
};

FlowProductRemodel.reSet = function(){
    $("#productCode").val("");
    $("#productName").val("");
    $("#operatorCode").val("");
    $("#zone").val("");
}

/**
 * 查询产品设置列表
 */
FlowProductRemodel.search = function () {
	 var queryData = {
		    	params:{}
		    };
		    queryData.params['productCode'] = $("#productCode").val();
		    queryData.params['productName'] = $("#productName").val();
		    queryData.params['operatorCode'] = $("#operatorCode").val();
		    queryData.params['zone'] = $("#zone").val();
		    FlowProductRemodel.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = FlowProductRemodel.initColumn();
    var table = new BSTable(FlowProductRemodel.id, "/flowProductRemodel/list", defaultColunms);
//   table.setPaginationType("client");
    FlowProductRemodel.table = table.init();
});
