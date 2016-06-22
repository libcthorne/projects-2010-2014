var PLAYER_SPEED = 2.5;
var PLAYER_START_SIZE = 13;
var PLAYER_START_X = CANVAS_WIDTH/2;
var PLAYER_START_Y = CANVAS_HEIGHT-100;

var PLAYER_BODY_COLOR = "#FFFFFF";
var PLAYER_LEG_COLOR = "#BBBBBB";
var PLAYER_EYE_COLOR = "#000000";

function Player() {
	WalkingDot.call(this, PLAYER_BODY_COLOR, PLAYER_LEG_COLOR, PLAYER_EYE_COLOR, PLAYER_START_SIZE, PLAYER_SPEED, PLAYER_START_X, PLAYER_START_Y);
}

Player.prototype = Object.create(WalkingDot.prototype);
Player.prototype.constructor = Player;

Player.prototype.freeze = function() {
	this.frozen = true;
}

Player.prototype.unfreeze = function() {
	this.frozen = false;
}

Player.prototype.update = function() {
	if (!this.frozen && !this.drillMode) {
		if (INPUT_IsLeftPressed()) {
			this.moveLeft();
		} else if (INPUT_IsRightPressed()) {
			this.moveRight();
		} else {
			this.stop();
		}
		
		if (INPUT_IsDownPressed() && this.y == PHYSICS.MAX_Y) {
			if (this.springValue < 8)
				this.springValue += 0.5;
		} else {
			if (this.springValue != 0) {
				this.velocity.y -= this.springValue;
				this.springValue = 0;
			}
		}
		
		if (INPUT_IsSpacePressed()) {
			if (this.y != PHYSICS.MAX_Y) {
				//this.drillMode = true;
			}
		}
	}
}

var player = new Player();
player.drillMode = false;