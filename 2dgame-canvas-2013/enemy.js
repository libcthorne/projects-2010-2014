function _ENEMY(e, centerPt, config) {
	config.solid = true;
	config.centerPt = centerPt;
	
	_ENTITY(e, config);

	e.config = config;
	e.image = config.movementImages[0];

	e.nextMovementImageIndex = 0;
	e.nextMovementImageTime = 0;

	e.health = config.maxHealth;

	e.nextMoveTime = 0;
	
	e.alive = true;
}

function ENEMY(centerPt) {
	_ENEMY(this, centerPt)
}

function ENEMY_IsAlive(e) {
	return e.alive;
}

function ENEMY_TakeDamage(e, damage) {
	if (!ENEMY_IsAlive(e))
		return;

	e.health -= damage;
	
	if (e.health <= 0) {
		e.alive = false;
		ENTITY_DeleteBoundingBox(e);

		e.health = 0;
		e.image = e.config.deadImage;
	} else {
		//e.image = e.painImage;
		//e.nextMovementImageTime += e.movementImageInterval;
	}
}