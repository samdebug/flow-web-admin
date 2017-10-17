/**
 * 合作伙伴账单管理管理初始化
 */
var CustomerBillQuery = {
    id: "CustomerBillQueryTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData: {}
};

/**
 * 初始化表格的列
 */
CustomerBillQuery.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: '客户名称', field: 'customerInfo.customerName', align: 'center', valign: 'middle'},
        {title: '产品名称', field: 'productName', align: 'center', valign: 'middle', sortable: true, sortName: 'product_name'},
        {title: '所属运营商', field: 'mobileOperator', align: 'center', valign: 'middle', formatter: formatMobileOperator},
        {title: '规格(M)', field: 'flowAmount', align: 'center', valign: 'middle', sortable: true, sortName: 'flow_amount'},
        {title: '账单日期', field: 'balanceDay', align: 'center', valign: 'middle', formatter: formatBalanceDay, sortable: true, sortName: 'balance_day'},
        {title: '客户结算价格(元)', field: 'customerBalancePrice', align: 'center', valign: 'middle', sortable: true, sortName: 'customer_balance_price'},
        {title: '结算数量(个)', field: 'sendNum', align: 'center', valign: 'middle', sortable: true, sortName: 'send_num'},
        {title: '小计金额(元)', field: 'customerAmount', align: 'center', valign: 'middle', sortable: true, sortName: 'customer_amount'}
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


CustomerBillQuery.exportCustomerBillQuery = function() {
	$('#queryForm').exportData({
        url: Feng.ctxPath + '/customerBalanceDay/downLoadCustomerBill',
        callback: function(data) {},
        failure: function(form, action) {}
    });
}

/**
 * 收集表单数据
 */
CustomerBillQuery.collectQueryData = function() {
	var array = $("#queryForm").serializeArray();
	CustomerBillQuery.queryData = {};
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerBillQuery.queryData[this.name] = this.value;
	    });
	}
}

/**
 * 查询合作伙伴账单管理列表
 */
CustomerBillQuery.search = function () {
	
	this.collectQueryData();
	
    CustomerBillQuery.table.refresh({query: CustomerBillQuery.queryData});
    cBQVInitCountAndPriceTotal();
};

/**
 * 重置表单
 * @returns
 */
CustomerBillQuery.reset = function() {
	CustomerBillQuery.queryData = {};
	$("#cbqv-customer-select").select2("val", "");
	document.getElementById("queryForm").reset();
}




/**
 * 初始化下发数和下发金额
 */
function cBQVInitCountAndPriceTotal() {
	
	var info = {};
	
	if (CustomerBillQuery.queryData["params['customerId']"])
		info.customerId = CustomerBillQuery.queryData["params['customerId']"];
	
	if (CustomerBillQuery.queryData["params['inputStartTime']"])
		info.inputStartTime = CustomerBillQuery.queryData["params['inputStartTime']"];
	
	if (CustomerBillQuery.queryData["params['inputEndTime']"])
		info.inputEndTime = CustomerBillQuery.queryData["params['inputEndTime']"];
	
	var ajax = new $ax(Feng.ctxPath + "/customerBalanceDay/initCustomerCountAndPriceTotal", function(data){
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
    $("#cbqv-customer-select").removeClass().css({"height":"32px", "width":"200px"}).select2({
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
    var defaultColunms = CustomerBillQuery.initColumn();
    var table = new BSTable(CustomerBillQuery.id, "/customerBalanceDay/query", defaultColunms);
//    table.setPaginationType("client");
    CustomerBillQuery.table = table.init();
    
    initSelect2();
    cBQVInitCountAndPriceTotal();
});
