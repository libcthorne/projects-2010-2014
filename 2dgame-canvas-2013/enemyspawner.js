var ENEMY_SPAWNER_ID = "enemyspawner";

var ENEMY_SPAWNER1_X = 64;
var ENEMY_SPAWNER1_Y = 42;

var ENEMY_SPAWNER2_X = 196;
var ENEMY_SPAWNER2_Y = 42;

var ENEMY_SPAWNER3_X = 64;
var ENEMY_SPAWNER3_Y = 214;

var ENEMY_SPAWNER4_X = 196;
var ENEMY_SPAWNER4_Y = 214;

var ENEMY_SPAWNER5_X = 128;
var ENEMY_SPAWNER5_Y = 214;

var ENEMY_SPAWNER_INTERVAL = 2000;

function _ENEMYSPAWNER(e, centerPt) {
	_ENTITY(
			e,
			{
				id:ENEMY_SPAWNER_ID,
				updateFunc:ENEMYSPAWNER_Update,
				centerPt:centerPt,
				solid:false
			}
	);
	
	e.nextSpawnTime = 0;
}

function ENEMYSPAWNER(centerPt) {
	_ENEMYSPAWNER(this, centerPt);
}

function ENEMYSPAWNER_Update(e) {
	if (!PLAYER_IsAlive(player))
		return;

	if (curTime > e.nextSpawnTime) {
		//if (Math.random() > 0.5)
			var enemy = new ENEMY1(ENTITY_GetPos(e));
		//else
		//	var enemy = new ENEMY3(ENTITY_GetPos(e));

		e.nextSpawnTime = curTime + ENEMY_SPAWNER_INTERVAL;
	}
}

var spawner1 = new ENEMYSPAWNER(new Point(ENEMY_SPAWNER1_X, ENEMY_SPAWNER1_Y));
var spawner2 = new ENEMYSPAWNER(new Point(ENEMY_SPAWNER2_X, ENEMY_SPAWNER2_Y));
var spawner3 = new ENEMYSPAWNER(new Point(ENEMY_SPAWNER3_X, ENEMY_SPAWNER3_Y));
var spawner4 = new ENEMYSPAWNER(new Point(ENEMY_SPAWNER4_X, ENEMY_SPAWNER4_Y));
var spawner5 = new ENEMYSPAWNER(new Point(ENEMY_SPAWNER5_X, ENEMY_SPAWNER5_Y));