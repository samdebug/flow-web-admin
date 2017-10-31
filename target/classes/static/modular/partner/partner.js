/**
 * 合作伙伴管理初始化
 */
var Partner = {
    id: "PartnerTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Partner.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'partnerId', visible: false, align: 'center', valign: 'middle'},
        {title: '合作伙伴名称', field: 'partnerName', align: 'center', valign: 'middle', sortable: true, sortName: 'partner_name', formatter: formatName},
        //{title: '伙伴编号', field: 'partnerNo', align: 'center', valign: 'middle', sortable: true, sortName: 'partner_no'},
        {title: '登录帐号', field: 'loginName', align: 'center', valign: 'middle'},
        //{title: '伙伴姓名', field: 'realName', align: 'center', valign: 'middle'},
        //{title: '伙伴类型', field: 'partnerType', align: 'center', valign: 'middle', formatter: formatType},
        {title: '合作伙伴等级', field: 'partnerLevel', align: 'center', valign: 'middle', sortable: true, sortName: 'partner_level', formatter: formatLevel},
        {title: '状态', field: 'status', align: 'center', valign: 'middle', sortable: true, formatter: formatStatus},
        {title: '账户余额(元)', field: 'balance', align: 'center', valign: 'middle'},
        {title: '授信额度(元)', field: 'creditAmount', align: 'center', valign: 'middle'},
        //{title: '结算折扣(%)', field: 'settlementDiscount', align: 'center', valign: 'middle'},
        //{title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true, sortName: 'create_time'},
        //{title: '修改者', field: 'updator', align: 'center', valign: 'middle'},
        //{title: '修改时间', field: 'updateTime', align: 'center', valign: 'middle', sortable: true, sortName: 'update_time'},
//        {title: '操作', field: 'myac', align: 'center', valign: 'middle', formatter : actionButtons}
    ];
};

/**
 * 检查是否选中
 */
Partner.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Partner.seItem = selected[0];
        return true;
    }
};


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
            return "无效";
            break;
        case '1':
            return "有效";
            break;
        case '2':
            return "无效";
            break;
        default:
            return "";
            break;
    }
}


function actionButtons(cellvalue, rowObject, options) {
//	var changeStatus=0;
//	var titleMsg="";
//	if(rowObject['status']=="0"){
//		changeStatus=1;
//		titleMsg="商用";
//	}else if(rowObject['status']=="1"){
//		changeStatus=2;
//		titleMsg="暂停";
//	}else if(rowObject['status']=="2"){
//		changeStatus=1;
//		titleMsg="商用";
//	}
//	
//	var statusAction='<button permCheck="auth_partner_manager_list,changestatus" onclick=\"changeStatus('+changeStatus+','+ rowObject['partnerId']
//	+ ')\" class=\"btn btn-xs btn-warning\" data-rel=\"tooltip\" title=\"'+titleMsg+'\" >'
//	+ '<i class=\"ace-icon fa fa-flag bigger-120\"></i>'
//	+ '</button>';
//	return '<div >'
//			+ '<button permCheck="auth_partner_manager_list,modifyLevel" onclick=\"modifyLevelEvent('
//			+ rowObject['partnerId']
//			+ ','
//			+ rowObject['partnerLevel']
//			+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"修改等级\" >'
//			+ '<i class=\"ace-icon fa fa-bolt bigger-120\"></i>'
//			+ '</button>'
//			+statusAction 
//			+ '</div>';
}

/**
 * 点击名称查看详情
 * @param cellvalue
 * @param options
 * @param rowObject
 * @returns
 */
function formatName(cellvalue, rowObject, options){
	return "<a href='javascript:;' onclick='Partner.openViewPartner("+rowObject.partnerId+")' >" + cellvalue + "</a>";
}

function formatLevel(cellvalue, rowObject, options) {
	return cellvalue == 3 ? "重要客户" : "普通客户";
}

/**
 * 打开详情
 */
Partner.openViewPartner = function(_partnerId){
	
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
//    layer.full(index);// 全屏
}


/**
 * 点击添加合作伙伴
 */
Partner.openAddPartner = function () {
    var index = layer.open({
        type: 2,
        title: '添加合作伙伴',
        area: ['860px', '620px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/partner/partner_add'
    });
    this.layerIndex = index;
    //layer.full(index);// 全屏
};

/**
 * 打开查看合作伙伴详情
 */
Partner.openPartnerDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '合作伙伴编辑',
            area: ['860px', '620px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/partner/partner_update/' + Partner.seItem.partnerId
        });
        this.layerIndex = index;
//        layer.full(index);// 全屏
    }
};

/**
 * 删除合作伙伴
 */
Partner.delete = function () {
    if (this.check()) {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
    		var ajax = new $ax(Feng.ctxPath + "/partner/delete", function (data) {
            	if ( data.code == 200 ) {
            		Feng.success("删除成功!");
                    Partner.table.refresh();
            	}else {
            		Feng.error("删除失败!" + data.message);
            	}
            }, function (data) {
                Feng.error("删除失败!");
            });
            ajax.set("partnerId", Partner.seItem.partnerId);
            ajax.start();
		});
    }
};

/**
 * 查询合作伙伴列表
 */
Partner.search = function () {
    var queryData = getQueryData();
//    queryData['condition'] = $("#condition").val();
    Partner.table.refresh({query: queryData});
};

/**
 * 充值、充值记录
 */
Partner.viewTreade = function() {
	if (!this.check()) {
		return;
	}
	var id = Partner.seItem.partnerId;
	var index = layer.open({
		type: 2,
		title: '充值',
		area: ['800px', '500px'], //宽高
		fix: false, //不固定
		//maxmin: true,
		content: Feng.ctxPath + '/partnerBill/partnerAccountDetail?partnerId='+id
	});
	this.layerIndex = index;
	//layer.full(index);// 全屏
}

/**
 * 重置密码
 */
Partner.resetPassword = function() {
	if (!this.check()) {
		return;
	}
	layer.confirm('确认重置密码？', function(index){
		layer.close(index);
		var ajax = new $ax(Feng.ctxPath + "/partner/resetPassword.ajax", function (data) {
	    	if ( data.code == 200 ) {
	    		Feng.success("重置成功!");
	            Partner.table.refresh();
	    	}else {
	    		Feng.error("重置失败!" + data.message);
	    	}
	    }, function (data) {
	        Feng.error("重置失败!");
	    });
	    ajax.set("partnerId", Partner.seItem.partnerId);
	    ajax.start();
	});
}


/////////////////////////////////////////////////////


function modifyLevelEvent(){
	if (!Partner.check()) {
		return;
	}
	var id = Partner.seItem.partnerId;
	var customerLevel = Partner.seItem.partnerLevel;
	
	$("#partnerLevel").val(customerLevel);
	$("#partner-level-dialog").modal({
        backdrop: "static",
        keyboard: false
    }).on("shown.bs.modal", function(e) {
    	 $("#partnerId").val(id);
    });
}


function partnerLevelSubmit(){
	var _data = {
			'partnerId' : $("#partnerId").val(),
			'partnerLevel' : $("#partnerLevel").val()
		};
	var ajax = new $ax(Feng.ctxPath + '/partner/modifyPartnerLevel.ajax', function (data) {
		if (data.code == 200) {
			$("#partner-level-dialog").modal("hide");
			Partner.search();
		} else {
			Feng.error(data.message);
		}
    }, function (data) {
        Feng.error("操作失败!");
    });
    ajax.set(_data);
    ajax.start();
}


function changeStatus() {
	if (!Partner.check()) {
		return;
	}
	var rowObject = Partner.seItem;
	var changeStatus=0;
	var titleMsg="";
	if(rowObject['status']=="0"){
		changeStatus=1;
		titleMsg="商用";
	}else if(rowObject['status']=="1"){
		changeStatus=2;
		titleMsg="暂停";
	}else if(rowObject['status']=="2"){
		changeStatus=1;
		titleMsg="商用";
	}
	
    $.ajax({
        url: Feng.ctxPath + '/partner/changeStatus.ajax?status=' + changeStatus + '&partnerId=' + Partner.seItem.partnerId,
        dataType: "json",
        success: function(data) {
            if (data.code == 200) {
            	Partner.search();
            } else {
            	Feng.error(data.message);
            }
        }
    });
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
    var defaultColunms = Partner.initColumn();
    var table = new BSTable(Partner.id, "/partner/list", defaultColunms);
//    table.setPaginationType("client");
    Partner.table = table.init();
});




