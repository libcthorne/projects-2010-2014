var room = {
	id:"green",

	doors:new Array(
		new Door("#0000FF", 60, CANVAS_HEIGHT, 40, 90, "start")
	),

	entities:undefined,
	testNPC:undefined,
	
	init:function() {
		var testNPC = new TestNPC();
		this.testNPC = testNPC;

		this.entities = new Array();
		this.entities.push(testNPC);
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

		/*// Text
		context.font = "12px Arial";
		context.fillStyle = "#000000"
		context.fillText("...", 510, CANVAS_HEIGHT-45);*/
	}
}

ROOMS.register(room);