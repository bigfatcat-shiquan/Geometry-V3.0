// 初始化部分元素
var current_input_box = null;

// 公共触发器
$(document).ready(function() {

	// 用户登录状态检查
	$.ajax({
		url: "/getCurrentUser",
		type: "GET",
		dataType: "text",
		success: function(data) {
			var login_status_result = JSON.parse(data);
			if (login_status_result['success']) {
				// 已登录，则更新头像等信息
				$("#login_status_href").css("display", "none");
				$("#login_status_picture").attr("src", login_status_result['user_picture']).css("display", "block");
				$("#detail_user_name").text("用户名： " + login_status_result['user_name']);
				$("#detail_user_nickname").text("昵称： " + login_status_result['user_nickname']);
				$("#detail_register_date").text("注册时间： " + login_status_result['register_date']);
			}
		},
		error: function() {
			$.messager.alert('错误', '后台系统获取用户信息出现故障!');
		}
	});

	// 鼠标悬停用户头像，弹出选项卡效果
	$("#login_status_picture").mouseover(function() {
		$("#user_info_panel").css("left", $(this).offset().left - 50).css("top", $(this).offset().top + 45);
		$("#user_info_panel").css("display", "block");
	});
	$("#login_status_picture").mouseout(function() {
		$("#user_info_panel").css("display", "none");
	});

	// 鼠标悬停header模块时，下划线效果
	let header_href_texts = $(".header_href");
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

function getImagePortion(canvas, imgObj, startX, startY, cropWidth, cropHeight, newWidth, newHeight) {
	// 构建结果图层
	var tnCanvas = document.createElement('canvas');
	var tnCanvasContext = tnCanvas.getContext('2d');
	tnCanvas.width = newWidth;
	tnCanvas.height = newHeight;
	// 构建缓冲图层
	var bufferCanvas = document.createElement('canvas');
	var bufferContext = bufferCanvas.getContext('2d');
	bufferCanvas.width = canvas.width();
	bufferCanvas.height = canvas.height();
	bufferContext.drawImage(imgObj, 0, 0, imgObj.width, imgObj.height, 0, 0, bufferCanvas.width, bufferCanvas.height);
	// 将缓冲图层绘制到结果图层以实现图片截取
	tnCanvasContext.drawImage(bufferCanvas, startX, startY, cropWidth, cropHeight, 0, 0, newWidth, newHeight);
	return tnCanvas.toDataURL("image/png");
}

function scrollBatchAppend(content_object, scroll_object, html_list, batch_size=30) {
	// 先对列表html元素按顺序分组
	var html_groups = [];
	var html_group = [];
	for (var i=0; i<html_list.length; i++) {
		if (i % batch_size === 0) {
			html_group = [];
			html_groups.push(html_group);
		}
		html_group.push(html_list[i]);
	}
	html_groups.push(html_group);
	// 第一组先加入，并计数
	html_groups[0].forEach((k) => content_object.append(k));
	var count = 1;
	// 绑定滚轮事件，分批次渲染
	scroll_object.scroll(function() {
		let ratio = $(this).scrollTop() / $(this).height();
		if(ratio > count && count < html_groups.length){
			html_groups[count].forEach((k) => content_object.append(k));
			count = parseInt(ratio) + 1;
		}
	});
}
