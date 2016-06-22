var WORLD_UPDATE_INTERVAL_MS = 10;
var WORLD_UPDATE_INTERVAL_SEC = WORLD_UPDATE_INTERVAL_MS/100;

var WORLD_ENTITY = {id:"world", solid:true};

var entityLayers = new Array();

for (var y = 0; y < MAP_HEIGHT; y++) {
	entityLayers[y] = new Array();
}

var curTime;
var worldCreationTime;

function WORLD_GetEntityCountByID(id) {
	var count = 0;

	for (var y = 0; y < MAP_HEIGHT; y++) {
		var entities = entityLayers[y];

		for (var i = 0; i < entities.length; i++) {
			if (entities[i].id == id)
				count++;
		}
	}

	return count;
}

function WORLD_GetElapsedTimeString() {
	var time = curTime - worldCreationTime;
	
	var minutes = Math.floor(time/1000/60);
	var seconds = Math.floor((time-minutes*60*1000)/1000);
	
	if (minutes < 10)
		minutes = "0" + minutes;

	if (seconds < 10)
		seconds = "0" + seconds;

	return minutes + ":" + seconds;
}

var lastSurvivalTimeString = "00:00";

function WORLD_GetSurvivalTimeString() {
	if (PLAYER_IsAlive(player))
		lastSurvivalTimeString = WORLD_GetElapsedTimeString();
		
	return lastSurvivalTimeString;
}

function WORLD_CurTime() {
	return curTime;
}

function WORLD_Update() {
	curTime = (new Date()).getTime();

	if (!worldCreationTime)
		worldCreationTime = curTime;

	for (var y = 0; y < MAP_HEIGHT; y++) {
		var entities = entityLayers[y]
		
		for (var i = 0; i < entities.length; i++) {
			if (entities[i].updatable)
				entities[i].update(curTime);
		}
	}

	if (PLAYER_IsAlive(player)) {
		if (!MAP_InShift()) {
			//if (PLAYER_InAttack(player))
			//	return;

			if (INPUT_IsLeftPressed())
				PLAYER_MoveLeft(player);
			else if (INPUT_IsRightPressed())
				PLAYER_MoveRight(player);
			else if (INPUT_IsUpPressed())
				PLAYER_MoveUp(player);
			else if (INPUT_IsDownPressed())
				PLAYER_MoveDown(player);
			else
				PLAYER_Stop(player);
				
			if (INPUT_IsSpacePressed())
				PLAYER_Attack(player);
		} else {
			// If player is moving, update map position
			var shift = MAP_UpdatePosition();
			
			PLAYER_Step(player);
		}
	}
}

setInterval(function() {
	WORLD_Update();
}, WORLD_UPDATE_INTERVAL_MS);