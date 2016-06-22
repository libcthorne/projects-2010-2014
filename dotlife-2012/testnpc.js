var TESTNPC_SPEED = 3;
var TESTNPC_SIZE = 20;
var TESTNPC_START_X = CANVAS_WIDTH/2+250;
var TESTNPC_START_Y = CANVAS_HEIGHT;

var TESTNPC_BODY_COLOR = "#FF0000";
var TESTNPC_LEG_COLOR = "#FFFFFF";
var TESTNPC_EYE_COLOR = "#000000";

var TESTNPC_MOVE_DISTANCE = 200;

function TestNPC() {
	WalkingDot.call(this, TESTNPC_BODY_COLOR, TESTNPC_LEG_COLOR, TESTNPC_EYE_COLOR, TESTNPC_SIZE, TESTNPC_SPEED, TESTNPC_START_X, TESTNPC_START_Y);

	this.moveDistance = 0;
	this.moveDirection = 0;
}

TestNPC.prototype = Object.create(WalkingDot.prototype);
TestNPC.prototype.constructor = TestNPC;

TestNPC.prototype.update = function() {
	if (this.dead) {
		this.stop();
		return;
	}

	if (this.moveDistance >= TESTNPC_MOVE_DISTANCE) {
		if (this.moveDirection == 0)
			this.moveDirection = 1;
		else
			this.moveDirection = 0;
		
		this.moveDistance = 0;
	} else {
		this.moveDistance += TESTNPC_SPEED;
	}

	if (this.moveDirection == 0)
		this.moveLeft();
	else
		this.moveRight();
	
}