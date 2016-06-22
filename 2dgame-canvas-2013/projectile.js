var PROJECTILE_IMAGE = "res/projectile/projectile1-1.png";
var projectileImage = new Image();
projectileImage.src = PROJECTILE_IMAGE;

var PROJECTILE_BB_WIDTH_PX = 8;
var PROJECTILE_BB_HEIGHT_PX = 8;

var PROJECTILE_MOVEMENT_SPEED_PPS = 400;
var PROJECTILE_MOVEMENT_SPEED = Math.floor(PROJECTILE_MOVEMENT_SPEED_PPS/MAP_BLOCK_SIZE_PX);
var PROJECTILE_MOVEMENT_INTERVAL = (1/PROJECTILE_MOVEMENT_SPEED)*1000;
var PROJECTILE_MOVEMENT_MINDIST = 1;

function _PROJECTILE(e, centerPt, dirX, dirY) {
	_ENTITY(
			e,
			{
				id:"projectile",
				drawFunc:PROJECTILE_Draw,
				updateFunc:PROJECTILE_Update,
				centerPt:centerPt,
				orientation:ENTITY_ORIENTATION_FRONT,
				bbWidthPx:PROJECTILE_BB_WIDTH_PX,
				bbHeightPx:PROJECTILE_BB_HEIGHT_PX,
				//bbCenterPxPt:new Point(PROJECTILE_BB_CENTER_X_PX, PROJECTILE_BB_CENTER_Y_PX),
				solid:false
			}
	);

	e.dirX = dirX;
	e.dirY = dirY;

	e.image = projectileImage;
	
	e.lastMovementTime = 0;
}

function PROJECTILE(centerPt, dirX, dirY) {
	_PROJECTILE(this, centerPt, dirX, dirY)
}

function PROJECTILE_Update(e) {
	if (e.lastMovementTime == 0) {
		e.lastMovementTime = curTime;
		return;
	}
	
	var interval = curTime - e.lastMovementTime;

	if (interval < PROJECTILE_MOVEMENT_INTERVAL)
		return;
		
	var dist = Math.floor(interval/PROJECTILE_MOVEMENT_INTERVAL);
	//console.log(interval, PROJECTILE_MOVEMENT_INTERVAL, dist);
	if (dist < PROJECTILE_MOVEMENT_MINDIST)
		return;

	for (var i = 0; i < dist; i++) {
		var dx = e.dirX;
		var dy = e.dirY;
		var pe = ENTITY_BBHitTest(e, dx, dy);

		if (pe) {
			if (pe.id == ENEMY1_CONFIG.id) {
				ENEMY1_Explode(pe);
				ENTITY_Delete(e);
				break;
			} else if (pe.id == ENEMY3_CONFIG.id) {
				ENEMY_TakeDamage(pe, 10);
				ENTITY_Delete(e);
				break;
			} else if (pe.solid) {
				ENTITY_Delete(e);
				break;
			} else {
				ENTITY_Shift(e, dx, dy);
			}
		} else {
			ENTITY_Shift(e, dx, dy);
		}
	}
	
	e.lastMovementTime = curTime;
}

function PROJECTILE_Draw(e, context) {
	var x = MAP_GetCoordinatePositionX(ENTITY_GetX(e))-PROJECTILE_BB_WIDTH_PX/2;
	var y = MAP_GetCoordinatePositionY(ENTITY_GetY(e))-PROJECTILE_BB_HEIGHT_PX;

	context.drawImage(e.image, x, y);
	
	/*// Bounding box
	for (var i = 0; i < e.bb.length; i++) {
		var pt = e.bb[i];
		
		context.beginPath();
		context.rect(MAP_GetCoordinatePositionX(pt.x), MAP_GetCoordinatePositionY(pt.y)-MAP_BLOCK_Y_CENTER_OFFSET-MAP_BLOCK_SIZE_PX/2, MAP_BLOCK_SIZE_PX, MAP_BLOCK_SIZE_PX);
		context.fillStyle = "#00FF00";
		context.fill();
	}*/
}