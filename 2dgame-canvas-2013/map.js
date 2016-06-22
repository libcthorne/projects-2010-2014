var MAP_WIDTH = MAP_WIDTH_PX/MAP_BLOCK_SIZE_PX;
var MAP_HEIGHT = MAP_HEIGHT_PX/MAP_BLOCK_SIZE_PX;

var currentMapImage = new Image();
currentMapImage.src = "res/map/test.png";

var mapOffsetX = CANVAS_WIDTH/2-MAP_BLOCK_SIZE_PX/2;
var mapOffsetY = CANVAS_HEIGHT/2-MAP_BLOCK_SIZE_PX/2;
var mapOffsetShiftX = 0;
var mapOffsetShiftY = 0;
var mapOffsetDelta = 2;

var mapData = new Array();

for (var x = 0; x < MAP_WIDTH_PX/MAP_BLOCK_SIZE_PX; x++) {
	mapData[x] = new Array();

	for (var y = 0; y < MAP_HEIGHT_PX/MAP_BLOCK_SIZE_PX; y++) {
		mapData[x][y] = undefined;
	}
}

function MAP_ShiftX(offsetX) {
	mapOffsetShiftX += offsetX*MAP_BLOCK_SIZE_PX;
}

function MAP_ShiftY(offsetY) {
	mapOffsetShiftY += offsetY*MAP_BLOCK_SIZE_PX;
}

function MAP_InShift() {
	return mapOffsetShiftX != 0 || mapOffsetShiftY != 0;
}

function MAP_UpdatePosition() {
	var mapShift = false;
	
	if (mapOffsetShiftX > 0) {
		mapOffsetX += mapOffsetDelta;
		mapOffsetShiftX -= mapOffsetDelta;
		mapShift = true;
	} else if (mapOffsetShiftX < 0) {
		mapOffsetX -= mapOffsetDelta;
		mapOffsetShiftX += mapOffsetDelta;
		mapShift = true;
	}
	
	if (mapOffsetShiftY > 0) {
		mapOffsetY += mapOffsetDelta;
		mapOffsetShiftY -= mapOffsetDelta;
		mapShift = true;
	} else if (mapOffsetShiftY < 0) {
		mapOffsetY -= mapOffsetDelta;
		mapOffsetShiftY += mapOffsetDelta;
		mapShift = true;
	}
	
	return mapShift;
}

function MAP_GetCoordinatePositionX(x) {
	return x*MAP_BLOCK_SIZE_PX+mapOffsetX;
	//return x*MAP_BLOCK_SIZE_PX+MAP_BLOCK_SIZE_PX/2+mapOffsetX;
}

function MAP_GetCoordinatePositionY(y) {
	//return y*MAP_BLOCK_SIZE_PX+mapOffsetY+MAP_BLOCK_Y_CENTER_OFFSET
	return y*MAP_BLOCK_SIZE_PX+MAP_BLOCK_SIZE_PX/2+MAP_BLOCK_Y_CENTER_OFFSET+mapOffsetY;
}

function MAP_GetPositionData(x, y) {
	return mapData[x][y];
}

function MAP_GetPositionDataSafe(x, y) {
	if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
		return WORLD_ENTITY;
		
	return mapData[x][y];
}

function MAP_SetPositionData(x, y, d) {
	mapData[x][y] = d;
}

function MAP_SetPositionDataSafe(x, y, d) {
	if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT)
		return;

	mapData[x][y] = d;
}

function MAP_SetPositionDataIfFree(x, y, d) {
	if (MAP_GetPositionDataSafe(x, y) == undefined) {
		MAP_SetPositionDataSafe(x, y, d);
	} else {
		return false;
	}
}

function MAP_PixelToBlock(px) {
	return Math.floor(px/MAP_BLOCK_SIZE_PX);
}

// http://kaioa.com/node/103
var renderToCanvas = function(width, height, renderFunction) {
	var buffer = document.createElement('canvas');
	buffer.width = width;
	buffer.height = height;
	renderFunction(buffer.getContext('2d'));
	return buffer;
};

/*var mapGridImage = renderToCanvas(MAP_WIDTH_PX, MAP_HEIGHT_PX, function(context) {
	for (var x = 0; x <= MAP_WIDTH_PX/MAP_BLOCK_SIZE_PX; x++) {
		for (var y = 0; y <= MAP_HEIGHT_PX/MAP_BLOCK_SIZE_PX; y++) {
			context.moveTo(0, y*MAP_BLOCK_SIZE_PX);
			context.lineTo(MAP_WIDTH_PX, y*MAP_BLOCK_SIZE_PX);
			context.stroke();
			
			context.moveTo(x*MAP_BLOCK_SIZE_PX, 0);
			context.lineTo(x*MAP_BLOCK_SIZE_PX, MAP_HEIGHT_PX);
			context.stroke();
		}
	}
});*/

function MAP_Draw(context) {
	context.drawImage(currentMapImage, mapOffsetX, mapOffsetY);
}