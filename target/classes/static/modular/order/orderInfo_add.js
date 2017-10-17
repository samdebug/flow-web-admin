var update_url = Feng.ctxPath + '/orderInfo/add.ajax';
var view_url = Feng.ctxPath + '/orderInfo/get.ajax';
var check_url = Feng.ctxPath + '/orderInfo/checkAllowBillIssued';
var pkId = getP('orderId');
var pOIAOrderType = getP('orderType');
var pCustomerId = getP('customerId');

var prodOrderList = null;
var prodData = null;
var proTotalData = null;
var isMarketing = false;
var isWYAdmin = false;
// 是否允许下发计费
var isBillIssued = false;
var leftSel = $("#dltSource"); 
var rightSel = $("#dltTarget");
var productIndex = 0;
var selectedTrArr = [];
var isSearchInit = true;

$(function() {
    initStatus();
    setListener();
    
    /**输入框绑定事件**/
	$('#text').bind('input propertychange',function(){
        getProductByOperatorCode("-1", null);
    });
});

/**
 * 状态初始化
 */
function initStatus() {
    // 初始化订单类型是流量包或流量+
    initFlowPackageOrPlus();
    // 初始化订单编号
    initOrderNumber();
    // 初始化【客戶名称】下拉列表
    initCustomerNameDDL();
}

/**
 * 初始化订单类型是流量包或流量+
 */
function initFlowPackageOrPlus() {
    // 流量包订单
    if (pOIAOrderType == 1) {
        $(".flowPackageDiaplay").show();
        $(".flowPlusDiaplay").hide();
        //$(".telFareDiaplay").hide();
        $(".flowplus-tbl-container").hide();
        $("#poia-price-total").hide();
    } else if (pOIAOrderType == 2) {
    	Feng.error("不支持的订单类型！");
//        $(".flowPackageDiaplay").hide();
//        //$(".telFareDiaplay").hide();
//        $(".flowPlusDiaplay").show();
//        $(".flowplus-tbl-container").show();
//        $("input[type='radio']:last").prop("checked", true);
//        $("#poia-price-total").show();
    }else if (pOIAOrderType == 4) {
    	Feng.error("不支持的订单类型！");
//        $(".flowPackageDiaplay").hide();
//        $(".flowPlusDiaplay").hide();
//        $(".telFareDiaplay").show();
//        $(".flowplus-tbl-container").hide();
//        $("#poia-price-total").hide();
    }
}

/**
 * 初始化订单编号
 */
function initOrderNumber() {
    if (pkId) {
        $("#orderNumberLbl").text(pkId);
    } else {
        $.ajax({
            url : Feng.ctxPath + '/orderInfo/getOrderNumber.ajax',
            dataType : 'json',
            success : function(data, status) {
                $("#orderNumberLbl").text(data);
            }
        });
    }
}

/**
 * 初始化【客戶名称】下拉列表
 */
function initCustomerNameDDL() {
    $.ajax({
        url : Feng.ctxPath + '/orderInfo/selectByPartnerId',
        dataType : 'json',
        success : function(data, status) {
            if (data && data.code && data.code != 200) {
            	Feng.error(data.message);
            } else {
                isWYAdmin = data.isWYAdmin;
                isMarketing = data.isMarketing;
                isBillIssued = data.isBillIssued;
                // 初始化select2组件
                initSelect2(data);
                initDisplayStatus();
                // 更新订单的情况下的初始化
                initUpdateViews();
                // 初始化流量包产品订单table
                initProductOrderTable(prodOrderList, null);
            }
        }
    });
}

/**
 * 初始化select2组件
 * @param data
 */
function initSelect2(data) {
    $("#customer-select").removeClass().css("width","240px").select2({
        minimumInputLength: 1,
        ajax: {
            url: Feng.ctxPath + '/orderInfo/selectCustomerInfoByName.ajax',
            dataType: 'json',
            async:false, 
            data: function (term) {
                return {
                    "customerName": term
                };
            },
            results: function (data) {
                return {
                    results: $.map(data.customerList, function (item) {
                        return {
                            id: item.customerId,
                            text: item.customerName
                        }
                    })
                };
            }
        }
    });
    /**
     * 动态判断客户所属合作伙伴是否允许下发计费
     * (管理员并且是流量加订单)
     */
    $("#customer-select").on("change", function() {
        if (isWYAdmin) {
            var $this = $(this);
            if ($("input[type=radio]:checked").val() == 3) {
            	var ajax = new $ax(check_url, function (data) {
        	        if ( data && data.code && data.code != 200 ) {
        	        	Feng.error(data.message ? data.message : "查询数据失败");
        	        	return;
        	        }
        	        isBillIssued = data.isBillIssued;
                    if (isBillIssued) {
                        $(".flowPlusAndBillTypeIssued").show();
                        if (pkId) {
                            $(".not-update").attr("disabled", "disabled");
                        }
                    } else {
                        $(".flowPlusAndBillTypeIssued").hide();
                    }
        	    }, function (data) {
        	        Feng.error("查询数据失败");
        	    });
        	    ajax.set( {'customerId': $this.val()} );
        	    ajax.start();
            } else {
                // 动态判断组合流量包Table是否展示
            	var ajax = new $ax(Feng.ctxPath + '/customer/get', function (data) {
        	        if ( data && !data.success ) {
        	        	Feng.error(data.message ? data.message : "查询数据失败");
        	        	return;
        	        }
        	        data = data.data;
        	        // 渠道直充合作伙伴
                    if (data.partnerInfo.partnerType == 2) {
                        $(".combo-package").hide();
                        $(".comboRadio").hide();
                        $("input[type=radio]").removeAttr("checked");
                    } else {
                        $(".combo-package").show();
                        $(".comboRadio").show();
                    }
                    $("input[type=radio][value=1]").trigger("click");
        	    }, function (data) {
        	        Feng.error("查询数据失败");
        	    });
        	    ajax.set( {'customerId' : $this.val()} );
        	    ajax.start();
            }
        }
        // 清空产品Table
        prodOrderList = null;
        prodData = null;
        productIndex = 0;
        $("#product-order-tbody").empty();
        $("#product-combo-order-tbody").empty();
        $("#product-telfare-order-tbody").empty();
        $("#product-plus-order-tbody").empty();
    });
}

function initDisplayStatus() {
    if (isMarketing || isWYAdmin) {
        $(".radioGroupDisplay").show();
    } else {
        $(".radioGroupDisplay").show();
        $(".combo-package").hide();
        $(".comboRadio").hide();
    }
    // 是流量加订单并且允许下发计费
    if ($("input[type=radio]:checked").val() == 3 && isBillIssued) {
        $(".flowPlusAndBillTypeIssued").show();
        if (pkId) {
            $(".not-update").attr("disabled", "disabled");
        }
    }
    // 新增完客户信息直接跳转过来的情况
    if (pCustomerId) {
		var ajax = new $ax(Feng.ctxPath + '/customer/get.ajax', function (data) {
	        if (data && !data.success) {
	        	Feng.error(data.message? data.message : "初始化数据错误！");
	        	return;
	        }
	        var obj = {};
            obj["id"] = pCustomerId;
            obj["text"] = data.data.customerName;
            $("#customer-select").select2("data", obj);
            $("#customer-select").trigger("change");
	    }, function (data) {
	        Feng.error("初始化数据失败!");
	    });
	    ajax.set( {'customerId' : pCustomerId} );
	    ajax.start();
    }
}

/**
 * 初始化产品订单table
 */
function initProductOrderTable(dataList, flowType) {
    var radioVal = $("input[type=radio]:checked").val();
    if (radioVal == 3) {
        $(".flow-plus-show").show();
    }
    var html = "";
    if (null != dataList && dataList.length > 0) {
        $.each(dataList, function(index, rowData) {
            html += "<tr>";
            // 非流量加订单
            if (radioVal != 3) {
                html += "<td class='center'><label><input type='checkbox' class='ace' value='"+ rowData.productId +"'><span class='lbl'></span></label></td>"
            }
            html += "<td><input type='hidden' name='flowProductInfoList[" + productIndex + "].productId' value='"+ rowData.productId +"'/>"
            + "<input type='hidden' name='flowProductInfoList[" + productIndex + "].packageId' value='"+ rowData.packageId +"'/>"
            + "<input type='hidden' name='flowProductInfoList[" + productIndex + "].productName' value='"+ rowData.productName +"'/>"
            + "<a href='javascript:void(0);' value='" + rowData.productId + "' class='product-name-detail'>" 
            + $.htmlspecialchars(rowData.productName) 
            + "</a>"
            + "</td>";
            
            var operatorCode = rowData.operatorCodeDesc != null ? rowData.operatorCodeDesc : "移动、电信、联通";
            html += "<td><span attrid='" + rowData.productId + "'>"+ operatorCode +"</span></td>";
            html += "<td><span attrid='" + rowData.productId + "'>"+ rowData.zoneDesc + "</span></td>";
            
            if ($("input[type=radio]:checked").val() == 3) {
                html += "<td><input style=\"width:80px;\" attrid='" + rowData.productId + "' name='flowProductInfoList[" + productIndex + "].productCount' class='numberFormat prodCountInput' value='"+ rowData.productCount +"' /></td>"
            } else {
                html += "<input type='hidden' attrid='" + rowData.productId + "' name='flowProductInfoList[" + productIndex + "].productCount' value='0' />";
            }
            // 产品定价
            html += "<td><span attrid='" + rowData.productId + "'>"+ rowData.productPrice +"</span></td>";
            // 结算价格
            html += "<td><input style=\"width:80px;\" attrid='" + rowData.productId + "' name='flowProductInfoList["+ productIndex +"].settlementAmount' class='numberFormat settlementAmountInput' value='" + rowData.settlementAmount + "' /></td>";
//            // 销售价格
//            if ($("input[type=radio]:checked").val() == 3) { // 流量加
//                html += "<input type='hidden' attrid='" + rowData.productId + "' name='flowProductInfoList["+ productIndex +"].settlementPrice' class='numberFormat productPriceInput' value='" + rowData.settlementPrice + "'/>";
//            } else {
//                html += "<td><input style=\"width:80px;\" attrid='" + rowData.productId + "' name='flowProductInfoList["+ productIndex +"].settlementPrice' class='numberFormat productPriceInput' value='" + rowData.settlementPrice + "'/></td>";
//            }
            var endPrice=rowData.endPrice==null||rowData.endPrice==0?"":rowData.endPrice;
            var salePrice=rowData.salePrice==null||rowData.salePrice==0?"":rowData.salePrice;
            var quarzTime=rowData.quarzTime==null?"":rowData.quarzTime;
            html += "<td>"+(rowData.settlementAmount*100/rowData.productPrice).toFixed(2)+"</td>"
//            html += "<td><input style=\"width:130px;\" class='numberFormat settQuarzInput' value='"+quarzTime+"'  name='flowProductInfoList["+ productIndex +"].quarzTime'  onFocus=\"WdatePicker({startDate: '%y-%M',dateFmt:'yyyy-MM-dd HH:mm',minDate:'%y-%M-%d  %H:{%m+1}',maxDate:'%y-%M-{%d+2}  %H:%m'})\"/></td>"
//            html += "<td>"+endPrice+"</td>"
//            html += "<td>"+salePrice+"</td>"
            html += "<td><button class='btn btn-danger btn-xs deleteBtn' attrid='" + rowData.productId + "'><i class='ace-icon fa fa-trash-o fa icon-only'></i></button></td>"
            html += "</tr>";
            productIndex++;
        });
    }
    if (flowType == null) {
        createTable(radioVal, html);
    } else {
        createTable(flowType, html);
    }
}

function createTable(switchData, html) {
    switch (switchData) {
        case "1":
            $("#product-order-tbody").append(html);
            table_rowspan("#product-order-table", 3);
            table_rowspan("#product-order-table", 4);
            break;
        case "2":
            $("#product-combo-order-tbody").append(html);
            table_rowspan("#product-combo-order-table", 3);
            table_rowspan("#product-combo-order-table", 4);
            break;
        case "3":
            $("#product-plus-order-tbody").append(html);
            table_rowspan("#product-plus-order-table", 2);
            table_rowspan("#product-plus-order-table", 3);
            break;
        case "4":
            $("#product-telfare-order-tbody").append(html);
            table_rowspan("#product-telfare-order-table", 2);
            table_rowspan("#product-telfare-order-table", 3);
            break;
        default:
            break;
    }
}

/**
 * 设置事件监听
 */
function setListener() {
    // 【添加产品】按钮点击事件
    chooseProductBtnClickHandler();
    // 【结算价格批量定价】按钮点击事件
    batchSetPriceBtnClickHandler();
    // 结算价格批量定价【提交】按钮点击事件
    batchSetPriceConfirmEvent();
    // 【销售价格批量定价】按钮点击事件
    batchSalePriceBtnClickHandler();
    // 销售价格批量定价【提交】按钮点击事件
    batchSalePriceConfirmEvent();
    //批量设置定时生效【提交】按钮点击事件
    batchQuarzConfirmEvent();
    //批量设置定时 按钮点击事件
    batchQuarzBtnClickHandler();
    
    // 订单提交
    orderSubmitBtnClickHandler();
    // radioButton点击事件
    radioBtnClickHandler();
    // 双向选择按钮组点击事件
    doubleSelectBtnClick();
    // 返回按钮点击事件
    poiaBackBtnClickHandler();
    //全选/反选checkbox点击事件
    checkBoxClickHandler();
}

/**
 * 【添加产品】按钮点击事件
 */
function chooseProductBtnClickHandler() {
    $("#choose-product").on('click', function(e) {
        e.preventDefault();
        // 取得合作伙伴对应的产品
        // 构造参数
        var radioProdType = $("input[type=radio]:checked").val();
        if (!radioProdType) {
            Feng.error("请选择产品类型");
            return;
        }
        var customerId = $("#customer-select").val();
        if (customerId == null || customerId == "") {
            Feng.error("请选择客户名称");
            return;
        }
        var url = "";
        var prodIds = [];
        if (prodOrderList != null) {
            $.each(prodOrderList, function(n, value) {
                prodIds.push(value.productId);
            });
        }
        //打开选择产品初始化
        $("#operatorCode").val("");
        $("#text").val("")
        
        if (isMarketing || isWYAdmin) {
            url = Feng.ctxPath + '/flowProductRemodel/flowProductRemodel?productType='+ radioProdType + '&customerId=' + customerId;
        } else {
            url = Feng.ctxPath + '/flowProductRemodel/flowProductRemodel?productType='+ radioProdType + '&customerId=' + customerId;
        }
        $.ajax({
            url : url,
            dataType : 'json',
            success : function(data, status) {
            	if ( data && data.code && data.code != 200 ) {
            		Feng.error(data.message ? data.message : "查询数据失败");
            		return;
            	}
            	var prodDataDB = data.concat();
                if (prodOrderList != null) {
                    for (var i = prodDataDB.length-1 ; i >= 0; i--) {
                        if ($.inArray(prodDataDB[i].productId, prodIds) >= 0) {
                            prodDataDB.splice(i, 1);
                        }
                    }
                }
                prodData = prodDataDB;
                proTotalData = prodDataDB;
                isSearchInit = true;
                showPartnerProdDialog();
            }
        });
    });
}

function showPartnerProdDialog() {
    $("#partner-prod-dialog").modal({
        backdrop: "static",
        keyboard: false
    });
    // 初始化双向选择列表
    initDoubleSelect();
    
}

/**
 * 添加按钮点击事件
 */
function prodAddBtnClickHandler() {
    var selVal = []; 
    rightSel.find("option").each(function() { 
        selVal.push(this.value); 
    });
    if (selVal.length == 0) {
        $("#product-error-span").fadeIn('slow');
        setTimeout(function() {
            $("#product-error-span").fadeOut('slow');
        }, 1500);
        return;
    }
    var prodDataTemp = proTotalData;
    // 初始化产品订单列表
    var prodOrderSelectedList = $.getSelectInfo(selVal, prodDataTemp);
    if (prodOrderList != null) {
        prodOrderList = prodOrderList.concat(prodOrderSelectedList);
    } else {
        prodOrderList = prodOrderSelectedList; 
    }
    $("#partner-prod-dialog").modal('hide');
    $.each(prodOrderSelectedList, function(n, value) {
        value.settlementPrice = value.productPrice != null ? value.productPrice : 0;
    });
    refreshProdOrderTable(prodOrderSelectedList, null);
    
}

$.getSelectInfo = function(selVal, prodDataTemp) {
    var arr = new Array();
    $.each(selVal, function(index, id) {
        $.each(prodDataTemp, function(n, value) {
            if (id == value.productId) {
                arr.push(value);
                return false;
            }
        });
        return true;
    });
    return arr;
};

/**
 * 刷新产品订单表
 */
function refreshProdOrderTable(dataList, flowType) {
    // 刷新产品订单Table
    initProductOrderTable(dataList, flowType);
    // 产品名称详情点击事件
    productNameLinkClickHandler();
    // 文本框输入限制
    inputNumberFormatHandler();
    // 删除按钮点击事件
    deleteBtnClickHandler();
}

/**
 * 【结算价格批量定价】按钮点击事件
 */
function batchSetPriceBtnClickHandler() {
    $("#batch-set-price").click(function(e) {
        e.preventDefault();
        var selectedCheckboxArr = $("input[type=checkbox]:checked");
        if (!selectedCheckboxArr || selectedCheckboxArr.length == 0) {
            Feng.error("请先选择需要批量定价的产品");
            return;
        }
        // 弹出模态框
        $("#batch-setprice-modal").modal({
            backdrop: "static",
            keyboard: false
        });
        $("#price-discount").val("");
        // 构造批量定价用数据
        selectedTrArr = [];
        $.each(selectedCheckboxArr, function(n, value) {
            selectedTrArr.push($(value).parents("tr"));
        });
    });
}

/**
 * 结算价格批量定价【提交】按钮点击事件
 */
function batchSetPriceConfirmEvent() {
    $("#batch-setprice-btn").click(function() {
        var discountVal = $("#price-discount").val();
        // 只允许输入1~100之间的整数
        var reg =  /^\d{1,8}\.?\d{0,2}$/;
        if (!reg.test(discountVal)) {
            $("#batch-error-span").text("请输入大于0不大于100的整数");
            $("#batch-error-span").fadeIn("slow");
            setTimeout(function() {
                $("#batch-error-span").fadeOut("slow");
            }, 1500);
            return;
        }
        $.each(selectedTrArr, function(index, nTr) {
            // 获取产品定价
            var productPriceVal = $(nTr).children().eq(5).text();
            // 设定结算价格
            if (!isNaN(productPriceVal)) {
                $(nTr).children().eq(6).children().val((Number(productPriceVal) * discountVal / 100).toFixed(2));
            } else {
                $(nTr).children().eq(6).children().val("");
            }
        });
        // 关闭模态框
        $("input[type=checkbox]:checked").removeAttr("checked");
        $("#batch-setprice-modal").modal("hide");
    });
}

/**
 * 【销售价格批量定价】按钮点击事件
 */
function batchSalePriceBtnClickHandler() {
//    $("#batch-sale-price").click(function(e) {
//        e.preventDefault();
//        var selectedCheckboxArr = $("input[type=checkbox]:checked");
//        if (!selectedCheckboxArr || selectedCheckboxArr.length == 0) {
//            Feng.error("请先选择需要批量定价的产品");
//            return;
//        }
//        // 弹出模态框
//        $("#batch-saleprice-modal").modal({
//            backdrop: "static",
//            keyboard: false
//        });
//        $("#sale-price-discount").val("");
//        // 构造批量定价用数据
//        selectedTrArr = [];
//        $.each(selectedCheckboxArr, function(n, value) {
//            selectedTrArr.push($(value).parents("tr"));
//        });
//    });
}

/**
 * 销售价格批量定价【提交】按钮点击事件
 */
function batchSalePriceConfirmEvent() {
    $("#batch-saleprice-btn").click(function() {
        var discountVal = $("#sale-price-discount").val();
        // 只允许输入1~100之间的整数
        var reg =  /^\d{1,8}\.?\d{0,2}$/;
        if (!reg.test(discountVal)) {
            $("#sale-error-span").text("请输入大于0不大于100的整数");
            $("#sale-error-span").fadeIn("slow");
            setTimeout(function() {
                $("#sale-error-span").fadeOut("slow");
            }, 1500);
            return;
        }
        $.each(selectedTrArr, function(index, nTr) {
            // 获取产品定价
            var productPriceVal = $(nTr).children().eq(5).text();
            // 设定销售价格
            if (!isNaN(productPriceVal)) {
                $(nTr).children().eq(7).children().val((Number(productPriceVal) * discountVal / 100).toFixed(2));
            } else {
                $(nTr).children().eq(7).children().val("");
            }
        });
        // 关闭模态框
        $("input[type=checkbox]:checked").removeAttr("checked");
        $("#batch-saleprice-modal").modal("hide");
    });
}


/**
 * 批量设置定时生效点击按钮
 */

function batchQuarzBtnClickHandler(){
	$("#batch-quarz-price").click(function(e) {
        e.preventDefault();
        var selectedCheckboxArr = $("input[type=checkbox]:checked");
        if (!selectedCheckboxArr || selectedCheckboxArr.length == 0) {
            Feng.error("请先选择需要设置定时的产品");
            return;
        }
        // 弹出模态框
        $("#batch-quarz-modal").modal({
            backdrop: "static",
            keyboard: false
        });
        $("#sale-price-discount").val("");
        // 批量设置定时时间
        selectedTrArr = [];
        $.each(selectedCheckboxArr, function(n, value) {
            selectedTrArr.push($(value).parents("tr"));
        });
    });
}


/**
 * 批量设置定时生效提交按钮
 */

function batchQuarzConfirmEvent(){
	 $("#batch-quarz-btn").click(function() {
		 	//获取定时时间
		    var quarzTime=$("#quarz-set-time").val();
		    //清空定时时间
		    $("#quarz-set-time").val("");
		    //给打上勾的产品赋值生效时间
		    $.each(selectedTrArr, function(index, nTr) {
	            // 获取产品定价
	            // 设定结算价格
	            if (isNaN(quarzTime)) {
	                $(nTr).children().eq(8).children().val(quarzTime);
	            } else {
	                $(nTr).children().eq(8).children().val("");
	            }
	        });
	        // 关闭模态框
	        $("input[type=checkbox]:checked").removeAttr("checked");
	        $("#batch-quarz-modal").modal("hide");
	    });
}








/**
 * 初始化双向选择列表
 */
function initDoubleSelect() {
    var radioProdType = $("input[type=radio]:checked").val();
    if (radioProdType == 1) {
    	var html = '<ul style="list-style-type:none;margin:0 0 10px 7px;cursor:pointer;padding:0px;"><li onclick="getProductByOperatorCode(\'-2\',this)"';
    		if("" == $("#operatorCode").val()){
    			html += 'class="many-selected"';
    		}
    		html += '>基础流量包</li>';
            html += '<ul class="sub_select" style="list-style-type:none;">';
        	html += '<li class="many-select';
        	if("YD" == $("#operatorCode").val()){
    			html += ' many-selected';
    		}
        	html +='" onclick="getProductByOperatorCode(\'YD\',this)">中国移动</li>';
        	html += '<li class="many-select';
        	if("LT" == $("#operatorCode").val()){
    			html += ' many-selected';
    		}
        	html += '" onclick="getProductByOperatorCode(\'LT\',this)">中国联通</li>';
        	html += '<li class="many-select';
        	if("DX" == $("#operatorCode").val()){
    			html += ' many-selected';
    		}
        	html += '" onclick="getProductByOperatorCode(\'DX\',this)">中国电信</li></ul></ul>';
        $("#poia-flow-type").html(html);
        $("#searchInput").show();
    } else if (radioProdType == 2) {
        $("#poia-flow-type").html("组合流量包");
        $("#searchInput").hide();
    } else if (radioProdType == 3) {
        $("#poia-flow-type").html("流量+产品");
        $("#searchInput").hide();
    }else if (radioProdType == 4) {
        $("#poia-flow-type").html("话费");
        $("#searchInput").hide();
    }
    $("#dltSource").empty();
    if(isSearchInit){
    	$("#dltTarget").empty();
    }
    $.each(prodData, function(n, value) {
        $("#dltSource").append("<option value='" + value.productId + "'>" + value.productName + "</option>")
    });
}

/**
 * 订单提交
 */
function orderSubmitBtnClickHandler() {
    $("#submitButton").click(function() {
    	submitHandler();
    });
}


function submitHandler() {
	var radioProdType = $("input[type=radio]:checked").val();
	var _value = $("#delivery-date-picker").val();
	if (radioProdType == 3 && (_value == "" || _value == null)) {
		Feng.error("请输入交货日期");
		return;
	} 
	if (_value && _value != "" && _value.lenght > 20) {
		Feng.error("交货日期格式不正确");
		return;
	}
	
	if ((isMarketing || isWYAdmin) && isBillIssued && $("#billing-type-select").val() == "") {
        Feng.error("请选择计费方式");
        return;
    }
    var finalProdList = prodOrderList;
    if (finalProdList == null || finalProdList.length == 0) {
        Feng.error("请选择要订购的产品");
        return;
    } else {
        if (checkInputValue()) {
            // 提交订单
            createOrderInfo();
            var _postData = $("#add-form").serializeJson();
            if (!_postData.customerId && $("#customer-select").val() != "") {
            	_postData.customerId = $("#customer-select").val();
            }
            $.ajaxSubmit(update_url, _postData, function(data) {
                if (data && data.code == 200) {
                	Feng.success("操作成功!");
                    closeCurrentDialog();
                } else {
                    Feng.error(data.message ? data.message : "操作失败");
                }
            }, $("#submitButton"));
        }
    }
}

/**
 * 关闭当前弹窗
 * @returns
 */
function closeCurrentDialog() {
	if ( window.parent.Customer != undefined && window.parent.Customer.layerIndex > -1 ){
		// 如果此弹窗时从 添加客户打开
		parent.layer.close(window.parent.Customer.layerIndex);
	} else if( window.parent.OrderInfo != undefined && window.parent.OrderInfo.layerIndex > -1 ) {
		// 订单列表进入
		window.parent.OrderInfo.table.refresh();// 刷新
		parent.layer.close(window.parent.OrderInfo.layerIndex);
	} else {
		
	}
	// TODO
}

/**
 * 构造订单信息对象
 */
function createOrderInfo() {
    if (pkId) {
        $("#orderId").val(pkId);
    } else {
        $("#orderId").val($("#orderNumberLbl").text());
    }
    var radioVal = $("input[type=radio]:checked").val();
    $("#orderType").val(radioVal == 3 ? 2 : 1);
    // 流量包订单
    if (radioVal != 3) {
        // 默认下发计费
        $("#billingType").val(2);
    } else {
        // 允许下发计费
        if (isBillIssued) {
            $("#billingType").val(parseInt($("#billing-type-select").val(), 10));
        } else {
            // 激活计费
            $("#billingType").val(1);
        }
    }
}

/**
 * 文本框输入限制
 */
function inputNumberFormatHandler() {
    /**$(".numberFormat").keypress(function(event) {
        var keyCode = event.which;
        if (keyCode == 8 || keyCode == 0 || keyCode == 46 || (keyCode >= 48 && keyCode <= 57))
            return true;
        else
            return false;
    });
    $(".numberNoPointFormat").keypress(function(event) {
        var keyCode = event.which;
        if (keyCode == 8 || keyCode == 0 || (keyCode >= 48 && keyCode <= 57))
            return true;
        else
            return false;
    });**/
}

/**
 * 删除按钮点击事件
 */
function deleteBtnClickHandler() {
    $(".deleteBtn").click(function() {
        // 解除合并的单元格
        $("tbody td[rowspan]").attr("rowspan","0");
        $("tbody td[style='display: none;']").removeAttr("style");
        
        var $btn = $(this);
        $.each(prodOrderList, function(n, value) {
            if ($btn.attr("attrid") == value.productId) {
                prodOrderList.splice(n, 1);
                return false;
            }
        });
        $btn.parent("td").parent("tr").remove();
        
        // 重新合并单元格
        table_rowspan("#product-order-table", 3);
        table_rowspan("#product-order-table", 4);
        table_rowspan("#product-combo-order-table", 3);
        table_rowspan("#product-combo-order-table", 4);
        table_rowspan("#product-plus-order-table", 2);
        table_rowspan("#product-plus-order-table", 3);
    });
}

/**
 * radioButton点击事件
 */
function radioBtnClickHandler() {
    $("input[type=radio]").change(function() {
        var radioVal = $("input[type=radio]:checked").val();
        if (radioVal == 3) {
            $(".flowplus-tbl-container").show();
            $("#poia-price-total").show();
        } else {
            $(".flowplus-tbl-container").hide();
            $("#poia-price-total").hide();
        }
    })
}

/**
 * 双向选择按钮组点击事件
 */
function doubleSelectBtnClick() {
    $("#toRight").click(function() {
        leftSel.find("option:selected").each(function() {
            $(this).remove().appendTo(rightSel);
        });
    });
    $("#toLeft").click(function() {
        rightSel.find("option:selected").each(function() {
            $(this).remove().appendTo(leftSel);
        });
    });
    leftSel.dblclick(function() {
        $(this).find("option:selected").each(function() {
            $(this).remove().appendTo(rightSel);
        });
    });
    rightSel.dblclick(function() {
        $(this).find("option:selected").each(function() {
            $(this).remove().appendTo(leftSel);
        });
    });
}

/**
 * 返回按钮点击事件
 */
function poiaBackBtnClickHandler() {
    $(".poia-back-btn").click(function(e) {
//        location.href = Feng.ctxPath + '/pages/orderInfo_list.shtml';
    	// 关闭当前弹窗
    	closeCurrentDialog();
    });
}

/**
 * 产品名称详情点击事件
 */
function productNameLinkClickHandler() {
    $(".product-name-detail").click(function(e) {
        var flowProductId = $(this).attr("value");
        $.ajax({
            url : Feng.ctxPath + '/flowProductRemodel/getProductById?productId=' + flowProductId,
            cache : false,
            dataType : 'json',
            success : function(data, status) {
            	if ( data && data.code && data.code != 200 ) {
            		Feng.error("获取数据错误");
            		return;
            	}
                showProdDetailDialog(e);
                initProdDetail(data);
            }
        });
    });
}

/**
 * 显示产品详细对话框
 * @param e
 */
function showProdDetailDialog(e) {
    e.preventDefault();
    $("#prod-detail-dialog").modal({
        backdrop: "static",
        keyboard: false
    });
}

/**
 * 初始化产品详情
 */
function initProdDetail(prodDetail) {
    $("#pdProdName").text(prodDetail.productName);
    $("#pdProdCode").text(prodDetail.productCode);
    var prodType = prodDetail.productType;
    if (prodType == 1) {
        $("#pdProdType").text("基础流量包");
    } else if (prodType == 2) {
        $("#pdProdType").text("组合流量包");
    } else if (prodType == 3) {
        $("#pdProdType").text("流量+产品");
    }else if (prodType == 4) {
        $("#pdProdType").text("话费");
    }
    if (prodDetail.productPrice == null) {
        $("#pdProdPrice").text("￥0");
    } else {
        $("#pdProdPrice").text("￥" + prodDetail.productPrice);
    }
    $("#pdProdTotalPrice").text("￥" + floatMul(prodDetail.productPrice != null ? prodDetail.productPrice : 0, prodDetail.productCount));
    $("#pdProdDescription").text(prodDetail.productDesc);
    $("#pdFlowAmount").text(prodDetail.flowPackageInfo.flowAmount);
    var operatorCode = prodDetail.flowPackageInfo.operatorCode;
    if (operatorCode == "YD") {
        $("#pdOperatorCode").text("移动");
    } else if (operatorCode == "LT") {
        $("#pdOperatorCode").text("联通");
    } else if (operatorCode == "DX") {
        $("#pdOperatorCode").text("电信");
    }
    $("#pdZone").text(prodDetail.zoneDesc);
}

/**
 * 添加全部
 */
function AddAll(ObjSource, ObjTarget) {
    // 目标列表的HTML加上原列表的所有HTML
    ObjTarget.append(ObjSource.html());
    // 原列表清空
    ObjSource.empty();
}

/**
 * 浮点数相乘
 */
function floatMul(arg1, arg2) {
    if (arg1 == null || arg2 == null) {
        return 0;
    }
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString(), arr;
    arr = s1.split(".");
    if (arr.length > 1) {
        m += arr[1].length;
    }
    arr = s2.split(".");
    if (arr.length > 1) {
        m += arr[1].length;
    }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}

/**
 * 浮点数相加
 */
function floatAdd(arg1, arg2) {
    var r1 = 0, r2 = 0, m, arr;
    arr = arg1.toString().split(".");
    if (arr.length > 1) {
        r1 = arr[1].length;
    }
    arr = arg2.toString().split(".");
    if (arr.length > 1) {
        r2 = arr[1].length;
    }
    m = Math.pow(10, Math.max(r1, r2));
    return (floatMul(arg1, m) + floatMul(arg2, m)) / m;
}

/**
 * 逗号分隔
 * @param num
 * @returns
 */
function commaDelimited(num) {
    return String(num).replace(/(\d)(?=(\d\d\d)+(?!\d))/g, '$1,');
}

function isNum(s) {
    var r = /^\d+(\.\d+)?$/;
    return r.test(s);
}

/**
 * 更新订单的情况下的初始化
 */
function initUpdateViews() {
    if (pkId) {
        $('#updId').val(pkId);
        
        var ajax = new $ax(view_url, function (data) {
	        if ( data && data.code && data.code != 200 ) {
	        	Feng.error(data.message ? data.message : "获取数据失败！")
	        	return;
	        }
	        $.dataInput($('.form-rollback'), data);
            initModifyViewStatus(data);
	    }, function (data) {
	        Feng.error("删除失败!");
	    });
	    ajax.set( {'orderId': pkId} );
	    ajax.start();
    }
}

/**
 * 初始化修改页面的值状态
 */
function initModifyViewStatus(data) {
    var status = data.status;
    if (status == 1) {
        $("label[name='status']").text("待生效");
    } else if (status == 3) {
        $("label[name='status']").text("暂停");
    }

    var obj = {};
    obj["id"] = data.customerId;
    obj["text"] = data.customerName;
    $("#customer-select").select2("data", obj);
    $("#customer-select").select2("disable");
    $("#customer-select").trigger("change");
    
    var prodType = ""
    if (data.orderDetailList[0] != null) {
        prodType = data.orderDetailList[0].productType;
    }
    if (prodType == 1) { 
        $("input[type=radio][value=1]").attr("checked",'checked');
    } else if (prodType == 2) {
        $("input[type=radio][value=2]").attr("checked",'checked');
    } else if (prodType == 3) {
        $("input[type=radio][value=3]").attr("checked",'checked');
        
        // 计费方式
        $.ajaxSubmit(check_url, {
            'customerId': data.customerId
        }, function(rtn) {
            isBillIssued = rtn.isBillIssued;
            // 允许下发计费
            if (isBillIssued) {
                $(".flowPlusAndBillTypeIssued").show();
                $(".not-update").attr("disabled", "disabled");
                $("#billing-type-select").val(data.billingType);
            }
        });
    }
    $("input[type=radio]").trigger("change");
    if ($("input[type=radio]:checked").val() == 3) {
        $(".flow-plus-show").show();
        $(".flowPackageDiaplay").hide();
        $(".flowPlusDiaplay").show();
    } else {
        $(".flowPackageDiaplay").show();
        $(".flowPlusDiaplay").hide();
        if (!isMarketing && !isWYAdmin) {
            $(".combo-package").hide();
        }
    }
    
    $("#delivery-date-picker").val(data.deliveryTime);
    
    var prodIds = [];
    var odList = data.orderDetailList;
    $.each(odList, function(n, value) {
        prodIds.push(value.productId);
    });
    if (prodIds.length > 0) {
        $.ajax({
            url : Feng.ctxPath + '/flowProductRemodel/getFlowProductRemodel?productIds=' + prodIds,
            dataType : 'json',
            success : function(data, status) {
            	if ( data && data.code && data.code != 200 ) {
            		Feng.error("获取数据失败！");
            		return;
            	}
                prodOrderList = JSON.parse(data.data);
                $.each(prodOrderList, function(n, value) {
                    $.each(odList, function(index, od) {
                        if (value.productId == od.productId) {
                            value.orderDetailId = od.orderDetailId;
                            value.productCount = od.amount;
                            value.settlementAmount = od.price;
                            value.settlementPrice = od.settlementPrice;
                            value.quarzTime=od.quarzTime;
                            value.endPrice=od.endPrice;
                            value.salePrice=od.salePrice;
                        }
                    });
                });
                var baseFlowPackage = [], comboFlowPackage = [],telFarePackage = [];
                $.each(prodOrderList, function(n, value) {
                    // 基础包
                    if (value.productType == 1) {
                        baseFlowPackage.push(value);
                    } else if (value.productType == 2) {
                        // 组合包
                        comboFlowPackage.push(value);
                    }else if (value.productType == 4) {
                        // 组合包
                        telFarePackage.push(value);
                    }
                });
                // 流量加
                if ($("input[type=radio]:checked").val() == 3) {
                    refreshProdOrderTable(prodOrderList, null);
                } else {
                    refreshProdOrderTable(baseFlowPackage, "1");
                    refreshProdOrderTable(comboFlowPackage, "2");
					refreshProdOrderTable(telFarePackage, "4");
                }
            }
        });
    }
}

//输入框内容校验
function checkInputValue() {
    var successFlag = true;
    if ($("input[type=radio]:checked").val() == 3) {
        $.each($(".prodCountInput"), function(n, obj) {
            var value = $(obj).val();
            if (value == null || value == "") {
                Feng.error("请输入订购数量");
                successFlag = false;
                return false;
            }
            var reg = /^([1-9]\d*)$/;
            if (!reg.test(value)) {
                Feng.error("订购数量必须为大于0的整数，请重新输入。");
                successFlag = false;
                return false;
            }
        });
    }
    $.each($(".settlementAmountInput"), function(n, obj) {
        var value = $(obj).val();
        if (value == null || value == "") {
            Feng.error("请输入结算价格");
            successFlag = false;
            return false;
        }
        if (value == 0) {
            Feng.error("结算价格不能为0");
            successFlag = false;
            return false;
        }
        var doubleReg = /^\d{1,8}\.?\d{0,2}$/;
        if (!doubleReg.test(value) || value.length > 8) {
            Feng.error("最多只能有八位整数和两位小数，请重新输入。");
            successFlag = false;
            return false;
        }
    });
    $.each($(".productPriceInput"), function(n, obj) {
        var value = $(obj).val();
        if (value == null || value == "") {
            Feng.error("请输入销售价格");
            successFlag = false;
            return false;
        }
        if (value == 0) {
            Feng.error("销售价格不能为0");
            successFlag = false;
            return false;
        }
        var doubleReg = /^\d{1,8}\.?\d{0,2}$/;
        if (!doubleReg.test(value) || value.length > 8) {
            Feng.error("最多只能有八位整数和两位小数，请重新输入。");
            successFlag = false;
            return false;
        }
    });
    return successFlag;
}

/**
 * desc : 合并指定表格（表格id为table_id）指定列（列数为table_colnum）的相同文本的相邻单元格
 * 
 * @table_id 表格id : 为需要进行合并单元格的表格的id。如在HTMl中指定表格 id="data" ，此参数应为 #data
 * @table_colnum : 为需要合并单元格的所在列.参考jQuery中nth-child的参数.若为数字，从最左边第一列为1开始算起;"even"
 *               表示偶数列;"odd" 表示奇数列; "3n+1" 表示的列数为1、4、7、......
 * @table_minrow ? : 可选的,表示要合并列的行数最小的列,省略表示从第0行开始 (闭区间)
 * @table_maxrow ? : 可选的,表示要合并列的行数最大的列,省略表示最大行列数为表格最后一行 (开区间)
 */
function table_rowspan(table_id, table_colnum) {
    if (table_colnum == "even") {
        table_colnum = "2n";
    } else if (table_colnum == "odd") {
        table_colnum = "2n+1";
    } else {
        table_colnum = "" + table_colnum;
    }
    var cols = [];
    var all_row_num = $(table_id + " tr td:nth-child(1)").length;
    var all_col_num = $(table_id + " tr:nth-child(1)").children().length;
    if (table_colnum.indexOf("n") == -1) {
        cols[0] = table_colnum;
    } else {
        var n = 0;
        var a = table_colnum.substring(0, table_colnum.indexOf("n"));
        var b = table_colnum.substring(table_colnum.indexOf("n") + 1);
        // alert("a="+a+"b="+(b==true));
        a = a ? parseInt(a) : 1;
        b = b ? parseInt(b) : 0;
        // alert(b);
        while (a * n + b <= all_col_num) {
            cols[n] = a * n + b;
            n++;
        }
    }
    var table_minrow = arguments[2] ? arguments[2] : 0;
    var table_maxrow = arguments[3] ? arguments[3] : all_row_num + 1;
    var table_firsttd = "";
    var table_currenttd = "";
    var table_SpanNum = 0;
    for (var j = 0; j < cols.length; j++) {
        $(table_id + " tr td:nth-child(" + cols[j] + ")").slice(table_minrow, table_maxrow).each(function(i) {
            var table_col_obj = $(this);
            if (table_col_obj.html() != "&nbsp;") {
                if (i == 0) {
                    table_firsttd = $(this);
                    table_SpanNum = 1;
                } else {
                    table_currenttd = $(this);
                    if (table_firsttd.text() == table_currenttd.text()) {
                        table_SpanNum++;
                        table_currenttd.hide(); // remove();
                        table_firsttd.attr("rowSpan", table_SpanNum);
                    } else {
                        table_firsttd = $(this);
                        table_SpanNum = 1;
                    }
                }
            }
        });
    }
}

/**
 * desc : 合并指定表格（表格id为table_id）指定行（行数为table_rownum）的相同文本的相邻单元格
 * 
 * @table_id 表格id : 为需要进行合并单元格的表格的id。如在HTMl中指定表格 id="data" ，此参数应为 #data
 * @table_rownum : 为需要合并单元格的所在行.参考jQuery中nth-child的参数.若为数字，从最左边第一列为1开始算起;"even"
 *               表示偶数行;"odd" 表示奇数行; "3n+1" 表示的行数为1、4、7、......
 * @table_mincolnum ? : 可选的,表示要合并行中的最小列,省略表示从第0列开始(闭区间)
 * @table_maxcolnum ? : 可选的,表示要合并行中的最大列,省略表示表格的最大列数(开区间)
 */
function table_colspan(table_id, table_rownum) {
    // if(table_maxcolnum == void 0){table_maxcolnum=0;}
    var table_mincolnum = arguments[2] ? arguments[2] : 0;
    var table_maxcolnum;
    var table_firsttd = "";
    var table_currenttd = "";
    var table_SpanNum = 0;
    $(table_id + " tr:nth-child(" + table_rownum + ")").each(function(i) {
        table_row_obj = $(this).children();
        table_maxcolnum = arguments[3] ? arguments[3] : table_row_obj.length;
        table_row_obj.slice(table_mincolnum, table_maxcolnum).each(function(i) {
            if (i == 0) {
                table_firsttd = $(this);
                table_SpanNum = 1;
            } else if ((table_maxcolnum > 0) && (i > table_maxcolnum)) {
                return "";
            } else {
                table_currenttd = $(this);
                if (table_firsttd.text() == table_currenttd.text()) {
                    table_SpanNum++;
                    if (table_currenttd.is(":visible")) {
                        table_firsttd.width(parseInt(table_firsttd.width()) + parseInt(table_currenttd.width()));
                    }
                    table_currenttd.hide(); // remove();
                    table_firsttd.attr("colSpan", table_SpanNum);
                } else {
                    table_firsttd = $(this);
                    table_SpanNum = 1;
                }
            }
        });
    });
}

//全选/反选checkbox点击事件
function checkBoxClickHandler() {
    $(".allSelect").click(function() {
        $(this).closest("table").find("input[type='checkbox']").prop("checked", $(this).prop("checked"));
    });
}

/*按运营商分类产品*/
function getProductByOperatorCode(operatorCode, obj) {
	if (obj != null) {
		$(".many-select").removeClass("many-selected");
		$(obj).addClass("many-selected");
	}
	// 取得合作伙伴对应的产品
    // 构造参数
    var radioProdType = $("input[type=radio]:checked").val();
    if (!radioProdType) {
        Feng.error("请选择产品类型");
        return;
    }
    var customerId = $("#customer-select").val();
    if (customerId == null || customerId == "") {
        Feng.error("请选择客户名称");
        return;
    }
    var url = "";
    var prodIds = [];
    if (prodOrderList != null) {
        $.each(prodOrderList, function(n, value) {
            prodIds.push(value.productId);
        });
    }
    var rightProducts = $("#dltTarget").val();
    var rightProdIds = [];
    if (rightProducts != null) {
    	for(var i=0;i<rightProducts.length;i++){
    		rightProdIds.push(parseInt(rightProducts[i]));
    	}
    }
    //增加搜索条件
    var text = $("#text").val();//输入框
	if(operatorCode == '-1'){
		operatorCode = $("#operatorCode").val();
	}else if(operatorCode == '-2'){
		$("#operatorCode").val("");
		operatorCode = $("#operatorCode").val();
	}else{
		$("#operatorCode").val(operatorCode);
	}
    
    if (isMarketing || isWYAdmin) {
        url = Feng.ctxPath + '/flowProductRemodel/flowProductRemodel?productType='+ radioProdType + '&customerId=' + customerId + "&operatorCode=" + operatorCode + "&text=" + text;
    } else {
        url = Feng.ctxPath + '/flowProductRemodel/flowProductRemodel?productType='+ radioProdType + '&customerId=' + customerId + "&operatorCode=" + operatorCode + "&text=" + text;
    }
    $.ajax({
        url : url,
        dataType : 'json',
        success : function(data, status) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error("获取数据错误");
        		return;
        	}
        	var prodDataDB = data.concat();
            if (prodOrderList != null) {
                for (var i = prodDataDB.length-1 ; i >= 0; i--) {
                    if ($.inArray(prodDataDB[i].productId, prodIds) >= 0) {
                        prodDataDB.splice(i, 1);
                    }
                }
            }
            //剔除右边已选的
            if (rightProducts != null) {
                for (var i = prodDataDB.length-1 ; i >= 0; i--) {
                    if ($.inArray(prodDataDB[i].productId, rightProdIds) >= 0) {
                        prodDataDB.splice(i, 1);
                    }
                }
            }
            prodData = prodDataDB;
            isSearchInit = false;
            showPartnerProdDialog();
        }
    });
}