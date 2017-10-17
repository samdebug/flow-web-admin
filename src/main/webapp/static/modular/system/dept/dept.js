/**
 * 部门管理初始化
 */
var Dept = {
    id: "DeptTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Dept.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', align: 'center', valign: 'middle'},
        {title: '父级部门', field: 'pName', align: 'center', valign: 'middle'},
        {title: '部门简称', field: 'simplename', align: 'center', valign: 'middle'},
        {title: '部门全称', field: 'fullname', align: 'center', valign: 'middle'},
        {title: '排序', field: 'num', align: 'center', valign: 'middle'},
        {title: '备注', field: 'tips', align: 'center', valign: 'middle'}];
};

/**
 * 检查是否选中
 */
Dept.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Dept.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加部门
 */
Dept.openAddDept = function () {
    var index = layer.open({
        type: 2,
        title: '添加部门',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/dept/dept_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看部门详情
 */
Dept.openDeptDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '部门详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/dept/dept_update/' + Dept.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除部门
 */
Dept.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/dept/delete", function (data) {
            Feng.success("删除成功!");
            Dept.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("deptId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询部门列表
 */
Dept.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    Dept.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = Dept.initColumn();
    var table = new BSTable(Dept.id, "/dept/list", defaultColunms);
    table.setPaginationType("client");
    Dept.table = table.init();
    $(".hidden-xs").children().removeClass("button-margin");
});