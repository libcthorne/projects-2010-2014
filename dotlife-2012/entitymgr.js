var ENTITYMGR = {
	entities:new Array(),
};

ENTITYMGR.add = function(entity) {
	this.entities.push(entity);
}

ENTITYMGR.remove = function(entity) {
	var entityIndex = this.entities.indexOf(entity);
	
	if (entityIndex != -1) {
		this.entities.splice(entityIndex, 1);
		
		return true;
	}
	
	// Entity not found
	return false;
}

ENTITYMGR.updateEntities = function() {
	for (var i = 0; i < this.entities.length; i++) {
		this.entities[i].update();
	}
}

ENTITYMGR.drawEntities = function(context) {
	for (var i = 0; i < this.entities.length; i++) {
		this.entities[i].draw(context);
	}
}