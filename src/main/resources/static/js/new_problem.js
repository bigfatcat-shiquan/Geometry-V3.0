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
	
	// 画板效果，装载paper库，点的添加，线段连接，移动，高亮，删除，保存	
	paper.install(window);
	window.onload = function() { 
		paper.setup(document.getElementById("draw_canvas"));
		
		// 初始化中间变量
		var canvas_left = $("#draw_canvas").offset().left;
		var canvas_top = $("#draw_canvas").offset().top;
		var current_draw = null;
		var all_points = {};
		
		// 点击绘制面板按钮
		$("#button_draw_point").on("click", function(){
			current_draw = "point";
			$(this).css({"background-color": "#aaaa7f"});
		});
		$("#button_draw_line").on("click", function(){
			current_draw = "line";
			$(this).css({"background-color": "#aaaa7f"});
		});
		
		// 点击按钮创建点，定义点相关的事件
		$("#draw_canvas").on("click", function(e){
			if (current_draw == "point") { 
				var new_position = new Point(e.pageX - canvas_left, e.pageY - canvas_top);
				var new_point_shape = new Shape.Circle({
				    center: new_position,
				    radius: 5,
				    fillColor: "#55557f",
					strokeWidth: 1, 
					strokeColor: "#000000"
				});	
				new_point_shape.onMouseEnter = function(event) {
					this.strokeColor = "#ff5500";
					this.strokeWidth = 3;
				}
				new_point_shape.onMouseLeave = function(event) {
					this.strokeColor = "#000000";
					this.strokeWidth = 1;
				}
				new_point_shape.onMouseDrag = function(event) {
					this.position['x'] += event.delta['x'];
					this.position['y'] += event.delta['y'];
				}
				
			}
			
		});
		
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
