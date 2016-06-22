import os, sys, math, random
import pygame

window_width = 1024
window_height = 768

pygame.init()
screen = pygame.display.set_mode((window_width, window_height))

clock = pygame.time.Clock()

tree_root_length = 300

branch_angle = 40
branch_length = 90

tree_start_x = window_width/2
tree_start_y = window_height-20

capture_id = 0

def drawBranches(root_x, root_y, root_angle, angle, length, depth, maxdepth, thickness):
	if depth >= maxdepth:
		return

	if length < 0:
		length = 0

	if thickness < 1:
		thickness = 1

	left_x = root_x - length*math.sin(math.radians(angle+root_angle))
	left_y = root_y - length*math.cos(math.radians(angle+root_angle))

	right_x = root_x + length*math.sin(math.radians(angle-root_angle))
	right_y = root_y - length*math.cos(math.radians(angle-root_angle))
	
	new_left_x = root_x - (length*1)*math.sin(math.radians(angle+root_angle))
	new_left_y = root_y - (length*1)*math.cos(math.radians(angle+root_angle))

	new_right_x = root_x + (length*1)*math.sin(math.radians(angle-root_angle))
	new_right_y = root_y - (length*1)*math.cos(math.radians(angle-root_angle))
	
	#print(str(length) + "," + str(angle) + " -> xlen=" + str(xlen))
	#print("ylen=" + str(ylen))
	
	branch_color_val = int(255 - depth/maxdepth * 200)
	#branch_color = (branch_color_val, branch_color_val, branch_color_val)
	left_branch_color = (branch_color_val - random.randrange(0, branch_color_val), 255 - random.randrange(0, branch_color_val), branch_color_val - random.randrange(0, branch_color_val))
	
	right_branch_color = (branch_color_val - random.randrange(0, branch_color_val), 255 - random.randrange(0, branch_color_val), branch_color_val - random.randrange(0, branch_color_val))
	
	# Left branch
	pygame.draw.line(screen, left_branch_color, (root_x, root_y), (left_x, left_y), thickness)

	# Right branch
	pygame.draw.line(screen, right_branch_color, (root_x, root_y), (right_x, right_y), thickness)
	
	#xlen2 = math.fabs((length*0.75)*math.cos(angle))
	#ylen2 = math.fabs((length*0.75)*math.sin(angle))

	global capture_id
	#pygame.image.save(screen, "tree_" + str(capture_id) + ".png")
	capture_id += 1
	
	pygame.display.flip()
	
	nb_length = length*random.randrange(5, 11)*0.1
	
	# Left subbranches
	drawBranches(new_left_x, new_left_y, angle+root_angle, angle*random.randrange(7, 13)*0.1, nb_length, depth+1, maxdepth, thickness-1)
	
	# Right subbranches
	drawBranches(new_right_x, new_right_y, -angle+root_angle, angle*random.randrange(7, 13)*0.1, nb_length, depth+1, maxdepth, thickness-1)

root_thickness = 8

tree_depth = 15
max_tree_depth = 15
original_tree_depth = tree_depth

while True:
	clock.tick(50)

	for event in pygame.event.get():
		if event.type == pygame.QUIT:
			sys.exit()
		elif event.type == pygame.KEYDOWN:
			if event.key == pygame.K_SPACE:
				tree_depth = original_tree_depth

	if tree_depth <= max_tree_depth:
		screen.fill((0, 0, 0))

		pygame.draw.line(screen, (255, 255, 255), (tree_start_x, tree_start_y), (tree_start_x, tree_start_y-tree_root_length), root_thickness)

		drawBranches(tree_start_x, tree_start_y-tree_root_length, 0, branch_angle, branch_length, 0, tree_depth, root_thickness)
		
		pygame.display.flip()
		
		print("tree level " + str(tree_depth))
		#pygame.image.save(screen, "tree_" + str(tree_depth) + ".png")
		
		tree_depth += 1