/**
 * 合作伙伴账单管理管理初始化
 */
var PartnerBill = {
    id: "PartnerBillTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
PartnerBill.initColumn = function () {
    return [
    	{field: 'selectItem', radio: true},
        {title: 'id', field: 'partnerId', visible: false, align: 'center', valign: 'middle'},
        {title: '合作伙伴名称', field: 'partnerName', align: 'center', valign: 'middle', formatter: formatName, sortable:true, sortName : 'partner_name'},
        {title: '合作伙伴类型', field: 'partnerType', align: 'center', valign: 'middle', formatter: formatType},
        {title: '合作伙伴状态', field: 'status', align: 'center', valign: 'middle', formatter : formatStatus},
        {title: '账户余额', field: 'balance', align: 'center', valign: 'middle' , sortable:true, sortName: 'balance'},
        {title: '授信额度', field: 'creditAmount', align: 'center', valign: 'middle', sortable:true, sortName: 'credit_amount'},
        {title: '当前费用', field: 'currentAmount', align: 'center', valign: 'middle', sortable:true, sortName: 'current_amount'},
        {title: '可用额度', field: 'availableCredit', align: 'center', valign: 'middle', sortable:true, sortName: 'available_credit'},
//        {title: '操作', field: 'myac', align: 'center', valign: 'middle', formatter: actionButtons}
    ];
};


function formatName(cellvalue, rowObject, options){
	return "<a href='javascript:;' onclick='PartnerBill.openPartnerView("+rowObject.partnerId+")' >" + cellvalue + "</a>";
}

function formatType(val) {
	switch (val) {
	case 1:
		return "流量营销";
		break;
	case 2:
		return "渠道直充";
		break;
	default:
		return "";
		break;
	}
}

function formatStatus(val) {
	switch (val) {
	case '0':
		return "待提交";
		break;
	case '1':
		return "商用";
		break;
	case '2':
		return "暂停";
		break;
	default:
		return "";
		break;
	}
}

function actionButtons(cellvalue, rowObject, options) {
	return '<div >'
			+ '<button permCheck="auth_partner_bill,accountDetail,hidden" onclick=\"PartnerBill.viewEvent('
			+ rowObject['partnerId']
			+ ')\" class=\"btn btn-xs btn-warning\" data-rel=\"tooltip\" title=\"账户明细\" >'
			+ '<i class=\"ace-icon fa fa-money bigger-120\"></i>'
			+ '</button></div>';
}

PartnerBill.viewEvent = function() {
	if (!this.check()) {
		return;
	}
	var id = PartnerBill.seItem.partnerId;
	var index = layer.open({
		type: 2,
		title: '账户明细',
		area: ['800px', '310px'], //宽高
		fix: false, //不固定
		maxmin: true,
		content: Feng.ctxPath + '/partnerBill/partnerAccountDetail?partnerId='+id
	});
	this.layerIndex = index;
	layer.full(index);// 全屏
};


/**
 * 检查是否选中
 */
PartnerBill.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        PartnerBill.seItem = selected[0];
        return true;
    }
};

/**
 * 在线充值输入框
 */
//PartnerBill.openOnlinePayBox = function () {
//    var index = layer.open({
//        type: 2,
//        title: '在线缴费充值',
//        area: ['800px', '310px'], //宽高
//        fix: false, //不固定
//        maxmin: true,
//        content: Feng.ctxPath + '/partnerBill/partnerBill_onlinePay'
//    });
//    this.layerIndex = index;
//};

/**
 * 打开查看合作伙伴账单管理详情
 */
//PartnerBill.openPartnerBillDetail = function () {
//    if (this.check()) {
//        var index = layer.open({
//            type: 2,
//            title: '合作伙伴账单管理详情',
//            area: ['800px', '420px'], //宽高
//            fix: false, //不固定
//            maxmin: true,
//            content: Feng.ctxPath + '/partnerBill/partnerBill_update/' + PartnerBill.seItem.id
//        });
//        this.layerIndex = index;
//    }
//};


/**
 * 打开详情
 */
PartnerBill.openPartnerView = function(_partnerId){
	
	if (!_partnerId || _partnerId <= 0)
		return;
	
	var index = layer.open({
        type: 2,
        title: '合作伙伴详情',
        area: ['860px', '620px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/partner/detail?id=' + _partnerId
    });
    this.layerIndex = index;
    layer.full(index);// 全屏
}


/**
 * 删除合作伙伴账单管理
 */
//PartnerBill.delete = function () {
//    if (this.check()) {
//        var ajax = new $ax(Feng.ctxPath + "/partnerBill/delete", function (data) {
//            Feng.success("删除成功!");
//            PartnerBill.table.refresh();
//        }, function (data) {
//            Feng.error("删除失败!");
//        });
//        ajax.set("partnerBillId",this.seItem.id);
//        ajax.start();
//    }
//};

/**
 * 查询合作伙伴账单管理列表
 */
PartnerBill.search = function () {
    var queryData = getQueryData();
    PartnerBill.table.refresh({query: queryData});
};

/**
 * 重置表单
 * @returns
 */
PartnerBill.reset = function() {
	document.getElementById("queryForm").reset();
}

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


$(function () {
    var defaultColunms = PartnerBill.initColumn();
    var table = new BSTable(PartnerBill.id, "/partner/list", defaultColunms);
//    table.setPaginationType("client");
    PartnerBill.table = table.init();
});
