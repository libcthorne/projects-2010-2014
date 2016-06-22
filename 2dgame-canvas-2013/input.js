var spacePressed = false;
var leftPressed = false;
var upPressed = false;
var rightPressed = false;
var downPressed = false;

function INPUT_IsSpacePressed() {
	return spacePressed;
}

function INPUT_IsLeftPressed() {
	return leftPressed;
}

function INPUT_IsRightPressed() {
	return rightPressed;
}

function INPUT_IsUpPressed() {
	return upPressed;
}

function INPUT_IsDownPressed() {
	return downPressed;
}

document.addEventListener("keydown", function(event) {
	var swallowKeyPress = true;
	
	switch (event.keyCode) {
	case 32: // space
		spacePressed = true;
		break;
	case 37: // left
	case 65: // a
		leftPressed = true;
		break;
	case 38: // up
	case 87: // w
		upPressed = true;
		break;
	case 39: // right
	case 68: // d
		rightPressed = true;
		break;
	case 40: // down
	case 83: // s
		downPressed = true;
		break;
	default:
		swallowKeyPress = false;
		break;
	}
	
	if (swallowKeyPress)
		event.preventDefault();
});

document.addEventListener("keyup", function(event) {
	switch (event.keyCode) {
	case 32: // space
		spacePressed = false;
		break;
	case 37: // left
	case 65: // a
		leftPressed = false;
		break;
	case 38: // up
	case 87: // w
		upPressed = false;
		break;
	case 39: // right
	case 68: // d
		rightPressed = false;
		break;
	case 40: // down
	case 83: // s
		downPressed = false;
		break;
	}
});