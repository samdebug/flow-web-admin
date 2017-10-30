@/*
    时间查询条件标签的参数说明:

    name : 查询条件的名称
    id : 查询内容的input框id
    isTime : 日期是否带有小时和分钟(true/false)
    pattern : 日期的正则表达式(例如:"yyyy-MM-dd")
@*/
<div class="input-group">
    <div class="input-group-btn">
        <button data-toggle="dropdown" class="btn btn-white dropdown-toggle"
                type="button">${name}
        </button>
    </div>
    <input type="text" class="form-control layer-date"
           onclick="laydate.render({elem: '#${id}', format:'${pattern}',istime: ${isTime}})" id="${id}" name="${id}"/>
</div>