/**
 * 初始化 BootStrap Table 的封装
 *
 * 约定：toolbar的id为 (bstableId + "Toolbar")
 *
 * @author liuyufeng
 */
(function () {
    var BSTable = function (bstableId, url, columns) {
        this.btInstance = null;					//jquery和BootStrapTable绑定的对象
        this.bstableId = bstableId;
        this.url = Feng.ctxPath + url;
        this.method = "post";
        this.paginationType = "server";			//默认分页方式是服务器分页,可选项"client"
        this.toolbarId = bstableId + "Toolbar";
        this.columns = columns;
        this.height = 665,						//默认表格高度665
            this.data = {};
    };
    var parms_ = {};
    BSTable.prototype = {
        /**
         * 初始化bootstrap table
         */
        init: function () {
            var tableId = this.bstableId;
            this.btInstance =
                $('#' + tableId).bootstrapTable({
                    contentType: "application/x-www-form-urlencoded",
                    url: this.url,				//请求地址
                    method: this.method,		//ajax方式,post还是get
                    ajaxOptions: {				//ajax请求的附带参数
                        data: this.data
                    },
                    toolbar: "#" + this.toolbarId,//顶部工具条
                    striped: true,     			//是否显示行间隔色
                    cache: false,      			//是否使用缓存,默认为true
                    pagination: true,     		//是否显示分页（*）
                    sortable: true,      		//是否启用排序
                    sortOrder: "desc",     		//排序方式
                    pageNumber: 1,      			//初始化加载第一页，默认第一页
                    pageSize: 14,      			//每页的记录行数（*）
                    pageList: [14, 50, 100],  	//可供选择的每页的行数（*）
                    queryParamsType: 'limit', 	//默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort
                    sidePagination: this.paginationType,   //分页方式：client客户端分页，server服务端分页（*）
                    search: false,      		//是否显示表格搜索，此搜索是客户端搜索，不会进服务端
                    strictSearch: true,			//设置为 true启用 全匹配搜索，否则为模糊搜索
                    showColumns: true,     		//是否显示所有的列
                    showRefresh: true,     		//是否显示刷新按钮
                    minimumCountColumns: 2,    	//最少允许的列数
                    clickToSelect: true,    	//是否启用点击选中行
                    searchOnEnterKey: true,		//设置为 true时，按回车触发搜索方法，否则自动触发搜索方法
                    columns: this.columns,		//列数组
                    pagination: true,			//是否显示分页条
                    height: this.height,
                    icons: {
                        refresh: 'glyphicon glyphicon-refresh',
                        toggle: 'glyphicon-list-alt',
                        columns: 'glyphicon-list'
                    },
                    iconSize: 'outline',
        		    showExport: this.showExport,  //是否显示导出按钮  
                    exportTypes: this.exportTypes,
                    exportDataType: this.exportDataType,
                    exportOptions: this.exportOptions,
                    queryParams:  function (params) {
                    	var params_={};
/*                        params_.params = {};
                        params_.params.createStartTime = $("#createStartTime").val();
                        params_.params.createEndTime = $("#createEndTime").val();
                        params_.params.partnerName = $("#partnerName").val();
                        params_.params.customerName = $("#customerName").val();
                        params_.params.appId = $("#appId").val();*/
                    	params_ = parms_;
                        params_.offset = params.offset;
                        params_.limit = params.limit;
                        params_.sort = params.sort;
                        params_.order = params.order;
                    	return params_;
                    },
                    onLoadError: function (data, res) { 
                    	if(res.responseJSON.message!=""){
                    		Feng.error(res.responseJSON.message);
                    		 $('#' + tableId).bootstrapTable('removeAll');  
                    		return;
                    	}
                    	Feng.error("系统异常请联系客服");
                    	return false;
                      }
                });
            return this;
        },

        /**
         * 设置分页方式：server 或者 client
         */
        setPaginationType: function (type) {
            this.paginationType = type;
        },
        
        setHeight : function(height){
        	this.height=height;
        },
        
        setParams: function (parms) {
        	this.parms_ = parms;
        },
        /**
         * 设置导出功能
         */
        setExport: function (showExport,exportTypes,exportDataType,exportOptions) {
        	this.showExport = showExport;
        	this.exportTypes = exportTypes;
        	this.exportDataType = exportDataType;
        	this.exportOptions = exportOptions;
        },
        /**
         * 设置ajax post请求时候附带的参数
         */
        set: function (key, value) {
            if (typeof key == "object") {
                for (var i in key) {
                    if (typeof i == "function")
                        continue;
                    this.data[i] = key[i];
                }
            } else {
                this.data[key] = (typeof value == "undefined") ? $("#" + key).val() : value;
            }
            return this;
        },

        /**
         * 设置ajax post请求时候附带的参数
         */
        setData: function (data) {
            this.data = data;
            return this;
        },

        /**
         * 清空ajax post请求参数
         */
        clear: function () {
            this.data = {};
            return this;
        },

        /**
         * 刷新 bootstrap 表格
         * Refresh the remote server data,
         * you can set {silent: true} to refresh the data silently,
         * and set {url: newUrl} to change the url.
         * To supply query params specific to this request, set {query: {foo: 'bar'}}
         */
        refresh: function (parms) {
            if (typeof parms != "undefined") {
                this.btInstance.bootstrapTable('refresh', parms);
                this.setParams(parms);
            } else {
                this.btInstance.bootstrapTable('refresh');
            }
        },
        
        /**
         * export table to excel;
         */
        export2Excel: function() {
        	// 获取BootstrapTable对象
        	var that = $('#' + this.bstableId).data('bootstrap.table');
        	// 从导出源码中抽取出来  自定执行 -  参考 bootstrap-table-export.js中的78行
        	doExport = function () {
                that.$el.tableExport($.extend({}, that.options.exportOptions, {
                    type: 'excel',
                    escape: false
                }));
            };
        	// 执行导出 - 参考bootstrap-table-export.js中85行代码
        	that.togglePagination();
            that.$el.on('load-success.bs.table', function () {
                doExport();
                that.$el.off('load-success.bs.table');
                that.togglePagination();
            });
        }
    };

    window.BSTable = BSTable;

}());