/**
 * 客户结算账单管理初始化
 */
var CustomerAccountAnalyse = {
    id: "CustomerAccountAnalyseTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
CustomerAccountAnalyse.initColumn = function () {
    return [
            [
             {
                title: "",
               colspan: 6,valign: 'middle',
                rowspan: 1
             },
             {
            	 title: "提交成功",
            	 colspan: 4,
            	 align: 'center', valign: 'middle',
            	 rowspan: 1
             },
             {
            	 title: "充值失败",
            	 colspan: 4,
            	 align: 'center', valign: 'middle',
            	 rowspan: 1
             },
             {
            	 title: "充值成功",
            	 colspan: 4,
            	 align: 'center', valign: 'middle',
            	 rowspan: 1
             },
             {
            	 title: "",
            	 colspan: 3,valign: 'middle',
            	 rowspan: 1
             }
          ],
    [
        {field: 'selectItem', radio: true},
        /*       {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'},
*/        {title: '客户编号', field: 'customerId',sortable : true,sortname : 'customer_id',align: 'center', valign: 'middle'},
        {title: '客户名称', field: 'customerName',align: 'center', valign: 'middle'},
        {title: '合作伙伴名称', field: 'partnerName',align: 'center', valign: 'middle'},
        {title: '应用ID', field: 'appId',align: 'center', valign: 'middle'},
        {title: '应用名称', field: 'appName',align: 'center', valign: 'middle'},
        {title: '个数', field: 'submitNum',align: 'center', valign: 'middle'},
        {title: '原价(元)', field: 'submitOriginalPrice',align: 'center', valign: 'middle'},
        {title: '代理(元)', field: 'submitDiscountPrice',align: 'center', valign: 'middle'},
        {title: '通道(元)', field: 'submitChannelPrice',align: 'center', valign: 'middle'},
        {title: '个数', field: 'rechargeFailNum',align: 'center', valign: 'middle'},
        {title: '原价(元)', field: 'rechargeFailOriginalPrice',align: 'center', valign: 'middle'},
        {title: '代理(元)', field: 'rechargeFailDiscountPrice',align: 'center', valign: 'middle'},
        {title: '通道(元)', field: 'rechargeFailChannelPrice',align: 'center', valign: 'middle'},
        {title: '个数', field: 'rechargeSuccessNum',align: 'center', valign: 'middle'},
        {title: '原价(元)', field: 'rechargeSuccessOriginalPrice',align: 'center', valign: 'middle'},
        {title: '代理(元)', field: 'rechargeSuccessDiscountPrice',align: 'center', valign: 'middle'},
        {title: '通道(元)', field: 'rechargeSuccessChannelPrice',align: 'center', valign: 'middle'},
        {title: '毛利(元)', field: 'profit',align: 'center', valign: 'middle'},
        {title: '结算金额', field: 'settlementAmout',align: 'center', valign: 'middle'}
//        {title: '客户类型', field: 'adviserType',align: 'center', valign: 'middle'},
    ]
    
    ];
};

/**
 * 检查是否选中
 */
CustomerAccountAnalyse.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        CustomerAccountAnalyse.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加客户结算账单
 */
CustomerAccountAnalyse.openAddCustomerAccountAnalyse = function () {
    var index = layer.open({
        type: 2,
        title: '添加客户结算账单',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/customerAccountAnalyse/customerAccountAnalyse_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看客户结算账单详情
 */
CustomerAccountAnalyse.openCustomerAccountAnalyseDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '客户结算账单详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/customerAccountAnalyse/customerAccountAnalyse_update/' + CustomerAccountAnalyse.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除客户结算账单
 */
CustomerAccountAnalyse.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/customerAccountAnalyse/delete", function (data) {
            Feng.success("删除成功!");
            CustomerAccountAnalyse.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("customerAccountAnalyseId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询客户结算账单列表
 */
CustomerAccountAnalyse.search = function () {
	var params_={};
    params_.params = {};
    params_.params.createStartTime = $("#createStartTime").val();
    params_.params.createEndTime = $("#createEndTime").val();
    params_.params.partnerName = $("#partnerName").val();
    params_.params.customerName = $("#customerName").val();
    params_.params.appId = $("#appId").val();
    CustomerAccountAnalyse.table.refresh({query: params_});
//    CustomerAccountAnalyse.table = table.init();
};

$(function () {
    var defaultColunms = CustomerAccountAnalyse.initColumn();
    var table = new BSTable(CustomerAccountAnalyse.id, "/customerAccountAnalyse/list", defaultColunms);
    /**
     * 参数1 是否开启导出
     * 参数2，导出数据类型
     * 参数3，导出数据内容，统一all
     * 参数4，导出选项
     *       ignoreColumn 忽略列索引
     *       fileName 导出文件名
     *       tableName 表名
     *       worksheetName 工作空间名
     */
    var exportOptions = {};
    exportOptions.ignoreColumn="[0]";
    exportOptions.fileName="客户结算账单";
    exportOptions.tableName="客户结算账单";
    exportOptions.worksheetName="客户结算账单";
    table.setExport(true,"['excel']","all",exportOptions);
    CustomerAccountAnalyse.table = table.init();
});
