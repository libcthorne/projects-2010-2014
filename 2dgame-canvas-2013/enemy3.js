var ENEMY3_CONFIG = {
	id:"enemy3",
	
	movementImages:[
		IMAGE("res/enemy/enemy3/enemy3-right-1.png"),
		IMAGE("res/enemy/enemy3/enemy3-right-step1.png"),
		IMAGE("res/enemy/enemy3/enemy3-right-step2.png"),
		IMAGE("res/enemy/enemy3/enemy3-right-step1.png"),
	],

	movementImageInterval:100,

	explosionImageInterval:80,

	width:32,
	height:32,
	
	maxHealth:10,
	
	bbWidthPx:32,
	bbHeightPx:32,
	bbCenterPxPt:new Point(16, 24),
	
	orientation:ENTITY_ORIENTATION_RIGHT,

	deleteWaitTime:3000,
	
	moveInterval:20,

	drawFunc:ENEMY3_Draw,
	updateFunc:ENEMY3_Update,
	
	healthBarWidth:40,
};

function _ENEMY3(e, centerPt) {
	_ENEMY(e, centerPt, ENEMY3_CONFIG);
	
	e.runningDirection = Math.random() > 0.5 ? 1:-1;
}

function ENEMY3(centerPt) {
	_ENEMY3(this, centerPt);
}

function ENEMY3_Update(e) {
	if (!ENEMY_IsAlive(e)) {
		if (e.deleteTime == null || curTime >= e.deleteTime) {
			ENTITY_Delete(e);
		}
		
		return;
	}

	if (curTime > e.nextMoveTime) {
		if (e.nextMoveTime != 0) {
			if (PLAYER_IsAlive(player)) {
				var dx = 1*e.runningDirection;
				var dy = Math.random()>0.5?1:-1;

				if (!ENTITY_BBCollisionTest(e, dx, 0)) {
					ENTITY_ShiftX(e, dx);
				} else {
					e.runningDirection *= -1;
				}
				
				if (!ENTITY_BBCollisionTest(e, 0, dy)) {
					ENTITY_ShiftY(e, dy);
				}
			}
		}
		
		e.nextMoveTime = curTime + e.config.moveInterval;
	}
	
	if (curTime > e.nextMovementImageTime) {
		e.nextMovementImageTime = curTime + e.config.movementImageInterval;
		
		e.image = e.config.movementImages[e.nextMovementImageIndex];
		e.nextMovementImageIndex = (e.nextMovementImageIndex+1)%e.config.movementImages.length;
	}
}

function ENEMY3_Draw(e, context) {
	var x = MAP_GetCoordinatePositionX(ENTITY_GetX(e))-e.config.width/2;
	var y = MAP_GetCoordinatePositionY(ENTITY_GetY(e))-e.config.height;
	
	// Bounding box
	/*if (e.health > 0) {
		for (var i = 0; i < e.bb.length; i++) {
			var pt = e.bb[i];
			
			context.beginPath();
			context.rect(MAP_GetCoordinatePositionX(pt.x), MAP_GetCoordinatePositionY(pt.y)-MAP_BLOCK_Y_CENTER_OFFSET-MAP_BLOCK_SIZE_PX/2, MAP_BLOCK_SIZE_PX, MAP_BLOCK_SIZE_PX);
			context.fillStyle = "#FF0000";
			context.fill();
		}
	}*/

	// Enemy
	if (e.image)
		context.drawImage(e.image, x, y);
	
	if (e.health > 0) {
		// Health bar
		context.beginPath();
		context.rect(x-4, y-8, (e.health/e.config.maxHealth)*e.config.healthBarWidth, 4);
		context.fillStyle = "#00FF00";
		context.fill();
		// Health bar outline
		context.beginPath();
		context.rect(x-4, y-8, e.config.healthBarWidth, 4);
		context.lineWidth = 1;
		context.strokeStyle = "black";
		context.stroke();
	}
}