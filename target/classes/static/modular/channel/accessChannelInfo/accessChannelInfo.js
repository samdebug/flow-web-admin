/**
 * 接入通道管理初始化
 */
var AccessChannelInfo = {
    id: "AccessChannelInfoTable",	// 表格id
    seItem: null,		// 选中的条目
    table: null,
    layerIndex: -1,
    queryData:{
    	params:{}
    }
};

/**
 * 初始化表格的列
 */
AccessChannelInfo.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '通道自增序列', field: 'channelSeqId', visible: false, align: 'center', valign: 'middle'},
      //  {title: '通道ID', field: 'channelId', align: 'center', valign: 'middle', formatter: formatName},
        {title: '供应商编码', field: 'supplierCode', align: 'center', valign: 'middle', sortable: true,sortName: 'supplier_code'},
        {title: '通道名称', field: 'channelName', align: 'center', valign: 'middle'},
       // {title: '流量下发适配器', field: 'adapterName', align: 'center',width:50, valign: 'middle'},
        {title: '折扣(%)', field: 'discount', align: 'center', valign: 'middle'},
        {title: '适用区域', field: 'zoneDesc', align: 'center', valign: 'middle'},
        {title: '运营商', field: 'operatorCodeDesc', align: 'center', valign: 'middle'},
        {title: '是否有效', field: 'isValidDesc' ,sortable: true,sortName: 'is_valid', align: 'center', valign: 'middle'},
       // {title: '备注', field: 'remark', visible: false, align: 'center', valign: 'middle'},
      //  {title: '创建者', field: 'creator', visible: false, align: 'center', valign: 'middle'},
     //   {title: '创建时间', field: 'createTime', visible: false, align: 'center', valign: 'middle'},
        {title: '修改者', field: 'updator', align: 'center', valign: 'middle'},
        {title: '修改时间', field: 'updateTime',sortable: true,sortName: 'update_time', align: 'center', valign: 'middle'},
     //   {title: '创建者IP', field: 'ip', visible: false, align: 'center', valign: 'middle'},
      //  {title: '是否允许转售', field: 'isAllowSaleDesc', sortable: true,sortName: 'is_allow_sale',align: 'center', valign: 'middle'},
      //  {title: '是否有短信通知', field: 'isSmsNoticeDesc', align: 'center', valign: 'middle'},
      //  {title: '通道类别', field: 'channelTypeDesc', align: 'center', valign: 'middle'},
       // {title: '累计成功数', field: 'succNum', sortable: true,sortName: 'succ_num', align: 'center', valign: 'middle'},
      //  {title: '累计失败数', field: 'failNum',  sortable: true,sortName: 'fail_num',align: 'center', valign: 'middle'},
        {title: '成功率(3小时内)', field: 'succRatio',sortable: true,sortName: 'succ_ratio', align: 'center', valign: 'middle'},
        {title: '总成功率', field: 'totalSuccRatio',  sortable: true,sortName: 'total_succ_ratio', align: 'center', valign: 'middle'},
        {title: '及时率(3小时内)', field: 'timlyRate',sortable: true,sortName: 'timly_rate',align: 'center', valign: 'middle'},
      //  {title: '支持地市', field: 'supportCity', align: 'center', valign: 'middle'},
       // {title: '操作', field: 'id', align: 'center', width:100,valign: 'middle',formatter:actionButtons}
    ];
};


/**
 * 检查是否选中
 */
AccessChannelInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        AccessChannelInfo.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加接入通道
 */
AccessChannelInfo.openAddAccessChannelInfo = function () {
    var index = layer.open({
        type: 2,
        title: '添加接入通道',
        area: ['1300px', '520px'], //宽高
        fix: false, // 不固定
        maxmin: true,
        content: Feng.ctxPath + '/accessChannelInfo/accessChannelInfo_add'
    });
    this.layerIndex = index;
	//layer.full(index);
};

/**
 * 打开查看接入通道详情
 */
AccessChannelInfo.openAccessChannelInfoDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '接入通道详情',
            area: ['1300px', '520px'], //宽高
            fix: false, // 不固定
            maxmin: true,
            content: Feng.ctxPath + '/accessChannelInfo/accessChannelInfo_update/' + AccessChannelInfo.seItem.channelSeqId
        });
        this.layerIndex = index;
    	//layer.full(index);
    }
};

/**
 * 删除接入通道
 */
AccessChannelInfo.delete = function () {
    if (this.check()) {
    	layer.confirm('是否要删除？', function(index){
    		layer.close(index);
        var ajax = new $ax(Feng.ctxPath + "/accessChannelInfo/delete", function (data) {
            Feng.success("删除成功!");
            AccessChannelInfo.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("accessChannelInfoId",AccessChannelInfo.seItem.channelSeqId);
        ajax.start();
    	});
    }
};

// 初始化区域下拉列表
AccessChannelInfo.initZoneSelect=function () {
    // 全国区域展示
    $.ajax({
        url: Feng.ctxPath + '/accessChannelInfo/selectAreaCodeAll',
        type: 'get',
        dataType: 'json',
        success: function(data) {
            var html = '';
            html += '<option value="">请选择区域</option>';
            for (var i = 0; i < data.data.length; i++) {
                html += '<option value="'+data.data[i].areaCode+'">' + data.data[i].areaName + '</option>';
            }
            $("#zone").html(html);
        }
    });
}

/**
 * 查询接入通道列表
 */
AccessChannelInfo.search = function () {
	var queryData = AccessChannelInfo.getQueryData();
	AccessChannelInfo.table.refresh({query: queryData});
};

AccessChannelInfo.getQueryData=function(){
	var array = $("#queryParams").serializeArray();
	if (array && array.length > 0) {
		$.each(array, function() {
			AccessChannelInfo.queryData.params[this.name] = this.value;
	    });
	}
	return AccessChannelInfo.queryData;
}

AccessChannelInfo.reSet=function(){
	document.getElementById("queryParams").reset();
}

//通道管理配置
var job_congfig_url = Feng.ctxPath + '/accessChannelInfo/jobConfig';
var channel_alarm_congfig_url = Feng.ctxPath + '/accessChannelInfo/channelAlarmConfig';

AccessChannelInfo.checkText=function(obj) {  
    // 检查是否是非数字值
    if (null == obj.value || '' == obj.value) {  
    	 Feng.error("不能输入非空");  
    }
} 
AccessChannelInfo.checkNum=function(obj) {  
    // 检查是否是非数字值
    if (isNaN(obj.value)) {  
        alert("不能输入非数字值");  
    }
}  

function initJobConfig(){ 
		$.get(job_congfig_url,function(data,status){
			if(data.success){
				$("#inHours").val(data.inHours);
				$("#stuckRows").val(data.stuckRows);
			}else{
			 Feng.error("获取通道定时任务配置失败，请刷新页面或联系技术人员");
			}
		});
	}



// 通道预警设置
function initChannelAlarmConfig(){ 
	$.get(channel_alarm_congfig_url,function(data,status){
	   	if(data.success){
	   		$("#emailAlarmNum").val(data.emailAlarmNum);
	   		$("#emailAlarmInterval").val(data.emailAlarmInterval);
	   		$("#smsAlarmNum").val(data.smsAlarmNum);
	   		$("#smsAlarmInterval").val(data.smsAlarmInterval);
	   		$("#emailAlarmRecivers").val(data.emailAlarmRecivers);
	   		$("#smsAlarmRecivers").val(data.smsAlarmRecivers);
	   	}else{
	   		Feng.error("获取通道预警配置失败，请刷新页面或联系技术人员");
	   	}
	  });
	
}

AccessChannelInfo.export2Excel=function(){
	var array = $("#queryParams").serializeArray();
	var str = "";
	if (array && array.length > 0) {
		$.each(array, function() {
			str= str+"params['"+this.name+"']="+this.value+"&";
	    });
	}
	location.href=encodeURI(Feng.ctxPath + "/accessChannelInfo/export?"+str);
}

$(function () {
    var defaultColunms = AccessChannelInfo.initColumn();
    var table = new BSTable(AccessChannelInfo.id, "/accessChannelInfo/list", defaultColunms);
    AccessChannelInfo.table = table.init();
    AccessChannelInfo.initZoneSelect();
    initJobConfig();
    initChannelAlarmConfig();
});


// //////////////////////////
function formatName(cellvalue, rowObject, index) {
    return '<a href="javascript:;" onclick="detail(\'' + rowObject['channelSeqId'] + '\')">' + cellvalue + '</a>';
}
function actionButtons(cellvalue, rowObject,index ) {
    var channelStatusTemp = rowObject['isValid'];
    var btnTitle = "";
    var changeStatus = 0;
    if (channelStatusTemp == 0) {
        btnTitle = "有效";
        changeStatus = 1;
    } else if (channelStatusTemp == 1) {
        btnTitle = "无效";
        changeStatus = 0;
    }
    return '<div >' 
            + '<button onclick=\"changeStatusEvent(' + changeStatus + ',' + rowObject['channelSeqId'] + ')\" class=\"btn btn-xs btn-warning\" permCheck=\"auth_access_channel_info_list,changestatus,hidden\" data-rel=\"tooltip\" title=\"' + btnTitle + '\" >' 
                + '<i class=\"ace-icon fa fa-flag bigger-120\"></i>' 
            + '</button>' 
         + '</div>';
}

function editEvent(id) {
	var index = layer.open({
        type: 2,
        title: '接入通道详情',
        area: ['900px', '520px'], //宽高
        fix: false, // 不固定
        maxmin: true,
        content: Feng.ctxPath + '/accessChannelInfo/accessChannelInfo_update/' + id
    });
	AccessChannelInfo.layerIndex = index;
//	layer.full(index);
};

function detail(id) {
	var accessId="";
	if(id==undefined){
			if (!AccessChannelInfo.check()) {
				return ;
		 }else{
			 accessId = AccessChannelInfo.seItem.channelSeqId
		 }
	}else{
		accessId=id
	}
	 var index = layer.open({
         type: 2,
         title: '接入通道详情',
         area: ['900px', '520px'], //宽高
         fix: false, // 不固定
         maxmin: true,
         content: Feng.ctxPath + '/accessChannelInfo/detail/' + accessId+"?isShow=0"
     });
	 AccessChannelInfo.layerIndex = index;
};

function changeRecords(id) {
	var accessId="";
	if(id==undefined){
			if (!AccessChannelInfo.check()) {
				return ;
		 }else{
			 accessId = AccessChannelInfo.seItem.channelSeqId
		 }
	}else{
		accessId=id
	}
	var index = layer.open({
        type: 2,
        title: '接入通变更历史',
        area: ['900px', '520px'], //宽高
        fix: false, // 不固定
        maxmin: true,
        content: Feng.ctxPath + '/accessChannelInfoHis/list/' + accessId
    });
	AccessChannelInfo.layerIndex = index;
};

function addToChannelGroup(id) {
	var accessId="";
	if(id==undefined){
			if (!AccessChannelInfo.check()) {
				return ;
		 }else{
			 accessId = AccessChannelInfo.seItem.channelSeqId
		 }
	}else{
		accessId=id
	}
	var index = layer.open({
        type: 2,
        title: '添加通道组',
        area: ['900px', '520px'], //宽高
        fix: false, // 不固定
        maxmin: true,
        content: Feng.ctxPath + '/accessChannelInfo/accessChannelInfo_addGroup?channelSeqId=' + accessId
    });
	AccessChannelInfo.layerIndex = index;
};


/**
 * 有效/无效 按钮点击事件
 */
function changeStatusEvent() {
		var accessId="";
	    var changeStatus = 0;
		if (!AccessChannelInfo.check()) {
					return ;
		}else{
			accessId = AccessChannelInfo.seItem.channelSeqId
		}
		var channelStatusTemp=AccessChannelInfo.seItem.isValid
	    if (channelStatusTemp == 0) {
	        changeStatus = 1;
	    } else if (channelStatusTemp == 1) {
	        changeStatus = 0;
	    }
		$.ajax({
		    url:  Feng.ctxPath + '/accessChannelInfo/changeStatus?status=' + changeStatus + '&channelSeqId=' + accessId,
		    dataType: "json",
		    success: function(data) {
		        if (data.success ==true) {
		        	Feng.success("设置成功!");
		        	 AccessChannelInfo.table.refresh();
		        } else {
		        	Feng.error(data.message);
		        }
		    }
		});
};
function deleteEvent(id) {
	layer.confirm('是否要删除？', function(index){
		layer.close(index);
    var ajax = new $ax(Feng.ctxPath + "/accessChannelInfo/delete", function (data) {
        Feng.success("删除成功!");
        AccessChannelInfo.table.refresh();
    }, function (data) {
        Feng.error("删除失败!");
    });
    ajax.set("accessChannelInfoId",AccessChannelInfo.seItem.channelSeqId);
    ajax.start();
	});
};
