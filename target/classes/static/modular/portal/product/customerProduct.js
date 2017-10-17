/**
 * 客户产品配置管理管理初始化
 */
var CustomeProduct = {
    id: "CustomeProductTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
CustomeProduct.initColumn = function () {
    return [
        {field: 'selectItem', radio: true, visible: false},
        {title: '编号', field: 'orderIdStr', align: 'center', valign: 'middle', formatter: formatOrderId},
        {title: '客户名称', field: 'customerName', align: 'center', valign: 'middle', formatter: formatCustomerName},
        {title: '产品类型', field: 'orderTypeDesc', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true, sortName: 'create_time'},
        {title: '订单状态', field: 'statusDesc', align: 'center', valign: 'middle' }
    ];
};


function formatCustomerName(cellvalue, rowObject, options) {
    return $.htmlspecialchars(cellvalue);
}

function formatOrderId(cellvalue, rowObject, options) {
    return '<a href="javascript:;" onclick="CustomeProduct.openCustomeProductView(\'' + cellvalue + '\')">' + cellvalue + '</a>';
}

/**
 * 重置表单
 */
CustomeProduct.reset = function(){
	$("#pOIL-customer-select").select2("val", "");
    document.getElementById("queryForm").reset();
}

/**
 * 检查是否选中
 */
CustomeProduct.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        CustomeProduct.seItem = selected[0];
        return true;
    }
};

/**
 * 查看
 */
CustomeProduct.openCustomeProductView = function(id) {
	  var index = layer.open({
	      type: 2,
	      title: '配置详情',
	      area: ['1100px', '600px'], //宽高
	      fix: false, //不固定
	      maxmin: true,
	      content: Feng.ctxPath + '/portal/product_view?orderId=' + id
	  });
	  this.layerIndex = index;
//	  layer.full(index);// 全屏
}

/**
 * 查询客户产品配置管理列表
 */
CustomeProduct.search = function () {
    CustomeProduct.table.refresh({query: $("#queryForm").serializeJson()});
};


$(function () { 
    var defaultColunms = CustomeProduct.initColumn();
    var table = new BSTable(CustomeProduct.id, "/orderInfo/query?params['customerId']=" + $("#customerId").val(), defaultColunms);
    CustomeProduct.table = table.init();
});
