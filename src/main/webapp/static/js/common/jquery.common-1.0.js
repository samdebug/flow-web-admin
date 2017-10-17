/** 
 * @fileoverview  卓望数码 jQuery Common Library
 * @description:封装一些jQuery公共操作方法
 * @author oCEAn Zhuang (zhuangruhai@aspirecn.com QQ: 153414843)
 * @version 1.0
 * @date 2013-09-18
 */
/**
 * 将form属性转化为JSON对象，支持复选框和select多选
 * @param {Object} $
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
;
(function($){
	$.fn.serializeJson = function(){
	var serializeObj = {};
	var array = this.serializeArray();
	$(array).each(function(){
		if(serializeObj[this.name]){
			if($.isArray(serializeObj[this.name])){
				serializeObj[this.name].push(this.value);
			}else{
				serializeObj[this.name]=[serializeObj[this.name],this.value];
			}
		}else{
			serializeObj[this.name]=this.value;
		}
	});
	return serializeObj;
};
})(jQuery);
/**
 * 对form进行jQuery扩展，可将json数据映射到对应的表单元素里,json中的属性名需跟form表单里的元素name一致。
 * @namespace jQuery扩展封装
 * @param {json} json格式数据
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		json2Form: function(json) {
			var _this = this;
			jQuery.each(jQuery(_this).serializeArray(),
				function(index) {
					var name = this['name'];
					for (var a in json) {
						var key = "";
						var val = "";
						if (name.indexOf('.') != -1) {
							key = name.split('.')[0];
							var getval = name.split('.')[1];
							val = json[a][getval];
						} else {
							key = name;
							val = json[a];
						}
						if (jQuery.trim(key) == jQuery.trim(a)) {
							var eve = jQuery(_this).find("[name='" + name + "']");
							if (jQuery(eve).length > 1) {
								for (var i = 0; i < jQuery(eve).length; i++) {
									//判断单选按钮  
									if (jQuery(jQuery(eve)[i]).attr("type") == 'radio') {
										if (jQuery(jQuery(eve)[i]).val() == val) {
											jQuery(jQuery(eve)[i]).attr("checked", true);
										}
									}
								}
							} else {
								jQuery(eve).val(val);
							}
						}
					}
				});
		}
	});
})(jQuery);

$.extend({
	str2Value: function(p,str) {
		var strs = str.split('.');
		var _tt = p;
		for (var i = 0;i < strs.length; i++){
				var tmp = strs[i];
				if (tmp.indexOf('[') != -1){
					var pre = tmp.substring(0,tmp.indexOf('[')),num = tmp.substring(tmp.indexOf('[') + 1,tmp.indexOf(']'));
					if (typeof (_tt) != 'undefined'&&typeof (_tt[pre]) != 'undefined'&& typeof (_tt[pre][num]) != 'undefined'){
						_tt = _tt[pre][num];
						continue;
					}else{
						return null;
					}
				}
				if (typeof (_tt) != 'undefined' && typeof (_tt[strs[i]]) != 'undefined' && typeof (_tt[strs[i]]) != 'undefined'){
					_tt = _tt[strs[i]];
					continue;
				}else{
					return null;
				}
		}
		return  _tt;
	}
});

;
(function() {
	jQuery.extend(jQuery.fn, {
		json2Form2: function(json) {
			var tmp = json,_form = $(this).attr('id');
			if (!_form){
				_form = (new Date()).getTime();//随机生成一个ID
				$(this).attr('id',_form);
			}
			jQuery("#" + _form + " :input" ).each(function(i){
				var _name = $(this).attr('name');
				if (_name){
					var vv = $.str2Value(tmp,_name);
					if (vv !== null){
						var _type = $(this).attr("type");
						if (_type == 'text' || _type == 'hidden'){
							$(this).val(vv);
						} else if (_type == 'radio'){
							$("#" + _form +" :input[type=radio][name='" + _name + "'][value='" + vv + "']").attr("checked","checked"); 
						} else if (_type == 'checkbox'){
							var arr = vv.split(',');
							for (var i = 0; i < arr.length ;i++){
								$("#" + _form +" :input[type=checkbox][name='" + _name + "'][value='" + arr[i] + "']").attr("checked","checked");
							}
						} 
						
					}
				}
			});
			jQuery("#" + _form + " textarea" ).each(function(i){
				var _name = $(this).attr('name');
				if (_name){
					var vv = $.str2Value(tmp,_name);
					if (vv !== null){
						$(this).val(vv);
					}
				}
			});
			jQuery("#" + _form + " select" ).each(function(i){
				var _name = $(this).attr('name');
				if (_name){
					var vv = $.str2Value(tmp,_name);
					if (vv !== null){
						if ($(this).attr('multiple')){
							var _ss = vv.split(',');
							for (var n = 0; n < _ss.length; n++){
								$(this).children('option[value="'+_ss[n] + '"]').attr("selected","selected");
							}
						}else{
							//if($(this).get(0).length > 0){
								$(this).val(vv);
							//}else{
								$(this).attr('_defaultValue',vv);
							//}
						}
						$(this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
					}
				}
			});
		}
	});
})(jQuery);

$.extend({
	dataInput: function(elems, datas, callback) {
		var _tmp = datas;
		elems.each(function(i){
			var _name = $(this).attr('name');
			if (_name){
				var vv = $.str2Value(_tmp,_name);
				if (vv !== null){
					vv = '' + vv;
					if ($(this).is('div') || $(this).is('span') || $(this).is('label') || $(this).is('td') ){
						$(this).html(vv);
					}else if ($(this).is('input')){
						var _type = $(this).attr("type");
						if (_type == 'text' || _type == 'hidden'){
							$(this).val(vv);
						} else if (_type == 'radio'){
							if ($(this).val() == vv){
								$(this).attr("checked","checked");
							} 
						} else if (_type == 'checkbox'){
							var arr = vv.split(',');
							for (var i = 0; i < arr.length ;i++){
								if ($(this).val() == arr[i]){
									$(this).attr("checked","checked");
								} 
							}
						} 
					}else if ($(this).is('select')){
						//if($(this).get(0).length > 0){
							$(this).val(vv);
						//}else{
							$(this).attr('_defaultValue',vv);
						//}
						$(this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
						/**var _tt = vv.split(',');
						for (var n = 0; n < _tt.length; n++ ){
							if ($(this).val() == _tt[n]){
								$(this).attr("selected","selected");
							}
						}**/
					}else if ($(this).is('textarea')){
						$(this).val(vv);
					}else{
						$(this).html(vv);
					}
					
				}
			}
		});
		if (typeof callback != 'undefined') {
			callback(datas);
		}
	}
});
$.extend({
	dataSubString: function(elems,callback) {
		elems.each(function(i){
			var subStringLength = $(this).attr('subStringLength');
			if (subStringLength){
					if ( $(this).is('span')){
						var spanText = $(this).text();
						var spanTextLength = $(this).text().replace(/[^\x00-\xff]/g, ' ').length;
						if(spanTextLength && spanTextLength > subStringLength){
						    var subStringSpanText = spanText.substring(0,subStringLength)+"...";
							$(this).text(subStringSpanText);
							$(this).attr('title',spanText);
						}
					}else if ($(this).is('input')){
						var inputVal = $(this).val();
						var inputValLength = $(this).val().replace(/[^\x00-\xff]/g, ' ').length;
						if(inputValLength && inputValLength > subStringLength){
						    var subStringInputVal = inputVal.substring(0,subStringLength)+"...";
							$(this).val(subStringInputVal);
							$(this).attr('title',inputVal);
						}
					}
			}
		});
		if (typeof callback != 'undefined') {
			callback(datas);
		}
	}
});

/**
 * 对ajax请求做了封装，统一项目的ajax请求。
 * @namespace jQuery扩展封装
 * @param {url} 请求的url地址
 * @param {params} JSON格式的参数,如{name:'abc','age':10}
 * @param {callback} 调用成功后回调函数,可不传
 * @return json数据
 */
;
$.extend({
	ajaxSubmit: function(url, params, callback, $btn) {
	    if ($btn){
            $btn.attr("disabled","disabled");
        }
		jQuery.ajax({
			url: url,
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
			    if ($btn){
                    $btn.removeAttr("disabled");
                }
				if (typeof callback == 'function') {
					callback(data);
				}
			},
			error: function() {
			    if ($btn){
                    $btn.removeAttr("disabled");
                }
				alert('发生系统错误');
			},
			beforeSend: function() {
				// Handle the beforeSend event
			},
			complete: function() {
				// Handle the complete event
			}
		});
	}
});


/**
 * 对select进行jQuery扩展，select动态数据获取封装。
 * @namespace jQuery扩展select封装
 * @param {json} json格式数据
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		ajaxRender: function(url, config, params) { //config:{firstOption:{text:'请选择分类',value:''},keyAlias:{text:'name',value:'value'}}
			var _this = this,
				c = config || {}, keyValue = {
					text: 'name',
					value: 'value'
				};
			if (c.keyValue) {
				keyValue = c.keyValue;
			}
			$(_this).empty();
			if (c.firstOption) {
				$(_this).append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
			}
			//var data = [{'name':'选项1','value':'值1'},{'name':'选项2','value':'值2'}];
			jQuery.ajax({
				url: url,
				type: 'GET',
				dataType: 'json',
				async:false,
				data: params,
				success: function(data) {
				    var root = c.root;
				    if(root){
				    	if(root.indexOf(".") > 0){
							arr =root.split(".");
							data = data[arr[0]][arr[1]];
						}else{
							data = data[root];
						}
				    }
					if (data) {
						$(_this).empty();
						if (c.firstOption) {
							$(_this).append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
						}
						for (var i = 0; i < data.length; i++) {
							$(_this).append('<option value="' + data[i][keyValue.value] + '">' + data[i][keyValue.text] + '</option>');
						}
						if ($(_this).attr('_defaultValue')){
							$(_this).val($(_this).attr('_defaultValue'));
							$(_this).trigger('change');//解决firefox下通过js改变值后不触发onchange事件,这里手动触发
						}
					}
				},
				error: function() {
					alert('列表数据获取发生系统错误......');
				},
				beforeSend: function() {
					// Handle the beforeSend event
				},
				complete: function() {
					// Handle the complete event
					//select下拉框选中
					if(c.selectedValue){
								$(_this).val(c.selectedValue);
							}
				}
			});
		},
		localRender:function(data, config){ //config:{firstOption:{text:'请选择分类',value:''},keyAlias:{text:'name',value:'value'}}
			var _this = this,
				c = config || {}, keyValue = {
					text: 'name',
					value: 'value'
				};
			if (c.keyValue) {
				keyValue = c.keyValue;
			}
			$(_this).empty();
			if (c.firstOption) {
				$(_this).append('<option value="' + c.firstOption.value + '">' + c.firstOption.text + '</option>');
			}
			//var data = [{'name':'选项1','value':'值1'},{'name':'选项2','value':'值2'}];
			if (data) {
				for (var i = 0; i < data.length; i++) {
					$(_this).append('<option value="' + data[i][keyValue.value] + '">' + data[i][keyValue.text] + '</option>');
				}
				if ($(_this).attr('_defaultValue')){
					$(_this).val($(_this).attr('_defaultValue'));
				}
			}
		}
	});
})(jQuery);


/**
 * 对移除和清空加了动态效果。
 * @namespace jQuery扩展移除和清空加了动态效果
 * @param {json} 
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		options : {
			speed : 500
		},
		fadeRemove: function(config) { 
			var _this = this;
			$.extend(_this.options, config);
			_this.fadeOut(_this.options.speed,function(){_this.remove();});
		},
		fadeEmpty: function(config) { 
			var _this = this;
			$.extend(_this.options, config);
			_this.fadeOut(_this.options.speed,function(){
				_this.empty();
				if (typeof _this.options.callback == 'function'){
					_this.options.callback();
				}
			});
		}
	});
})(jQuery);
;
(function($){
		/**
		 * 拖放效果
		 * @class jQuery扩展拖放效果
		 * @options options 参数json对象,handler : false,//是否使用手柄，默认为false; opacity : 0.5 //拖放时间浮层的透明度，默认为0.5
		 * @return void
		 * @example
		 * $(this).drag({handler: $('#handlerId'),opacity: 0.75});
		 */
		  $.fn.drag=function(options){
			   //默认配置
				 var defaults = {
					 handler : false,
					 opacity : 0.5
				};   
                // 覆盖默认配置
				var opts = $.extend(defaults, options);
				this.each(function(){						   
				//初始标记变量
				var isMove = false,
					//handler如果没有设置任何值，则默认为移动对象本身，否则为所设置的handler值
					handler = opts.handler?$(this).find(opts.handler):$(this),
					_this = $(this), //移动的对象
					dx = 0,dy = 0;							    
					$(document).mousemove(function(event){//移动鼠标，改变对象位置
							// console.log(isMove);
							if(isMove){							   
								//获得鼠标移动后位置
								var eX = event.pageX,eY = event.pageY;
								//更新对象坐标
								_this.css({'left' : eX-dx,'top' : eY-dy});					   
							}
						}).mouseup(function(){//当放开鼠标，停止拖动
							isMove = false;
							if (_this.css('display') != 'none'){
								_this.fadeTo('fast', 1);
							}
					 });
					//当按下鼠标，设置标记变量isMouseDown为true			   
					handler.mousedown(function(event){ 
					//判断最后触发事件的对象是否是handler
					if($(event.target).is(handler)){ 
							isMove = true;
							$(this).css('cursor','move');
							//console.log(isMove);
							_this.fadeTo('fast', opts.opacity);
							//鼠标相对于移动对象的坐标
							dx = event.pageX-parseInt(_this.css("left"));
							dy = event.pageY-parseInt(_this.css("top"));
						}
 					}); 
 				 });
			  };
})(jQuery);

$.extend({
	_getViewPort: function(obj) {
		var viewportwidth = 0,
			viewportheight = 0;
		if (typeof window.innerWidth != 'undefined') {
			obj = obj || window;
			viewportwidth = obj.innerWidth;
			viewportheight = obj.innerHeight;
		} else if (typeof document.documentElement != 'undefined' && typeof document.documentElement.clientWidth != 'undefined' && document.documentElement.clientWidth != 0) {
			obj = obj || document.documentElement;
			viewportwidth = obj.clientWidth;
			viewportheight = obj.clientHeight;
		}
		return {
			width: viewportwidth,
			height: viewportheight
		};
	},
	_calPosition: function(w, h) {
		//l = (Math.max($(document).width(), $(window).width()) - w) / 2;
		//t = $(window).scrollTop() + $(window).height() / 9;

		//w = this.width(),
		//h = this.height(),
		st = document.documentElement.scrollTop + document.body.scrollTop,
		sl = document.documentElement.scrollLeft,
		vW = this._getViewPort().width,
		vH = this._getViewPort().height,
		l = vW / 2 - w / 2 + sl,
		//t = vH / 2 + st - (vH/2 < h || (h < 50 && h > 0) ? h / 2 : 240);
		t = vH / 2 - h / 2 + st;
		/**var t = (($(window).height() / 2) - (h / 2)) -75;
		var l = (($(window).width() / 2) - (w / 2));
		if( t < 0 ) t = 0;
		if( l < 0 ) l = 0;
		
		// IE6 fix
		if( $.browser.msie && parseInt($.browser.version) <= 6 ) t = t + $(window).scrollTop();**/
		return [l, t];
	},
	_setPosition: function(C, B) {
		if (!C) {
			return false;
		}
		C.css({
			position: 'absolute'
		});
		var lt = this._calPosition(C.width(),C.height());
		C.css({
			left: lt[0],
			top: lt[1],
			'z-index': 9991
		});
		if (B){
			var $h = Math.max($(document).height(), $(window).height());
			$hh = $("html").height();
			//$h = Math.max($h,$hh);
			B.height($h).width('100%');
		}
	}
});
/**
 * 浮层。
 * @namespace jQuery扩展浮层封装
 * @return void
 */
;
(function() {
	jQuery.extend(jQuery.fn, {
		_createMask: function() {
			var divMaskId = "_maskDivId";
			if (!document.getElementById(divMaskId)) {
				$('<div id="' + divMaskId + '" style="display:none;"></div>').appendTo('body');
			}
			this._mask = $('#' + divMaskId);
			this._mask.css({
				background: '#fff',
				filter: 'alpha(opacity=60)',
				'-moz-opacity': 0.6,
				opacity: 0.6,
				position: 'absolute',
				left: 0,
				top: 0,
				'z-index': 999
			});
			//$('#' + divMaskId).bgiframe();
			this._mask.show();
			return this._mask;
		},
		floatDiv: function(options) {
			var defaults = {};
			this._YQ = $.extend(defaults, options);
			var show = this._YQ.show;
			var _this = this;
			if (this._YQ && !show) {
				$(_this).slideUp(200);
				var divMaskId = "_maskDivId";
				if (document.getElementById(divMaskId)) {
					$('#' + divMaskId).hide();
				}
				return;
			}
			var mask = this._createMask();
			$(this).slideDown(200);
			$._setPosition($(this), mask);
			if (this._YQ && this._YQ.clsBtn) {
				this._YQ.clsBtn.click(function() {
					$(_this).slideUp(200);
					mask.hide();
				});
			}
		}
	});
})(jQuery);
/**
 * 对checkbox全选做简单封装。
 * @namespace jQuery扩展函数
 * @param {chkAllId} checkbox全选Id,{chkName} 被选checkbox name,
 * @return void
 */
;
$.extend({
	checkbox_chkAll: function(chkAllId, chkName,callback) {
		var checkBoxes = $("input[name=" + chkName + "]");
		$('#' + chkAllId).click(function() {
			var isCheck = $(this).attr('checked') || false;
			$.each(checkBoxes, function() {
				$(this).attr('checked', isCheck);
			});
			if (callback) callback($('#' + chkAllId),checkBoxes);
		});
		
	}
});

//Tabs
//参数：按钮（JQ对象）[，对应的层（JQ对象），当前按钮class]
Tabs = function($bts,$divs,cls){
	this.bts = $bts;
	this.divs = $divs || $('<div />');
	this.cls = cls || 'up';
};
Tabs.prototype = {
	init: function(eventType,stopDefault,callback){
		eventType = eventType || 'click';
		stopDefault = stopDefault || false;
		var _this = this;
		
		this.bts.bind(eventType,function(){
			var index = _this.bts.index(this);
			_this.bts.removeClass(_this.cls);
			$(this).addClass(_this.cls);
			_this.show(index, this);
			if (typeof callback == 'function'){
				callback(index,_this.bts.eq(index),_this.divs.eq(index));
			}
			//return false;
		});
		this.bts.click(function(){
			return stopDefault;
		});
	},
	show: function(index, bt){
		this.divs.hide().eq(index).show();
		this.done(index, bt);
	},
	done: function(index, bt){
		
	}
};
/**
 * 获取url参数值。
 * @param n 为参数名
 **/
var getP = function(n) {
	var hrefstr, pos, parastr, para, tempstr;
	hrefstr = window.location.href;
	pos = hrefstr.indexOf("?");
	parastr = hrefstr.substring(pos + 1);
	para = parastr.split("&");
	tempstr = "";
	for (var i = 0; i < para.length; i++) {
		tempstr = para[i];
		pos = tempstr.indexOf("=");
		if (tempstr.substring(0, pos).toLowerCase() == n.toLowerCase()) {
			return tempstr.substring(pos + 1);
		}
	}
	return '';
};

var isEndTimeGtStartTime = function(startTime,endTime){  
    var start=new Date(startTime.replace("-", "/").replace("-", "/"));  
    var end=new Date(endTime.replace("-", "/").replace("-", "/"));  
    if(end<start){  
        return false;  
    }  
    return true;  
};


$.extend({
	iframeSubmit : function(setting){
		var id = (new Date()).getTime();
		var ifrmName = 'ifm' + id;
		setting = setting || {};
		if (setting['url'] && setting['params']){
			for (var p in setting['params']){
				setting['url'] = addUrlParam(setting['url'],p,setting['params'][p]);
			}
		}
		frame = (jQuery("<iframe frameborder='0' width='0' height='0'/>")
				.attr({
					'id' : id,
					'name' : setting['ifrmName'] || ifrmName,
					src : setting.url || 'about:blank'
				}))
				.appendTo(document.documentElement);
		// frame加载后的回调喊出
		function cb() {
			// hideLoading();
			var response = '';
			// r.argument = setting ? setting.argument : null;
			try { //
				var doc = frame.contents();

				if (doc && doc[0].body) {// 处理html数据
					response = eval('(' + doc.find('body').html() + ')');
				}
				if (doc && doc[0].XMLDocument) {// 处理xml数据
					response = doc[0].XMLDocument;
				} else {// 其他数据 当作文本来处理
					response = eval('(' + doc + ')');
				}
			} catch (e) {
				// ignore
			}
			frame.unbind('load', cb);

			if (setting.callback && typeof setting.callback == "function") {
				setting.callback.call(frame, response, setting);
			}
			// this.fireEvent("requestcomplete", this, r, o);

			setTimeout(function() {
				frame.remove();
			}, 100);
		}
		frame.bind('load', cb);
	}
});
(function($){
	/**
	 * 为form表单添加导出功能
	 * @memberOf {TypeName} 
	 */
	$.fn.extend({
		exportData:function(setting){
			var id = (new Date()).getTime();
			var ifrmName = 'ifm' + id;
			frame = (
				jQuery("<iframe frameborder='0' width='0' height='0'/>")
				.attr({'id':id,'name':ifrmName,src:"about:blank"})
			).appendTo( document.documentElement );
			$(this).attr('target',ifrmName).attr('method','post');
			if (!$(this).attr('method')){
				$(this).attr('method','post');
			}
	        //frame加载后的回调喊出
	        function cb(){
	        	//hideLoading();
	            var response = ''
	
	           // r.argument = setting ? setting.argument : null;
	
	            try { //
	                var doc = frame.contents();
	                
	                if(doc && doc[0].body){//处理html数据
	                    response =eval('(' + doc.find('body').html() + ')'); 
	                }
	                if(doc && doc[0].XMLDocument){//处理xml数据
	                    response = doc[0].XMLDocument;
	                }else {//其他数据 当作文本来处理
	                    response = eval('(' + doc + ')');
	                }
		            }
	            catch(e) {
	                // ignore
	            }
				frame.unbind('load',cb);
				
				if(setting.callback && typeof setting.callback == "function"){
					setting.callback.call(frame,response,setting);
				}
	            //this.fireEvent("requestcomplete", this, r, o);
	
	            setTimeout(function(){frame.remove();}, 100);
	        }
	
	        frame.bind('load',cb);
	        //form.submit();
	        //拼参数
	        var p = setting.data;
            if(typeof p == "object"){
                p = $.param(p);
            }
            if($.ajaxDefaultParams){
                var extras = $.param($.ajaxDefaultParams);
                p = p ? (p + '&' + extras) : extras;
            }
	        //如果用自己定义的参数就不用从表单中取    
	        if(setting.data==null){
	                setting.url = setting.url || $(this).attr('action');
	                //var f = $(this).serialize();
	                //p = p ? (p + '&' + f) : f;
	         }	        
			//最终的url
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + '_dc=' + (new Date().getTime());
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + p;
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + "acceptContentType=html&isAjax=true";
	        //showLoading();
	        //frame.attr('src',setting.url);
	         $(this).attr('action',setting.url).submit();
		},
		upload:function(setting){
			var id = (new Date()).getTime();
			var ifrmName = 'ifm' + id;
			frame = (
				jQuery("<iframe frameborder='0' width='0' height='0' name='"+ifrmName+"'/>")
				.attr({'id':id,'name':ifrmName,src:Aspire.prm.SSL_SECURE_URL}).addClass('export-hidden')
			).appendTo( document.documentElement );
			
			$(this).attr('target',ifrmName);
	        
	        //frame加载后的回调喊出
	        function cb(){
	        	hideLoading();
	            var response = ''
	
	           // r.argument = setting ? setting.argument : null;
	
	            try { //
	                var doc = frame.contents();
	                if(doc && doc.find('body')){//处理html数据
	                    response =eval('(' + doc.find('body').html() + ')'); 
	                }else if(doc && doc[0].XMLDocument){//处理xml数据
	                    response = doc[0].XMLDocument;
	                }else {//其他数据 当作文本来处理
	                    response = eval('(' + doc + ')');
	                }
		           }catch(e) {
	                // ignore
	            }
				frame.unbind('load',cb);
				if(setting.callback && typeof setting.callback == "function"){
					setting.callback.call(frame,response,setting);
				}
	            //this.fireEvent("requestcomplete", this, r, o);
	
	            setTimeout(function(){frame.remove();}, 100);
	        }
	
	        frame.bind('load',cb);
	        //form.submit();
	        //拼参数
	        var p = setting.data;
            if(typeof p == "object"){
                p = $.param(p);
            }
            if($.ajaxDefaultParams){
                var extras = $.param($.ajaxDefaultParams);
                p = p ? (p + '&' + extras) : extras;
            }
	        //如果用自己定义的参数就不用从表单中取    
	        if(setting.data==null){
	                setting.url = setting.url || $(this).attr('action');
	                //var f = $(this).serialize();
	                //p = p ? (p + '&' + f) : f;
	         }	        
			//最终的url
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + '_dc=' + (new Date().getTime());
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + p;
	         setting.url += (setting.url.indexOf('?') != -1 ? '&' : '?') + "acceptContentType=html&isAjax=true";
	        //frame.attr('src',setting.url);
	         showLoading();
	        $(this).attr('action',setting.url).submit();
		}
		
	})
})(jQuery);
;
(function() {
	jQuery.extend(jQuery.fn, {
		changePlus: function(callback) {
			if(window.navigator.userAgent.indexOf("MSIE")>=1){
				$(this).get(0).onpropertychange = callback;
			}else{
				$(this).bind('change',callback);
			}
		}
	});
})(jQuery);


/**
 * @namespace 截取文本长度，超出部分以...代替
 * @param name
 *            当前字段
 * @param names
 *            需要截取的字段数组
 * @param text
 *            文本内容
 * @returns
 */
function getShortText(name,names,text){
	var shortText = "";
	var index = $.inArray(name,names);
	if(text.length>13 & index>=0){
		shortText = text.substring(0,13)+"...";
		return shortText;
	}
	return shortText;
};
;(function($) {
    $.fn.extend({
    	/**
		 * 查看更多插件。
		 * 
		 * @class 点击查看更多
		 * @param {options}
		 *            参数如 { len :80, //截取的长度 moreLink : '<a
		 *            href="javascript:;">...查看全部↓</a>',//显示全部的html标签 lessLink : '<a
		 *            href="javascript:;">收起↑</a>'//收起的html标签 };。
		 * @return void
		 */
        readmore: function(_options) {
				var _default = {
					len :80,
					moreLink : '<a href="javascript:;">...查看全部↓</a>',
					lessLink : '<a href="javascript:;">收起↑</a>'
			};
			$.extend(_default,_options||{});
            //遍历匹配元素的集合
            this.each(function(options) {
				var current = $(this),content = $(this).text(),moreLink = $(_default.moreLink);
				if (content.length > _default.len && !$(this).attr('_bindReadmore')){
					$(this).show();
					$(this).attr('_bindreadmore',true);
					var txt = content.substring(0,_default.len);
					current.data('_readmore',current.html());
					var _moreF = function(){
						var lessF = $(_default.lessLink).bind('click',_lessF);
						current.html(current.data('_readmore')).append(lessF);
					};
					var _lessF = function(){
						var moreLink = $(_default.moreLink).bind('click',_moreF);
						current.html(txt).append(moreLink);
					};
					moreLink.bind('click', _moreF);
					current.html(txt).append(moreLink).show();
				}
				
            });
        }
    });      
})(jQuery); 

/**
 * 扩展checkbox方法values，多选获取选中值的方法，多个以逗号分隔
 * @class jquery扩展checkbox添加values方法，多选获取选中值的方法，多个以逗号分隔
 */
;
(function($) {
	jQuery.extend(jQuery.fn, {
		values: function(datas) {
			var chkValue = [];
			if (datas){
				chkValue = datas.split(','); 
				for (var i = 0; i < chkValue.length; i++){
					$(this).each(function(){
						if($(this).val() == chkValue[i]){
							$(this).attr('checked',true);
						}
					 });
				}
			}else{
				$(this).each(function(){
					if(!$(this).attr('disabled') && $(this).is(":checked")){
						chkValue.push($(this).val());  
					}
				 });
				return chkValue.join(',');
			}
			
		}
	});
})(jQuery);

/**
 * 扩展select方法selected，多选获取选中值的方法，多个以逗号分隔
 * @class jquery扩展select添加selected方法，多选获取选中值的方法，多个以逗号分隔
 */
;
(function($) {
	jQuery.extend(jQuery.fn, {
		selected: function(datas) {
			if (datas){
				var arr = datas.split(',');
				for (var i = 0 ; i < arr.length; i++){
					$(this).find("option[value='"+arr[i] + "']").attr("selected",true);
				}
			}
		}
	});
})(jQuery);

/**
 * @namespace 扩展字符串函数replaceAll
 * @param s1 被替换的字符
 * @param s1 要替换的字符
 */
String.prototype.replaceAll  = function(s1,s2){       
	return this.replace(new RegExp(s1,"gm"),s2);       
};
/**
 * 扩展数组的包含功能
 * @param {Object} obj
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
Array.prototype.contains = function(element) {  
    for (var i = 0; i < this.length; i++) {  
        if (this[i] == element) {  
            return true;  
        }  
    }  
    return false;  
};
function addUrlParam(url, name, value) {
	var newUrl = "";
	var reg = new RegExp("(^|)" + name + "=([^&]*)(|$)");
	var tmp = name + "=" + value;
	if (url.match(reg) != null) {
		newUrl = url.replace(eval(reg), tmp);
	} else {
		if (url.match("[\?]")) {
			newUrl = url + "&" + tmp;
		} else {
			newUrl = url + "?" + tmp;
		}
	}
	return newUrl;
}
function getCookie(name) { 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return decodeURIComponent(arr[2]); 
    else 
        return null; 
}






/**
 * 特殊文字check
 */
$(function() {
    $.extend({
        htmlspecialchars : function htmlspecialchars(ch) {
            if (ch != null && ch != undefined) {
                ch = ch.replace(/&/g, "&amp;");
                ch = ch.replace(/"/g, "&quot;");
                ch = ch.replace(/'/g, "&#039;");
                ch = ch.replace(/</g, "&lt;");
                ch = ch.replace(/>/g, "&gt;");
            }
            return ch;
        }
    });
    document.getElementsByTagName("form").onkeydown=function(e){
    	if(e.keyCode=="13"){
    		e.preventDefault();
    	}
    }
    
});

/**
 * 浮点数加法
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
 * 浮点数减法
 */
function floatSub(arg1, arg2) {
    var r1 = 0, r2 = 0, m, n, arr;
    arr = arg1.toString().split(".");
    if (arr.length > 1) {
        r1 = arr[1].length;
    }
    arr = arg2.toString().split(".");
    if (arr.length > 1) {
        r2 = arr[1].length;
    }
    n = Math.max(r1, r2);
    m = Math.pow(10, n);
    return round((floatMul(arg1, m) - floatMul(arg2, m)) / m, n);
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
 * 四舍五入
 */
function round(value, places) {
    if (!places) {
        places = 0;
    }
    var m = Math.pow(10, places);
    return Math.round(floatMul(value, m)) / m;
}


/**
 * 加载用户默认头像 - 将此函数的调用放置于img的onerror事件中
 * @returns
 */
function loadDefaultUserAvator(event) {
	event.target.src=Feng.ctxPath + "/static/img/user-default.png";
	event.target.onerror = null;// 避免放置在onerror中，重新加载的图片也不存在造成循环调用
}