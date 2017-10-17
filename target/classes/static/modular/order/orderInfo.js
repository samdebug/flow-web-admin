/**
 * 客户产品配置管理管理初始化
 */
var OrderInfo = {
    id: "OrderInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
OrderInfo.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '订单编号', field: 'orderIdStr', align: 'center', valign: 'middle', formatter: formatOrderId},
        {title: '客户名称', field: 'customerName', align: 'center', valign: 'middle', formatter: formatCustomerName},
        {title: '合作伙伴', field: 'partnerName', align: 'center', valign: 'middle'},
        {title: '合作类型', field: 'partnerTypeDesc', align: 'center', valign: 'middle'},
        {title: '订单类型', field: 'orderTypeDesc', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true, sortName: 'create_time'},
        {title: '订单状态', field: 'statusDesc', align: 'center', valign: 'middle' },
        {title: '操作', field: 'myac', align: 'center', valign: 'middle', formatter: formatActionButtons}
    ];
};


function formatCustomerName(cellvalue, rowObject, options) {
    return $.htmlspecialchars(cellvalue);
}

function formatOrderId(cellvalue, rowObject, options) {
    return '<a href="javascript:;" onclick="OrderInfo.openOrderInfoView(\'' + cellvalue + '\')">' + cellvalue + '</a>';
}

function formatActionButtons(cellvalue, rowObject, options) {
    var btnHtml = "<div>";
    // 需求变更：管理员和合作伙伴都可以改变订单状态 start
    var orderStatusTemp = rowObject['status'];
    var btnTitle = "";
    var changeStatus = 0;
    if (orderStatusTemp == 1) {
        btnTitle = "生效";
        changeStatus = 2;
    } else if (orderStatusTemp == 2) {
        btnTitle = "暂停";
        changeStatus = 3;
    } else if (orderStatusTemp == 3) {
        btnTitle = "生效";
        changeStatus = 2;
    }
    btnHtml += '<button onclick=\"OrderInfo.orderTypeBtnClickHandler('+ changeStatus + ',\'' + rowObject['orderIdStr'] + '\')\" class=\"btn btn-xs btn-warning\" permCheck=\"auth_order_manager_list,changestatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" ><i class=\"ace-icon fa fa-flag bigger-120\"></i></button>';
    // 需求变更：管理员和合作伙伴都可以改变订单状态 end
    
    if (rowObject.status != 2) {
        btnHtml += '&nbsp;<button onclick=\"OrderInfo.openOrderInfoDetail(\'' + rowObject['orderIdStr'] + '\', '+ rowObject['orderType'] +')\" class=\"btn btn-xs btn-info\" permCheck=\"auth_order_manager_list,modify,hidden\" data-rel=\"tooltip\" title=\"修改\" ><i class=\"ace-icon fa fa-pencil bigger-120\"></i></button>';
    }
    // TODO 
    btnHtml += '&nbsp;<button onclick=\"OrderInfo.exportHandler(\'' + rowObject['orderIdStr'] + '\')\" class=\"btn btn-xs btn-primary\" permCheck=\"auth_order_manager_list,exportById,hidden\" data-rel=\"tooltip\" title=\"导出\" ><i class=\"ace-icon fa fa-share-square-o bigger-120\"></i></button>';
    btnHtml += "</div>";
    return btnHtml;
}


/**
 * 暂停/生效 按钮点击事件
 */
OrderInfo.orderTypeBtnClickHandler = function(status, orderId) {
	var ajax = new $ax(Feng.ctxPath + '/orderInfo/changeStatus?status=' + status + '&orderId=' + orderId, function(data){
    	if ( data && data.code && data.code == 200 ) {
    		Feng.success("操作成功！");
    		OrderInfo.table.refresh();
    	} else {
    		Feng.error(data.message ? data.message : "操作失败！");
    	}
    },function(data){
        Feng.error("操作失败！");
    });
    ajax.start();
}

/**
 * 重置表单
 */
OrderInfo.reset = function(){
	$("#pOIL-customer-select").select2("val", "");
    document.getElementById("queryForm").reset();
}
/**
 * 导出指定订单
 */
OrderInfo.exportHandler = function(id) {
	$('#queryForm').exportData({
        url: Feng.ctxPath + '/orderInfo/downLoadOrderByOrderId',
        data: {'orderId' : id},
        callback: function(data) {},
        failure: function(form, action) {}
    });
}
/**
 * 全部
 */
OrderInfo.exportAllHandler = function() {
	$('#queryForm').exportData({
        url: Feng.ctxPath + '/orderInfo/downLoadOrder',
        callback: function(data) {
        },
        failure: function(form, action) {
        }
    });
}


/**
 * 初始化【客戶名称】下拉列表
 */
function pOILInitCustomerNameDDL() {
    $.ajax({
        url : Feng.ctxPath + '/orderInfo/selectByPartnerId',
        dataType : 'json',
        success : function(data, status) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data.message ? data.message : "查询数据失败");
        		return;
        	}
            isWYAdmin = data.isWYAdmin;
            isMarketing = data.isMarketing;

            // 初始化select2组件
            initSelect2();
        }
    });
}

/**
 * 初始化select2组件
 * @param data
 */
function initSelect2() {
    $("#pOIL-customer-select").removeClass().css({"height":"32px", "width":"190px"}).select2({
        minimumInputLength: 1,
        ajax: {
            url: Feng.ctxPath + '/orderInfo/selectCustomerInfoByName',
            dataType: 'json',
            data: function (term) {
                return {
                    "customerName": term
                };
            },
            results: function (data) {
                return {
                    results: $.map(data.customerList, function (item) {
                        return {
                            id: item.customerId,
                            text: item.customerName
                        }
                    })
                };
            }
        }
    });
}




/**
 * 检查是否选中
 */
OrderInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        OrderInfo.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加客户产品配置管理
 */
OrderInfo.openAddOrderInfo = function () {
	//// 直接添加 流量包  orderType = 1
    var index = layer.open({
        type: 2,
        title: '添加客户配置产品',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/orderInfo/add?orderType=1'
    });
    this.layerIndex = index;
    layer.full(index);// 全屏
};

/**
 * 打开查看客户产品配置管理详情 - 编辑
 */
OrderInfo.openOrderInfoDetail = function(id, orderType) {
    var index = layer.open({
        type: 2,
        title: '客户产品配置编辑',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/orderInfo/add?orderId=' + id + '&orderType=' + orderType
    });
    this.layerIndex = index;
    layer.full(index);// 全屏
};

/**
 * 查看
 */
OrderInfo.openOrderInfoView = function(id) {
	  var index = layer.open({
	      type: 2,
	      title: '客户产品配置详情',
	      area: ['800px', '420px'], //宽高
	      fix: false, //不固定
	      maxmin: true,
	      content: Feng.ctxPath + '/orderInfo/view?orderId=' + id
	  });
	  this.layerIndex = index;
	  layer.full(index);// 全屏
}

/**
 * 删除客户产品配置管理
 */
OrderInfo.delete = function () {
    if (!this.check()) {
        return;
    }
    layer.confirm('是否要删除？', function(index){
		layer.close(index);
		var ajax = new $ax(Feng.ctxPath + '/orderInfo/delete?orderId=' + OrderInfo.seItem.orderIdStr, function (data) {
	        if ( data && data.code && data.code != 200 ) {
	        	Feng.error(data.message ? data.message : "删除失败！");
	        	return;
	        }
			Feng.success("删除成功!");
	        OrderInfo.table.refresh();
	    }, function (data) {
	        Feng.error("删除失败!");
	    });
	    ajax.start();
	});
};

/**
 * 查询客户产品配置管理列表
 */
OrderInfo.search = function () {
    OrderInfo.table.refresh({query: $("#queryForm").serializeJson()});
};

/**
 * 文本框输入限制
 */
function inputNumberFormatHandler() {
    $(document.getElementById("params['orderId']")).keypress(function(event) {
        var keyCode = event.which;
        if (keyCode == 8 || keyCode == 0 || keyCode >= 48 && keyCode <= 57)
            return true;
        else
            return false;
    });
}

$(function () {
    var defaultColunms = OrderInfo.initColumn();
    var table = new BSTable(OrderInfo.id, "/orderInfo/query", defaultColunms);
    
    var exportOptions = {};
    exportOptions.ignoreColumn="[0]";
    exportOptions.fileName="客户信息管理列表";
    exportOptions.tableName="客户信息管理列表";
    exportOptions.worksheetName="客户信息管理列表";
    table.setExport(false,"['excel']","all",exportOptions);
    
    OrderInfo.table = table.init();
    
    pOILInitCustomerNameDDL();
    
    inputNumberFormatHandler();
});
