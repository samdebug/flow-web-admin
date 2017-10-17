/**
 * 设置个性化参数管理初始化
 */
var PendTaskSetting = {
    id: "PendTaskSettingTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
PendTaskSetting.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
PendTaskSetting.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        PendTaskSetting.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加设置个性化参数
 */
PendTaskSetting.openAddPendTaskSetting = function () {
    var index = layer.open({
        type: 2,
        title: '添加设置个性化参数',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/pendTaskSetting/pendTaskSetting_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看设置个性化参数详情
 */
PendTaskSetting.openPendTaskSettingDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '设置个性化参数详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/pendTaskSetting/pendTaskSetting_update/' + PendTaskSetting.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除设置个性化参数
 */
PendTaskSetting.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/pendTaskSetting/delete", function (data) {
            Feng.success("删除成功!");
            PendTaskSetting.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("pendTaskSettingId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询设置个性化参数列表
 */
PendTaskSetting.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    PendTaskSetting.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = PendTaskSetting.initColumn();
    var table = new BSTable(PendTaskSetting.id, "/pendTaskSetting/list", defaultColunms);
    table.setPaginationType("client");
    PendTaskSetting.table = table.init();
    
    //发邮件设置
    $('input[name="sendemail"]').bind('click', function() {
		if ($(this).val() == '1') {
			// $('#emailSendTimeTr1,#emailSendTimeTr2').show();
			$($('input[name="emailSendTime"]')).each(function(i) {
				$(this).attr('disabled', false);
			});
			$("#reset").attr('disabled', false);
		} else {
			// $('#emailSendTimeTr1,#emailSendTimeTr2').hide();
			$($('input[name="emailSendTime"]')).each(function(i) {
				$(this).attr('checked', false).attr('disabled', true);
			});
			$("#reset").attr('disabled', true);
		}
	});
    
    //加载用户个性化设置数据
	var ajax = new $ax(Feng.ctxPath + "/pendTaskSetting/get.ajax", function(data){
		//用户邮箱
		$("#staffEmail").text(data.user.email);
		//开通状态
		if (data.pendTaskSetting.sendemail == '1') {
			$("#sendemailYRadio").attr('checked',true);
			$($('input[name="emailSendTime"]')).each(function(i) {
				$(this).attr('disabled', false);
			});
			var emailsendtime = data.pendTaskSetting.emailsendtime;
			if (emailsendtime) {
				$('input[name="emailSendTime"]').values(emailsendtime);
			}
		} else {
			$("#sendemailNRadio").attr('checked',true);
			// $('#emailSendTimeTr1,#emailSendTimeTr2').hide();
			$($('input[name="emailSendTime"]')).each(function(i) {
				$(this).attr('checked', false).attr('disabled', true);
			});
		}
    },function(data){
        Feng.error("查询失败!" + data.responseJSON.message + "!");
    });
    ajax.start();
    
});

//重置按钮
PendTaskSetting.reset = function() {
	$('input[name="emailSendTime"]').each(function() {
		$(this).attr('checked', false);
	});
};

//修改用户个性化设置
PendTaskSetting.editSubmit=function() {
	var params = {
			sendemail : $('input[name="sendemail"]:checked').val(),
			emailsendtime : $('input[name="emailSendTime"]').values(),
		};
	if (params['sendemail'] === '1' && !params['emailsendtime']) {
		alert('请勾选邮件待办提醒时间');
		return;
	}
	//提交信息
    var ajax = new $ax(Feng.ctxPath + "/pendTaskSetting/update.ajax", function(data){
        Feng.success("修改成功!");
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
	ajax.set(params);
    ajax.start();
};
