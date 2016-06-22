function vec2(x, y) {
	this.x = x;
	this.y = y;
}

vec2.prototype.dot = function(v) {
	return this.x*v.x + this.y*v.y;
}

vec2.prototype.add = function(v) {
	return new vec2(this.x+v.x, this.y+v.y);
}

vec2.prototype.sub = function(v) {
	return new vec2(this.x-v.x, this.y-v.y);
}

vec2.prototype.len = function() {
	return Math.sqrt(this.x*this.x+this.y*this.y);
}