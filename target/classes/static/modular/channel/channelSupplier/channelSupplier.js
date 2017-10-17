/**
 * 通道供应商管理初始化
 */
var ChannelSupplier = {
    id: "ChannelSupplierTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
ChannelSupplier.initColumn = function () {
    return [
        {field: 'selectItem', checkBox: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
        {title: '供应商编码', field: 'supplierCode',  align: 'center', valign: 'middle',formatter:ChannelSupplier.formatName},
        {title: '供应商名称', field: 'supplierName',  align: 'center',valign: 'middle'},
        {title: '状态', field: 'isValidDesc',  align: 'center',sortable : true,sortName : 'is_valid', valign: 'middle'},
       // {title: '供应商姓名', field: 'linkName',  align: 'center', valign: 'middle'},
        {title: '流量下发适配器', field: 'adapterName',  align: 'center', valign: 'middle'},
        //{title: '供应商电话', field: 'mobile',  align: 'center', valign: 'middle'},
        //{title: '供应商邮箱', field: 'email',  align: 'center', valign: 'middle'},
        //{title: '供应商地址', field: 'address',  align: 'center', valign: 'middle'},
        {title: '账户余额', field: 'balance',  align: 'center', valign: 'middle',sortable : true,sortName : 'balance'},
        {title: '累计消耗', field: 'consumeAmount', sortable : true,sortName : 'consume_amount', align: 'center', valign: 'middle'},
        {title: '累计充值', field: 'rechargeAmount', sortable : true,sortName : 'recharge_amount', align: 'center', valign: 'middle'},
        {title: '合同编号', field: 'contractNo', align: 'center', valign: 'middle'},
        {title: '创建者', field: 'creator',  align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime',sortable : true,sortName : 'create_time',  align: 'center', valign: 'middle'}
      //  {name : 'myac',	index : '',	width : 120,fixed : true,sortable : false,resize : false,formatter : ChannelSupplier.actionButtons} 
    ];
};

ChannelSupplier.formatName=function(cellvalue, rowObject,index ) {
	return '<a href="javascript:;" class="supplier"  onclick="ChannelSupplier.detailEvent(\''
			+ rowObject.supplierCode + '\')">' + cellvalue
			+ '</a>';
}

ChannelSupplier.actionButtons = function(cellvalue,rowObject,index ) {
	var channelStatusTemp = rowObject['isValid'];
    var btnTitle = "";
    var changeStatus = 2;
    if (channelStatusTemp == 2) {
        btnTitle = "有效";
        changeStatus = 1;
    } else if (channelStatusTemp == 1) {
        btnTitle = "无效";
        changeStatus = 2;
    }
	return '<div >'
			+ '<button onclick=\"ChannelSupplier.editEvent(\''
			+ rowObject['supplierCode']
			+ '\')" class=\"btn btn-xs btn-info\" permCheck=\"auth_channel_supplier_list,add,hidden\" data-rel=\"tooltip\" title=\"编辑\" >'
			+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
			+ '</button>'
			+ '<button onclick=ChannelSupplier.changeStatusEvent(' + changeStatus + ',\"' + rowObject['supplierCode'] + '\") class=\"btn btn-xs btn-warning\" permCheck=\"auth_channel_supplier_list,changestatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" >' 
                + '<i class=\"ace-icon fa fa-flag bigger-120\"></i>' 
            + '</button>'
			+ '<button onclick=\"ChannelSupplier.deleteEvent(\''
			+ rowObject['supplierCode']
			+ '\')" class=\"btn btn-xs btn-danger\" permCheck=\"auth_channel_supplier_list,delete,hidden\" data-rel=\"tooltip\" title=\"删除\" >'
			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
			+ '</button>'
			+'<button onclick=\"ChannelSupplier.chargeEvent(\''+ rowObject['supplierCode'] + '\')\" class=\"btn btn-xs btn-warning\" permCheck="auth_channel_supplier_list,rechargeDetail,hidden" data-rel=\"tooltip\" title=\"充值明细\" >'
			+ '<i class=\"ace-icon fa fa-money bigger-120\"></i>'
			+ '</button>' + '</div>';
	}



ChannelSupplier.changeStatusEvent=function(status,supplierCode){
	var ajax = new $ax(Feng.ctxPath + "/channelSupplier/changeStatus", function (data) {
    	if(data.code==200){
   		 Feng.success(data.message);
   		 ChannelSupplier.table.refresh();
       	}
       	if(data.code==0){
      		 Feng.error(data.message);
       	}
    }, function (data) {
        Feng.error("删除失败!");
    });
    ajax.set("channelSupplierId",supplierCode).set("status",status);
    ajax.start();
}


ChannelSupplier.deleteEvent=function(supplierCode){
	layer.confirm('是否要删除？', function(index){
		layer.close(index);
		var ajax = new $ax(Feng.ctxPath + "/channelSupplier/delete", function (data) {
	    	if(data.code==200){
	   		 Feng.success(data.message);
	   		 ChannelSupplier.table.refresh();
	       	}
	       	if(data.code==0){
	      		 Feng.error(data.message);
	       	}
	    }, function (data) {
	        Feng.error("删除失败!");
	    });
	    ajax.set("channelSupplierId",supplierCode);
	    ajax.start();
	});
}


ChannelSupplier.chargeEvent=function() {
	 if (this.check()) {
		var index = layer.open({
	        type: 2,
	        title: '通道供应商充值详情',
	        area: ['800px', '480px'], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: Feng.ctxPath + '/channelSupplierRecharge?supplierCode=' + ChannelSupplier.seItem.supplierCode
	    });
	 }
	 ChannelSupplier.layerIndex=index;
};


//批量生效
ChannelSupplier.useAll=function(){
	var selected = $('#' + this.id).bootstrapTable('getSelections');
	 if(selected.length == 0){
		 Feng.error("请选择要操作的数据");
		return false;
	  }
	var JosnList=[];
	//遍历访问这个集合  
	selected.forEach(function(e){ 
		var item={"status":1,"supplierCode":e.supplierCode};
		JosnList.push(item);
	}) 
	$.ajax({
	  	type:"POST",
	    url:Feng.ctxPath +"/channelSupplier/batchUpdateStatus",
	 	dataType:"json",
	 	contentType:"application/x-www-form-urlencoded; charset=utf-8",
	 	data:{"params":JSON.stringify(JosnList)},
	 	success:function(data){
	 		if(data.code!=200){
	 			Feng.error("批量修改状态失败!");	
	 		}else{
	 			Feng.success("批量修改状态成功!");
	 			ChannelSupplier.table.refresh();
	 		}
	 	}
	});
}
//批量失效
ChannelSupplier.unUseAll=function(){
	var selected = $('#' + this.id).bootstrapTable('getSelections');
	 if(selected.length == 0){
		 Feng.error("请选择要操作的数据");
		return false;
	  }
	var JosnList=[];
	//遍历访问这个集合  
	selected.forEach(function(e){ 
		var item={"status":2,"supplierCode":e.supplierCode};
		JosnList.push(item);
	}) 
	$.ajax({
	  	type:"POST",
	    url:Feng.ctxPath +"/channelSupplier/batchUpdateStatus",
	 	dataType:"json",
	 	contentType:"application/x-www-form-urlencoded; charset=utf-8",
	 	data:{"params":JSON.stringify(JosnList)},
	 	success:function(data){
	 		if(data.code!=200){
	 			Feng.error("批量修改状态失败!");	
	 		}else{
	 			Feng.success("批量修改状态成功!");
	 			ChannelSupplier.table.refresh();
	 		}
	 	}
	});
}


/**
 * 检查是否选中
 */
ChannelSupplier.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        ChannelSupplier.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加通道供应商
 */
ChannelSupplier.openAddChannelSupplier = function () {
    var index = layer.open({
        type: 2,
        title: '添加通道供应商',
        area: ['900px', '620px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelSupplier/channelSupplier_add'
    });
    this.layerIndex = index;
};

ChannelSupplier.editEvent=function(supplierCode) {
    var index = layer.open({
        type: 2,
        title: '通道供应商详情',
        area: ['900px', '620px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelSupplier/channelSupplier_update/' +supplierCode
    });
    this.layerIndex = index;
};

ChannelSupplier.detailEvent=function (supplierCode) {
	var index = layer.open({
        type: 2,
        title: '通道供应商详情',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/channelSupplier/detail/' + supplierCode
    });
};

/**
 * 打开查看通道供应商详情
 */
ChannelSupplier.openChannelSupplierDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '编辑通道供应商详情',
            area: ['900px', '620px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/channelSupplier/channelSupplier_update/' + ChannelSupplier.seItem.supplierCode
        });
        this.layerIndex = index;
    }
};

/**
 * 删除通道供应商
 */
ChannelSupplier.delete = function () {
    if (this.check()) {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
	        var ajax = new $ax(Feng.ctxPath + "/channelSupplier/delete", function (data) {
	        	if(data.code==200){
	       		 Feng.success(data.message);
	       		 ChannelSupplier.table.refresh();
		       	}
		       	if(data.code==0){
		      		 Feng.error(data.message);
		       	}
	        }, function (data) {
	            Feng.error("删除失败!");
	        });
	        ajax.set("channelSupplierId",ChannelSupplier.seItem.supplierCode);
	        ajax.start();
    	});
    }
};

/**
 * 查询通道供应商列表
 */
ChannelSupplier.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['supplierName'] = $("#supplierName").val();
    queryData.params['isValid'] = $("#isValid").val();
    ChannelSupplier.table.refresh({query: queryData});
};

ChannelSupplier.reSet = function(){
	$("#supplierName").val("");
	$("#isValid").val("");
}


$(function () {
    var defaultColunms = ChannelSupplier.initColumn();
    var table = new BSTable(ChannelSupplier.id, "/channelSupplier/list", defaultColunms);
    //table.setPaginationType("client");
    ChannelSupplier.table = table.init();

    
});
