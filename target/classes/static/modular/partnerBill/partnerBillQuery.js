/**
 * 合作伙伴账单管理管理初始化
 */
var PartnerBillQuery = {
    id: "PartnerBillQueryTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
PartnerBillQuery.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '合作伙伴名称', field: 'partnerInfo.partnerName', align: 'center', valign: 'middle'},
        {title: '产品名称', field: 'productName', align: 'center', valign: 'middle', sortable: true, sortName: 'product_name'},
        {title: '所属运营商', field: 'mobileOperator', align: 'center', valign: 'middle', formatter: formatMobileOperator},
        {title: '规格(M)', field: 'flowAmount', align: 'center', valign: 'middle', sortable: true, sortName: 'flow_amount'},
        {title: '账单日期', field: 'balanceDay', align: 'center', valign: 'middle', formatter: formatBalanceDay, sortable: true, sortName: 'balance_day'},
        {title: '合作伙伴结算价格(元)', field: 'partnerBalancePrice', align: 'center', valign: 'middle', sortable: true, sortName: 'partner_balance_price'},
        {title: '结算数量(个)', field: 'sendNum', align: 'center', valign: 'middle', sortable: true, sortName: 'send_num'},
        {title: '小计金额(元)', field: 'partnerAmount', align: 'center', valign: 'middle', sortable: true, sortName: 'partner_amount'}
    ];
};


function formatMobileOperator(cellvalue, rowObject, options) {
    switch (cellvalue) {
        case "YD":
            return "移动";
            break;
        case "LT":
            return "联通";
            break;
        case "DX":
            return "电信";
            break;
        case "YD/LT/DX":
            return "移动/联通/电信";
            break;
        default:
            return cellvalue;
            break;
    }
}

function formatBalanceDay(cellvalue, rowObject, options) {
    return cellvalue.substr(0, 4) + "-" + cellvalue.substr(4, 2) + "-" + cellvalue.substr(6, 2);
}


PartnerBillQuery.exportPartnerBillQuery = function() {
	$('#queryForm').exportData({
        url: Feng.ctxPath + '/customerBalanceDay/downLoadPartnerBill',
        callback: function(data) {},
        failure: function(form, action) {}
    });
}

/**
 * 收集表单数据
 */
PartnerBillQuery.collectQueryData = function() {
	var array = $("#queryForm").serializeArray();
	PartnerBillQuery.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			PartnerBillQuery.queryData[this.name] = this.value;
	    });
	}
}

/**
 * 查询合作伙伴账单管理列表
 */
PartnerBillQuery.search = function () {
	
	this.collectQueryData();
	
    PartnerBillQuery.table.refresh({query: PartnerBillQuery.queryData});
    pBQVInitCountAndPriceTotal();
};

/**
 * 重置表单
 * @returns
 */
PartnerBillQuery.reset = function() {
	PartnerBillQuery.queryData = {};
	$("#pbqv-partner-select").select2("val", "");
	document.getElementById("queryForm").reset();
}




/**
 * 初始化下发数和下发金额
 */
function pBQVInitCountAndPriceTotal() {
	
	var info = {};
	
	if (PartnerBillQuery.queryData["params['partnerId']"])
		info.partnerId = PartnerBillQuery.queryData["params['partnerId']"];
	
	if (PartnerBillQuery.queryData["params['inputStartTime']"])
		info.inputStartTime = PartnerBillQuery.queryData["params['inputStartTime']"];
	
	if (PartnerBillQuery.queryData["params['inputEndTime']"])
		info.inputEndTime = PartnerBillQuery.queryData["params['inputEndTime']"];
	
	var ajax = new $ax(Feng.ctxPath + "/customerBalanceDay/initPartnerCountAndPriceTotal", function(data){
    	if ( data && data.errMsg ) {
    		Feng.error(data.errMsg);
    	} else {
    		$("#issuedCountAll").text(data.count);
            $("#issuedAmountAll").text(data.priceTotal);
    	}
    },function(data){
        Feng.error("查询失败!" + data.responseJSON.message + "!");
    });
    ajax.set(info);
    ajax.start();
	
}


/**
 * 初始化select2组件
 * @param data
 */
function initSelect2() {
    $("#pbqv-partner-select").removeClass().css({"height":"32px", "width":"200px"}).select2({
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
    var defaultColunms = PartnerBillQuery.initColumn();
    var table = new BSTable(PartnerBillQuery.id, "/customerBalanceDay/queryPartner", defaultColunms);
//    table.setPaginationType("client");
    PartnerBillQuery.table = table.init();
    
    initSelect2();
    pBQVInitCountAndPriceTotal();
});
