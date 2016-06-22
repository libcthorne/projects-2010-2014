var room = {
	id:"start",

	doors:new Array(
		new Door("#FF0000", 50, CANVAS_HEIGHT, 40, 70, "red"),
		new Door("#00FF00", CANVAS_WIDTH-90, CANVAS_HEIGHT, 40, 70, "green")
	),

	init:function() {
	},
	
	deinit:function() {	
	},
	
	update:function() {
		for (var i = 0; i < this.doors.length; i++) {
			this.doors[i].update();
		}
	},
	
	draw:function(context) {
		// Background
		context.fillStyle = "#000000";
		context.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

		// Doors
		for (var i = 0; i < this.doors.length; i++) {
			this.doors[i].draw(context);
		}
	}
}

ROOMS.register(room);