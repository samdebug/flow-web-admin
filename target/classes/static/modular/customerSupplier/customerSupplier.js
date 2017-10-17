/**
 * 客户供应商管理管理初始化
 */
var CustomerSupplier = {
    id: "CustomerSupplierTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    
    supplierId: "cSupplierTable",
    supplierTable: null,
    layerIndex: -1,
    queryData : {}
};

/**
 * 初始化表格的列
 */
CustomerSupplier.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '日期', field: 'tradeTime', align: 'center', valign: 'middle'},
        {title: '客户账号', field: 'customerName', align: 'center', valign: 'middle'},
        {title: '客户充值金额', field: 'tradeAmount', align: 'center', valign: 'middle'},
        {title: '客户端授信金额', field: 'creditAmount', align: 'center', valign: 'middle'}
    ];
};

/**
 * 供应商table
 */
CustomerSupplier.initSupplierColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '日期', field: 'inputTime', align: 'center', valign: 'middle'},
        {title: '供应商名称', field: 'companyName', align: 'center', valign: 'middle'},
        {title: '供应商充值金额', field: 'money', align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
//CustomerSupplier.check = function () {
//    var selected = $('#' + this.id).bootstrapTable('getSelections');
//    if(selected.length == 0){
//        Feng.info("请先选中表格中的某一记录！");
//        return false;
//    }else{
//        CustomerSupplier.seItem = selected[0];
//        return true;
//    }
//};

/**
 * 点击添加客户供应商管理
 */
//CustomerSupplier.openAddCustomerSupplier = function () {
//    var index = layer.open({
//        type: 2,
//        title: '添加客户供应商管理',
//        area: ['800px', '420px'], //宽高
//        fix: false, //不固定
//        maxmin: true,
//        content: Feng.ctxPath + '/customerSupplier/customerSupplier_add'
//    });
//    this.layerIndex = index;
//};

/**
 * 打开查看客户供应商管理详情
 */
//CustomerSupplier.openCustomerSupplierDetail = function () {
//    if (this.check()) {
//        var index = layer.open({
//            type: 2,
//            title: '客户供应商管理详情',
//            area: ['800px', '420px'], //宽高
//            fix: false, //不固定
//            maxmin: true,
//            content: Feng.ctxPath + '/customerSupplier/customerSupplier_update/' + CustomerSupplier.seItem.id
//        });
//        this.layerIndex = index;
//    }
//};

/**
 * 删除客户供应商管理
 */
//CustomerSupplier.delete = function () {
//    if (this.check()) {
//        var ajax = new $ax(Feng.ctxPath + "/customerSupplier/delete", function (data) {
//            Feng.success("删除成功!");
//            CustomerSupplier.table.refresh();
//        }, function (data) {
//            Feng.error("删除失败!");
//        });
//        ajax.set("customerSupplierId",this.seItem.id);
//        ajax.start();
//    }
//};

/**
 * 查询客户供应商管理列表
 */
CustomerSupplier.search = function () {
    var queryData = getQueryData();
    
    CustomerSupplier.table.refresh({query: queryData});
    CustomerSupplier.supplierTable.refresh({query: queryData});
    
    chargeDiff();
};

/**
 * 清空查询条件
 */
CustomerSupplier.reset = function() {
	document.getElementById("queryForm").reset();
	CustomerSupplier.queryData = {};
}

function getQueryData(){
	var array = $("#queryForm").serializeArray();
	CustomerSupplier.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerSupplier.queryData[this.name] = this.value;
	    });
	}
	return CustomerSupplier.queryData;
}


function chargeDiff() {
	var ajax = new $ax(Feng.ctxPath + "/customerSupplier/chargeDiff", function (data) {
		if ( data && data.success ) {
			$("#charge_diff").text(data.sum_tradeAmount+"（客户充值金额）- "+data.sum_money+"（供应商充值金额）= "+data.charge_diff +"（元）");
			return;
		}
		$("#charge_diff").text("暂无数据");
	}, function (data) {
		Feng.error("计算充值净值差失败!");
		$("#charge_diff").text("暂无数据");
	});
	ajax.set(CustomerSupplier.queryData);
	ajax.start();
}

$(function () {
    var defaultColunms = CustomerSupplier.initColumn();
    var table = new BSTable(CustomerSupplier.id, "/customerSupplier/customerList", defaultColunms);
//    table.setPaginationType("client");
    CustomerSupplier.table = table.init();
    
    
    var supplierTable = new BSTable(CustomerSupplier.supplierId, "/customerSupplier/supplierList", CustomerSupplier.initSupplierColumn());
//    supplierTable.setPaginationType("client");
    CustomerSupplier.supplierTable = supplierTable.init();
    
    chargeDiff();
    
    // 隐藏 两个table的toolbar（不显示原生的刷新和列表按钮）
    $(".bootstrap-table .fixed-table-toolbar").hide();
    
});
