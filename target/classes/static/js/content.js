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

    //双击table事件
    $('table').on('dbl-click-row.bs.table', function (e, row, element){  
        var hrefTag = element.find("a");
        if (hrefTag.length){
            hrefTag.click();
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
                return false
            }
        }
        $(searchbox).css({"border":"1px solid #e5e6e7","padding":"20px 20px 10px 0px"});
    }

    $(".layer-date").click();

    console.log();
})  