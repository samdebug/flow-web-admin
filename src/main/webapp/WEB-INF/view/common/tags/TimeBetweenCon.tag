@/*
    时间查询条件标签的参数说明:

    name : 查询条件的名称
    startId : 查询内容的input框id
    startValue：input初始值
    endValue：input初始值
    endId : 查询内容的input框id
    isTime : 日期是否带有小时和分钟(true/false)
    pattern : 日期的正则表达式(例如:"YYYY-MM-DD")
@*/
<div class="input-group">
    <div class="input-group-btn">
        <button data-toggle="dropdown" class="btn btn-white dropdown-toggle" style="margin-bottom: 0px;" type="button">${name}</button>
    </div> 
    
    <input type="text" class="form-control layer-date" 
    	@if(isNotEmpty(startValue)){
            	value="${startValue}"
        @}
    	onclick="laydate({istime: ${isTime}, format: '${pattern}'})" id="${startId}" name="${startId}" />
    
    <span class="input-group-addon input-group-btn"><i class="fa fa-exchange"></i></span>
    
    <input type="text" class="form-control layer-date" 
		    	@if(isNotEmpty(endValue)){
		            	value="${endValue}"
		        @}
           onclick="laydate({istime: ${isTime}, format: '${pattern}'})" id="${endId}" name="${endId}"/>
          
</div>