/**
 * 客户充值管理管理初始化
 */
var CustomerCharge = {
    id: "CustomerChargeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    queryData:{}
};

/**
 * 初始化表格的列
 */
CustomerCharge.initColumn = function () {
    return [
//    	{field: 'selectItem', radio: true},
        {title: '客户ID', field: 'customerId', visible: false, align: 'center', valign: 'middle'},
        {title: '客户名称', field: 'customerName',  align: 'center', valign: 'middle'},
        {title: '状态', field: 'status',  align: 'center', valign: 'middle',formatter:formatStatus},
        {title: '账户余额', field: 'balance',  align: 'center', valign: 'middle', formatter: formatBalance},
        {title: '授信额度', field: 'creditAmount', align: 'center', valign: 'middle'},
//        {title: '未确认金额', field: 'currentAmount', align: 'center', valign: 'middle'},
        {title: '可用额度', field: 'availableCredit', align: 'center', valign: 'middle', formatter: formatAvailableCredit},
        {title: '联系人电话', field: 'linkmanMobile', align: 'center', valign: 'middle'},
        {title: '联系人邮箱', field: 'linkmanEmail', align: 'center', valign: 'middle'}
    ];
};

//列表formatter

function formatBalance(cellvalue, rowObject,index ) {
	return Number(rowObject.balance) - Number(rowObject.currentAmount);
}

function formatAvailableCredit(cellvalue, rowObject,index) {
	return Number(rowObject.balance) - Number(rowObject.currentAmount) + Number(rowObject.creditAmount);
}

function formatStatus( cellvalue,rowObject,index){
    switch (cellvalue) {
        case 0:
            return "保存";
            break;
        case 1:
            return "正常";
            break;
        case 2:
            return "暂停";
            break;
        default:
            return "";
            break;
    }
}


function detailEvent() {
	var datas = $('#' + CustomerCharge.id).bootstrapTable('getData');// 所有数据
	if ( !datas || !datas.length || datas.lenght <= 0 ) {
		Feng.info("暂无数据...");
		return;
	}
	
	var index = layer.open({
        type: 2,
        title: '交易明细',
        area: ['800px', '600px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/portal/charge_view?customerId=' + datas[0].customerId
    });
	CustomerCharge.layerIndex = index;
//	layer.full(index);
}

CustomerCharge.restForm=function() {
	document.getElementById("queryForm").reset();
	CustomerCharge.queryData = {};
}
/**
 * 查询流量订单列表
 */
CustomerCharge.search = function () {
    var queryData = this.getQueryData();
    CustomerCharge.table.refresh({query: queryData});
};

CustomerCharge.getQueryData=function(){
	var array = $("#queryForm").serializeArray();
	if (array && array.length > 0) {
		$.each(array, function() {
			CustomerCharge.queryData[this.name] = this.value;
	    });
	}
	return CustomerCharge.queryData;
}

/**
 * 检查是否选中
 */
CustomerCharge.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        CustomerCharge.seItem = selected[0];
        return true;
    }
};


/**
 * 弹出对话框 - 修改基础信息
 */
CustomerCharge.openInfoDialog = function() {
	var datas = $('#' + this.id).bootstrapTable('getData');// 所有数据
	var _data = null;
	if (datas && datas.length > 0 ){
		_data = datas[0];
	}
	if ( !_data ) {
		Feng.info("暂无数据...");
		return;
	}
	$("#customer-info-title").html("【"+ _data.customerName +"】");
    // 弹出模态框
    $("#customer-info-dialog").modal({
        backdrop: "static",
        keyboard: false
    }).on("shown.bs.modal", function(e) {
        $("#contactMobile").val(_data.linkmanMobile ? _data.linkmanMobile : "");
        $("#contactEmail").val(_data.linkmanEmail ? _data.linkmanEmail : "");
    });
}

/**
 * 保存数据
 */
CustomerCharge.saveDialogInfo = function() {
	var mobile = $("#contactMobile").val();
	var email = $("#contactEmail").val();
	
	if ( !/^1\d{10}$/.test(mobile) ){
		this.showDialogErrorMsg("#error-div", "#errorMsg", "请正确输入联系人号码");
		return;
	}
	if ( !/^(([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6}\,))*([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$$/.test(email) ){
		this.showDialogErrorMsg("#error-div", "#errorMsg", "邮箱格式不正确");
		return;
	}
	// to save 
	var ajax = new $ax(Feng.ctxPath + "/portal/saveCustomerInfo", function(data){
    	if ( data && data.code && data.code != 200 ) {
    		CustomerCharge.showDialogErrorMsg("#error-div", "#errorMsg", data.message ? data.message : "提交数据失败！");
    		return;
    	}
    	$("#customer-info-dialog").modal("hide");
    	Feng.success("保存成功");
    	//refresh
    	CustomerCharge.search();
    },function(data){
    	CustomerCharge.showDialogErrorMsg("#error-div", "#errorMsg", "提交数据失败!");
    });
    ajax.set( {"mobile": mobile, "email": email});
    ajax.start();
}

//错误消息
CustomerCharge.showDialogErrorMsg = function(outObj, inObj, errorMsg) {
    $(outObj).removeClass('hide');
    $(outObj).fadeIn(1000);
    $(inObj).html(errorMsg);
    setTimeout(function() {
        $(outObj).fadeOut(2000);
    }, 1500);
}


$(function () {
    var defaultColunms = CustomerCharge.initColumn();
    var table = new BSTable(CustomerCharge.id, "/customer/list", defaultColunms);
    //table.setPaginationType("client");
    CustomerCharge.table = table.init();
});
