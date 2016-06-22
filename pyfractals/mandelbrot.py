import os, sys
import pygame

window_width = 640
window_height = 480

pygame.init()
screen = pygame.display.set_mode((window_width, window_height))

clock = pygame.time.Clock()

test_box_x = 10

while True:
	clock.tick(50)

	for event in pygame.event.get():
		if event.type == pygame.QUIT:
			sys.exit()

	if pygame.key.get_pressed()[pygame.K_RETURN]:
		test_box_x += 1
	
	screen.fill((255, 255, 255))
	
	#pygame.draw.rect(screen, (255, 255, 255), (test_box_x, 200, 20, 20))

	for i in range(50, 500):
		for px in range(0, window_width):
			for py in range(0, window_height):
				x0 = -2.5 + (px/window_width) * 3.5
				y0 = -1 + (py/window_height) * 2
				
				x = 0
				y = 0
				
				iteration = 0
				max_iteration = i
				
				while x*x + y*y < 2*2 and iteration < max_iteration:
					xtemp = x*x - y*y + x0
					y = 2*x*y + y0
					
					x = xtemp
					
					iteration += 1

				color = max(0, 255 - (iteration/(max_iteration/4))*255)
				
				if color == 0:
					pass
					#print("zero")
			
				screen.set_at((px, py), (color, color, color))

		pygame.display.flip()
		print("rendered, i=" + str(i))
		#pygame.image.save(screen, "mb_" + str(i) + ".png")
	