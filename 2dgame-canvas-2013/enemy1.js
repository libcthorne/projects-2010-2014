var ENEMY1_CONFIG = {
	id:"enemy1",
	
	movementImages:[
		IMAGE("res/enemy/enemy1-1.png"),
		IMAGE("res/enemy/enemy1-2.png"),
		IMAGE("res/enemy/enemy1-3.png"),
	],
	
	movementImageInterval:50,
	
	explosionImages:[
		IMAGE("res/enemy/enemy1-explode-2.png"),
		IMAGE("res/enemy/enemy1-explode-3.png"),
		IMAGE("res/enemy/enemy1-explode-4.png"),	
	],
	
	explosionImageInterval:80,

	width:32,
	height:32,
	
	maxHealth:30,
	
	bbWidthPx:32,
	bbHeightPx:32,
	bbCenterPxPt:new Point(16, 24),
	
	orientation:ENTITY_ORIENTATION_FRONT,

	deleteWaitTime:3000,
	
	moveInterval:40,

	explosionRange:PxToMapBlock(32),	
	explosionDamage:20,
	
	drawFunc:ENEMY1_Draw,
	updateFunc:ENEMY1_Update,
	
	healthBarWidth:40,
};

function _ENEMY1(e, centerPt) {
	_ENEMY(e, centerPt, ENEMY1_CONFIG);
	
	e.exploding = false;
	
	e.nextExplosionImageIndex = 0;
	e.nextExplosionImageTime = 0;
	
	e.chainExplode = false;
}

function ENEMY1(centerPt) {
	_ENEMY1(this, centerPt);
}

function ENEMY1_ChainExplode(e) {
	e.chainExplode = true;
	e.explodeTime = curTime + 80;
}

function ENEMY1_Explode(e) {
	var entities = ENTITY_BBRectangleTest(e, e.config.explosionRange, e.config.explosionRange);

	ENTITY_DeleteBoundingBox(e);

	for (var i = 0; i < entities.length; i++) {
		var entity = entities[i];
		
		if (entity.id == ENEMY1_CONFIG.id) {
			ENEMY1_ChainExplode(entity);
		} else if (entity.id == ENEMY3_CONFIG.id) {
			ENEMY_TakeDamage(entity, 30);
		} else if (entity.id == PLAYER_ENTITY_ID) {
			PLAYER_TakeDamage(entity, e.config.explosionDamage);
		}
	}

	e.health = 0;
	e.exploding = true;
	e.deleteTime = curTime + e.config.deleteWaitTime;
}

function ENEMY1_Update(e) {
	if (!ENEMY_IsAlive(e)) {
		if (curTime >= e.deleteTime) {
			ENTITY_Delete(e);
		}
		
		return;
	}
	
	if (e.chainExplode) {
		if (curTime > e.explodeTime) {
			ENEMY1_Explode(e);
			e.chainExplode = false;
		}
		
		return;
	}

	if (e.exploding) {
		if (curTime > e.nextExplosionImageTime) {
			var explosionImage = e.config.explosionImages[e.nextExplosionImageIndex++];
			
			if (explosionImage == undefined) {
				e.alive = false;

				return;
			}
			
			e.nextExplosionImageTime = curTime + e.config.explosionImageInterval;

			e.image = explosionImage;
		}
	} else {
		if (curTime > e.nextMoveTime) {
			if (e.nextMoveTime != 0) {
				if (PLAYER_IsAlive(player)) {
					var ex = ENTITY_GetX(e);
					var ey = ENTITY_GetY(e);
					var px = ENTITY_GetX(player);
					var py = ENTITY_GetY(player);
					
					if (Math.abs(ex-px) <= e.config.explosionRange && Math.abs(ey-py) <= e.config.explosionRange) {
						ENEMY1_Explode(e);
						return;
					} else {
						var dx = 0;
						var dy = 0;

						if (ex-px > 4) {
							dx = -1;
						} else if (ex-px < -4) {
							dx = 1;
						} else {
							dx = 0;
							dy = ey > py ? -1 : 1;
						}
						
						if (ey-py > 4) {
							dy = -1;
						} else if (ey-py < -4) {
							dy = 1;
						} else {
							dx = ex > px ? -1 : 1;
							dy = 0;
						}
						
						/*switch (Math.floor(Math.random()*9)+1) {
						case 1: // x
						case 2:
							dx = ex > px ? -1 : 1;
							dy = 0;
							break;
						case 3: // y
						case 4:
							dx = 0;
							dy = ey > py ? -1 : 1;
							break;
						case 5: // x + random y
							dx = ex > px ? -1 : 1;
							dy = Math.random() > 0.5 ? -1 : 1;
							break;
						case 6: // y + random x
							dx = Math.random() > 0.5 ? -1 : 1;
							dy = ey > py ? -1 : 1;
							break;
						case 7: // x + y
						case 8:
						case 9:
							dx = ex > px ? -1 : 1;
							dy = ey > py ? -1 : 1;
							break;
						}*/

						if (!ENTITY_BBCollisionTest(e, dx, 0)) {
							ENTITY_ShiftX(e, dx);
						}
				
						if (!ENTITY_BBCollisionTest(e, 0, dy)) {
							ENTITY_ShiftY(e, dy);
						}
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
}

function ENEMY1_Draw(e, context) {
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