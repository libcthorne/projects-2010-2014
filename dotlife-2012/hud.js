var HUD = {}

HUD.draw = function(context) {
	/*// Debug box
	context.beginPath();
	context.rect(0, 0, 100, 55);
	context.fillStyle = "#FFFFFF";
	context.fill();
	// Debug box outline
	context.beginPath();
	context.rect(0, 0, 100, 55);
	context.lineWidth = 1;
	context.strokeStyle = "black";
	context.stroke();*/

	// Draw FPS
	context.font = "12px Arial";
	context.fillStyle = "#FFFFFF"
	context.fillText("FPS: " + displayedFPS, 5, 15);
}