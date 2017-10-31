@/*
    select标签中各个参数的说明:
    name : select的名称
    id : select的id
    underline : 是否带分割线
    requiredTag : 是否需要在label前添加*标记
@*/
<div class="form-group">
    <label class="col-sm-3 control-label">
    @if(isNotEmpty(requiredTag) && requiredTag=="true"){
    	<span style="color: red">*</span>
    @}
    ${name}</label>
    <div class="col-sm-9">
        <select class="form-control" id="${id}" name="${id}" 
        	@if(isNotEmpty(disabled)){
            	disabled="${disabled}"
            @}
        >
            ${tagBody!}
        </select>
        @if(isNotEmpty(hidden)){
            <input class="form-control" type="hidden" id="${hidden}" value="${hiddenValue!}">
        @}
    </div>
</div>
@if(isNotEmpty(underline) && underline == 'true'){
    <div class="hr-line-dashed"></div>
@}


