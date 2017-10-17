/**

 * 合作伙伴账单管理管理初始化
 */
var CustomerSettlementOrder = {
    id: "CustomerSettlementOrderTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
CustomerSettlementOrder.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '客户名称', field: 'customerInfo.customerName', align: 'center', valign: 'middle'},
        {title: '结算月份', field: 'month', align: 'center', valign: 'middle', formatter: formatMonth, sortable: true, sortName :'month'},
        {title: '账单金额', field: 'payMoney', align: 'center', valign: 'middle', sortable: true, sortName :'pay_money'},
        {title: '状态', field: 'balanceStatus', align: 'center', valign: 'middle', formatter : formatBalanceStatus, sortable: true, sortName :'balance_status'},
        {title: '调整金额', field: 'adjustMoney', align: 'center', valign: 'middle', formatter: formatAdjustMoney, sortable: true, sortName :'adjust_money'},
        {title: '应结算金额', field: 'balanceMoney', align: 'center', valign: 'middle', sortable: true, sortName :'balance_money'},
        {title: '操作', field: 'myac', align: 'center', valign: 'middle', formatter: formatActionButtons}
    ];
};

function formatMonth(cellvalue, rowObject, options) {
    return cellvalue.substr(0,4) + "-" + cellvalue.substr(4,2);
}

function formatBalanceStatus(cellvalue, rowObject, options) {
    if (cellvalue == 0) {
        return "待确认";
    } else if (cellvalue == 1) {
        return "已确认";
    } else {
        return "";
    }
}

function formatAdjustMoney(cellvalue, rowObject, options) {
    if (cellvalue > 0) {
        return "+" + cellvalue;
    } else {
        return cellvalue;
    }
}


function formatActionButtons(cellvalue, rowObject, options) {
    var btnHtml = "<div>";
    var balanceStatusTemp = rowObject['balanceStatus'];
    var year = rowObject['month'].substr(0, 4);
    var month = rowObject['month'].substr(4, 2);
    var day = new Date().getDate();
    var isLegalDate = new Date().getFullYear() == year && new Date().getMonth() == month && (day>=3 && day<= 11);
    // 只有上月的按钮可用
    if (isLegalDate && balanceStatusTemp == 0) {
        var btnTitle = "";
        var changeStatus = 0;
        if (balanceStatusTemp == 0) {
            btnTitle = "确认结算";
            changeStatus = 1;
            btnHtml += '<button onclick=\"cSOLBalanceStatusBtnClickHandler('+ changeStatus + ',' + rowObject['balanceMonthId'] + ')\" class=\"btn btn-xs btn-warning\" permCheck=\"auth_customer_settlement_order_manager,changeStatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" ><i class=\"ace-icon fa fa-flag bigger-120\"></i></button>';
        } else if (balanceStatusTemp == 1) {
            btnTitle = "已确认";
            btnHtml += '<button class=\"btn btn-xs btn-warning disabled\" permCheck=\"auth_customer_settlement_order_manager,changeStatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" ><i class=\"ace-icon fa fa-flag bigger-120\"></i></button>';
        }
        btnHtml += '<button onclick=\"cSOLChangeBalanceBtnClickHandler(' + rowObject['balanceMonthId'] + ')\" class=\"btn btn-xs btn-success\" permCheck=\"auth_customer_settlement_order_manager,changeAdjustBalance,hidden\" data-rel=\"tooltip\" title=\"调整\" ><i class=\"ace-icon fa fa-pencil bigger-120\"></i></button>';
    }
    btnHtml += '<button onclick=\"cSOLDownloadBtnClickHandler(' + rowObject['balanceMonthId'] + ')\" class=\"btn btn-xs btn-info\" permCheck=\"auth_customer_settlement_order_manager,downLoadSettlementOrder,hidden\" data-rel=\"tooltip\" title=\"下载结算单\" ><i class=\"ace-icon fa fa-download bigger-120\"></i></button>';
    btnHtml += "</div>";
    return btnHtml;
}


//调整结算单状态
function cSOLBalanceStatusBtnClickHandler(status, balanceMonthId) {
	layer.confirm('是否要确认结算？(确认后金额不可再调整)', function(index){
		layer.close(index);
		$.ajax({
            url: Feng.ctxPath + '/customerBalanceMonth/changeStatus?status=' + status + '&balanceMonthId=' + balanceMonthId,
            dataType: "json",
            success: function(data) {
                if (data && data.code == 200) {
                	CustomerSettlementOrder.search();
                } else {
                	Feng.error(data.message);
                }
            }
        });
	});
}


//调整金额
function cSOLChangeBalanceBtnClickHandler(balanceMonthId) {
    // 弹出模态框
    $("#customer-settlement-dialog").modal({
        backdrop: "static",
        keyboard: false
    }).on("shown.bs.modal", function(e) {
        $("#csol-adjust-money").val("");
        $("#csol-remark").val("");
        $("#customer-settlement-btn").click(function() {
            cSOLChangeBtnClickHandler(balanceMonthId);
        });
    });
}


function cSOLChangeBtnClickHandler(balanceMonthId) {
	var changeBalance = $("#csol-adjust-money").val();
    var changeReason = $("#csol-remark").val();
    // 非空验证
    if (changeBalance == "") {
        showErrorMsg("#csol-error", "#errorMsg", "请输入调整金额");
        return;
    }
    // 整数或小数
    var numReg = /^(-|\+)?[0-9]+(\.[0-9]*)?$/;
    if (!numReg.test(changeBalance)) {
        showErrorMsg("#csol-error", "#errorMsg", "只能输入整数或小数，请重新输入。");
        return;
    }
    // 8位整数2位小数
    var doubleReg = /^(-|\+)?\d{1,8}\.?\d{0,2}$/;
    if (!doubleReg.test(changeBalance)) {
        showErrorMsg("#csol-error", "#errorMsg", "最多只能有八位整数和两位小数，请重新输入。");
        return;
    }
    // 两位小数验证
    if (changeBalance.indexOf(".") != -1) {
        var reg = /^(-|\+)?\d+\.+\d{1,2}$/;
        if (!reg.test(changeBalance)) {
            showErrorMsg("#csol-error", "#errorMsg", "最多只能有两位小数，请重新输入。");
            return;
        }
    }
    // 非空验证
    if (changeReason == "") {
        showErrorMsg("#csol-error", "#errorMsg", "请输入调整原因");
        return;
    }
    layer.confirm("是否要确认调整？(调整后自动确认该月结算单)", function(index) {
    	layer.close(index);
    	var ajax = new $ax(Feng.ctxPath + "/customerBalanceMonth/changeAdjustBalance", function (data) {
        	if ( data.code == 200 ) {
        		Feng.success("操作成功!");
        		$("#customer-settlement-dialog").modal("hide");
        		CustomerSettlementOrder.table.search();
        	}else {
        		Feng.error("操作失败!" + data.message);
        	}
        }, function (data) {
            Feng.error("操作失败!");
        });
        ajax.set({'balanceMonthId': balanceMonthId, 'changeBalance': changeBalance, 'changeReason': changeReason});
        ajax.start();
    });
}


//错误消息
function showErrorMsg(outObj, inObj, errorMsg) {
    $(outObj).removeClass('hide');
    $(outObj).fadeIn(1000);
    $(inObj).html(errorMsg);
    setTimeout(function() {
        $(outObj).fadeOut(2000);
    }, 1500);
}


//下载当月结算单
function cSOLDownloadBtnClickHandler(balanceMonthId) {
//	CustomerSettlementOrder.exportCustomerSettlementOrder();
	$('#queryForm').exportData({
        url: Feng.ctxPath + '/customerBalanceMonth/downLoadSettlementOrder',
        data: {
            balanceMonthId: balanceMonthId
        },
        callback: function(data) {},
        failure: function(form, action) {}
    });
}

CustomerSettlementOrder.exportCustomerSettlementOrder = function() {
	Feng.info("暂不支持下载查看...");
}

/**
 * 收集表单数据
 */
CustomerSettlementOrder.collectQueryData = function() {
	var array = $("#queryForm").serializeArray();
	CustomerSettlementOrder.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerSettlementOrder.queryData[this.name] = this.value;
	    });
	}
}

/**
 * 查询合作伙伴账单管理列表
 */
CustomerSettlementOrder.search = function () {
	
	this.collectQueryData();
	
    CustomerSettlementOrder.table.refresh({query: CustomerSettlementOrder.queryData});
};

/**
 * 重置表单
 * @returns
 */
CustomerSettlementOrder.reset = function() {
	CustomerSettlementOrder.queryData = {};
	$("#csol-customer-select").select2("val", "");
	document.getElementById("queryForm").reset();
}


/**
 * 初始化select2组件
 * @param data
 */
function initSelect2() {
    $("#csol-customer-select").removeClass().css({"height":"32px", "width":"200px"}).select2({
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


$(function () {
    var defaultColunms = CustomerSettlementOrder.initColumn();
    var table = new BSTable(CustomerSettlementOrder.id, "/customerBalanceMonth/query", defaultColunms);
//    table.setPaginationType("client");
    CustomerSettlementOrder.table = table.init();
    
    initSelect2();
    
    
});
