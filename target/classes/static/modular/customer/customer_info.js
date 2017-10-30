/**
 * 初始化客户管理详情对话框
 */
var CustomerInfoDlg = {
    customerInfoData : {},
    persistentData : null
};

/**
 * 清除数据
 */
CustomerInfoDlg.clearData = function() {
    this.customerInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerInfoDlg.set = function(key, val) {
    this.customerInfoData[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CustomerInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
CustomerInfoDlg.close = function() {
    parent.layer.close(window.parent.Customer.layerIndex);
}

/**
 * 收集数据
 */
CustomerInfoDlg.collectData = function() {
//    this.set('id');
    var _array = $("form").serializeArray();
	if ( !_array || _array.length <= 0 ) {
		return;
	}
	$.each(_array, function() {
		CustomerInfoDlg.customerInfoData[this.name] = this.value;
    });
}

/**
 * 提交添加
 */
CustomerInfoDlg.addSubmit = function() {

	if (!$('form').valid()) {// 手动校验
		return;
	}
	
    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customer/add", function(data){
    	if ( data && data.code == 200 ) {
    		//CustomerInfoDlg.persistentData = data;
    		Feng.success("添加成功!");
    		//openCreateOrder();
    		window.parent.Customer.table.refresh();
    		CustomerInfoDlg.close();
    	} else {
    		if (data && data.message) {
    			Feng.error("添加失败!" + data.message + "!");
    			return;
    		}
    		Feng.error("添加失败!");
    	}
    },function(data){
        Feng.error("添加失败!");
    });
    ajax.set(this.customerInfoData);
    ajax.start();
}


///**
// * 是否创建订单
// * @returns
// */
//function openCreateOrder() {
//	$("#order-confirm-modal").modal({
//        backdrop: "static",
//        keyboard: false
//    }).on("shown.bs.modal", function (e) {
//        $("#cancel-btn").click(function() {
//            $("#order-confirm-modal").modal("hide");
//            CustomerInfoDlg.close();
//        });
//        $("#confirm-btn").click(function() {
//            $("#order-confirm-modal").modal("hide");
//            // 跳转到新增订单页面
//            redirectToOrderAddPage();
//        });
//    });
//}

//跳转到新增订单页面
//function redirectToOrderAddPage() {
//    location.href = Feng.ctxPath + '/orderInfo/add?orderType=1&customerId=' + CustomerInfoDlg.persistentData.customerId;
//}


/**
 * 提交修改
 */
CustomerInfoDlg.editSubmit = function() {
	
	if (!$('form').valid()) {// 手动校验
		return;
	}

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/customer/update", function(data){
    	if ( data && data.code == 200 ) {
    		Feng.success("修改成功!");
            window.parent.Customer.table.refresh();
            CustomerInfoDlg.close();
    	} else {
    		if (data && data.message) {
    			Feng.error("修改失败!" + data.message + "!");
    			return;
    		}
    		Feng.error("修改失败!");
    	}
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.customerInfoData);
    ajax.start();
}

/**
 * 初始化合作伙伴列表
 * @returns
 */
function initPartners() {
	
	var ajax = new $ax(Feng.ctxPath + "/customer/queryPartners", function(data){
		if ( data && data.length > 0 ) {
    		var html = '<option value="" partnerType="1">----- 请选择 -----</option>';
    		$.each(data, function(){
    			html += '<option value="' + this.value + '" partnerType="' + this.partnerType + '">' + this.displayName + "</option>";
    		});
    		$("#partnerId").html(html);
    		showAccount(data[0].partnerType);
    	} else {
    		Feng.error("暂无合作伙伴信息!");
    	}
    },function(data){
        Feng.error("获取合作伙伴数据失败!" + data.responseJSON.message + "!");
    });
    ajax.set({});
    ajax.start();
}

function showAccount(partnerType) {
	if (partnerType == "1") {//合作伙伴类型是 流量营销 
		$("#linkmanMobile").unbind('keydown');
		$("#linkmanMobile").unbind('keyup');
		$("#account").removeAttr("readonly");
	} else {
		$("#account").attr("readonly", "readonly");
		$("#linkmanMobile").on('keyup', function() {
			copyAccount();
		});
		$("#linkmanMobile").on('blur',function(){
			$("#account").blur();
		});
		$("#linkmanMobile").on('keydown', function() {
			copyAccount();
		});
	}
}

function copyAccount() {
	var val = $("#linkmanMobile").val();
	$("#account").val(val);
}


function soloAccount(value) {
	var updId = $("#customerId").val();
	if (value == null || value == "") {
		return false;
	}
	var flag = false;
	$.ajax({
		url : Feng.ctxPath + '/customer/soloAccount.ajax',
		type : 'post',
		data : {
			"updId" : updId,
			"account" : value
		},
		dataType : 'json',
		async : false,
		success : function(data) {
			if (data.code == 200) {
				flag = true;
			}
		}
	});
	return flag;
}


$(function() {

	// 新增模式
	if (!$("#customerId").val()) {
		
		initPartners();
		
		$("#partnerId").on(
			'change',
			function() {
				var obj = document.getElementById("partnerId");
				var index = obj.selectedIndex;
				var partnerType = obj.options[index].getAttribute("partnerType");
				showAccount(partnerType);
				if(partnerType==2){
					$("#linkmanMobile").val("");
					$("#account").val("");
				}
			}
		);
	}
	
	$.validator.addMethod("soloAccount", function(value, element, params) {
		return soloAccount(value);
	}, "该账号不可用");
	
	var add_validator = $("form")
		.validate(
			{
				rules : {
					'account' : {
						required : true,
						maxlength : 20,
						checkAccount: true,
						soloAccount : true
					},
					'customerName' : {
						required : true,
						checkCustomerName:true,
						maxlength : 128
					},
					'linkmanName' : {
						required : false,
						maxlength : 20
					},
					'linkmanMobile' : {
						required : false,
						maxlength : 32,
						mobile : true
					},
					'linkmanEmail' : {
						required : false,
						maxlength : 128,
						multipleEmailValid : true
					},
					'address' : {
						required : false,
						maxlength : 256
					}
				},
				submitHandler : function(form) {
					return false;
				}
			}
		);
	
});
