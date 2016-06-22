var BLOCK_ENTITY_ID = "block";

function _BLOCK(e, centerPt, width, height, color) {
	_ENTITY(
			e,
			{
				id:BLOCK_ENTITY_ID,
				drawFunc:BLOCK_Draw,
				centerPt:centerPt,
				bbWidthPx:width,
				bbHeightPx:height,
				solid:true
			}
	);
	
	e.color = color;
}

function BLOCK(centerPt, width, height, color) {
	_BLOCK(this, centerPt, width, height, color);
}

function BLOCK_Draw(e, context) {
	var x = MAP_GetCoordinatePositionX(ENTITY_GetX(e));
	var y = MAP_GetCoordinatePositionY(ENTITY_GetY(e))-MAP_BLOCK_SIZE_PX;

	context.beginPath();
	context.rect(x, y, MAP_BLOCK_SIZE_PX+2, MAP_BLOCK_SIZE_PX+2);
	context.fillStyle = e.color;
	context.fill();
}

function BLOCK_Create(xPx, yPx, widthPx, heightPx, color) {
	var blockX = MAP_PixelToBlock(xPx);
	var blockY = MAP_PixelToBlock(yPx);
	var blockWidth = MAP_PixelToBlock(widthPx);
	var blockHeight = MAP_PixelToBlock(heightPx);

	for (var x = blockX; x < blockX+blockWidth; x++) {
		for (var y = blockY; y < blockY+blockHeight; y++) {
			var block = new BLOCK(new Point(x, y), 0, 0, color);
		}
	}
}

var TESTBLOCK_COLOR = "#FF0000"

var TESTBLOCK1_X_PX = 104;
var TESTBLOCK1_Y_PX = 112;
var TESTBLOCK1_WIDTH_PX = 40;
var TESTBLOCK1_HEIGHT_PX = 48;

BLOCK_Create(TESTBLOCK1_X_PX, TESTBLOCK1_Y_PX, TESTBLOCK1_WIDTH_PX, TESTBLOCK1_HEIGHT_PX, TESTBLOCK_COLOR);

var TESTBLOCK2_X_PX = 116;
var TESTBLOCK2_Y_PX = 188;
var TESTBLOCK2_WIDTH_PX = 56;
var TESTBLOCK2_HEIGHT_PX = 16;

BLOCK_Create(TESTBLOCK2_X_PX, TESTBLOCK2_Y_PX, TESTBLOCK2_WIDTH_PX, TESTBLOCK2_HEIGHT_PX, TESTBLOCK_COLOR);

var TESTBLOCK3_X_PX = 116;
var TESTBLOCK3_Y_PX = 188;
var TESTBLOCK3_WIDTH_PX = 16;
var TESTBLOCK3_HEIGHT_PX = 48;

BLOCK_Create(TESTBLOCK3_X_PX, TESTBLOCK3_Y_PX, TESTBLOCK3_WIDTH_PX, TESTBLOCK3_HEIGHT_PX, TESTBLOCK_COLOR);

var TESTBLOCK4_X_PX = 116;
var TESTBLOCK4_Y_PX = 236;
var TESTBLOCK4_WIDTH_PX = 56;
var TESTBLOCK4_HEIGHT_PX = 16;

BLOCK_Create(TESTBLOCK4_X_PX, TESTBLOCK4_Y_PX, TESTBLOCK4_WIDTH_PX, TESTBLOCK4_HEIGHT_PX, TESTBLOCK_COLOR);

var TESTBLOCK5_X_PX = 156;
var TESTBLOCK5_Y_PX = 204;
var TESTBLOCK5_WIDTH_PX = 16;
var TESTBLOCK5_HEIGHT_PX = 12;

BLOCK_Create(TESTBLOCK5_X_PX, TESTBLOCK5_Y_PX, TESTBLOCK5_WIDTH_PX, TESTBLOCK5_HEIGHT_PX, TESTBLOCK_COLOR);