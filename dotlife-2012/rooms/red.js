var room = {
	id:"red",

	doors:new Array(
		new Door("#0000FF", 90, CANVAS_HEIGHT, 40, 30, "start")
	),
	
	entities:undefined,
	
	init:function() {
		this.entities = new Array();
		this.entities.push(new BouncyBall(40, 40));
	},
	
	deinit:function() {
		for (var i = 0; i < this.entities.length; i++) {
			var entity = this.entities[i];
			entity.remove();
		}
		
		this.entities = undefined;
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