var WORLD_UPDATE_INTERVAL_MS = 10;
var WORLD_UPDATE_INTERVAL_SEC = WORLD_UPDATE_INTERVAL_MS/100;

var curTime;
var worldCreationTime;

var WORLD = {}

WORLD.getElapsedTimeString = function() {
	var time = curTime - worldCreationTime;
	
	var minutes = Math.floor(time/1000/60);
	var seconds = Math.floor((time-minutes*60*1000)/1000);
	
	if (minutes < 10)
		minutes = "0" + minutes;

	if (seconds < 10)
		seconds = "0" + seconds;

	return minutes + ":" + seconds;
}

WORLD.curTime = function() {
	return curTime;
}

WORLD.reset = function() {
	player.x = CANVAS_WIDTH/2;
	player.y = CANVAS_HEIGHT;
	player.size = PLAYER_START_SIZE;
}

var TICK_INTERVAL = 30;

var lastUpdate = 0;
var extraTime = 0;

WORLD.update = function() {
	curTime = (new Date()).getTime();

	if (lastUpdate == 0) {
		worldCreationTime = curTime;
		lastUpdate = curTime;
		return;
	}
	
	var timePassed = curTime-lastUpdate;
	
	lastUpdate = curTime;
	
	if (extraTime >= TICK_INTERVAL) {
		timePassed += extraTime;
		extraTime -= TICK_INTERVAL;
	}

	while (timePassed >= TICK_INTERVAL) {
		ENTITYMGR.updateEntities();
		
		ROOMS.update();

		PHYSICS.update();
		
		timePassed -= TICK_INTERVAL;
	}
	
	if (timePassed > 0)
		extraTime += timePassed;
}

ROOMS.set("red");