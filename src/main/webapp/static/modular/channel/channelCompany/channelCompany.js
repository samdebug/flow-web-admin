/**
 * 上游供应商管理初始化
 */
var ChannelCompany = {
    id: "ChannelCompanyTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelCompany.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},   
        {title: 'id', field: 'companyId', visible: false, align: 'center', valign: 'middle'},
        {title: '上游供应商编码', field: 'companyCode', align: 'center', valign: 'middle'},
        {title: '上游供应商名称', field: 'companyName', align: 'center', valign: 'middle'},
        {title: '合同编号', field: 'contractNo', align: 'center', valign: 'middle'},
        {title: '状态', field: 'isValidDesc', align: 'center', valign: 'middle'},
        {title: '账户余额', field: 'balance', align: 'center', valign: 'middle',sortable : true,sortName : 'balance'},
        {title: '当天充值', field: 'money', align: 'center', valign: 'middle'},
        {title: '未确认金额', field: 'unCheckMoney', align: 'center', valign: 'middle'},
        {title: '可用余额', field: 'allowPrice', align: 'center', valign: 'middle'},
//        {title: '客户经理', field: 'manager', align: 'center', valign: 'middle'},
        {title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center',sortable : true,sortName : 'create_time', valign: 'middle'}
    ];
};


ChannelCompany.actionButtons=function(cellvalue, rowObject, index) {
	return '<div >'
			+ '<button onclick=\"ChannelCompany.openChannelCompanyDetailById(\''
			+rowObject['companyId']
			+ '\')" class=\"btn btn-xs btn-info\" permCheck=\"auth_channel_supplier_list,add,hidden\" data-rel=\"tooltip\" title=\"编辑\" >'
			+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
			+ '</button>'
			+ '<button onclick=\"ChannelCompany.deleteById(\''
			+rowObject['companyId']
			+ '\')" class=\"btn btn-xs btn-danger\" permCheck=\"auth_channel_supplier_list,delete,hidden\" data-rel=\"tooltip\" title=\"删除\" >'
			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
			+ '</button>'+'<button onclick=\"ChannelCompany.detailEvent(\''+ rowObject['companyId'] + '\','+'\''+ rowObject['companyName'] + '\')\" class=\"btn btn-xs btn-warning\"  data-rel=\"tooltip\" title=\"充值明细\" >'
			+ '<i class=\"ace-icon fa fa-money bigger-120\"></i>'
			+ '</button>'
			+ '</div>';
}


ChannelCompany.detailEvent=function() {
	if(!ChannelCompany.check()){
		return ;
	}
	 var index = layer.open({
	        type: 2,
	        title: '上游供应商充值详情',
	        area: ['800px', '480px'], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: Feng.ctxPath + '/channelCompanyRecharge?companyId=' +ChannelCompany.seItem.companyId +'&companyName='+ChannelCompany.seItem.companyName
	    });
	 ChannelCompany.layerIndex = index;
};



/**
 * 检查是否选中
 */
ChannelCompany.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelCompany.seItem = selected[0];
        return true;
    }
};




/**
 * 点击添加上游供应商
 */
ChannelCompany.openAddChannelCompany = function () {
    var index = layer.open({
        type: 2,
        title: '添加上游供应商',
        area: ['900px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelCompany/channelCompany_add'
    });
    this.layerIndex = index;
//    layer.full(index);
};

/**
 * 打开查看上游供应商详情
 */
ChannelCompany.openChannelCompanyDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '上游供应商详情',
            area: ['900px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelCompany/channelCompany_update/' + ChannelCompany.seItem.companyId
        });
        this.layerIndex = index;
    }
};

/**
 * 删除上游供应商
 */
ChannelCompany.delete = function () {
    if (this.check()) {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
	        var ajax = new $ax(Feng.ctxPath + "/channelCompany/delete", function (data) {
	            Feng.success("删除成功!");
	            ChannelCompany.table.refresh();
	        }, function (data) {
	            Feng.error("删除失败!" + data.responseJSON.message + "!");
	        });
	        ajax.set("companyId",ChannelCompany.seItem.companyId);
	        ajax.start();
    	});	
    }
};

/**
 * 打开查看上游供应商详情
 */
ChannelCompany.openChannelCompanyDetailById = function (id) {
    if (id!=undefined&&id!="") {
        var index = layer.open({
            type: 2,
            title: '上游供应商详情',
            area: ['900px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelCompany/channelCompany_update/' +id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除上游供应商
 */
ChannelCompany.deleteById = function (id) {
    if (id!=undefined&&id!="") {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
	        var ajax = new $ax(Feng.ctxPath + "/channelCompany/delete", function (data) {
	            Feng.success("删除成功!");
	            ChannelCompany.table.refresh();
	        }, function (data) {
	            Feng.error("删除失败!" + data.responseJSON.message + "!");
	        });
	        ajax.set("companyId",id);
	        ajax.start();
    	});
	}
    
};

/**
 *發送油件
 */
ChannelCompany.sendMail=function(){
	$.ajax({
	  	type:"POST",
	    url:Feng.ctxPath + '/channelCompany/sendSms',
	 	dataType:"json",
	 	contentType:"application/x-www-form-urlencoded; charset=utf-8",
	 	data:{},
	 	success:function(val){
	 		if(val.code==200){
	 			Feng.success(val.message);
	 		}else{
	 			Feng.error(val.message);
	 		}
	 	}
	});

}
/**
 * 查询上游供应商列表
 */
ChannelCompany.search = function () {
    var queryData = {
    	params:{}
    };
    queryData.params['companyName'] = $("#companyName").val();
    queryData.params['contactsName'] = $("#contactsName").val();
    queryData.params['manager'] = $("#manager").val();
    queryData.params['contractNo'] = $("#contractNo").val();
    queryData.params['isValid'] = $("#isValid").val();
    ChannelCompany.table.refresh({query: queryData});
};

ChannelCompany.reSet = function(){
     $("#companyName").val("");
     $("#contactsName").val("");
     $("#manager").val("");
     $("#contractNo").val("");
     $("#isValid").val("");
}

ChannelCompany.export2Excel=function(){
	location.href=encodeURI(Feng.ctxPath + "/channelCompany/export?params['companyName']=" + $("#companyName").val() 
			+ "&params['contactsName']=" + $("#contactsName").val()
			+ "&params['manager']=" + $("#manager").val()
			+ "&params['contractNo']=" + $("#contractNo").val()
			+ "&params['isValid']=" + $("#isValid").val());
}


$(function () {
    var defaultColunms = ChannelCompany.initColumn();
    var table = new BSTable(ChannelCompany.id, "/channelCompany/list", defaultColunms);
    //table.setPaginationType("client");
    ChannelCompany.table = table.init();
});
