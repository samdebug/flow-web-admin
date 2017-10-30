var $parentNode = window.parent.document;

function $childNode(name) {
    return window.frames[name]
}

// tooltips
$('.tooltip-demo').tooltip({
    selector: "[data-toggle=tooltip]",
    container: "body"
});

// 使用animation.css修改Bootstrap Modal
$('.modal').appendTo("body");

$("[data-toggle=popover]").popover();

//折叠ibox
$('.collapse-link').click(function () {
    var ibox = $(this).closest('div.ibox');
    var button = $(this).find('i');
    var content = ibox.find('div.ibox-content');
    content.slideToggle(200);
    button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
    ibox.toggleClass('').toggleClass('border-bottom');
    setTimeout(function () {
        ibox.resize();
        ibox.find('[id^=map-]').resize();
    }, 50);
});

//关闭ibox
$('.close-link').click(function () {
    var content = $(this).closest('div.ibox');
    content.remove();
});

//判断当前页面是否在iframe中
if (top == this) {
    var gohome = '<div class="gohome"><a class="animated bounceInUp" href="index.html?v=4.0" title="返回首页"><i class="fa fa-home"></i></a></div>';
    $('body').append(gohome);
}

//animation.css
function animationHover(element, animation) {
    element = $(element);
    element.hover(
        function () {
            element.addClass('animated ' + animation);
        },
        function () {
            //动画完成之前移除class
            window.setTimeout(function () {
                element.removeClass('animated ' + animation);
            }, 2000);
        });
}

//拖动面板
function WinMove() {
    var element = "[class*=col]";
    var handle = ".ibox-title";
    var connect = "[class*=col]";
    $(element).sortable({
            handle: handle,
            connectWith: connect,
            tolerance: 'pointer',
            forcePlaceholderSize: true,
            opacity: 0.8,
        })
        .disableSelection();
};

$(function () {
    //去除重置按钮
    $(".reset").children().each(function(){
        if ($.trim($(this).text()) == "重置"){
            $(this).attr('id','undo-btn');
            $(this).hide();
        }else if($.trim($(this).text()) == "查询"){
            $(this).attr('id','search-btn');
        }else{
            $(this).hide();
        }
    });

    //监听回车事件
    $('.gray-bg').bind('keypress',function(event){
        if(event.keyCode == "13") {
            $("#search-btn").click();
        }
    });

    //表格加载成功时事件
    $('table').on('load-success.bs.table', function (e, data) {
        var hrefTag = $(e.target).find("a");
        var btnText = $(".btnWrapper").eq(1).children("span").text();
        if (hrefTag.length && btnText == "编辑"){
            hrefTag.each(function(){
                $(this).click(function(){
                    $("#editBtn").show();
                    $(".layui-layer-setwin").last().find("#editBtn").show();
                })
            })
        }
    })

    //单击显示详情事件
    $('table').on('click-row.bs.table', function (e, row, element){
        var hrefTag = element.find("a");
        var btnText = $(".btnWrapper").eq(1).children("span").text();
        if (hrefTag.length && btnText == "编辑"){
            hrefTag.click(function(){
                $(".layer-date").click();
                $(".layui-layer-setwin").last().find("#editBtn").show();
            })
        }
        initButton();
    }); 

    //双击table事件
    $('table').on('dbl-click-row.bs.table', function (e, row, element){  
        var hrefTag = element.find("a");
        var btnText = $(".btnWrapper").eq(1).children("span").text();
        if (hrefTag.length && btnText == "编辑"){
            $(".layui-layer-iframe").remove();
            $(".layui-layer-shade").remove();
            hrefTag.click();
            $(".layer-date").click();
            $(".layui-layer-setwin").last().find("#editBtn").show();
            /*if (!$("#editBtn").length){
                var element = $(".layui-layer-setwin");
                element.prepend('<a title="编辑" style="color: #2f2e3d;" id="editBtn" href="javascript:;"><i style="font-size: 15px;" class="fa fa-icon fa-pencil"></i></a>');
            }*/
            initButton();
        }
    }); 

    let newButton = $(".columns-right").find("button");
    newButton.each(function(){
        let that = $(this);
        if (that.attr("title") == "刷新"){
            that.after('<span class="btnTips" id="btnTips-refresh" style="display: none;">刷新</span>');
            that.hover(function (){
                that.attr("title","");
                timer = setTimeout(function(){
                    $(".columns-right").find("#btnTips-refresh").show();
                },600);  
            },function (){
                that.attr("title","刷新");
                clearTimeout(timer);
                $(".columns-right").find("#btnTips-refresh").hide();  
            });
        }else{
            that.after('<span class="btnTips" id="btnTips-column" style="display: none;left: 8px;">列</span>');
            that.hover(function (){
                that.attr("title","");
                timer = setTimeout(function(){
                    $(".columns-right").find("#btnTips-column").show();
                },600);  
            },function (){
                that.attr("title","列");
                clearTimeout(timer);
                $(".columns-right").find("#btnTips-column").hide();  
            });
        }
        
    });

    if (!$(".hidden-xs").find(".btnWrapper").length){
        $(".hidden-xs").find('button').each(function(){
            let that = $(this);
            let icon = that.find("i");
            let text = $.trim(that[0].innerText);
            var X = that.offset().top;
            var Y = that.offset().left;
            that.text(""); 
            that.append(icon);
            that.after('<span class="btnTips" style="display: none;left:8px">' + text + '</span>');
            that.hover(function (){
                timer = setTimeout(function(){
                    that.next("span").css("left",Y-80);
                    that.next('span').show();
                },600);  
            },function (){
                clearTimeout(timer);
                that.next('span').hide();  
            }); 
        })
    }

    //添加查询边框
    var searchbox = $(".row-lg").children().children().get(0);
    if (searchbox){
        var searchboxClass = $(searchbox).attr("class");
        if(searchboxClass != undefined){
            if (searchboxClass.indexOf("bootstrap-table") >= 0 || searchboxClass.indexOf("noborder") >= 0){
                return
            }
        }
        $(searchbox).css({"border":"1px solid #e5e6e7","padding":"20px 20px 10px 0px"});
    }
    //日期选择器点击第一次无效修复
    $(".layer-date").click();

    initButton();
    //直接跳到编辑页面
    function initButton(){
        if (window.parent.document != null){
           $(".layui-layer", window.parent.document).last().click(function(e){
                if (e.target.parentElement.id == "editBtn"){
                    $(".layui-layer-shade", window.parent.document).hide();
                    $(".layui-layer", window.parent.document).hide();
                    $(".btnWrapper", window.parent.document).eq(1).children("button").click();
                }
            })
        }
    }
    
    //弹出框自定义footer
    var layerIframe = $(".layui-layer-iframe", window.parent.document);
    var layerButton = $($(".layui-layer-iframe", window.parent.document).children(".layui-layer-content").children().contents().find(".btn-group-m-t").find("button"));
    if (layerButton.length){
        var buttonHtml = ""
        layerButton.each(function(){
            if (!$(this).attr("id")){
                if ($.trim($(this).text()) == "提交" || $.trim($(this).text()) == "保存"){
                    $(this).attr("id","submitFooterBtn");
                }else{
                    $(this).attr("id","cancelFooterBtn");
                }
            }
            var cloneBtn = $(this).clone(true);
            cloneBtn.children("i").remove();
            if ($.trim(cloneBtn.text()) == "提交" || $.trim($(this).text()) == "保存"){
                cloneBtn.attr("class","btn btn-confirm");
                cloneBtn.text($.trim("提交"));
            }else{
                cloneBtn.attr("class","btn btn-cancel");
                cloneBtn.text($.trim(cloneBtn.text()));
            }
            buttonHtml = buttonHtml + cloneBtn[0].outerHTML;
        });
        var footer = '<div class="modal-footer" style="display: block;"><div class="bootstrap-dialog-footer"><div class="bootstrap-dialog-footer-buttons">' + buttonHtml + '</div></div></div>'
        if (!layerIframe.last().find(".modal-footer").length){
            layerIframe.last().append(footer);
        }

        layerIframe.last().find(".modal-footer").find("button").each(function(){
            $(this).attr("onclick","");
            $(this).click(function(){
                $($(".layui-layer-iframe", window.parent.document).children(".layui-layer-content").children().contents().find(".btn-group-m-t").find('#' + $(this).attr("id"))).click();
            })
        })
    }
    //buttonHtml = '<button class="btn btn-danger" type="button" onclick="parent.layer.close(window.parent.Partner.layerIndex);"><i class="ace-icon fa fa-arrow-left"></i> 返 回</button>';
    layerButton.hide();
})          