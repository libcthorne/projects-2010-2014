var ENTITY_ORIENTATION_FRONT = 0;
var ENTITY_ORIENTATION_BACK = 1;
var ENTITY_ORIENTATION_LEFT = 2;
var ENTITY_ORIENTATION_RIGHT = 3;

function _ENTITY(e, config) {
	e.id = config.id;
	e.center = config.centerPt;
	
	if (config.orientation)
		e.orientation = config.orientation;

	e.solid = config.solid;
	
	if (config.bbWidthPx != null && config.bbHeightPx != null)
		ENTITY_CreateBoundingBox(e, config.bbWidthPx, config.bbHeightPx, config.bbCenterPxPt);
	
	if (config.drawFunc) {
		e.draw = function(context) { config.drawFunc(this, context); };
		e.drawable = true;
	} else {
		e.drawable = false;
	}
	
	if (config.updateFunc) {
		e.update = function(curTime) { config.updateFunc(this); };
		e.updatable = true;
	} else {
		e.updatable = false;
	}
	
	if (config.centerPt.y > 0 && config.centerPt.y < MAP_HEIGHT) {
		entityLayers[config.centerPt.y].push(e);
	}
}

function ENTITY(config) {
	_ENTITY(this, config);
}

function ENTITY_Delete(e) {
	var entities = entityLayers[e.center.y];
	entities.splice(entities.indexOf(e), 1);
	
	ENTITY_DeleteBoundingBox(e);
}

function ENTITY_OrientationToString(orientation) {
	switch (orientation) {
	case ENTITY_ORIENTATION_FRONT:
		return "front";
	case ENTITY_ORIENTATION_BACK:
		return "back";
	case ENTITY_ORIENTATION_LEFT:
		return "left";
	case ENTITY_ORIENTATION_RIGHT:
		return "right";
	}
}

function ENTITY_GetOrientation(e) {
	return e.orientation;
}

function ENTITY_CreateBoundingBox(e, widthPx, heightPx, centerPxPt) {
	var width = Math.floor(widthPx/MAP_BLOCK_SIZE_PX);
	var height = Math.floor(heightPx/MAP_BLOCK_SIZE_PX);

	var x0;
	var y0;
	
	if (centerPxPt) {
		x0 = e.center.x-Math.floor(centerPxPt.x/MAP_BLOCK_SIZE_PX);
		y0 = e.center.y-Math.floor(centerPxPt.y/MAP_BLOCK_SIZE_PX);
	} else {
		x0 = e.center.x-Math.floor(width/2);
		y0 = e.center.y-Math.floor(height/2);
	}

	e.bb = new Array();
	e.bb.width = width;
	e.bb.height = height;

	if (width == 0 && height == 0) {
		e.bb.push(new Point(x0, y0));
	} else {
		for (var px = 0; px < width; px++) {
			e.bb.push(new Point(x0+px, y0));
			
			if (height > 1)
				e.bb.push(new Point(x0+px, y0+height-1));
		}

		for (var py = 1; py < height-1; py++) {
			e.bb.push(new Point(x0, y0+py));
			
			if (width > 1)
				e.bb.push(new Point(x0+width-1, y0+py));
		}
	}

	if (centerPxPt) {
		//e.center = new Point(x0+Math.floor(centerPxPt.x/MAP_BLOCK_SIZE_PX), y0+Math.floor(centerPxPt.y/MAP_BLOCK_SIZE_PX));
		e.bbRotatable = true;
	} else {
		e.bbRotatable = false;
	}

	for (var i = 0; i < e.bb.length; i++) {
		MAP_SetPositionDataIfFree(e.bb[i].x, e.bb[i].y, e);
	}
}

function ENTITY_DeleteBoundingBox(e) {
	if (e.bb == undefined)
		return;

	ENTITY_ClearMapData(e);

	e.bb = undefined;
}

function ENTITY_Rotate90(e) {
	for (var i = 0; i < e.bb.length; i++) {
		var x2 = e.center.x-e.bb[i].x;
		e.bb[i].x = e.center.x-(e.bb[i].y-e.center.y);
		e.bb[i].y = e.center.y-x2;
	}
}

function ENTITY_Rotate180(e) {
	for (var i = 0; i < e.bb.length; i++) {
		e.bb[i].x = e.center.x-(e.bb[i].x-e.center.x);
		e.bb[i].y = e.center.y-(e.bb[i].y-e.center.y);
	}
}

function ENTITY_Rotate270(e) {
	for (var i = 0; i < e.bb.length; i++) {
		var x2 = e.bb[i].x-e.center.x;
		e.bb[i].x = e.center.x+(e.bb[i].y-e.center.y);
		e.bb[i].y = e.center.y-x2;
	}
}

function ENTITY_Rotate(e, newOrientation) {
	if (!e.bbRotatable)
		return;

	switch (e.orientation) {
	case ENTITY_ORIENTATION_FRONT:
		switch (newOrientation) {
		case ENTITY_ORIENTATION_BACK:
			ENTITY_Rotate180(e);
			break;
		case ENTITY_ORIENTATION_LEFT:
			ENTITY_Rotate90(e);
			break;
		case ENTITY_ORIENTATION_RIGHT:
			ENTITY_Rotate270(e);
			break;
		}
		break;
	case ENTITY_ORIENTATION_BACK:
		switch (newOrientation) {
		case ENTITY_ORIENTATION_FRONT:
			ENTITY_Rotate180(e);
			break;
		case ENTITY_ORIENTATION_LEFT:
			ENTITY_Rotate270(e);
			break;
		case ENTITY_ORIENTATION_RIGHT:
			ENTITY_Rotate90(e);
			break;
		}
		break;
	case ENTITY_ORIENTATION_LEFT:
		switch (newOrientation) {
		case ENTITY_ORIENTATION_FRONT:
			ENTITY_Rotate270(e);
			break;
		case ENTITY_ORIENTATION_BACK:
			ENTITY_Rotate90(e);
			break;
		case ENTITY_ORIENTATION_RIGHT:
			ENTITY_Rotate180(e);
			break;
		}
		break;		
	case ENTITY_ORIENTATION_RIGHT:
		switch (newOrientation) {
		case ENTITY_ORIENTATION_FRONT:
			ENTITY_Rotate90(e);
			break;
		case ENTITY_ORIENTATION_BACK:
			ENTITY_Rotate270(e);
			break;
		case ENTITY_ORIENTATION_LEFT:
			ENTITY_Rotate180(e);
			break;
		}
		break;
	}
}

function ENTITY_ClearMapData(e) {
	for (var i = 0; i < e.bb.length; i++) {
		if (MAP_GetPositionDataSafe(e.bb[i].x, e.bb[i].y) == e)
			MAP_SetPositionDataSafe(e.bb[i].x, e.bb[i].y, undefined);
	}
}

function ENTITY_ShiftX(e, dx) {
	if (dx == 0)
		return;

	e.center.x += dx;

	ENTITY_ClearMapData(e);

	for (var i = 0; i < e.bb.length; i++) {
		e.bb[i].x += dx;
		
		MAP_SetPositionDataIfFree(e.bb[i].x, e.bb[i].y, e);
	}
}

function ENTITY_ShiftY(e, dy) {
	if (dy == 0)
		return;

	var entityLayerOld = entityLayers[e.center.y];
	entityLayerOld.splice(entityLayerOld.indexOf(e), 1);
	
	e.center.y += dy;
	
	var entityLayerNew = entityLayers[e.center.y];
	entityLayerNew.push(e);
	
	ENTITY_ClearMapData(e);

	for (var i = 0; i < e.bb.length; i++) {
		e.bb[i].y += dy;
		
		MAP_SetPositionDataIfFree(e.bb[i].x, e.bb[i].y, e);
	}
}

function ENTITY_Shift(e, dx, dy) {
	if (dx == 0 && dy == 0)
		return;

	e.center.x += dx;

	var entityLayerOld = entityLayers[e.center.y];
	entityLayerOld.splice(entityLayerOld.indexOf(e), 1);
	
	e.center.y += dy;
	
	var entityLayerNew = entityLayers[e.center.y];
	entityLayerNew.push(e);

	ENTITY_ClearMapData(e);

	for (var i = 0; i < e.bb.length; i++) {
		e.bb[i].x += dx;
		e.bb[i].y += dy;
		
		MAP_SetPositionDataIfFree(e.bb[i].x, e.bb[i].y, e);
	}
}

// Select entities within a rectangle created from origin+range
function ENTITY_BBRectangleTest(e, xrange, yrange) {
	var entities = new Array();

	for (var x = e.center.x - xrange; x <= e.center.x + xrange; x++) {
		for (var y = e.center.y - yrange; y <= e.center.y + yrange; y++) {
			var pe = MAP_GetPositionDataSafe(x, y);

			if (pe == undefined)
				continue;

			if (pe == WORLD_ENTITY)
				continue;

			if (pe == e)
				continue;
			
			if (entities.indexOf(pe) == -1)
				entities.push(pe);
		}
	}

	return entities;
}

function ENTITY_BBHitTest(e, dx, dy) {
	for (var i = 0; i < e.bb.length; i++) {
		var pt = e.bb[i];
		var nx = pt.x+dx;
		var ny = pt.y+dy;
	
		var pe = MAP_GetPositionDataSafe(nx, ny);

		if (pe == undefined)
			continue;

		if (pe == e)
			continue;

		return pe;
	}
}

function ENTITY_BBCollisionTest(e, dx, dy) {
	if (!e.bb)
		console.log(e);

	var pe = ENTITY_BBHitTest(e, dx, dy);

	return pe != undefined && pe.solid;
}

function ENTITY_GetX(e) {
	return e.center.x;
}

function ENTITY_GetY(e) {
	return e.center.y;
}

function ENTITY_GetPos(e) {
	return new Point(e.center.x, e.center.y);
}

// TODO: width for nonBB ents

function ENTITY_GetWidth(e) {
	if (!e.bb)
		return 0;

	return e.bb.width;
}

function ENTITY_GetHeight(e) {
	if (!e.bb)
		return 0;

	return e.bb.height;
}