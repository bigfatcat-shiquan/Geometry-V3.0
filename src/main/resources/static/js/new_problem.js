// 初始化全局变量
var problem_name = null;
var problem_picture = null;
var problem_author_id = null;
var points_set = new Set();
var points_location_x = new Map();
var points_location_y = new Map();
var initial_equals_str_set = new Set();
var need_prove_equal_str = null;

// 初始化函数名称
var renamePoint;
var setProblemName;
var saveDraw;
var deleteCondition;

// 触发器
$(document).ready(function() {
	
	// 画板效果，装载paper库，点的添加，重命名，线段连接，拖拽移动，高亮，删除，保存
	paper.install(window);
	window.onload = function() { 
		paper.setup(document.getElementById("draw_canvas"));
		
		// 初始化中间变量
		var canvas_left = $("#draw_canvas").offset().left;
		var canvas_top = $("#draw_canvas").offset().top;
		var current_draw = null;
		var all_points = new Map();
		var current_point_name_ascii = 65;
		var current_point_text = null;
		var line_endpoint_selected = null;
		
		// 初始化起始图形对象及分组
		var temp_point = new Shape.Circle({
			center: [1, 1],
			radius: 5,
			fillColor: "#55557f",
			strokeWidth: 1, 
			strokeColor: "#000000",
			visible: false
		});
		var temp_line = new Path.Line({
			from: [1, 1],
			to: [2, 2],
			strokeWidth: 3, 
			strokeColor: "#55557f", 
			visible: false
		});
		var line_group = new Group([]);
		
		// 点击绘制面板的按钮
		$("#button_draw_select").on("click", function(){
			current_draw = "select";
			$(this).linkbutton("select");
			$("#draw_tool_buttons").children().not("#" + $(this).attr('id')).linkbutton("unselect");
		});
		$("#button_draw_point").on("click", function(){
			current_draw = "point";
			$(this).linkbutton("select");
			$("#draw_tool_buttons").children().not("#" + $(this).attr('id')).linkbutton("unselect");
		});
		$("#button_draw_line").on("click", function(){
			current_draw = "line";
			$(this).linkbutton("select");
			$("#draw_tool_buttons").children().not("#" + $(this).attr('id')).linkbutton("unselect");
		});
		$("#button_draw_delete").on("click", function(){
			current_draw = "delete";
			$(this).linkbutton("select");
			$("#draw_tool_buttons").children().not("#" + $(this).attr('id')).linkbutton("unselect");
		});
		$("#button_draw_save").on("click", function(){
			current_draw = null;
			$("#draw_tool_buttons").children().linkbutton("unselect");
			saveDraw();
		});
		
		// 点击按钮后在画板上创建点或线段，定义点或线段相关的事件
		$("#draw_canvas").on("click", function(e){
			if (current_draw === "point") {
				// 创建点对象及点名称对象
				if (current_point_name_ascii >= 91) {
					$.messager.alert('无效', '图中点的数量已经太多了');
					return;
				}
				while (all_points.has(String.fromCharCode(current_point_name_ascii))) {
					current_point_name_ascii++;
				}
				var click_coordinate = new Point(e.pageX - canvas_left, e.pageY - canvas_top);
				var new_point_shape = new Shape.Circle({
				    center: click_coordinate,
				    radius: 5,
				    fillColor: "#55557f",
					strokeWidth: 1, 
					strokeColor: "#000000"
				});	
				var new_point_text = new PointText({
					point: [click_coordinate['x'] - 20, click_coordinate['y'] + 20], 
					justification: "center", 
					fillColor: "black",
					fontFamily: "Times New Roman",
					fontWeight: "bold",
					fontSize: 20, 
					content: String.fromCharCode(current_point_name_ascii++)
				});
				new_point_shape.point_text = new_point_text;
				new_point_text.point_shape = new_point_shape;
				new_point_shape.group = new Group([new_point_shape, new_point_text]);
				new_point_shape.as_first_of_lines = new Set();
				new_point_shape.as_last_of_lines = new Set();
				all_points.set(new_point_text.content, new_point_shape);
				// 定义相关事件效果
				new_point_shape.onMouseEnter = function() {
					this.strokeColor = "#ff5500";
					this.strokeWidth = 3;
				}
				new_point_shape.onMouseLeave = function() {
					this.strokeColor = "#000000";
					this.strokeWidth = 1;
				}
				new_point_shape.onMouseDrag = function(event) {
					if (current_draw === "select") {
						this.group.position['x'] += event.delta['x'];
						this.group.position['y'] += event.delta['y'];
						this.as_first_of_lines.forEach(function(value){
							value.firstSegment.point.x += event.delta['x'];
							value.firstSegment.point.y += event.delta['y'];
						});
						this.as_last_of_lines.forEach(function(value){
							value.lastSegment.point.x += event.delta['x'];
							value.lastSegment.point.y += event.delta['y'];
						});
					}
				}
				new_point_text.onClick = function() {
					if (current_draw === "select") {
						current_point_text = this;
						$("#dialog_rename_point_name").dialog("open");
						$("#dialog_rename_point_name").children("input").val(this.content);
					}
				}
				new_point_shape.onClick = function() {
					if (current_draw === "line") {
						if (line_endpoint_selected == null) {
							line_endpoint_selected = this;
						} else if (line_endpoint_selected === this) {
							$.messager.alert('无效', '线段的两个端点不能是同一个');
						} else {
							// 创建线段对象
							var new_line = new Path.Line({
							    from: line_endpoint_selected.position,
							    to: this.position,
								strokeWidth: 3, 
								strokeColor: "#55557f"
							});
							line_endpoint_selected.as_first_of_lines.add(new_line);
							this.as_last_of_lines.add(new_line);
							line_group.addChild(new_line);						
							line_endpoint_selected = null;
							temp_line.visible = false;
							// 定义相关事件效果
							new_line.onMouseEnter = function() {
								this.strokeColor = "#ff5500";
								this.strokeWidth = 4;
							}
							new_line.onMouseLeave = function() {
								this.strokeColor = "#55557f";
								this.strokeWidth = 3;
							}
							new_line.onClick = function(){
								if (current_draw === "delete") {
									this.remove();
								}
							}
						}
					}
					if (current_draw === "delete") {
						this.group.remove();
						all_points.delete(this.point_text.content);
						this.as_first_of_lines.forEach(function(value){
							value.remove();
						});
						this.as_last_of_lines.forEach(function(value){
							value.remove();
						});
					}
				}
			}
		});
		
		// 面板相关光标提示效果
		$("#draw_canvas").mouseenter(function() {
			if (current_draw === "point") {
				temp_point.visible = true;
			}
			if (current_draw === "delete") {
				$("#draw_canvas").css("cursor", "url('../static/img/buttons/draw_button_delete_2.ico'), auto");
			}
		});
		$("#draw_canvas").mousemove(function(e) {
			if (current_draw === "point") {
				temp_point.position = new Point(e.pageX - canvas_left, e.pageY - canvas_top);
			}
			if (current_draw === "line" && line_endpoint_selected != null) {
				var over_coordinate = new Point(e.pageX - canvas_left, e.pageY - canvas_top);
				temp_line.firstSegment.point.x = line_endpoint_selected.position['x'];
				temp_line.firstSegment.point.y = line_endpoint_selected.position['y'];
				temp_line.lastSegment.point.x = over_coordinate['x'];
				temp_line.lastSegment.point.y = over_coordinate['y'];
				temp_line.visible = true;
			}
		});
		$("#draw_canvas").mouseleave(function() {
			temp_point.visible = false;
			temp_line.visible = false;
			line_endpoint_selected = null;
			$("#draw_canvas").css("cursor", "default");
		});
		
		// 其他对话框效果
		renamePoint = function() {
			var new_name = $("#dialog_rename_point_name").children("input").val();
			if (new_name === "") {
				$.messager.alert('无效', '点名称不能为空');
				$("#dialog_rename_point_name").dialog("close");
				return;
			}
			if (all_points.has(new_name)) {
				$.messager.alert('无效', '点名称与已有的重复');
				$("#dialog_rename_point_name").dialog("close");
				return;
			}
			if (current_point_text != null) {
				var old_name = current_point_text.content;
				current_point_text.content = new_name;
				all_points.delete(old_name);
				all_points.set(new_name, current_point_text.point_shape);
				$("#dialog_rename_point_name").dialog("close");
			}
		}
		setProblemName = function () {
			problem_name = $("#dialog_input_problem_name").children("input").val();
			if (problem_name === "") {
				problem_name = "未命名题目";
			}
			$("#dialog_input_problem_name").dialog("close");
			$.messager.alert('提示', '绘制结果已成功保存');
		}
		
		// 保存绘制结果操作
		saveDraw = function() {
			var min_x = $("#draw_canvas").width() / 2;
			var max_x = $("#draw_canvas").width() / 2 + 10;
			var min_y = $("#draw_canvas").height() / 2;
			var max_y = $("#draw_canvas").height() / 2 + 10;
			var ratio = $("#draw_canvas").height() / $("#draw_canvas").width();
			// 点坐标信息
			points_set = new Set();
			points_location_x = new Map();
			points_location_y = new Map();
			all_points.forEach(function(point_shape, point_name) {
				let point_x = point_shape.position['x'];
				let point_y = point_shape.position['y'];
				points_set.add(point_name);
				points_location_x.set(point_name, point_x.toFixed(2));
				points_location_y.set(point_name, point_y.toFixed(2));
				if (point_x < min_x) min_x = point_x;
				if (point_x > max_x) max_x = point_x;
				if (point_y < min_y) min_y = point_y;
				if (point_y > max_y) max_y = point_y;
			});
			// 截取题目图片
			var min_width = max_x - min_x + 120;
			var min_height = max_y - min_y + 80;
			var cropWidth = Math.max(min_width, min_height/ratio);
			var cropHeight = Math.max(min_height, min_width*ratio);
			var image = new Image();
			image.src = document.getElementById("draw_canvas").toDataURL("image/png");
			image.onload = function() {
				problem_picture = getImagePortion($("#draw_canvas"), image, min_x-60, min_y-40,
													cropWidth, cropHeight, cropWidth, cropHeight);
			}
			// 题目名称
			$("#dialog_input_problem_name").dialog("open");
		}
	}
	
	// 输入新已知条件后提交按钮效果
	$("#button_submit_input_condition").on("click", function() {
		var condition_str_left = $("#new_conditions_input_left").val();
		var condition_str_right = $("#new_conditions_input_right").val();
		if (checkMathStrInput([condition_str_left, condition_str_right])) {
			var condition_type = $("#new_condition_type_selector").val();
			var the_new_condition = condition_str_left + condition_type + condition_str_right;
			if (initial_equals_str_set.has(the_new_condition)) {
				$.messager.alert('无效', '输入了重复的条件');
			} else {
				initial_equals_str_set.add(the_new_condition);
				$("#total_known_conditions_list").append("<li><span>" + the_new_condition + "</span>" +
					"<a class='easyui-linkbutton button_condition_delete' onclick='deleteCondition(this)'>删除</a></li>");
				$.parser.parse($("#total_known_conditions_list"));
				$("#total_known_conditions_count").text(initial_equals_str_set.size + "个");
			}
		}
	});
	
	// 已知条件删除按钮效果
	deleteCondition = function(the_button) {
		initial_equals_str_set.delete($(the_button).parent().children("span").text());
		$(the_button).parent().remove();
		$("#total_known_conditions_count").text(initial_equals_str_set.size + "个");
	};
	
	// 输入待求证等量关系后提交按钮效果
	$("#button_submit_need_prove").on("click", function() {
		var condition_str_left = $("#new_need_prove_input_left").val();
		var condition_str_right = $("#new_need_prove_input_right").val();
		if (checkMathStrInput([condition_str_left, condition_str_right])) {
			var condition_type = $("#new_need_prove_type_selector").val();
			var the_need_prove = condition_str_left + condition_type + condition_str_right;
			if (need_prove_equal_str != null) {
				$.messager.alert('无效', '待求证的等量关系式已有，请先清除再重新添加');
			} else {
				need_prove_equal_str = the_need_prove;
				$("#final_need_prove_str").text(the_need_prove);
			}
		}
	});
	
	// 待求证等量关系清除按钮效果
	$("#button_delete_need_prove").on("click", function() {
		if (need_prove_equal_str != null) {
			need_prove_equal_str = null;
			$("#final_need_prove_str").text("");
		}
	});

	// 数学表达式输入校验
	function checkMathStrInput(math_str_list=[]) {
		var check_ok = true;
		var legal_char;
		math_str_list.forEach(function(math_str) {
			// 检查是否为空字符串
			if (math_str === '') {
				check_ok = false;
				return;
			}
			// 检查是否在规范字符列表中
			if (math_str.indexOf('°') !== -1) {
				legal_char = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '∠', '°', '+', '*', '(', ')'];
			} else {
				legal_char = ['∠', '+', '*', '(', ')'];
			}
			for(let math_char of math_str){
				if (!legal_char.includes(math_char) && !points_set.has(math_char)) {
					$.messager.alert({title:'无效',
						msg:'输入的数学表达式 ' + math_str + ' 不符合规范，' +
							'请输入与上方所绘制图形相匹配的表达式，且倍数或次方请转化为加法或乘法形式输入',
						width: 600});
					check_ok = false;
					return;
				}
			}
		});
		return check_ok;
	}

	// 保存并提交题目操作，异步请求服务
	$("#button_start_the_new_problem").mouseover(function() {
		if ($(this).is(":animated")) {
			$(this).stop();
		}
		$(this).animate({opacity: 0.5}, "fast")
	});
	$("#button_start_the_new_problem").mouseout(function() {
		if ($(this).is(":animated")) {
			$(this).stop();
		}
		$(this).animate({opacity: 1.0}, "fast")
	});
	$("#button_start_the_new_problem").on("click", function() {
		// 整理题目信息并校验
		if (problem_name === null || need_prove_equal_str === null || points_set.size === 0) {
			$.messager.alert('无效', '题目信息缺失：没有绘制题目图并保存，或没有输入待求证结论信息');
			return;
		}
		var data_object = {
			"problem_name": problem_name,
			"problem_picture": problem_picture,
			"problem_author_id": problem_author_id,
			"points_set": setToJson(points_set),
			"points_location_x": mapToJson(points_location_x),
			"points_location_y": mapToJson(points_location_y),
			"initial_equals_str_set": setToJson(initial_equals_str_set),
			"need_prove_equal_str": need_prove_equal_str
		};
		// 异步请求保存该题目，并转到该题目详情页
		$.ajax({
			url: "/startNewProblem",
			type: "POST",
			data: JSON.stringify(data_object),
			contentType: "application/json",
			success: function(data) {
				window.location.replace("/getProblemPage" + "?id=" + JSON.parse(data)["problem_id"]);
			},
			beforeSend: function() {},
			error: function () {
				$.messager.alert('错误', '提交题目信息出现错误');
			}
		});
	});

});
