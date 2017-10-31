/**
 *  客户产品配置js
 */



var FlowProductDialog = {
	isSearchInit : false,
	dataList : [],
	selectedList : [],
	tempList : [],
	currentType : 'ALL',
	leftSel : $("#dltSource"),
	rightSel : $("#dltTarget"),
	url: Feng.ctxPath + '/flowProductRemodel/flowProductRemodel?productType=1&partnerInfoId='
};

FlowProductDialog.showDialog = function() {
    $("#partner-prod-dialog").modal({
        backdrop: "static",
        keyboard: false
    });
    // 初始化双向选择列表
    this.initDoubleSelect();
}

FlowProductDialog.clearData = function() {
	this.dataList = [];
	this.selectedList = [];
	this.tempList = [];
	$("#showProduct").empty();
}

/**
 * 双向选择按钮组点击事件
 */
FlowProductDialog.doubleSelectBtnClick = function() {
    $("#toRight").click(function() {
    	FlowProductDialog.leftSel.find("option:selected").each(function() {
    		FlowProductDialog.tempList.push(getProductInfoInArray(this.value, FlowProductDialog.dataList));// 临时区域
            $(this).remove().appendTo(FlowProductDialog.rightSel);
        });
    });
    $("#toLeft").click(function() {
    	FlowProductDialog.rightSel.find("option:selected").each(function() {
    		FlowProductDialog.removeFromTemp(this.value);
    		
    		if ( $(this).attr("d-type") == FlowProductDialog.currentType )
    			$(this).remove().appendTo(FlowProductDialog.leftSel);
    		else
    			$(this).remove();
        });
    });
    FlowProductDialog.leftSel.dblclick(function() {
        $(this).find("option:selected").each(function() {
        	FlowProductDialog.tempList.push(getProductInfoInArray(this.value, FlowProductDialog.dataList));// 临时区域
           	$(this).remove().appendTo(FlowProductDialog.rightSel);
        });
    });
    FlowProductDialog.rightSel.dblclick(function() {
        $(this).find("option:selected").each(function() {
        	FlowProductDialog.removeFromTemp(this.value);
        	
        	if ( $(this).attr("d-type") == FlowProductDialog.currentType )
            	$(this).remove().appendTo(FlowProductDialog.leftSel);
        	else 
        		$(this).remove()
        });
    });
    
    /**输入框绑定事件**/
	$('#text').bind('input propertychange',function(){
		getDataAndOpenDialog("-1");
    });
}

// 从临时选择区中移除指定的信息
FlowProductDialog.removeFromTemp = function(productId) {
	for (var i = 0; i<FlowProductDialog.tempList.length; i++) {
    	if ( FlowProductDialog.tempList[i].productId == productId ) {
    		FlowProductDialog.tempList.splice(i, 1);// remove
    		break;
    	}
    }
}

/**
 * 从已选列表中删除指定产品信息
 */
FlowProductDialog.removeFromSelectedList = function(productId) {
	for (var i = 0; i<FlowProductDialog.selectedList.length; i++) {
    	if ( FlowProductDialog.selectedList[i].productId == productId ) {
    		FlowProductDialog.selectedList.splice(i, 1);// remove
    		break;
    	}
    }
}

// /**
//  * 添加全部
//  */
// FlowProductDialog.AddAll = function(ObjSource, ObjTarget) {
//     // 目标列表的HTML加上原列表的所有HTML
//     ObjTarget.append(ObjSource.html());
//     // 原列表清空
//     ObjSource.empty();
// }

/**
 * 是否在临时选择区中存在
 */
FlowProductDialog.isExistInTemp = function(productId) {
	for (var i = 0; i<FlowProductDialog.tempList.length; i++) {
    	if ( FlowProductDialog.tempList[i].productId == productId ) {
    		return true;
    	}
    }
	return false;
}


FlowProductDialog.clickOperatorCode = function(operatorCode, obj) {
	if (obj != null) {
		$(".many-select").removeClass("many-selected");
		$(obj).addClass("many-selected");
	}
	isSearchInit = false;
	getDataAndOpenDialog(operatorCode);
}

/**
 * 初始化双向选择列表
 */
FlowProductDialog.initDoubleSelect = function() {
	FlowProductDialog.currentType = 'ALL'
   	var html = '<ul style="list-style-type:none;margin:0 0 10px 7px;cursor:pointer;padding:0px;"><li onclick="FlowProductDialog.clickOperatorCode(\'-2\',this)"';
	if("" == $("#operatorCode").val()){
		html += 'class="many-selected"';
	}
	html += ' d-type="ALL">基础流量包</li>';
    html += '<ul class="sub_select" style="list-style-type:none;">';
   	html += '<li class="many-select';
   	if("YD" == $("#operatorCode").val()){
		html += ' many-selected';
		FlowProductDialog.currentType = "YD";
	}
   	html +='" onclick="FlowProductDialog.clickOperatorCode(\'YD\',this)" d-type="YD">中国移动</li>';
   	html += '<li class="many-select';
   	if("LT" == $("#operatorCode").val()){
		html += ' many-selected';
		FlowProductDialog.currentType = "LT";
	}
   	html += '" onclick="FlowProductDialog.clickOperatorCode(\'LT\',this)" d-type="LT">中国联通</li>';
   	html += '<li class="many-select';
   	if("DX" == $("#operatorCode").val()){
		html += ' many-selected';
		FlowProductDialog.currentType = "DX";
	}
   	html += '" onclick="FlowProductDialog.clickOperatorCode(\'DX\',this)" d-type="DX">中国电信</li></ul></ul>';
   	$("#poia-flow-type").html(html);
   	$("#searchInput").show();
    
    $("#dltSource").empty();
    
    if ( isSearchInit ) $("#dltTarget").empty();
   	
    $.each(this.dataList, function(n, value) {
    	if ( !isExistInArray(value.productId, FlowProductDialog.selectedList) 
    			&& !FlowProductDialog.isExistInTemp(value.productId) )
        	$("#dltSource").append("<option value='" + value.productId + "' d-type='"+FlowProductDialog.currentType+"'>" + value.productName + "</option>")
    });
    
}

function isExistInArray(productId, array) {
	for (var i=0; i<array.length; i++) {
		if ( array[i].productId == productId ) {
			return true;
		}
	}
	return false;
}

function getProductInfoInArray(productId, array) {
	for (var i=0; i<array.length; i++) {
		if ( array[i].productId == productId ) {
			return array[i];
		}
	}
	return null;
}

/**
 * 添加按钮点击事件
 */
FlowProductDialog.okClickHandler = function() {
    if (FlowProductDialog.tempList.length == 0) {
        $("#product-error-span").fadeIn('slow');
        setTimeout(function() {
            $("#product-error-span").fadeOut('slow');
        }, 1500);
        return;
    }
    $("#partner-prod-dialog").modal('hide');
    
    FlowProductDialog.selectedList = FlowProductDialog.selectedList.concat(FlowProductDialog.tempList);
    FlowProductDialog.tempList = [];// 清空临时区
    refreshProdOrderTable();
}

/**
 * 刷新table 
 */
var productIndex = -1;
function refreshProdOrderTable() {
	if ( !FlowProductDialog.selectedList || FlowProductDialog.selectedList.length < 1 )
		return;
	
	$("#checkAll").prop("checked", false);
	var _table =$("#showProduct");
	
	for (var i = 0; i<FlowProductDialog.selectedList.length; i++) {
		var item = FlowProductDialog.selectedList[i];
		if (!item) continue;
		
		// 已经存在table中则忽略
		if ($("#showProduct tr[d-productId='" + item.productId + "']").length > 0)
			continue;
		
		productIndex += 1;
		var _html = "<tr d-index='"+productIndex+"' d-productId='" + item.productId + "' >";
		_html += "<td class='center'><label><input type='checkbox' class='ace' value='"+ item.productId +"' onclick='setCheck(this)'><span class='lbl'></span></label></td>"
		
		_html += "<td>" + $.htmlspecialchars(item.productName) + "</td>";
		
		_html += "<td>" + item.productCode + "</td>";
		
		_html += "<td>" + item.operatorCodeDesc + "</td>";
		
		_html += "<td>" + item.zoneDesc + "</td>";
		
		_html += "<td d-price='"+ item.productPrice +"'>" + item.productPrice + "</td>";
		
		_html += "<td><input d-tag='settlementAmount' id='settlementAmount-input-"+ productIndex +"' style=\"width:90px;\" attrid='" + item.productId + "' name='flowProductInfoList["+ productIndex +"].settlementAmount' class='numberFormat settlementAmountInput' value='" + item.settlementAmount + "' maxlength='10' onblur='update2DiscountOnchange(this, " + item.productPrice + ")'/></td>";
		
		_html += "<td><input d-tag='discount' id='discount-input-"+ productIndex +"' type='text' style='width:80px;' value='" + ( item.settlementAmount*100/item.productPrice).toFixed(2) + "' maxlength='8' onblur='updatePriceOnchange(this," + item.productPrice + ")'/></td>";
		
		// 添加一些隐藏域
		_html += "<input type='hidden' name='flowProductInfoList["+productIndex+"].productId' value='"+ item.productId +"' />";
		_html += "<input type='hidden' name='flowProductInfoList["+productIndex+"].packageId' value='"+ item.packageId +"' />";
		_html += "<input type='hidden' name='flowProductInfoList["+productIndex+"].productName' value='"+ item.productName +"' />";
		_html += "<input type='hidden' name='flowProductInfoList["+productIndex+"].productCount' value='"+ item.productCount +"' />";
		
		_html += "</tr>";
		_table.append(_html);
	}
	
}

function toOpenDialog(operatorCode) {
	isSearchInit = true;
	FlowProductDialog.tempList = [];// 每次重新打开dailog清空临时选择区
	$("#text").val("");
	getDataAndOpenDialog(operatorCode);
}

/**
 * 请求数据并打开弹框
 */
function getDataAndOpenDialog(operatorCode) {
	
	var _partnerId = $("#partnerId").val();
	
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
    
	var _url = FlowProductDialog.url + _partnerId 
		+ "&operatorCode=" + operatorCode 
		+ "&text=" + text 
		+ "&customerId=" + $("#customerId").val();
    
    $.ajax({
        url : _url,
        dataType : 'json',
        success : function(data, status) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data.message ? data.message : "查询数据失败");
        		return;
        	}
        	FlowProductDialog.dataList = [];
            
            for ( var i=0; i<data.length; i++ ) {
            	FlowProductDialog.dataList[i] = 
            		{
            			index: i,
            			productId: data[i].productId, 
            			packageId: data[i].packageId, 
            			productName: data[i].productName, 
            			productCode: data[i].productCode, 
            			operatorCode: data[i].operatorCode, 
            			operatorCodeDesc: data[i].operatorCodeDesc, 
            			productPrice: data[i].productPrice, 
            			zone: data[i].zone, 
            			zoneDesc: data[i].zoneDesc, 
            			productCount: 0, 
            			settlementAmount: data[i].productPrice
            		};
            } 
            FlowProductDialog.showDialog();
        }
    });
}



FlowProductDialog.doubleSelectBtnClick();

$("#partnerId").on("change", function(){
	FlowProductDialog.clearData();
});

// 全选 - 不选
$("#checkAll").on("click", function(){
	$("#showProduct input[type=checkbox]").prop("checked", $(this).prop("checked"))
});

function setCheck(subCheck){
	if ($(subCheck).prop("checked")) {
		console.log("---");
		var _f = $("#showProduct input[type=checkbox]").length == $("#showProduct input[type=checkbox]:checked").length;
		console.log(_f);
		$("#checkAll").prop("checked", _f);
	} else {
		$("#checkAll").prop("checked", false);
	}
}

/**
 * 获取选中的产品列表
 */
function getSelectRows() {
	return $("#showProduct input[type=checkbox]:checked").parent().parent().parent("tr");// --label -> td -> tr
}

function deleteProducts() {
	var _rows = getSelectRows();
	if ( !_rows || _rows.length <= 0 ) {
		Feng.info("请勾选需要删除的数据");
		return ;
	}
	for (var i=0; i<_rows.length; i++) {
		var _o = _rows[i];
		FlowProductDialog.removeFromSelectedList($(_o).attr("d-productId"));
		$(_o).remove();
	}
}


function setPriceByBatch() {
	var _rows = getSelectRows();
	if ( !_rows || _rows.length <= 0 ) {
		Feng.info("请勾选需要批量设价格的数据");
		return ;
	}
	// 弹出模态框 
	$("#batch-saleprice-modal").modal({
	     backdrop: "static",
	     keyboard: false
	 });
	 $("#sale-price-discount").val("");
}

// 批量修改价格 确定按钮
$("#batch-saleprice-btn").click(function() {
    var discountVal = $("#sale-price-discount").val();
    // 只允许输入1~100之间的整数
    var reg =  /^\d{1,3}(\.\d{1,2})?$/;
    if (!reg.test(discountVal) || Number(discountVal) > 100) {
        $("#sale-error-span").text("请输入0（不包含）到100的整数或小数（最多两位小数）");
        $("#sale-error-span").fadeIn("slow");
        setTimeout(function() {
            $("#sale-error-span").fadeOut("slow");
        }, 1500);
        return;
    }
    $.each(getSelectRows(), function(index, nTr) {
        // 获取产品定价
        var productPriceVal = $(nTr).find("td[d-price]").attr("d-price");
        $(nTr).find("input[d-tag='discount']").val(discountVal);
        // 设定销售价格
        if (!isNaN(productPriceVal)) {
            $(nTr).find("input[d-tag='settlementAmount']").val((Number(productPriceVal) * discountVal / 100).toFixed(2));
        } else {
        	$(nTr).find("input[d-tag='settlementAmount']").val("");
        }
    });
    // 关闭模态框
    $("#batch-saleprice-modal").modal("hide");
});

/**
 * 更改价格自动更新折扣
 */
function update2DiscountOnchange(thisObj, price) {
	var _value = $(thisObj).val();
	if ( !/^\d+(\.\d{1,2})?$/.test(_value) ) {
		Feng.error("请输入一个合法的数字（最多两位小数）");
		return ;
	}
	if ( Number(_value) <= 0 ) {
		Feng.error("必须大于0");
		return;
	}
	$("#" + $(thisObj).attr("id").replace("settlementAmount", "discount")).val((Number(_value)*100/Number(price)).toFixed(2));
}

/**
 * 更改折扣自动更新价格
 */
function updatePriceOnchange(thisObj, price) {
	var _value = $(thisObj).val();
	if ( !/^\d{1,4}(\.\d{1,2})?$/.test(_value) ) {
		Feng.error("请输入一个合法的数字（整数位最多4位，小数位最多2位）");
		return ;
	}
	if ( Number(_value) <= 0 ) {
		Feng.error("必须大于0");
		return;
	}
	$("#" + $(thisObj).attr("id").replace("discount", "settlementAmount")).val((Number(_value)*Number(price)/100).toFixed(2));
}

/**
 * 若是编辑模式下显示当前客户配置的产品信息
 * @returns
 */
function getCustomerProductsAndShow() {
	var _customerId = $("#customerId").val();
	// 有值  说明是编辑模式
	if (!/^\d+$/.test(_customerId)) {
		return;
	}
	//提交信息
    var ajax = new $ax(Feng.ctxPath + "/orderInfo/getByCustomer?customerId=" + _customerId, function(data){
    	if ( data && data.code != 200 ) {
    		Feng.error(data && data.message ? data.message : "查询客户产品配置信息失败！");
    		return;
    	}
    	
    	if ( data && data.data.orderDetailList && data.data.orderDetailList.length <= 0 )
    		return;// no data
    	
    	if ( data.data.orderType != 1 ) {// 如果订单不是流量包类型就不展示
    		return;
    	}
    	
    	$("#orderId").val(data.data.orderIdStr);
    	for (var i=0; i<data.data.orderDetailList.length; i++) {
    		var _item = data.data.orderDetailList[i];
    		FlowProductDialog.selectedList[i] = 
	    		{
	    			index: i,
	    			productId: _item.productId, 
	    			packageId: _item.packageId, 
	    			productName: _item.productName, 
	    			productCode: _item.productCode, 
	    			operatorCode: _item.operatorCode, 
	    			operatorCodeDesc: _item.operatorCodeDesc, 
	    			productPrice: _item.productPrice, 
	    			zone: _item.zone, 
	    			zoneDesc: _item.zoneDesc, 
	    			productCount: 0, 
	    			settlementAmount: _item.price
	    		};
    	}
    	refreshProdOrderTable();
    },function(data){
        Feng.error("查询产品配置信息错误！");
        console.error(data);
    });
    ajax.set(this.customerInfoData);
    ajax.start();
}

$(function(){
	
	// 如果是编辑模式  显示当前客户配置的产品列表
	getCustomerProductsAndShow();
	
});
