/**
 * 订单分析报表管理初始化
 */
var OrderAnalysis = {
    id: "OrderAnalysisTable",	// 表格id
    seItem: null,		// 选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
OrderAnalysis.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle',sortable:true},
        {title: '归属地', field: 'area_name',  align: 'center', valign: 'middle',sortable:true},
	   {title: '全国通道金额', field: 'country_price',  align: 'center', valign: 'middle',sortname : 'country_price',sortable:true},
	   {title: '分省通道金额', field: 'provincep',  align: 'center', valign: 'middle',sortname : 'province_price',sortable:true},
	   {title: '地市通道金额', field: 'city_price', align: 'center', valign: 'middle',sortname : 'city_price',sortable:true},
	   {title: '全国通道订单数', field: 'country_count',  align: 'center', valign: 'middle',sortname : 'country_count',sortable:true},
	   {title: '分省通道订单数', field: 'province_count',  align: 'center', valign: 'middle',sortname : 'province_count',sortable:true},
	   {title: '地市通道订单数', field: 'city_count',  align: 'center', valign: 'middle',sortname : 'city_count',sortable:true},
	   {title: '订单总金额', field: 'total_price',  align: 'center', valign: 'middle',sortname : 'total_price',sortable:true},
	   {title: '金额订单占比', field: 'rate',  align: 'center', valign: 'middle',formatter:getRate}
    ];
};

// 获取金额订单占比
function getRate(cellvalue, rowObject, options){
//	var total_price =$("#total_price").val();
//	var rate=(rowObject.total_price*100/total_price).toFixed(2);
//	if(isNaN(rate)){
//		return '-';
//	}
//	return rate +"%"; 
	return cellvalue;
}


/**
 * 检查是否选中
 */
OrderAnalysis.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        OrderAnalysis.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加订单分析报表
 */
OrderAnalysis.openAddOrderAnalysis = function () {
    var index = layer.open({
        type: 2,
        title: '添加订单分析报表',
        area: ['800px', '420px'], // 宽高
        fix: false, // 不固定
        maxmin: true,
        content: Feng.ctxPath + '/orderAnalysis/orderAnalysis_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看订单分析报表详情
 */
OrderAnalysis.openOrderAnalysisDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '订单分析报表详情',
            area: ['800px', '420px'], // 宽高
            fix: false, // 不固定
            maxmin: true,
            content: Feng.ctxPath + '/orderAnalysis/orderAnalysis_update/' + OrderAnalysis.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除订单分析报表
 */
OrderAnalysis.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/orderAnalysis/delete", function (data) {
            Feng.success("删除成功!");
            OrderAnalysis.table.refresh();
        }, function (data) {
            Feng.error("删除失败!");
        });
        ajax.set("orderAnalysisId",this.seItem.id);
        ajax.start();
    }
};

function load(params_){
			require.config({
			    paths:{
			        echarts:'/static/js/plugins/echarts/echarts',
			        'echarts/chart/bar' : '/static/js/plugins/echarts/echarts',
			        'echarts/chart/line': '/static/js/plugins/echarts/echarts'
			    }
			});
		    var ajax = new $ax(Feng.ctxPath + "/orderAnalysis/chart", function (data) {
/*		    	var data = {  "rate": [14.29,7.14,78.57],
					         "areaName": ["山西","广东1","广东河北16"],
					         "province": [0,3,0],
					         "city": [0,0,0],
					         "country": [6,0,33]
					     };*/
		    	
				require(
				        [
				            'echarts',
				            'echarts/chart/bar',
				            'echarts/chart/line'
				        ],
				        function(ec) {
				            // --- 折柱 ---
				            var myChart = ec.init(document.getElementById('main'));
				            
				
				        	myChart.setOption({
					               tooltip : {
					        trigger: 'axis'
					    },
					    toolbox: {
					        show : true,
					        feature : {
					            mark : {show: true},
					            dataView : {show: true, readOnly: false},
					            magicType: {show: true, type: ['line', 'bar']},
					            restore : {show: true},
					            saveAsImage : {show: true}
					        }
					    },
					    calculable : true,
					    legend: {
					        data:['全国通道','分省通道','地市通道','订单总金额占比']
					    },
					    xAxis : [
					        {
					            type : 'category',
					            "axisLabel":{  
					                interval: 0  
					            },
					            data : data.areaName,
					            axisLabel:{  
				                    interval:0,  
				                    rotate:30,// 倾斜度 -90 至 90 默认为0
				                    margin:1,  
				                    textStyle:{  
				                        fontWeight:"bolder",  
				                        color:"#000000"  
				                    }  
				                },    
					        }
					    ],
					    	
					    yAxis : [
					        {
					            type : 'value',
					            name : '金额',
					            axisLabel : {
					                formatter: '{value} 元'
					            }
					        },
					        {
					            type : 'value',
					            name : '百分比',
					            axisLabel : {
					                formatter: '{value} %'
					            }
					        }
					    ],
					    series : [
					         {
					            name:'全国通道',
					            type:'bar',
					            stack: '柱子',
					            data:data.country,
					            barWidth:30,
					            barMaxWidth:50// 最大宽度
					        },
					        {
					            name:'分省通道',
					            type:'bar',
					            stack: '柱子',
					            data:data.province,
					            barWidth:30,
					            barMaxWidth:50// 最大宽度
					        },
					        {
					            name:'地市通道',
					            type:'bar',
					            stack: '柱子',
					            data:data.city,
					            barWidth:30,
					            barMaxWidth:50// 最大宽度
					        },
					        {
					            name:'订单总金额占比',
					            type:'line',
					            yAxisIndex: 1,
					            data:data.rate
					        }
					    ]
					            });
				        
				            
				            
				        }
				);
		    }, function (data) {
//		        Feng.error("删除失败!");
		    });
//		    ajax.set("orderAnalysisId","orderAnalysisId");
		    ajax.set(params_);
		    ajax.start();
		
}
/**
 * 查询订单分析报表列表
 */
OrderAnalysis.search = function () {
	var params_={};
    params_.params = {};
    params_.params.beginCheckTime = $("#beginCheckTime").val();
    params_.params.endCheckTime = $("#endCheckTime").val();
    params_.params.partnerName = $("#partnerName").val();
    params_.params.customerName = $("#customerName").val();
    params_.params.operatorCode = $("#operatorCode").val();
    params_.params.channelName = $("#channelName").val();
    load(params_);
    OrderAnalysis.table.refresh({query: params_});
};

$(function () {
    var defaultColunms = OrderAnalysis.initColumn();
    var table = new BSTable(OrderAnalysis.id, "/orderAnalysis/list", defaultColunms);
// table.setPaginationType("client");
    OrderAnalysis.table = table.init();
    
    OrderAnalysis.search();
});

