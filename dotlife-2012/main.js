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
	MAP.draw(context);

	ENTITYMGR.drawEntities(context);

	HUD.draw(context);
}

var KEY_FRAME_INTERVAL = 1000;

var lastFrameTime;
var fps;
var lastKeyFrameTime = 0;
var displayedFPS;

function animate() {
	WORLD.update();

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