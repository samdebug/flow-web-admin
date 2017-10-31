/**
 * 客户管理管理初始化
 */
var Customer = {
    id: "CustomerTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Customer.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'customerId', visible: false, align: 'center', valign: 'middle'},
        //{title: '客户简称', field: 'shorterName', align: 'center', valign: 'middle', formatter: formatShortName, sortable:true, sortName:'shorter_name'},
        {title: '客户名称', field: 'customerName', align: 'center', valign: 'middle', sortable:true, sortName:'customer_name', formatter: formatShortName},
        {title: '登录账号', field: 'account', align: 'center', valign: 'middle'},
        //{title: '客户类型', field: 'adviserTypeDesc', align: 'center', valign: 'middle'},
        {title: '客户等级', field: 'customerLevel', align: 'center', valign: 'middle', sortable:true, sortName:'customer_level', formatter: formatLevel},
        {title: '状态', field: 'status', align: 'center', valign: 'middle', formatter: formatStatus},
        {title: '合作伙伴', field: 'partnerName', align: 'center', valign: 'middle'},
        //{title: '累计存款', field: 'rechargeAmount', align: 'center', valign: 'middle', sortable:true, sortName:'recharge_amount'},
        {title: '账号余额(元)', field: 'balance', align: 'center', valign: 'middle', sortable:true, sortName:'balance'},
        //{title: '未确认金额', field: 'currentAmount', align: 'center', valign: 'middle', sortable:true, sortName:'current_amount'},
        {title: '授信额度(元)', field: 'creditAmount', align: 'center', valign: 'middle', sortable:true, sortName:'credit_amount'},
        //{title: '可用额度', field: 'availableCredit', align: 'center', valign: 'middle', sortable:true, sortName:'available_credit'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable:true, sortName:'create_time'}
        //{title: '单笔亏损(%)', field: 'orderRiskSetting', align: 'center', valign: 'middle', sortable:true, sortName:'order_risk_setting'},
//        {title: '操作', field: 'myac', align: 'center', valign: 'middle', formatter: actionButtons}
    ];
};


function formatLevel(cellValue) {
	if (cellValue == 3) {
		return "重要客户";
	}
	return "普通客户"
}

/**
 * 格式化状态字段
 * @param cellValue
 * @returns
 */
function formatStatus(cellValue) {
	switch (cellValue) {
	case 0:
		return "保存";
		break;
	case 1:
		return "有效";
		break;
	case 2:
		return "失效";
		break;
	default:
		break;
	}
	return "";
}

/**
 * 点击简称查看详情
 * @param cellValue
 * @param rowObject
 * @param option
 * @returns
 */
function formatShortName(cellValue, rowObject, option) {
	return "<a href='javascript:;' onclick='Customer.openViewCustomer("+rowObject.customerId+",\""+ rowObject.customerName +"\")' >" + cellValue + "</a>";
}

function actionButtons(cellvalue, rowObject, options) {
	var changeStatus = 0;
	var titleMsg = "";
	if (rowObject['status'] == 0) {
		changeStatus = 1;
		titleMsg = "商用";
	} else if (rowObject['status'] == 1) {
		changeStatus = 2;
		titleMsg = "失效";
	} else if (rowObject['status'] == 2) {
		changeStatus = 1;
		titleMsg = "商用";
	}

	var statusAction = '<button permCheck="auth_customer_manager_zc,changestatus" onclick=\"changeStatus('
			+ changeStatus
			+ ','
			+ rowObject['customerId']
			+ ')\" class=\"btn btn-xs btn-warning\" data-rel=\"tooltip\" title=\"'
			+ titleMsg
			+ '\" >'
			+ '<i class=\"ace-icon fa fa-flag bigger-120\"></i>'
			+ '</button>';

	return '<div >'
			+ '<button permCheck="auth_customer_manager_zc,modifyLevel" onclick=\"modifyLevelEvent('
			+ rowObject['customerId']
			+ ','
			+ rowObject['customerLevel']
			+ ','
			+ rowObject['orderRiskSetting']
			+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"修改等级\" >'
			+ '<i class=\"ace-icon fa  fa-bolt bigger-120\"></i>'
			+ '</button>&nbsp;&nbsp;'
			+ statusAction
			+ '</div>';
}


/**
 * 检查是否选中
 */
Customer.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Customer.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加客户管理
 */
Customer.openAddCustomer = function () {
    var index = layer.open({
        type: 2,
        title: '添加客户',
        area: ['900px', '620px'], //宽高
        fix: false, //不固定
//        maxmin: true,
        content: Feng.ctxPath + '/customer/customer_add'
    });
    this.layerIndex = index;
//    layer.full(index);// 全屏
};

/**
 * 打开查看客户管理详情
 */
Customer.openCustomerDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '编辑客户',
            area: ['900px', '620px'], //宽高
            fix: false, //不固定
            //maxmin: true,
            content: Feng.ctxPath + '/customer/customer_update/' + Customer.seItem.customerId
        });
        this.layerIndex = index;
        //layer.full(index);// 全屏   
    }
};

var tempObj = {};
/**
 * view info
 */
Customer.openViewCustomer = function(_customerId, _customerName) {
	if (!_customerId || _customerId <= 0)
		return;
	
	var index = layer.open({
        type: 2,
        title: '客户详情',
        area: ['860px', '520px'], //宽高
        fix: false, //不固定
        //maxmin: true,
        content: Feng.ctxPath + '/customer/detail?id=' + _customerId
    });
	tempObj = {customerId: _customerId, customerName: _customerName};
    this.layerIndex = index;
    //layer.full(index);// 全屏
    
    if (!$("#lockBtn").length){
        var element = $(".layui-layer-setwin");
        element.prepend('<a onclick="Customer.changePassword()" title="重设密码" style="color: #2f2e3d;" id="lockBtn" href="javascript:;"><i style="font-size: 15px;" class="fa fa-icon fa-unlock-alt"></i></a>');
    }
}


/**
 * 重置密码
 */
Customer.changePassword = function() {
	
	layer.confirm('是否确认将【'+ tempObj.customerName +'】的登录密码重置为初始密码？', function(index){
		layer.close(index);
		var ajax = new $ax(Feng.ctxPath + "/customer/resetPw", function (data) {
			if ( !data || !data.code || data.code != 200 ) {
				Feng.error(data && data.message ? data.message : "操作失败");
				return;
			}
	        Feng.success("已重置为初始密码!");
	    }, function (data) {
	        Feng.error("操作出错!");
	    });
	    ajax.set("customerId", tempObj.customerId);
	    ajax.start();
	});
}

/**
 * 充值、充值记录
 */
Customer.viewTreade = function() {
	if (!this.check()) {
		return;
	}
	var id = Customer.seItem.customerId;
	var index = layer.open({
		type: 2,
		title: '充值',
		area: ['800px', '500px'], //宽高
		fix: false, //不固定
		//maxmin: true,
		content: Feng.ctxPath + '/customerCharge/customerChargeView?customerId='+id
	});
	this.layerIndex = index;
	//layer.full(index);// 全屏
}

/**
 * 删除客户管理
 */
Customer.delete = function () {
	if (!this.check()) {
		return;
	}
	layer.confirm('确认删除？', function(index){
		layer.close(index);
		var ajax = new $ax(Feng.ctxPath + "/customer/delete", function (data) {
			if ( !data || !data.code || data.code != 200 ) {
				Feng.error(data && data.message ? data.message : "删除失败！");
				return;
			}
	        Feng.success("删除成功!");
	        Customer.table.refresh();
	    }, function (data) {
	        Feng.error("删除失败!");
	    });
	    ajax.set("customerId", Customer.seItem.customerId);
	    ajax.start();
	});
    
};

/**
 * 查询客户管理列表
 */
Customer.search = function () {
    var queryData = getQueryData();
    Customer.table.refresh({query: queryData});
};

Customer.reset = function(){
	document.getElementById("queryForm").reset();
	$("#customer-partner-select").select2("val", "");
};

function getQueryData(){
	var array = $("#queryForm").serializeArray();
	var res = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			res[this.name] = this.value;
	    });
	}
	return res;
}



/////////////////////////


function changeOrderRiskSetting(){
	var customerLevel = $("#customerLevel").val();
	if(customerLevel >=4){
		$("#orderRiskSetting").val("");
		$("#orderRiskSetting_form").show();
	}else{
		$("#orderRiskSetting").val("");
		$("#orderRiskSetting_form").hide();
	}
}

function modifyLevelEvent(){
	if (!Customer.check()) {
		return;
	}
	var id = Customer.seItem.customerId;
	var customerLevel = Customer.seItem.customerLevel;
	var orderRiskSetting = Customer.seItem.orderRiskSetting;
	
	$("#customerLevel").val(customerLevel);
	changeOrderRiskSetting();
	if(customerLevel >= 4){
		$("#orderRiskSetting").val(orderRiskSetting);
	}
	$("#customer-level-dialog").modal({
        backdrop: "static",
        keyboard: false
    }).on("shown.bs.modal", function(e) {
    	$("#customerId").val(id);
    });
}


// 错误消息
function showErrorMsg(outObj, inObj, errorMsg) {
    $(outObj).removeClass('hide');
    $(outObj).fadeIn(1000);
    $(inObj).html(errorMsg);
    setTimeout(function() {
        $(outObj).fadeOut(2000);
    }, 1500);
}

function customerLevelSubmit(){
	var orderRiskSetting = $("#orderRiskSetting").val();
	var customerLevel = $("#customerLevel").val();
	
	if(customerLevel > 3){
		// 非空验证
        if (orderRiskSetting == "") {
            showErrorMsg("#orderRisk-error", "#errorMsg", "请输入单笔订单风控比例");
            return;
        }
        // 整数或小数
        var numReg = /^(-|\+)?[0-9]+(\.[0-9]*)?$/;
        if (!numReg.test(orderRiskSetting)) {
            showErrorMsg("#orderRisk-error", "#errorMsg", "只能输入整数或小数，请重新输入。");
            return;
        }
        // 7位整数2位小数
        var doubleReg = /^(-|\+)?\d{1,7}\.?\d{0,2}$/;
        if (!doubleReg.test(orderRiskSetting)) {
            showErrorMsg("#orderRisk-error", "#errorMsg", "最多只能有七位整数和两位小数，请重新输入。");
            return;
        }
        // 两位小数验证
        if (orderRiskSetting.indexOf(".") != -1) {
            var reg = /^(-|\+)?\d+\.+\d{1,2}$/;
            if (!reg.test(orderRiskSetting)) {
                showErrorMsg("#orderRisk-error", "#errorMsg", "最多只能有两位小数，请重新输入。");
                return;
            }
        }
	}
	
	var _temp = {
			'customerId' : $("#customerId").val(),
			'customerLevel' : $("#customerLevel").val(),
			'orderRiskSetting':orderRiskSetting
		};
	var ajax = new $ax(Feng.ctxPath + "/customer/modifyCustomerLevel", function (data) {
		if (data.code == 200) {
			$("#customer-level-dialog").modal("hide");
			Customer.search();
		} else {
			Feng.error(data.message);
		}
    }, function (data) {
        Feng.error("操作失败!");
    });
    ajax.set(_temp);
    ajax.start();
}



function changeStatus() {
	if (!Customer.check()) {
		return;
	}
	var rowObject = Customer.seItem;
	var changeStatus = 0;
	var titleMsg = "";
	if (rowObject['status'] == 0) {
		changeStatus = 1;
		titleMsg = "商用";
	} else if (rowObject['status'] == 1) {
		changeStatus = 2;
		titleMsg = "失效";
	} else if (rowObject['status'] == 2) {
		changeStatus = 1;
		titleMsg = "商用";
	}
	
	var ajax = new $ax(Feng.ctxPath + '/customer/changeStatus?status=' + changeStatus + '&customerId=' + Customer.seItem.customerId, 
		function (data) {
			if (data.code == 200) {
				Customer.search();
			} else {
				Feng.error(data.message);
			}
	    }, function (data) {
	        Feng.error("操作失败!");
	    }
	);
    ajax.start();
}





/**
 * 初始化select2组件
 * @param data
 */
function initSelect2() {
    $("#customer-partner-select").removeClass().css({"height":"32px", "width":"190px"}).select2({
        minimumInputLength: 1,
        ajax: {
            url: Feng.ctxPath + '/customerBalanceDay/selectPartnerInfoByName',
            dataType: 'json',
            data: function (term) {
                return {
                    "partnerName": term
                };
            },
            results: function (data) {
                return {
                    results: $.map(data.partnerList, function (item) {
                        return {
                            id: item.partnerId,
                            text: item.partnerName
                        }
                    })
                };
            }
        }
    });
}


/**
 * 导出表单数据
 */
Customer.exportExcel = function () {
	Customer.table.export2Excel();
}

$(function () {
    var defaultColunms = Customer.initColumn();
    var table = new BSTable(Customer.id, "/customer/list", defaultColunms);
//    table.setPaginationType("client");
    
    var exportOptions = {};
    exportOptions.ignoreColumn="[0]";
    exportOptions.fileName="客户信息管理列表";
    exportOptions.tableName="客户信息管理列表";
    exportOptions.worksheetName="客户信息管理列表";
    table.setExport(false,"['excel']","all",exportOptions);
    
    Customer.table = table.init();
    
    
    initSelect2();
    
    $("#customerLevel").on("change", function(){
    	changeOrderRiskSetting();
    });
    
});
