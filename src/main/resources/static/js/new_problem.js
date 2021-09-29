// 初始化全局变量
var points_set = new Set();
var points_location_x = new Map();
var points_location_y = new Map();
var initial_equals_str_set = new Set();
var need_prove_equal_str = "";

// 触发器
$(document).ready(function(){
	
	// 输入新已知条件后提交按钮效果
	$("#button_submit_input_condition").on("click", function() {
		var condition_str_left = $("#new_conditions_input_left").val();
		var condition_str_right = $("#new_conditions_input_right").val();
		if(condition_str_left != '' && condition_str_right != '') {
			var condition_type = $("#new_condition_type_selector").val();
			var the_new_condition = condition_str_left + condition_type + condition_str_right;
			if(initial_equals_str_set.has(the_new_condition)) {
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
	
});
