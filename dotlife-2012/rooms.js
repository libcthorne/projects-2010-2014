var ROOMS = {
	rooms:new Array(),
	currentRoom:undefined,
}

ROOMS.register = function(room) {
	this.rooms.push(room);
}

ROOMS.set = function(roomID) {
	for (var i = 0; i < this.rooms.length; i++) {
		var room = this.rooms[i];
		
		if (room.id == roomID) {
			// Deinitialise current room
			if (this.currentRoom != undefined)
				this.currentRoom.deinit();
			
			// Set new room
			this.currentRoom = room;
			
			// Initialise new room
			this.currentRoom.init();
			
			return;
		}
	}
	
	console.log("Couldn't find room: " + roomID);
}

ROOMS.update = function() {
	this.currentRoom.update();
}

ROOMS.draw = function(context) {
	this.currentRoom.draw(context);
}