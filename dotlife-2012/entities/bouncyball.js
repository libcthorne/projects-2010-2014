function BouncyBall(x, y) {
	Entity.call(this, x, y);
	
	this.radius = 10;
	this.bouncy = true;
}

BouncyBall.prototype = Object.create(Entity.prototype);
BouncyBall.prototype.constructor = BouncyBall;

BouncyBall.prototype.update = function() {
}

BouncyBall.prototype.draw = function(context) {
	context.fillStyle = "#FF0000";
	context.beginPath();
	context.arc(this.x, this.y, this.radius, 0, Math.PI*2);
	context.closePath();
	context.fill();
}