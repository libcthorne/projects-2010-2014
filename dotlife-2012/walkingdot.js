function WalkingDot(bodyColor, legColor, eyeColor, size, walkSpeed, x, y) {
	Entity.call(this, x, y);

	this.bodyColor = bodyColor;
	this.legColor = legColor;
	this.eyeColor = eyeColor;
	this.size = size;
	this.walkSpeed = walkSpeed;
	this.leftLeg = 0;
	this.rightLeg = 0;
	this.headShiftX = 0;
	this.headShiftY = 0;
	this.springValue = 0;
	this.stopped = true;
	this.orientation = 0; // 0=left, 1=right
	this.dead = false;
	
	this.radius = size;
}

WalkingDot.prototype = Object.create(Entity.prototype);
WalkingDot.prototype.constructor = WalkingDot;

WalkingDot.prototype.move = function() {
	if (this.stopped) {
		this.walkTime = curTime;
		this.stopped = false;
	}
	
	var t = curTime-this.walkTime;
	var legValue = Math.sin(t/200*this.walkSpeed/(this.size/10))*15*this.size/20;
	//var headShiftXValue = Math.sin(t/100*this.walkSpeed/3)*2*this.walkSpeed/2*this.size/30;
	//var headShiftYValue = Math.sin(t/100*this.walkSpeed/3)*2*this.walkSpeed/2*this.size/30;
	var headShiftXValue = 0;
	var headShiftYValue = 0;
	
	if (this.orientation == 0) {
		this.x -= this.walkSpeed;
		this.leftLeg = legValue;
		this.rightLeg = -legValue;
		this.headShiftX = headShiftXValue;
		//this.headShiftY = -headShiftYValue;
	} else {
		this.x += this.walkSpeed;
		this.leftLeg = legValue;
		this.rightLeg = -legValue;
		this.headShiftX = headShiftXValue;
		this.headShiftY = -headShiftYValue;
	}
}

WalkingDot.prototype.moveLeft = function() {
	this.orientation = 0;
	this.move();
}

WalkingDot.prototype.moveRight = function() {
	this.orientation = 1;
	this.move();
}

WalkingDot.prototype.stop = function() {
	this.stopped = true;
	this.headShiftX = 0;
	this.headShiftY = 0;

	var legValue = this.size/10;

	if (Math.abs(this.leftLeg) > legValue) {
		this.leftLeg += (this.leftLeg > 0) ? -legValue : legValue;
		this.rightLeg += (this.rightLeg > 0) ? -legValue : legValue;
		//this.headShiftX = -Math.sin(curTime/100)*4;
	} else {
		this.leftLeg = 0;
		this.rightLeg = 0;
	}
}

WalkingDot.prototype.die = function() {
	this.dead = true;
}

WalkingDot.prototype.getSize = function() {
	return this.size;
}

WalkingDot.prototype.getCenterX = function() {
	return this.x+this.headShiftX;
}

WalkingDot.prototype.setCenterX = function(x) {
	this.x = x-this.headShiftX;
}

WalkingDot.prototype.getCenterY = function() {
	return this.y-this.size-8+this.headShiftY+this.springValue;
}

WalkingDot.prototype.setCenterY = function(y) {
	this.y = y-(-this.size-8+this.headShiftY+this.springValue);
}

WalkingDot.prototype.getLegJointX = function() {
	return this.x;
}

WalkingDot.prototype.getLegJointY = function() {
	return this.y-this.size-10+this.headShiftY;
}


WalkingDot.prototype.getLeftFootX = function() {
	if (this.drillMode) {
		return this.x;
	}
	
	return this.x+this.leftLeg;
}

WalkingDot.prototype.getLeftFootY = function() {
	/*if (this.drillMode) {
		return this.y-this.size-30;
	}*/

	return this.y;
}

WalkingDot.prototype.getRightFootX = function() {
	if (this.drillMode) {
		return this.x;
	}
	
	return this.x+this.rightLeg;
}

WalkingDot.prototype.getRightFootY = function() {
	/*if (this.drillMode) {
		return this.y-this.size-30;
	}*/
	
	return this.y;
}

WalkingDot.prototype.drawLeftLeg = function(context) {
	context.strokeStyle = this.legColor;
	context.lineWidth = 3;
	
	context.beginPath();
	context.moveTo(this.getLegJointX(), this.getLegJointY());
	context.lineTo(this.getLeftFootX(), this.getLeftFootY());
	context.stroke();
}

WalkingDot.prototype.drawRightLeg = function(context) {
	context.strokeStyle = this.legColor;
	context.lineWidth = 3;

	context.beginPath();
	context.moveTo(this.getLegJointX(), this.getLegJointY());
	context.lineTo(this.getRightFootX(), this.getRightFootY());
	context.stroke();
}

WalkingDot.prototype.drawBody = function(context) {
	context.fillStyle = this.bodyColor;
	context.beginPath();
	context.arc(this.getCenterX(), this.getCenterY(), this.size, 0, Math.PI*2);
	context.closePath();
	context.fill();
}

WalkingDot.prototype.drawEye = function(context) {
	context.strokeStyle = this.eyeColor;
	context.lineWidth = 2;

	if (this.drillMode) {
		var spinValue = Math.tan(curTime/100);
		
		if (spinValue != 100) {
			if (Math.abs(spinValue) <= 1) {
				context.beginPath();
				context.moveTo(this.x-this.size*spinValue+this.headShiftX, this.y-this.size-10+this.springValue);
				context.lineTo(this.x-this.size*spinValue+this.headShiftX+0.3*(spinValue>0?1:-1)*this.size, this.y-this.size-10+this.springValue);
				context.stroke();
			}
		}
	} else {
		if (this.orientation == 0) {
			context.beginPath();
			context.moveTo(this.x-this.size+this.headShiftX, this.y-this.size-10+this.springValue);
			context.lineTo(this.x-this.size+this.headShiftX+0.3*this.size, this.y-this.size-10+this.springValue);
			context.stroke();
		} else {
			context.beginPath();
			context.moveTo(this.x+this.size+this.headShiftX, this.y-this.size-10+this.springValue);
			context.lineTo(this.x+this.size+this.headShiftX-0.3*this.size, this.y-this.size-10+this.springValue);
			context.stroke();
		}
	}
}

WalkingDot.prototype.update = function() {
}

WalkingDot.prototype.draw = function(context) {
	if (this.dead) {
		this.size -= 1;
	}
	
	if (this.size < 0)
		return;

	if (this.orientation == 0) {	
		//this.drawLeftLeg(context);
		this.drawBody(context);
		//this.drawRightLeg(context);
	} else {
		//this.drawRightLeg(context);
		this.drawBody(context);
		//this.drawLeftLeg(context);
	}
	
	this.drawEye(context);
}