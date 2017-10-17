/**
 * 合作伙伴账单管理管理初始化
 */
var PartnerSettlementOrder = {
    id: "PartnerSettlementOrderTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
PartnerSettlementOrder.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '合作伙伴名称', field: 'partnerInfo.partnerName', align: 'center', valign: 'middle'},
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
            btnHtml += '<button onclick=\"pSOLBalanceStatusBtnClickHandler('+ changeStatus + ',' + rowObject['balanceMonthId'] + ')\" class=\"btn btn-xs btn-warning\" permCheck=\"auth_partner_settlement_order_manager,changeStatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" ><i class=\"ace-icon fa fa-flag bigger-120\"></i></button>';
        } else if (balanceStatusTemp == 1) {
            btnTitle = "已确认";
            btnHtml += '<button class=\"btn btn-xs btn-warning disabled\" permCheck=\"auth_partner_settlement_order_manager,changeStatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" ><i class=\"ace-icon fa fa-flag bigger-120\"></i></button>';
        }
        btnHtml += '<button onclick=\"pSOLChangeBalanceBtnClickHandler(' + rowObject['balanceMonthId'] + ')\" class=\"btn btn-xs btn-success\" permCheck=\"auth_partner_settlement_order_manager,changeAdjustBalance,hidden\" data-rel=\"tooltip\" title=\"调整\" ><i class=\"ace-icon fa fa-pencil bigger-120\"></i></button>';
    }
    btnHtml += '<button onclick=\"pSOLDownloadBtnClickHandler(' + rowObject['balanceMonthId'] + ')\" class=\"btn btn-xs btn-info\" permCheck=\"auth_partner_settlement_order_manager,downLoadSettlementOrder,hidden\" data-rel=\"tooltip\" title=\"下载结算单\" ><i class=\"ace-icon fa fa-download bigger-120\"></i></button>';
    btnHtml += "</div>";
    return btnHtml;
}


//调整结算单状态
function pSOLBalanceStatusBtnClickHandler(status, balanceMonthId) {
	layer.confirm('是否要确认结算？(确认后金额不可再调整)', function(index){
		layer.close(index);
		$.ajax({
            url: Feng.ctxPath + '/partnerBalanceMonth/changeStatus?status=' + status + '&balanceMonthId=' + balanceMonthId,
            dataType: "json",
            success: function(data) {
                if (data && data.code == 200) {
                	PartnerSettlementOrder.search();
                } else {
                	Feng.error(data.message);
                }
            }
        });
	});
}


//调整金额
function pSOLChangeBalanceBtnClickHandler(balanceMonthId) {
    // 弹出模态框
    $("#partner-settlement-dialog").modal({
        backdrop: "static",
        keyboard: false
    }).on("shown.bs.modal", function(e) {
        $("#psol-adjust-money").val("");
        $("#psol-remark").val("");
        $("#partner-settlement-btn").click(function() {
            pSOLChangeBtnClickHandler(balanceMonthId);
        });
    });
}


function pSOLChangeBtnClickHandler(balanceMonthId) {
    var changeBalance = $("#psol-adjust-money").val();
    var changeReason = $("#psol-remark").val();
    // 非空验证
    if (changeBalance == "") {
        showErrorMsg("#psol-error", "#errorMsg", "请输入调整金额");
        return;
    }
    // 整数或小数
    var numReg = /^(-|\+)?[0-9]+(\.[0-9]*)?$/;
    if (!numReg.test(changeBalance)) {
        showErrorMsg("#psol-error", "#errorMsg", "只能输入整数或小数，请重新输入。");
        return;
    }
    // 8位整数2位小数
    var doubleReg = /^(-|\+)?\d{1,8}\.?\d{0,2}$/;
    if (!doubleReg.test(changeBalance)) {
        showErrorMsg("#psol-error", "#errorMsg", "最多只能有八位整数和两位小数，请重新输入。");
        return;
    }
    // 两位小数验证
    if (changeBalance.indexOf(".") != -1) {
        var reg = /^(-|\+)?\d+\.+\d{1,2}$/;
        if (!reg.test(changeBalance)) {
            showErrorMsg("#psol-error", "#errorMsg", "最多只能有两位小数，请重新输入。");
            return;
        }
    }
    // 非空验证
    if (changeReason == "") {
        showErrorMsg("#psol-error", "#errorMsg", "请输入调整原因");
        return;
    }
    layer.confirm("是否要确认调整？(调整后自动确认该月结算单)", function(index) {
    	layer.close(index);
    	var ajax = new $ax(Feng.ctxPath + "/partnerBalanceMonth/changeAdjustBalance", function (data) {
        	if ( data.code == 200 ) {
        		Feng.success("操作成功!");
        		$("#partner-settlement-dialog").modal("hide");
        		PartnerSettlementOrder.table.search();
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
function pSOLDownloadBtnClickHandler(balanceMonthId) {
	$('#queryForm').exportData({
        url: Feng.ctxPath + '/partnerBalanceMonth/downLoadSettlementOrder',
        data: {
            balanceMonthId: balanceMonthId
        },
        callback: function(data) {},
        failure: function(form, action) {}
    });
}

PartnerSettlementOrder.exportPartnerSettlementOrder = function() {
	Feng.info("暂不支持下载查看...");
}

/**
 * 收集表单数据
 */
PartnerSettlementOrder.collectQueryData = function() {
	var array = $("#queryForm").serializeArray();
	PartnerSettlementOrder.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			PartnerSettlementOrder.queryData[this.name] = this.value;
	    });
	}
}

/**
 * 查询合作伙伴账单管理列表
 */
PartnerSettlementOrder.search = function () {
	
	this.collectQueryData();
	
    PartnerSettlementOrder.table.refresh({query: PartnerSettlementOrder.queryData});
};

/**
 * 重置表单
 * @returns
 */
PartnerSettlementOrder.reset = function() {
	PartnerSettlementOrder.queryData = {};
	$("#psol-partner-select").select2("val", "");
	document.getElementById("queryForm").reset();
}


/**
 * 初始化select2组件
 * @param data
 */
function initSelect2() {
    $("#psol-partner-select").removeClass().css({"height":"32px", "width":"200px"}).select2({
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


$(function () {
    var defaultColunms = PartnerSettlementOrder.initColumn();
    var table = new BSTable(PartnerSettlementOrder.id, "/partnerBalanceMonth/query", defaultColunms);
//    table.setPaginationType("client");
    PartnerSettlementOrder.table = table.init();
    
    initSelect2();
    
    
});
