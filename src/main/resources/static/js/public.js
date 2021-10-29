// 初始化部分元素
var current_input_box = null;

// 公共触发器
$(document).ready(function() {
	
	// 鼠标悬停header模块时，下划线效果
	let header_href_texts = $(".header_content a");
	let line = $("#header_text_underline");   
	header_href_texts.mouseover(function() {   
		if (line.is(":animated")) {
			line.stop();
		}   
		line.css("left", $(this).offset().left + $(this).width() / 2).css("width", 0);
        line.animate({left: $(this).offset().left, width: $(this).width()}, "fast");
    });   
	header_href_texts.mouseout(function() {
		if (line.is(":animated")) {
			line.stop();
		}   
		line.animate({left: $(this).offset().left + $(this).width() / 2, width: 0}, "fast");
	});
	
	// 鼠标悬停序号图标时，闪烁效果
	let guide_icons = $(".guide_icon");
	guide_icons.mouseover(function() {
		if ($(this).is(":animated")) {
			$(this).stop();
		}
		$(this).animate({opacity: 0.5}, "fast")
	});
	guide_icons.mouseout(function() {
		if ($(this).is(":animated")) {
			$(this).stop();
		}
		$(this).animate({opacity: 1.0}, "fast")
	});
	
	// 输入工具按钮效果
	$(":text").on("click", function() {
		current_input_box = $(this);
	});
	$(".button_input").on("click", function() {
		if (current_input_box != null) {
			current_input_box.val(current_input_box.val() + $(this).text());
		}
	});
	
	// 开关按钮效果
	$(".switchbutton_setting_1").each(function () {
		// 开关元素注入
		$(this).append("<span></span><p>说明</p>");
		$.parser.parse($(this));
		// 开关初始状态
		if ($(this).data('switch').checked) {
			$(this).children("p").text($(this).data('switch').onText);
		} else {
			$(this).css("background", "rgb(176,190,203)");
			$(this).children("span").css("left", "+=50px");
			$(this).children("p").text($(this).data('switch').offText);
			$(this).children("p").css("left", "-=30px");
		}
		// 开关点击状态
		$(this).on("click", function () {
			$(this).data('switch').checked = !$(this).data('switch').checked;
			if ($(this).data('switch').checked) {
				$(this).css("background", "rgb(54, 164, 255)");
				$(this).children("span").animate({left: "-=50px"}, "fast");
				$(this).children("p").text($(this).data('switch').onText);
				$(this).children("p").animate({left: "+=30px"}, "fast");
			} else {
				$(this).css("background", "rgb(176,190,203)");
				$(this).children("span").animate({left: "+=50px"}, "fast");
				$(this).children("p").text($(this).data('switch').offText);
				$(this).children("p").animate({left: "-=30px"}, "fast");
			}
		});
	});

});

// 公共工具方法
function setToJson(this_set) {
	return JSON.stringify(Array.from(this_set));
}

function mapToJson(this_map) {
	let temp_object = {};
	this_map.forEach(function(item, key) {
		temp_object[key] = item;
	})
	return JSON.stringify(temp_object);
}
