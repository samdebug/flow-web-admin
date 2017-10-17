$(function(){
	/*图标tips样式设置*/
	
	 //获取鼠标位置
/*    function mousePosition(ev){
   	 var e = ev || window.event;
        var scrollX = $("#wrapperOut").scrollLeft();
        var scrollY = $("#wrapperOut").scrollTop();
        var x =  e.clientX;
        var y = e.clientY;
        return { 'x': x, 'y': y };
    }
    
    document.onmousemove = mouseMove;
    function mouseMove(ev){
        ev = ev || window.event;
        var mousePos = mousePosition(ev);
        $(".btnTips").css({
            left:mousePos.x+5,
            top:mousePos.y+5
        })
    };*/
    
	var timer;
	$(".btnWrapper").hover(function (){
		var that = $(this);
		timer = setTimeout(function(){
			that.find(".btnTips").show();
	    },600);  
	},function (){
		clearTimeout(timer);
		$(this).find(".btnTips").hide();  
	}); 

	$(".btn.btn-primary").mouseout(function(){
		  $(this).css({
			  "background-color": "transparent","color": "#999","border": "none"
		  });
	});
});

