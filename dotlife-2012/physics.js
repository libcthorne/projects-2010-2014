var PHYSICS = {
	MIN_Y:0,
	MAX_Y:CANVAS_HEIGHT,
	GRAVITY_VALUE:0.2,
};

/*
	http://stackoverflow.com/questions/1073336/circle-line-collision-detection
    E is the starting point of the ray,
    L is the end point of the ray,
    C is the center of sphere you're testing against
    r is the radius of that sphere
*/
PHYSICS.lineIntersectsCircle = function(e, l, c, r) {
	var d = l.sub(e);
	var f = e.sub(c);

	var a = d.dot(d);
	var b = 2*f.dot(d);
	var c = f.dot(f) - r*r;

	var discriminant = b*b-4*a*c;

	if (discriminant < 0) {
		// no intersection
		return false;
	} else {
		// ray didn't totally miss sphere,
		// so there is a solution to
		// the equation.

		discriminant = Math.sqrt(discriminant);

		// either solution may be on or off the ray so need to test both
		// t1 is always the smaller value, because BOTH discriminant and
		// a are nonnegative.
		var t1 = (-b - discriminant)/(2*a);
		var t2 = (-b + discriminant)/(2*a);

		// 3x HIT cases:
		//          -o->             --|-->  |            |  --|->
		// Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit), 

		// 3x MISS cases:
		//       ->  o                     o ->              | -> |
		// FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

		if( t1 >= 0 && t1 <= 1 )
		{
			// t1 is the intersection, and it's closer than t2
			// (since t1 uses -b - discriminant)
			// Impale, Poke
			
			//console.log("impale, poke");
			
			return true;
		}

		// here t1 didn't intersect so we are either started
		// inside the sphere or completely past it
		if( t2 >= 0 && t2 <= 1 )
		{
			// ExitWound
			
			//console.log("exit wound");
			
			// temp
			return false;
		}

		// no intn: FallShort, Past, CompletelyInside
		return false;
	}
}

PHYSICS.moveEntity = function(entity) {
	entity.x += entity.velocity.x;
	entity.y += entity.velocity.y;
	
	//console.log("velocity length: " + entity.velocity.len());
}

PHYSICS.update = function() {
	// Simulate movement

	for (var i = 0; i < ENTITYMGR.entities.length; i++) {
		var entity = ENTITYMGR.entities[i];
		
		PHYSICS.moveEntity(entity);
	}
	
	// Gravity
	
	for (var i = 0; i < ENTITYMGR.entities.length; i++) {
		var entity = ENTITYMGR.entities[i];
		
		if (entity.getCenterY()+entity.radius < this.MAX_Y) {
			var entityCollision = false;
			
			for (var j = 0; j < ENTITYMGR.entities.length; j++) {
				var entity2 = ENTITYMGR.entities[j];

				if (entity == entity2)
					continue;
				
				var ec = new vec2(entity.getCenterX(), entity.getCenterY());
				var e2c = new vec2(entity2.getCenterX(), entity2.getCenterY());
				var er = entity.radius;
				var e2r = entity2.radius;
				
				//console.log("checking if " + entity.toString() + " collides with " + entity2.toString());
				
				if (ec.sub(e2c).len() <= er+e2r) {
					entityCollision = true;
					
					if (entity.bouncy) {
						entity.velocity.y = -entity.velocity.y;
					} else {
						entity.velocity.y = 0;
						// TEMP TEMP TEMP
						entity.setCenterY(this.MAX_Y-entity.radius);
					}
					
					break;
				}
			}
			
			if (!entityCollision)
				entity.velocity.y += this.GRAVITY_VALUE;
		} else {
			if (entity.bouncy) {
				entity.velocity.y = -entity.velocity.y;
			} else {
				entity.velocity.y = 0;
				// TEMP TEMP TEMP
				entity.setCenterY(this.MAX_Y-entity.radius);
			}
		}
	}
}

/*float a = d.Dot( d ) ;
float b = 2*f.Dot( d ) ;
float c = f.Dot( f ) - r*r ;

float discriminant = b*b-4*a*c;
if( discriminant < 0 )
{
  // no intersection
}
else
{
  // ray didn't totally miss sphere,
  // so there is a solution to
  // the equation.

  discriminant = sqrt( discriminant );

  // either solution may be on or off the ray so need to test both
  // t1 is always the smaller value, because BOTH discriminant and
  // a are nonnegative.
  float t1 = (-b - discriminant)/(2*a);
  float t2 = (-b + discriminant)/(2*a);

  // 3x HIT cases:
  //          -o->             --|-->  |            |  --|->
  // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit), 

  // 3x MISS cases:
  //       ->  o                     o ->              | -> |
  // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

  if( t1 >= 0 && t1 <= 1 )
  {
    // t1 is the intersection, and it's closer than t2
    // (since t1 uses -b - discriminant)
    // Impale, Poke
    return true ;
  }

  // here t1 didn't intersect so we are either started
  // inside the sphere or completely past it
  if( t2 >= 0 && t2 <= 1 )
  {
    // ExitWound
    return true ;
  }

  // no intn: FallShort, Past, CompletelyInside
  return false ;
}*/