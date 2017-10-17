/**
 * 流量包管理初始化
 */
var FlowPackageInfo = {
    id: "FlowPackageInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
FlowPackageInfo.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
//        {title: 'id', field: 'packageId', visible: false, align: 'center', valign: 'middle'},
        {title: '流量包ID', field: 'packageId', align: 'center', valign: 'middle', sortable:true, sortName:'package_id',formatter:FlowPackageInfo.formatId},
        {title: '流量包名称', field: 'packageName', align: 'center', valign: 'middle'},
        {title: '对应运营商', field: 'operatorCode', align: 'center', valign: 'middle',formatter : FlowPackageInfo.formatCode},
        {title: '有效期(天)', field: 'activePeriod', align: 'center', valign: 'middle',sortable:true, sortName:'active_period'},
        {title: '流量大小(M)', field: 'flowAmount', align: 'center', valign: 'middle',sortable:true, sortName:'flow_amount'},
        {title: '适用区域', field: 'zoneDesc', align: 'center', valign: 'middle'},
        {title: '流量包类型', field: 'isCombo', align: 'center', valign: 'middle'},
        {title: '流量包种类', field: 'packageTypeDesc', align: 'center', valign: 'middle'},
        {title: '标准销售价格(元)', field: 'salePrice', align: 'center', valign: 'middle' ,sortable:true, sortName:'sale_price'},
        {title: '标准成本价格(元)', field: 'costPrice', align: 'center', valign: 'middle',sortable:true, sortName:'cost_price'},
//        {title: '操作', field: 'myac', align: 'center', valign: 'middle' ,formatter : FlowPackageInfo.actionButtons}
    ];
};

FlowPackageInfo.formatId=function(cellvalue, rowObject,index ) {
	return '<a href="javascript:;" onclick="FlowPackageInfo.detail(\''
			+ rowObject.packageId + '\')">' + cellvalue
			+ '</a>';
}

FlowPackageInfo.formatCode = function(val, options, rowObject) {
	var result="";
	var resultMap=new Array();
		var obj=options['flowPackageInfos'];
		
		if(obj!=null&&obj!=''&&obj!="undefined"){
			for(var i=0;i<obj.length;i++){
				if(resultMap[FlowPackageInfo.setCode(obj[i].operatorCode)]==true){
					continue;
				}
				resultMap[FlowPackageInfo.setCode(obj[i].operatorCode)]=true;
				result+=FlowPackageInfo.setCode(obj[i].operatorCode)+',';
			}
			if(result.length>0){
				result=result.substring(0,result.length-1);
			}
		}else{
			result=FlowPackageInfo.setCode(val);
		}
	return result;
}

FlowPackageInfo.setCode = function(val){
	switch (val) {
	case "YD":
		return "移动";
		break;
	case "LT":
		return "联通";
		break;
	case "DX":
		return "电信";
		break;
	default:
		return "";
		break;
	}
}
//FlowPackageInfo.actionButtons=function(cellvalue, rowObject, index) {
//	return '<div >'
//			+ '<button onclick=\"FlowPackageInfo.openChannelAdapterDetailById('
//			+ rowObject.adapterId
//			+ ')\" class=\"btn btn-xs btn-info\" data-rel=\"tooltip\" title=\"编辑\" >'
//			+ '<i class=\"ace-icon fa fa-pencil bigger-120\"></i>'
//			+ '</button>'
//			+ '<button onclick=\"FlowPackageInfo.deleteById('
//			+ rowObject.adapterId
//			+ ')\" class=\"btn btn-xs btn-danger\" data-rel=\"tooltip\" title=\"删除\" >'
//			+ '<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>'
//			+ '</button>' + '</div>';
//}
/**
 * 检查是否选中
 */
FlowPackageInfo.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        FlowPackageInfo.seItem = selected[0];
        return true;
    }
};

FlowPackageInfo.reSet = function(){
    $("#packageName").val("");
    $("#operatorCode").val("");
    $("#zone").val("");
}

/**
 * 点击添加流量包
 */
FlowPackageInfo.openAddFlowPackageInfo = function () {
    var index = layer.open({
        type: 2,
        title: '添加流量包',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowPackageInfo/flowPackageInfo_add'
    });
    this.layerIndex = index;//	全屏
    layer.full(index);
    
};

/**
 * 打开修改
 */
FlowPackageInfo.openFlowPackageInfoUpdate = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '修改流量包',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/flowPackageInfo/flowPackageInfo_update/' + FlowPackageInfo.seItem.packageId
        });
        this.layerIndex = index;
        layer.full(index);
    }
};

/**
 * 打开查看流量包详细参数
 */
FlowPackageInfo.detail = function (packageId) {
    var index = layer.open({
        type: 2,
        title: '流量包详情',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/flowPackageInfo/detail/' + packageId
    });
    this.layerIndex = index;
    layer.full(index);//全屏
}

/**
 * 删除流量包
 */
FlowPackageInfo.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/flowPackageInfo/delete", function (data) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data && data.message ? data.message : "删除失败！");
        		return;
        	}
            Feng.success("删除成功!");
            FlowPackageInfo.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("flowPackageInfoId",this.seItem.packageId);
        ajax.start();
    }
};

/**
 * 查询流量包列表
 */
FlowPackageInfo.search = function () {
    var queryData = {
	    	params:{}
	    };
	    queryData.params['packageName'] = $("#packageName").val();
	    queryData.params['operatorCode'] = $("#operatorCode").val();
	    queryData.params['zone'] = $("#zone").val();
    FlowPackageInfo.table.refresh({query: queryData});
}


$(function () {
    var defaultColunms = FlowPackageInfo.initColumn();
    var table = new BSTable(FlowPackageInfo.id, "/flowPackageInfo/list", defaultColunms);
//    table.setPaginationType("client");
    FlowPackageInfo.table = table.init();
    
});
