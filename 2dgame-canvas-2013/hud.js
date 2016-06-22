function HUD_Draw(context) {
	// Debug box
	context.beginPath();
	context.rect(0, 0, 100, 55);
	context.fillStyle = "#FFFFFF";
	context.fill();
	// Debug box outline
	context.beginPath();
	context.rect(0, 0, 100, 55);
	context.lineWidth = 1;
	context.strokeStyle = "black";
	context.stroke();

	// Draw FPS
	context.font = "12px Arial";
	context.fillStyle = "#000000"
	context.fillText("FPS: " + displayedFPS, 5, 15);
	
	// Player position
	context.font = "12px Arial";
	context.fillStyle = "#000000"
	context.fillText("PP: (" + ENTITY_GetX(player) + ", " + ENTITY_GetY(player) + ")", 5, 30);
	
	// Enemy count
	var enemyCount = WORLD_GetEntityCountByID(ENEMY1_CONFIG.id);
	context.font = "12px Arial";
	context.fillStyle = "#000000"
	context.fillText("Enemies: " + enemyCount, 5, 45);

	// Enemy position
	//context.font = "12px Arial";
	//context.fillStyle = "#000000"
	//context.fillText("EP: (" + enemy.x + "," + enemy.y + ")", 5, 45);
	
	// Survival time
	
	context.beginPath();
	context.rect(265, 0, 50, 22);
	context.fillStyle = "#FFFFFF";
	context.fill();

	context.beginPath();
	context.rect(265, 0, 50, 22);
	context.lineWidth = 1;
	context.strokeStyle = "black";
	context.stroke();
	
	context.font = "12px Arial";
	context.fillStyle = "#000000"
	context.fillText(WORLD_GetSurvivalTimeString(), 275, 15);
	
	// Radar
	RADAR_Draw(context);
}