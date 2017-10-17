var view_url = Feng.ctxPath + '/orderInfo/get.ajax';
var pkId = getP('orderId');

var baseTableId = "#base-detail-table";
//var comboTableId = "#combo-detail-table";
//var telFareTableId = "#telFare-detail-table";
//var plusTableId = "#plus-detail-table";
var orderRecordTableId = "#order-record-table";
var isWYAdmin = false;

$(function() {
	pOILInit();
});

function pOILInit() {
    $.ajax({
        url : Feng.ctxPath + '/orderInfo/orderInfoViewInit',
        dataType : 'json',
        success : function(data, status) {
            isWYAdmin = data.isWYAdmin;
            initStatus();
        }
    });
}

function initStatus() {
    if (pkId) {
    	var ajax = new $ax(view_url, function(data){
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data.message ? data.message : "获取数据失败！");
        		return;
        	}
        	// 初始化页面的值
            initViewsData(data)
            setTimeout(function() {
            	// 初始化客户订单历史记录
                initOrderDealRecord();
            	// 初始化产品详细table
            	initProductDetailTable(data);
			}, 600);
        },function(data){
            Feng.error("添加失败!");
        });
        ajax.set( {'orderId': pkId});
        ajax.start();
    } else {
        Feng.error('参数错误!');
    }
}

/**
 * 初始化页面的值
 */
function initViewsData(data) {
    $.dataInput($('.form-control-static'), data);
    if (data.orderType == 2) {
        $(".pOIVFlowPlus").css("display", "block");
        $("p[name='orderType']").text("流量+产品");
    } else {
        $(".pOIVFlowPlus").css("display", "none");
        $("p[name='orderType']").text("流量包");
    }
    $("#poiv-customerName").html($.htmlspecialchars(data.customerName));
}

/**
 * 初始化产品详细table
 */
function initProductDetailTable(data) {
    var baseFlowPackage = [], comboFlowPackage = [], flowPlusProduct = [],telFareProduct = [];
    $.each(data.orderDetailList, function(n, value) {
        // 流量加
        if (value.orderType == 2) {
            $(".isFlowPackageShow").hide();
            $(".isFlowPlusShow").show();
            flowPlusProduct.push(value);
            return;
        }
        // 基础包
        if (value.productType == 1) {
            baseFlowPackage.push(value);
        } else if (value.productType == 2) {
            // 组合包
            comboFlowPackage.push(value);
        }else if (value.productType == 4) {
            // 话费
            telFareProduct.push(value);
        }
    });
    createFlowProductTable($(baseTableId), baseFlowPackage, 1);
    //createFlowProductTable($(comboTableId), comboFlowPackage, 1);
	//createFlowProductTable($(telFareTableId), telFareProduct, 1);
    //createFlowProductTable($(plusTableId), flowPlusProduct, 2);
    
    // 直充合作伙伴
    if (data.partnerType == 2) {
        $(".comboPackage").hide();
    }
}

function createFlowProductTable($table, data, orderType) {
    $table.dataTable({
        "iDisplayLength": 99,
        "bDeferRender":true,
        "bLengthChange":false,
        "bInfo":false,
        "bPaginage" : false,
        "bSort" : false,
        "bFilter" : false,
        "bDestroy" : true,
        "bAutoWidth" : true,
        "sScrollY":"",
        "aaData" : data,
        "oLanguage" : {
            "oPaginate" : {
                "sNext" : "",
                "sPrevious" : ""
            },
            "sEmptyTable" : "无产品记录",
            "sInfoEmpty" : "",
            "sLoadingRecords" : "请稍后，正在加载中...",
        },
        "aoColumns": [ {
            mData : "productName",
            mRender : function(value, type, rowData) {
                return "<label value='" + rowData.productId + "' >" + $.htmlspecialchars(value) + "</label>";
            }
        }, {
            mData : "operatorCodeDesc",
            mRender : function(value, type, rowData) {
                var operatorCode = value != null ? value : "移动、电信、联通";
                return "<label value='"+ value +"' >" + operatorCode + "</label>";
            }
        }, {
            mData : "zoneDesc",
            mRender : function(value, type, rowData) {
                return "<label value='"+ value +"' >" + value + "</label>";
            }
        }, {
            mData : "amount",
            bVisible : orderType == 2 ? true : false,
            mRender : function(value, type, rowData) {
                return "<label value='"+ value +"' >" + commaDelimited(value) + "</label>";
            }
        }, {
            mData : "productCode",
            bVisible : orderType == 2 ? false : true,
            mRender : function(value, type, rowData) {
                return "<label value='"+ value +"' >" + value + "</label>";
            }
        }, {
            mData : "productPrice",
            mRender : function(value, type, rowData) {
                if (value != null) {
                    return "<label>￥" + commaDelimited(value) + "</label>";
                } else {
                    return "";
                }
            }
        }, {
            mData : "price",
            mRender : function(value, type, rowData) {
                if (value != null) {
                    return "<label>￥" + commaDelimited(value) + "</label>";
                } else {
                    return "";
                }
            }
        }, {
            mData : "discount",
            sDefaultContent: "",
            mRender : function(value, type, rowData) {
                return "<label>" + commaDelimited((rowData.price*100/rowData.productPrice).toFixed(2)) + "</label>";
            }
        }
//        ,{
//            mData : "quarzTime",
//            sDefaultContent: "",
//            mRender : function(value, type, rowData) {
//            	value=value==null?"":value;
//                return "<label>" + value+ "</label>";
//            }
//        
//        },
//        {
//            mData : "endPrice",
//            sDefaultContent: "",
//            mRender : function(value, type, rowData) {
//            	value=value==null||value==0?"":value;
//                return "<label>" +value + "</label>";
//            }
//        
//        }
//        ,{
//            mData : "salePrice",
//            sDefaultContent: "",
//            mRender : function(value, type, rowData) {
//            	value=value==null||value==0?"":value;
//                return "<label>" +value + "</label>";
//             }
//        
//        }
        ],
    });
    
}

function initOrderDealRecord() {
    if (!pkId) {
        Feng.error("没有获取到客户订单");
    }
    $.ajax({
        url : Feng.ctxPath + '/orderDealRecord/getOrderDealRecord?sourceId=' + pkId + '&type=2',
        type : 'get',
        dataType : 'json',
        success : function(data) {
        	if ( data && data.code && data.code != 200 ) {
        		Feng.error(data.message ? data.message : "获取数据失败！");
        		return;
        	}
            initOrderDealRecordTable(data);
        }
    });
}

function initOrderDealRecordTable(data) {
    $(orderRecordTableId).dataTable({
        "iDisplayLength": 99,
        "bDeferRender":true,
        "bLengthChange":false,
        "bInfo":false,
        "bPaginage" : false,
        "bSort" : false,
        "bFilter" : false,
        "bDestroy" : true,
        "sScrollY":"",
        "aaData" : data,
        "oLanguage" : {
            "oPaginate" : {
                "sNext" : "",
                "sPrevious" : ""
            },
            "sEmptyTable" : "无订单历史修改记录",
            "sInfoEmpty" : "",
            "sLoadingRecords" : "请稍后，正在加载中...",
        },
        "aoColumns": [ {
            mData : "inputTime"
        }, {
            mData : "dealRecordId",
            mRender : function(value, type, rowData) {
                return "<label><a href='javascript:;' onclick='downloadEvent(" + rowData.dealRecordId + ")'>" 
                        + rowData.inputTime.substr(0, 10) + " 修改记录</a>" 
                        + "</label>";
            }
        }, {
            mData : "creator"
        }],
    });
    table_rowspan("#base-detail-table", 2);
    table_rowspan("#base-detail-table", 3);
    table_rowspan("#combo-detail-table", 2);
    table_rowspan("#combo-detail-table", 3);
    table_rowspan("#plus-detail-table", 2);
    table_rowspan("#plus-detail-table", 3);
}

// 下载订单处理记录
function downloadEvent(dealRecordId) {
	window.location.href = encodeURI(Feng.ctxPath + "/orderDealRecord/download?dealRecordId=" + dealRecordId);
}

/**
 * 逗号分隔
 * @param num
 * @returns
 */
function commaDelimited(num) {
    return String(num).replace(/(\d)(?=(\d\d\d)+(?!\d))/g, '$1,');
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


/**
 * 返回
 * @returns
 */
function goBack() {
	
	if ( window.parent.OrderInfo != undefined && window.parent.OrderInfo.layerIndex > -1 ){
		// 如果是从订单列表页面进入
		parent.layer.close(window.parent.OrderInfo.layerIndex);
	} else {
		// TODO other
	}
	
}