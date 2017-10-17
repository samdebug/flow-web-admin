/**
 * 产品设置管理初始化
 */
var FlowProductInfo = {
    id: "FlowProductInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
FlowProductInfo.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
//        {title: 'id', field: 'productId', visible: false, align: 'center', valign: 'middle'},
        {title: '产品ID号', field: 'productId', align: 'center', valign: 'middle', sortable:true, sortName:'product_id'},
        {title: '产品名称', field: 'productName', align: 'center', valign: 'middle', formatter:FlowProductInfo.formatName},
        {title: '产品代码', field: 'productCode', align: 'center', valign: 'middle'},
        {title: '产品类别', field: 'productTypeDesc', align: 'center', valign: 'middle'},
        {title: '容量大小(M)', field: 'flowPackageInfo.flowAmount', align: 'center', valign: 'middle',sortable : true, sortname : 'flow_package_info.flowAmount',},
        {title: '适用区域', field: 'zoneDesc', align: 'center', valign: 'middle'},
        {title: '适用运营商', field: 'flowPackageInfo.operatorName', align: 'center', valign: 'middle'},
        {title: '产品定价(元)', field: 'productPrice', align: 'center', valign: 'middle',sortable:true, sortName:'product_price'},
//        {title: '产品归属', field: 'productBelongTo', align: 'center', valign: 'middle'},
//        {title: '产品描述', field: 'productDesc', align: 'center', valign: 'middle'},
//        {title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
//        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle'},
//        {title: '更新者', field: 'updator', align: 'center', valign: 'middle'},
//        {title: '更新时间', field: 'updateTime', align: 'center', valign: 'middle'},
//        {title: '流量包ID', field: 'packageId', align: 'center', valign: 'middle'},
//        {title: '操作', field: 'myac', align: 'center', valign: 'middle' ,formatter : FlowProductInfo.actionButtons}
    ];
    
};


FlowProductInfo.formatName=function(cellvalue, rowObject,index ) {
	return '<a href="javascript:;" onclick="FlowProductInfo.detail(\''
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
FlowProductInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        FlowProductInfo.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加产品设置
 */
FlowProductInfo.openAddFlowProductInfo = function () {
    var index = layer.open({
        type: 2,
        title: '添加产品',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowProductInfo/flowProductInfo_add'
    });
    this.layerIndex = index;
    layer.full(index);//全屏
};

/**
 * 打开查看产品设置修改
 */
FlowProductInfo.openFlowProductInfoUpdate = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '修改产品',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/flowProductInfo/flowProductInfo_update/' + FlowProductInfo.seItem.productId
        });
        this.layerIndex = index;
        layer.full(index);//全屏
    }
};

/**
 * 打开查看产品详细参数
 */
FlowProductInfo.detail = function (productId) {
    var index = layer.open({
        type: 2,
        title: '产品详情',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowProductInfo/detail/' + productId
    });
    this.layerIndex = index;
    layer.full(index);//全屏
}

/**
 * 删除产品设置
 */
FlowProductInfo.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/flowProductInfo/delete", function (data) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data && data.message ? data.message : "操作失败！");	
        		return;
        	}
            Feng.success("删除成功!");
            FlowProductInfo.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("flowProductInfoId",this.seItem.productId);
        ajax.start();
    }
};

FlowProductInfo.reSet = function(){
    $("#productCode").val("");
    $("#productName").val("");
    $("#productType").val("");
}

/**
 * 查询产品设置列表
 */
FlowProductInfo.search = function () {
	 var queryData = {
		    	params:{}
		    };
		    queryData.params['productCode'] = $("#productCode").val();
		    queryData.params['productName'] = $("#productName").val();
		    queryData.params['productType'] = $("#productType").val();
    FlowProductInfo.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = FlowProductInfo.initColumn();
    var table = new BSTable(FlowProductInfo.id, "/flowProductInfo/list", defaultColunms);
//   table.setPaginationType("client");
    FlowProductInfo.table = table.init();
});
