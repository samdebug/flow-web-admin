/**
 * 测试管理初始化
 */
var Test = {
    id: "TestTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Test.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
Test.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        Test.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加测试
 */
Test.openAddTest = function () {
    var index = layer.open({
        type: 2,
        title: '添加测试',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/test/test_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看测试详情
 */
Test.openTestDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '测试详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/test/test_update/' + Test.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除测试
 */
Test.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/test/delete", function (data) {
            Feng.success("删除成功!");
            Test.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("testId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询测试列表
 */
Test.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    Test.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = Test.initColumn();
    var table = new BSTable(Test.id, "/test/list", defaultColunms);
    table.setPaginationType("client");
    Test.table = table.init();
    $(".hidden-xs").children().removeClass("button-margin");
});
