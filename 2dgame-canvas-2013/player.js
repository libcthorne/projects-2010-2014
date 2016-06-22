var player;

var PLAYER_ENTITY_ID = "player";

var PLAYER_WIDTH_PX = 32;
var PLAYER_HEIGHT_PX = 32;

var PLAYER_BB_WIDTH_PX = 12;
var PLAYER_BB_HEIGHT_PX = 12;
var PLAYER_BB_CENTER_X_PX = 4;
var PLAYER_BB_CENTER_Y_PX = 4;

var PLAYER_IMAGE_X = CANVAS_WIDTH/2-PLAYER_WIDTH_PX/2;
var PLAYER_IMAGE_Y = CANVAS_HEIGHT/2-PLAYER_HEIGHT_PX+MAP_BLOCK_Y_CENTER_OFFSET;

var PLAYER_START_X = 4;
var PLAYER_START_Y = 4;

var PLAYER_START_IMAGE = "res/player/player-front.png";
var PLAYER_NAME = "Chrisaster";

var PLAYER_MAX_HEALTH = 100;
var PLAYER_DEAD_IMAGE = "res/player/player-dead.png";
var PLAYER_ATTACK_INTERVAL = 150;

var WEAPON_START_IMAGE = "res/weapons/sword/sword-front.png";

var PLAYER_IMAGES = {
	"front": {
		"still":"res/player/player-front.png",
		"lstep":"res/player/player-front-lstep.png",
		"rstep":"res/player/player-front-rstep.png",
	},
	
	"back": {
		"still":"res/player/player-back.png",
		"lstep":"res/player/player-back-lstep.png",
		"rstep":"res/player/player-back-rstep.png"
	},
	
	"left": {
		"still":"res/player/player-left.png",
		"lstep":"res/player/player-left-lstep.png",
		"rstep":"res/player/player-left-rstep.png"
	},
	
	"right": {
		"still":"res/player/player-right.png",
		"lstep":"res/player/player-right-lstep.png",
		"rstep":"res/player/player-right-rstep.png"
	},
};

var WEAPON_IMAGES = {
	"front": {
		"still":"res/weapons/sword/sword-front.png",
		"lstep":"res/weapons/sword/sword-front-lstep.png",
		"rstep":"res/weapons/sword/sword-front-rstep.png",
		"attack": [
			"res/weapons/sword/sword-front.png",
			"res/weapons/sword/sword-front-attack-1.png",
			"res/weapons/sword/sword-front-attack-2.png",
			"res/weapons/sword/sword-front-attack-1.png",
		]
	},
	
	"back": {
		"still":"res/weapons/sword/sword-back.png",
		"lstep":"res/weapons/sword/sword-back-lstep.png",
		"rstep":"res/weapons/sword/sword-back-rstep.png",
		"attack": [
			"res/weapons/sword/sword-back.png",
			"res/weapons/sword/sword-back-attack-1.png",
			"res/weapons/sword/sword-back-attack-2.png",
			"res/weapons/sword/sword-back-attack-1.png",
		]
	},
	
	"left": {
		"still":"res/weapons/sword/sword-left.png",
		"lstep":"res/weapons/sword/sword-left-lstep.png",
		"rstep":"res/weapons/sword/sword-left-rstep.png",
		"attack": [
			"res/weapons/sword/sword-left.png",
			"res/weapons/sword/sword-left-attack-1.png",
			"res/weapons/sword/sword-left-attack-2.png",
			"res/weapons/sword/sword-left-attack-1.png",
		]
	},

	"right": {
		"still":"res/weapons/sword/sword-right.png",
		"lstep":"res/weapons/sword/sword-right-lstep.png",
		"rstep":"res/weapons/sword/sword-right-rstep.png",
		"attack": [
			"res/weapons/sword/sword-right.png",
			"res/weapons/sword/sword-right-attack-1.png",
			"res/weapons/sword/sword-right-attack-2.png",
			"res/weapons/sword/sword-right-attack-1.png",
		]
	},
}

function _PLAYER(p, centerPt) {
	_ENTITY(
			p,
			{
				id:PLAYER_ENTITY_ID,
				drawFunc:PLAYER_Draw,
				updateFunc:PLAYER_Update,
				centerPt:centerPt,
				orientation:ENTITY_ORIENTATION_FRONT,
				bbWidthPx:PLAYER_BB_WIDTH_PX,
				bbHeightPx:PLAYER_BB_HEIGHT_PX,
				bbCenterPxPt:new Point(PLAYER_BB_CENTER_X_PX, PLAYER_BB_CENTER_Y_PX),
				solid:true
			}
	);

	p.image = new Image();
	p.image.src = PLAYER_START_IMAGE;
	
	p.weaponImage = new Image();
	p.weaponImage.src = WEAPON_START_IMAGE;

	p.playerName = PLAYER_NAME;

	p.movementImages = new Array();
	
	for (var orientation = ENTITY_ORIENTATION_FRONT; orientation <= ENTITY_ORIENTATION_RIGHT; orientation++) {
		var playerOrientationImages = PLAYER_IMAGES[ENTITY_OrientationToString(orientation)];

		var stillImage = new Image();
		stillImage.src = playerOrientationImages["still"];
		var lstepImage = new Image();
		lstepImage.src = playerOrientationImages["lstep"];
		var rstepImage = new Image();
		rstepImage.src = playerOrientationImages["rstep"];
		
		p.movementImages[orientation] = new Array();
		p.movementImages[orientation][0] = stillImage;
		p.movementImages[orientation][1] = lstepImage;
		p.movementImages[orientation][2] = stillImage;
		p.movementImages[orientation][3] = rstepImage;
	}

	p.weaponImages = new Array();

	for (var orientation = ENTITY_ORIENTATION_FRONT; orientation <= ENTITY_ORIENTATION_RIGHT; orientation++) {
		var weaponOrientationImages = WEAPON_IMAGES[ENTITY_OrientationToString(orientation)];

		var stillImage = new Image();
		stillImage.src = weaponOrientationImages["still"];
		var lstepImage = new Image();
		lstepImage.src = weaponOrientationImages["lstep"];
		var rstepImage = new Image();
		rstepImage.src = weaponOrientationImages["rstep"];
			
		p.weaponImages[orientation] = new Array();
		p.weaponImages[orientation][0] = new Array();
		p.weaponImages[orientation][0][0] = stillImage;
		p.weaponImages[orientation][0][1] = lstepImage;
		p.weaponImages[orientation][0][2] = stillImage;
		p.weaponImages[orientation][0][3] = rstepImage;
		
		var attackImagesSrc = weaponOrientationImages["attack"];
		var attackImages = new Array();
		
		for (var i = 0; i < attackImagesSrc.length; i++) {
			var attackImage = new Image();
			attackImage.src = attackImagesSrc[i];
			attackImages[i] = attackImage;
		}
		
		p.weaponImages[orientation][1] = attackImages;
	}
	
	p.stepCount = 0;
	p.stepLimit = 10;
	
	p.movementImageIndex = 0;
	
	p.playerStopped = true;
	p.playerInAttack = false;
	
	p.attackImageIndex = 0;
	p.attackImageInterval = 50;
	p.nextAttackImgChangeTime = 0;
	
	p.nextAttackTime = 0;
	
	p.alive = true;
	p.health = PLAYER_MAX_HEALTH;

	p.deadImage = new Image();
	p.deadImage.src = PLAYER_DEAD_IMAGE;
}

function PLAYER(centerPt) {
	_PLAYER(this, centerPt);
}

function PLAYER_Step(p) {
	if (p.playerStopped) {
		p.playerStopped = false;
		PLAYER_UpdateMovementImage(p);
	} else {
		p.stepCount += 1;
	}

	if (p.stepCount >= p.stepLimit) {
		p.movementImageIndex = (p.movementImageIndex+1)%p.movementImages[p.orientation].length;
		
		PLAYER_UpdateMovementImage(p);
		
		p.stepCount = 0;
	}
}

function PLAYER_UpdateMovementImage(p) {
	if (PLAYER_InAttack(p))
		return;

	p.image = p.movementImages[p.orientation][p.movementImageIndex];
	p.weaponImage = p.weaponImages[p.orientation][0][p.movementImageIndex];
}

function PLAYER_Stop(p) {
	if (!p.playerStopped) {
		playerStepCount = 0;
		p.movementImageIndex = 0;
		PLAYER_UpdateMovementImage(p);

		p.playerStopped = true;
	}
}

function PLAYER_Turn(p) {
	p.movementImageIndex = 0;
	p.stepCount = p.stepLimit;
	PLAYER_UpdateMovementImage(p);
	
	p.nextAttackTime = 0;
}

function PLAYER_SetOrientation(p, orientation) {
	if (p.orientation != orientation) {
		ENTITY_Rotate(p, orientation);
		
		p.orientation = orientation;
		
		PLAYER_Turn(p);
	} else if (p.playerStopped) {
		PLAYER_Turn(p);
	}
}

function PLAYER_IsMoving(p) {
	return !p.playerStopped;
}

function PLAYER_MoveUp(p) {
	if (!ENTITY_BBCollisionTest(p, 0, -1)) {
		MAP_ShiftY(1);
		
		ENTITY_ShiftY(p, -1);
	} else {
		PLAYER_Stop(p);
	}
	
	PLAYER_SetOrientation(p, ENTITY_ORIENTATION_BACK);
}

function PLAYER_MoveDown(p) {
	if (!ENTITY_BBCollisionTest(p, 0, 1)) {
		MAP_ShiftY(-1);
		
		ENTITY_ShiftY(p, 1);
	} else {
		PLAYER_Stop(p);
	}
	
	PLAYER_SetOrientation(p, ENTITY_ORIENTATION_FRONT);
}

function PLAYER_MoveLeft(p) {
	if (!ENTITY_BBCollisionTest(p, -1, 0)) {
		MAP_ShiftX(1);
		
		ENTITY_ShiftX(p, -1);
	} else {
		PLAYER_Stop(p);
	}
	
	PLAYER_SetOrientation(p, ENTITY_ORIENTATION_LEFT);
}

function PLAYER_MoveRight(p) {
	if (!ENTITY_BBCollisionTest(p, 1, 0)) {
		MAP_ShiftX(-1);
		
		ENTITY_ShiftX(p, 1);
	} else {
		PLAYER_Stop(p);
	}
	
	PLAYER_SetOrientation(p, ENTITY_ORIENTATION_RIGHT);
}

function PLAYER_InAttack(p) {
	return p.playerInAttack;
}

function PLAYER_StartAttack(p) {
	p.playerInAttack = true;
	p.attackImageIndex = 0;
	p.nextAttackImgChangeTime = 0;
}

function PLAYER_EndAttack(p) {
	p.playerInAttack = false;
	
	p.weaponImage = p.weaponImages[p.orientation][0][p.movementImageIndex];
}

function PLAYER_Attack(p) {
	if (curTime < p.nextAttackTime)
		return;

	p.nextAttackTime = curTime + PLAYER_ATTACK_INTERVAL;
	
	//if (PLAYER_InAttack(p))
	//	return;

	//PLAYER_Stop(p);
	//PLAYER_StartAttack(p);
	
	var attackX = 0;
	var attackY = 0;
	var attackRange = 50;

	switch (p.orientation) {
	case ENTITY_ORIENTATION_FRONT:
		attackY = 1;
		
		break;
	case ENTITY_ORIENTATION_BACK:
		attackY = -1;
		
		break;
	case ENTITY_ORIENTATION_LEFT:
		attackX = -1;
		
		break;
	case ENTITY_ORIENTATION_RIGHT:
		attackX = 1;
		
		break;
	}

	var projectilePos = ENTITY_GetPos(p);
	
	if (attackX != 0) {
		projectilePos.x += attackX*3;
		projectilePos.y -= 1;
	} else if (attackY != 0) {
		projectilePos.x += 1;
		projectilePos.y += attackY*3;
	}

	var projectile = new PROJECTILE(projectilePos, attackX, attackY);

	/*for (var i = 1; i <= attackRange; i++) {
		var pe = MAP_GetPositionDataSafe(ENTITY_GetX(p)+i*attackX, ENTITY_GetY(p)+i*attackY);

		if (pe == undefined)
			continue;

		if (pe.id == "enemy") {
			ENEMY_TakeDamage(pe, 10);
			break;
		}
	}*/
}

function PLAYER_IsAlive(p) {
	return p.alive;
}

function PLAYER_Die(p) {
	p.alive = false;
	p.image = p.deadImage;
}

function PLAYER_TakeDamage(p, damage) {
	p.health -= damage;
	
	if (p.health <= 0) {
		PLAYER_Die(p);
	}
}

function PLAYER_Update(p) {
	if (!PLAYER_IsAlive(p))
		return;

	if (PLAYER_InAttack(p)) {
		if (curTime >= p.nextAttackImgChangeTime) {
			p.nextAttackImgChangeTime = curTime+p.attackImageInterval;
			
			p.weaponImage = p.weaponImages[p.orientation][1][p.attackImageIndex++];
			
			if (!p.weaponImage) {
				PLAYER_EndAttack(p);
			}
		}
	}
}

var PLAYER_MAX_HEALTH_BAR_WIDTH = 40;

function PLAYER_Draw(p, context) {
	// Bounding box
	for (var i = 0; i < p.bb.length; i++) {
		var pt = p.bb[i];
		
		context.beginPath();
		context.rect(MAP_GetCoordinatePositionX(pt.x), MAP_GetCoordinatePositionY(pt.y)-MAP_BLOCK_Y_CENTER_OFFSET-MAP_BLOCK_SIZE_PX/2, MAP_BLOCK_SIZE_PX, MAP_BLOCK_SIZE_PX);
		context.fillStyle = "#00FF00";
		context.fill();
	}

	// Player
	context.drawImage(p.image, PLAYER_IMAGE_X, PLAYER_IMAGE_Y);
	
	if (PLAYER_IsAlive(p)) {
		// Weapon
		//context.drawImage(p.weaponImage, PLAYER_IMAGE_X, PLAYER_IMAGE_Y);

		/*// Player name
		context.font = "12px Arial";
		context.fillStyle = "#000000"
		context.fillText(p.playerName, PLAYER_IMAGE_X-14, PLAYER_IMAGE_Y-4);*/
		
		var x = PLAYER_IMAGE_X;
		var y = PLAYER_IMAGE_Y;

		// Health bar
		context.beginPath();
		context.rect(x-4, y-8, (p.health/PLAYER_MAX_HEALTH)*PLAYER_MAX_HEALTH_BAR_WIDTH, 4);
		context.fillStyle = "#00FF00";
		context.fill();
		// Health bar outline
		context.beginPath();
		context.rect(x-4, y-8, PLAYER_MAX_HEALTH_BAR_WIDTH, 4);
		context.lineWidth = 1;
		context.strokeStyle = "black";
		context.stroke();
	} else {
		/*// Player name
		context.font = "12px Arial";
		context.fillStyle = "#000000"
		context.fillText(p.playerName + "(DEAD)", PLAYER_IMAGE_X-14, PLAYER_IMAGE_Y-4);*/
	}
}

player = new PLAYER(new Point(PLAYER_START_X, PLAYER_START_Y));

// TEMP
mapOffsetX -= ENTITY_GetX(player)*MAP_BLOCK_SIZE_PX;
mapOffsetY -= ENTITY_GetY(player)*MAP_BLOCK_SIZE_PX;