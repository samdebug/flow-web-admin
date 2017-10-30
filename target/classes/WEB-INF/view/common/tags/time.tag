@/*
        时间查询条件标签的参数说明:

    name : 查询条件的名称
    id : 查询内容的input框id
    isTime : 日期是否带有小时和分钟(true/false)
    pattern : 日期的正则表达式(例如:"yyyy-MM-dd")
@*/

<div class="form-group">
    <label class="col-sm-3 control-label">${name}</label>
     <div class="col-sm-9">
    	<input type="text" class="${class!}"
           onclick="laydate.render({elem: '#${id}', format:'${pattern}',istime: ${isTime},type:'datetime'})" id="${id}" name="${id}"    
           		@if(isNotEmpty(value)){
                    value="${tool.dateType(value)}" 
               @}
               @if (isNotEmpty(disabled)){
               		disabled="${isTime}" 
               @}
              />
       </div>
</div>