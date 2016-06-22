window.requestAnimFrame = (function(callback) {
	return 	window.requestAnimationFrame ||
			window.webkitRequestAnimationFrame ||
			window.mozRequestAnimationFrame ||
			window.oRequestAnimationFrame ||
			window.msRequestAnimationFrame ||
			function(callback) {
				window.setTimeout(callback, 1000 / 60);
			};
})();

function draw(context) {	
	MAP_Draw(context);
	
	for (var y = 0; y < MAP_HEIGHT; y++) {
		var entities = entityLayers[y]
		
		for (var i = 0; i < entities.length; i++) {
			if (entities[i].drawable)
				entities[i].draw(context);
		}
	}

	HUD_Draw(context);
}

var KEY_FRAME_INTERVAL = 1000;

var lastFrameTime;
var fps;
var lastKeyFrameTime = 0;
var displayedFPS;

function animate() {
	if (lastFrameTime == undefined) {
		var currentTime = new Date().getTime();
		lastFrameTime = currentTime;
	} else {
		var keyFrame = false;

		var currentTime = new Date().getTime();
		var frameTimeDelta = (currentTime - lastFrameTime)/1000;
		lastFrameTime = currentTime;
		fps = Math.ceil(1/frameTimeDelta);
			
		if (currentTime - lastKeyFrameTime > KEY_FRAME_INTERVAL) {
			keyFrame = true;
			displayedFPS = fps;
			lastKeyFrameTime = currentTime;
		}
	
		var canvas = document.getElementById("mainCanvas");
		var context = canvas.getContext("2d");

		context.clearRect(0, 0, canvas.width, canvas.height);

		draw(context);
	}

	requestAnimFrame(function() {
		animate();
	});
}

animate();