function Door(fillColor, x, y, width, height, roomID) {
	this.fillColor = fillColor;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.roomID = roomID;
	this.openX = 0;
}

Door.prototype.draw = function(context) {
	if (this.invisible)
		return;

	context.fillStyle = this.fillColor;
	context.fillRect(this.x, this.y-this.height, this.width-this.openX, this.height);

	context.fillStyle = "#000099";
	context.fillRect(this.x+this.width-this.openX, this.y-this.height, this.openX, this.height);

	// Frame

	context.strokeStyle = "#FFFFFF";
	context.lineWidth = 2;
	
	context.beginPath();
	context.moveTo(this.x, this.y-this.height);
	context.lineTo(this.x, this.y);
	context.stroke();
	
	context.beginPath();
	context.moveTo(this.x+this.width, this.y-this.height);
	context.lineTo(this.x+this.width, this.y);
	context.stroke();
	
	context.beginPath();
	context.moveTo(this.x, this.y-this.height);
	context.lineTo(this.x+this.width, this.y-this.height);
	context.stroke();

	// Handle

	if (this.width-8-this.openX > 0) {
		context.strokeStyle = "#FFFFFF";
		context.lineWidth = 2;
		context.beginPath();
		context.moveTo(this.x+this.width-8-this.openX, this.y-this.height/2+1);
		context.lineTo(this.x+this.width-8-this.openX, this.y-this.height/2-1);
		context.stroke();
	}
}

Door.prototype.update = function() {
	if (INPUT_IsSpacePressed()) {
		if (player.x > this.x && player.x < this.x+this.width) {
			this.opening = true;
			//player.stop();
			player.leftLeg = 0;
			player.rightLeg = 0;
			player.headShiftX = 0;
			player.headShiftY = 0;
			player.freeze();
		}
	}
	
	if (this.opening) {
		if (this.openX < 40) {
			this.openX += 3;
		} else if (player.size > 0.1) {
			player.size *= 0.9;
		} else {
			this.opening = false;
			this.openX = 0;
			player.unfreeze();
			WORLD.reset();
			ROOMS.set(this.roomID);
		}
	}
}