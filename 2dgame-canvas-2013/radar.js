var RADAR_WIDTH_PX = 128;
var RADAR_HEIGHT_PX = 128;

var MAPRADAR_WIDTH_RATIO = Math.floor(MAP_WIDTH/RADAR_WIDTH_PX);
var MAPRADAR_HEIGHT_RATIO = Math.floor(MAP_HEIGHT/RADAR_HEIGHT_PX);

function RADAR_Draw(context) {
	// Radar
	context.beginPath();
	context.rect(CANVAS_WIDTH-RADAR_WIDTH_PX, 0, RADAR_WIDTH_PX, RADAR_HEIGHT_PX);
	context.fillStyle = "#FFFFFF";
	context.fill();
	// Radar outline
	context.beginPath();
	context.rect(CANVAS_WIDTH-RADAR_WIDTH_PX-1, 1, RADAR_WIDTH_PX, RADAR_HEIGHT_PX);
	context.lineWidth = 1;
	context.strokeStyle = "black";
	context.stroke();
	
	var radarImageData = context.createImageData(RADAR_WIDTH_PX, RADAR_HEIGHT_PX);

	/*for (var i = 0; i < radarImageData.data.length; i+=4) {
		var y = Math.floor(i/4/RADAR_WIDTH_PX);
		var x = i/4 - y*RADAR_WIDTH_PX;

		var mx = x*Math.floor(MAP_WIDTH_PX/RADAR_WIDTH_PX/MAP_BLOCK_SIZE_PX);
		var my = y*Math.floor(MAP_HEIGHT_PX/RADAR_HEIGHT_PX/MAP_BLOCK_SIZE_PX);
		var found = false;

		for (var mdx = 0; mdx < 4; mdx++) {
			for (var mdy = 0; mdy < 4; mdy++) {
				var md = MAP_GetPositionDataSafe(mx+mdx, my+mdy);
				
				if (md == undefined)
					continue;
				
				var r;
				var g;
				var b;
				
				if (md.id == ENEMY1_CONFIG.id) {
					r = 255;
					b = 0;
					b = 0;
					found = true;
				} else if (md.id == PLAYER_ENTITY_ID) {
					r = 0;
					g = 255;
					b = 0;
					found = true;				
				}
				
				if (found) {
					radarImageData.data[i+0] = r;
					radarImageData.data[i+1] = g;
					radarImageData.data[i+2] = b;
					radarImageData.data[i+3] = 255;				
				}
			}
			
			if (found)
				break;
		}
	}*/
	
	for (var y = 0; y < MAP_HEIGHT; y++) {
		var entities = entityLayers[y]
		
		for (var i = 0; i < entities.length; i++) {
			var entity = entities[i];
			
			var r;
			var g 
			var b;
			var rwidth;
			var rheight;
			var draw = false;
		
			if ((entity.id == ENEMY1_CONFIG.id || entity.id == ENEMY3_CONFIG.id) && ENEMY_IsAlive(entity)) {
				r = 255;
				g = 0;
				b = 0;
				rwidth = 1;
				rheight = 1;
				draw = true;
			} else if (entity.id == PLAYER_ENTITY_ID) {
				r = 0;
				g = 255;
				b = 0;
				rwidth = 2;
				rheight = 2;
				draw = true;
			} else if (entity.id == ENEMY_SPAWNER_ID) {
				r = 0;
				g = 0;
				b = 255;
				rwidth = 3;
				rheight = 3;
				draw = true;			
			} else if (entity.id == BLOCK_ENTITY_ID) {
				r = 0;
				g = 0;
				b = 0;
				rwidth = 0;
				rheight = 0;
				draw = true;
			}
			
			if (draw) {
				var rx = Math.floor(ENTITY_GetX(entity)/MAPRADAR_WIDTH_RATIO);
				var ry = Math.floor(ENTITY_GetY(entity)/MAPRADAR_HEIGHT_RATIO);
				//var rwidth = Math.floor(ENTITY_GetWidth(entity)/MAPRADAR_WIDTH_RATIO/2);
				//var rheight = Math.floor(ENTITY_GetHeight(entity)/MAPRADAR_HEIGHT_RATIO/2);

				for (var rdx = -rwidth; rdx <= rwidth; rdx++) {
					for (var rdy = -rheight; rdy <= rheight; rdy++) {
						var ri = (ry+rdy)*RADAR_WIDTH_PX*4+(rx+rdx)*4;
						
						radarImageData.data[ri+0] = r;
						radarImageData.data[ri+1] = g;
						radarImageData.data[ri+2] = b;
						radarImageData.data[ri+3] = 255;
					}
				}
			}
		}
	}

	context.putImageData(radarImageData, CANVAS_WIDTH-RADAR_WIDTH_PX, 0);
}