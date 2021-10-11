// 初始化全局变量
var problem_name = null;
var problem_picture = null;
var problem_author_id = null;
var points_set = new Set();
var points_location_x = new Map();
var points_location_y = new Map();
var initial_equals_str_set = new Set();
var need_prove_equal_str = null;

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
		
		// 点击绘制面板的按钮
		$("#button_draw_select").on("click", function(){
			current_draw = null;
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
		
		// 点击按钮后在画板上创建点或线段，定义点或线段相关的事件
		$("#draw_canvas").on("click", function(e){
			if (current_draw == "point") { 
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
				new_point_group = new Group([new_point_shape, new_point_text]);
				new_point_shape.group = new_point_group;
				new_point_shape.as_first_of_lines = new Set();
				new_point_shape.as_last_of_lines = new Set();
				all_points.set(new_point_text.content, new_point_shape);
				// 定义相关事件效果
				new_point_shape.onMouseEnter = function(event) {
					this.strokeColor = "#ff5500";
					this.strokeWidth = 3;
				}
				new_point_shape.onMouseLeave = function(event) {
					this.strokeColor = "#000000";
					this.strokeWidth = 1;
				}
				new_point_shape.onMouseDrag = function(event) {
					if (current_draw == null) {
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
				new_point_text.onClick = function(event) {
					if (current_draw == null) {
						current_point_text = this;
						$("#dialog_rename_point_name").dialog("open");
						$("#dialog_rename_point_name").children("input").val(this.content);
					}
				}
				new_point_shape.onClick = function(event) {
					if (current_draw == "line") {
						if (line_endpoint_selected == null) {
							line_endpoint_selected = this;
						} else if (line_endpoint_selected == this) {
							$.messager.alert('无效', '线段的两个端点不能是同一个');
						} else {
							var new_line = new Path.Line({
							    from: line_endpoint_selected.position,
							    to: this.position,
								strokeWidth: 3, 
								strokeColor: "#55557f"
							});
							line_endpoint_selected.as_first_of_lines.add(new_line);
							this.as_last_of_lines.add(new_line);
							// new_line.insertBelow(line_endpoint_selected);
							// new_line.insertBelow(this);
							line_endpoint_selected = null;
						}
					}
				}
			}
		});
		
		// 其他对话框效果
		renamePoint = function() {
			var new_name = $("#dialog_rename_point_name").children("input").val();
			if (new_name == "") {
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
		
		// 效果包括：1.创建点及对应名称 2.创建点之间连线 3.点在线上高亮提示 4.拖拽改变点位置以及连线位置 5.保存
	}
	
	// 输入新已知条件后提交按钮效果
	$("#button_submit_input_condition").on("click", function() {
		var condition_str_left = $("#new_conditions_input_left").val();
		var condition_str_right = $("#new_conditions_input_right").val();
		if (condition_str_left != '' && condition_str_right != '') {
			var condition_type = $("#new_condition_type_selector").val();
			var the_new_condition = condition_str_left + condition_type + condition_str_right;
			if (initial_equals_str_set.has(the_new_condition)) {
				$.messager.alert('无效', '输入了重复的条件');
			} else {
				initial_equals_str_set.add(the_new_condition);
				$("#total_known_conditions_list").append("<li><span>" + the_new_condition + "</span>" +
					"<a class='easyui-linkbutton button_condition_delete' onclick='delete_condition(this)'>删除</a></li>");
				$.parser.parse($("#total_known_conditions_list"));
				$("#total_known_conditions_count").text(initial_equals_str_set.size + "个");
			}
		}
	});
	
	// 已知条件删除按钮效果
	delete_condition = function(the_button) {
		initial_equals_str_set.delete($(the_button).parent().children("span").text());
		$(the_button).parent().remove();
		$("#total_known_conditions_count").text(initial_equals_str_set.size + "个");
	};
	
	// 输入待求证等量关系后提交按钮效果
	$("#button_submit_need_prove").on("click", function() {
		var condition_str_left = $("#new_need_prove_input_left").val();
		var condition_str_right = $("#new_need_prove_input_right").val();
		if (condition_str_left != '' && condition_str_right != '') {
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
		let a = new Map();
		a.set("A", 2.0);
		a.set("B", "5.0");
		alert(mapToJson(a));
		alert(setToJson(initial_equals_str_set));
		// $.post("/geometry3/startNewProblem",
		//     {
		//       "problem_name": problem_name,
		//       "problem_picture": problem_picture, 
		// 	  "problem_author_id": problem_author_id, 
		// 	  "points_set": setToJson(points_set), 
		// 	  "points_location_x": mapToJson(points_location_x), 
		// 	  "points_location_y": mapToJson(points_location_y), 
		// 	  "initial_equals_str_set": setToJson(initial_equals_str_set), 
		// 	  "need_prove_equal_str": need_prove_equal_str
		//     },
		//     function(data,status){
		//       alert("数据：" + data + "\n状态：" + status);
		// 	}
		// );
	});
	
});