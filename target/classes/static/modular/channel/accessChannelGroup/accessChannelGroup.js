/**
 * 接入通道组管理初始化
 */
var AccessChannelGroup = {
    id: "AccessChannelGroupTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
AccessChannelGroup.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
       // {title: '通道组ID', field: 'channelGroupId', visible: false, align: 'center', valign: 'middle'},
       // {title: '流量下发通道', field: 'dispatchChannel', align: 'center', valign: 'middle',formatter:AccessChannelGroup.formatName},
        {title: '通道组名称', field: 'groupName', align: 'center', valign: 'middle',formatter:AccessChannelGroup.formatName},
        {title: '创建者', field: 'creator', align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle',sortable : true,sortName : 'create_time'},
        //{title: '修改者', field: 'updator', visible: false, align: 'center', valign: 'middle'},
       // {title: '修改时间', field: 'updateTime', visible: false, align: 'center', valign: 'middle',sortable : true,sortName : 'update_time'},
        {title: '是否默认通道组', field: 'isDefDesc', align: 'center', valign: 'middle'},
       // {title: '质量指标名称', field: 'channelQualityQuota.quotaName', align: 'center', valign: 'middle'},
        {title: '是否可引用', field: 'isQuoteDesc', align: 'center', valign: 'middle'},
       // {title: '操作', field: '',formatter:AccessChannelGroup.actionButtons}
    ];
};


AccessChannelGroup.formatName=function(cellvalue, rowObject, index) {
    return '<a href="javascript:;" onclick="AccessChannelGroup.detail(\'' + rowObject.channelGroupId + '\')">' + cellvalue + '</a>';
}
AccessChannelGroup.actionButtons=function(cellvalue, rowObject, index) {
    var html =  '<div >' 
	         + '<button onclick=\"AccessChannelGroup.detail(' + rowObject.channelGroupId + ')\" class=\"btn btn-xs btn-success\" permCheck=\"auth_access_channel_group_list,detail,hidden\" data-rel=\"tooltip\" title=\"详情\" >' 
	         + '<i class=\"ace-icon fa fa-eye bigger-120\"></i>' 
	         + '</button>' 
             + '<button onclick=\"AccessChannelGroup.editEvent(' + rowObject.channelGroupId + ')\" class=\"btn btn-xs btn-info\" permCheck=\"auth_access_channel_group_list,add,hidden\" data-rel=\"tooltip\" title=\"编辑\" >' 
             +       '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>' 
             + '</button>' 
             + '<button onclick=\"AccessChannelGroup.deleteEvent(' + rowObject.channelGroupId + ')\" class=\"btn btn-xs btn-danger\" permCheck=\"auth_access_channel_group_list,delete,hidden\" data-rel=\"tooltip\" title=\"删除\" >' 
             +       '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>' 
             + '</button>';
            if(1 == rowObject.isQuote){
              html  += '<button onclick=\"AccessChannelGroup.groupAddToChannelGroup(' + rowObject.channelGroupId + ')\" class=\"btn btn-xs btn-danger\" permCheck=\"auth_access_channel_group_list,groupAddToGroup,hidden\" data-rel=\"tooltip\" title=\"添加到通道组\" >'; 
              html	+=  '<i class=\"ace-icon glyphicon glyphicon-plus bigger-120\"></i>';
              html  += '</button>';
            }
    html += '</div>';
    return html;  
}

AccessChannelGroup.editEvent=function(id) {
	 var index = layer.open({
         type: 2,
         title: '接入通道组详情',
         area: ['900px', '520px'], //宽高
         fix: false, //不固定
         maxmin: true,
         content: Feng.ctxPath + '/accessChannelGroup/accessChannelGroup_update/' + id
     });
     this.layerIndex = index;
};


AccessChannelGroup.detail=function(id) {
	var accessId="";
	if(id==undefined){
			if (!AccessChannelGroup.check()) {
				return ;
		 }else{
			 accessId = AccessChannelGroup.seItem.channelGroupId
		 }
	}else{
		accessId=id
	}
	var index = layer.open({
        type: 2,
        title: '接入通道组详情',
        area: ['900px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/accessChannelGroup/detail/' +accessId+"?isShow=0"
    });
    this.layerIndex = index;
};


AccessChannelGroup.deleteEvent=function(id) {
	layer.confirm('是否要删除？', function(index){
		layer.close(index);
    var ajax = new $ax(Feng.ctxPath + "/accessChannelGroup/delete", function (data) {
    	if (data.code=200) {
    		 Feng.success("删除成功!");
             AccessChannelGroup.table.refresh();
		}else{
			 Feng.error(data.message);
		}
    }, function (data) {
        Feng.error(data.message);
    });
    ajax.set("accessChannelGroupId",id);
    ajax.start();
	});
};


AccessChannelGroup.groupAddToChannelGroup=function(id) {
	var accessId="";
	if(id==undefined){
			if (!AccessChannelGroup.check()) {
				return ;
		 }else{
			 accessId = AccessChannelGroup.seItem.channelGroupId
		 }
	}else{
		accessId=id
	}
		var index = layer.open({
	        type: 2,
	        title: '添加接入通道组',
	        area: ['900px', '520px'], //宽高
	        fix: false, //不固定
	        maxmin: true,
	        content: Feng.ctxPath + '/accessChannelGroup/accessChannelGroup_toGroup?channelGroupId='+accessId
	    });
	    this.layerIndex = index;
};
//////////////////////////////////
/**
 * 检查是否选中
 */
AccessChannelGroup.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        AccessChannelGroup.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加接入通道组
 */
AccessChannelGroup.openAddAccessChannelGroup = function () {
    var index = layer.open({
        type: 2,
        title: '添加接入通道组',
        area: ['900px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/accessChannelGroup/accessChannelGroup_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看接入通道组详情
 */
AccessChannelGroup.openAccessChannelGroupDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '接入通道组详情',
            area: ['900px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/accessChannelGroup/accessChannelGroup_update/' + AccessChannelGroup.seItem.channelGroupId
        });
        this.layerIndex = index;
    }
};

/**
 * 删除接入通道组
 */
AccessChannelGroup.delete = function () {
    if (this.check()) {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
        var ajax = new $ax(Feng.ctxPath + "/accessChannelGroup/delete", function (data) {
        	if (data.code=200) {
        		 Feng.success("删除成功!");
                 AccessChannelGroup.table.refresh();
			}else{
				 Feng.error(data.message);
			}
        }, function (data) {
            Feng.error(data.message);
        });
        ajax.set("accessChannelGroupId",AccessChannelGroup.seItem.channelGroupId);
        ajax.start();
    	});
    }
};

/**
 * 查询接入通道组列表
 */
AccessChannelGroup.search = function () {
    var queryData = {
    		params:{}
    };
    queryData.params['dispatchChannel'] = $("#dispatchChannel").val();
    queryData.params['groupName'] = $("#groupName").val();
    queryData.params['quota_id'] = $("#quota_id").val();
    queryData.params['isQuote'] = $("#isQuote").val();
    AccessChannelGroup.table.refresh({query: queryData});
};


AccessChannelGroup.reSet=function (){
	 $("#dispatchChannel").val("");
	 $("#groupName").val("");
	 $("#quota_id").select2("val", "");
	 $("#isQuote").val("");
}

AccessChannelGroup.initSelect2 = function () {
    $("#quota_id").removeClass().css({"width":"200px","height":"35px"}).select2({
        minimumInputLength: 1,
        ajax: {
            url: Feng.ctxPath  + '/channelQualityQuota/selectQualityInfo.do',
            dataType: 'json',
            data: function (term) {
                return {
                    "quotaName": term
                };
            },
            results: function (data) {
                return {
                    results: $.map(data.qualityQuotaList, function (item) {
                        return {
                            id: item.quotaId,
                            text: item.quotaName
                        }
                    })
                };
            }
        }
    });
}


$(function () {
    var defaultColunms = AccessChannelGroup.initColumn();
    var table = new BSTable(AccessChannelGroup.id, "/accessChannelGroup/list", defaultColunms);
   // table.setPaginationType("client");
    AccessChannelGroup.table = table.init();
    AccessChannelGroup.initSelect2();
});
