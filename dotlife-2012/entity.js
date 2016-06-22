function Entity(x, y) {
	this.x = x;
	this.y = y;
	this.velocity = new vec2(0, 0);
	this.radius = 1;
	this.bouncy = false;
	
	ENTITYMGR.add(this);
}

Entity.prototype.getCenterX = function() {
	return this.x;
}

Entity.prototype.setCenterX = function(x) {
	this.x = x;
}

Entity.prototype.getCenterY = function() {
	return this.y;
}

Entity.prototype.setCenterY = function(y) {
	this.y = y;
}

Entity.prototype.remove = function() {
	return ENTITYMGR.remove(this);
}

Entity.prototype.toString = function() {
	return "[Entity " + ENTITYMGR.entities.indexOf(this) + "]";
}