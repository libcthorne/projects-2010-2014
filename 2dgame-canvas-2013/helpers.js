function IMAGE(url) {
	var img = new Image();
	img.src = url;
	return img;
}

function PxToMapBlock(px) {
	return Math.floor(px/MAP_BLOCK_SIZE_PX);
}