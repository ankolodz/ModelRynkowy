import triangulation
import random

from datetime import datetime
import os
import sys

import networkx as nx
import numpy as np
import matplotlib.pyplot as plt

import shapefile 
import pickle

filedir = "shapefiles/test/"
filename = "road"

print len(sys.argv)

# function to generate .prj file information using spatialreference.org
def getWKT_PRJ (epsg_code):
	import urllib
	# access projection information
	wkt = urllib.urlopen("http://spatialreference.org/ref/epsg/{0}/prettywkt/".format(epsg_code))
	# remove spaces between charachters
	remove_spaces = wkt.read().replace(" ","")
	# place all the text on one line
	output = remove_spaces.replace("\n", "")
	return output

def init():
	if not os.path.exists(os.path.dirname(filedir)):
		try:
			os.makedirs(os.path.dirname(filedir))
		except OSError as exc: # Guard against race condition
			if exc.errno != errno.EEXIST:
				raise

def generateGraph(edges, points):
	"""
	points = list of coordinates x y of each point
	edges = tupples of point indexes
	"""
	G = nx.DiGraph()

	for i, point in enumerate(points):
		G.add_node(i, pos = tuple(point))

	pos = nx.get_node_attributes(G,'pos')

	for i, edge in enumerate(edges):
		x, y = edge
		G.add_edge(x, y)
		G.add_edge(y, x)

	return G

def showGraph(graph):
	pos = nx.get_node_attributes(graph,'pos')
	#strong = nx.is_strongly_connected(G)
	#print strong
	nx.draw_networkx_edges(graph, pos)
	nx.draw_networkx_nodes(graph, pos, node_size=100, node_color='blue', alpha=0.3)
	plt.draw()
	plt.show()

def generateShapefile( dirname, filename, graph, points ):
	# shapefile generator element
	writer = shapefile.Writer(shapefile.POLYLINE)

	edges = graph.edges()
	positions = nx.get_node_attributes(graph,'pos')

	for e in edges:
	 	i1, i2 = e
		writer.line(parts=[[[ graph.node[i1]['pos'][0], graph.node[i1]['pos'][1] ],
							[ graph.node[i2]['pos'][0], graph.node[i2]['pos'][1] ]]])

	writer.save( dirname + filename )

	pickle.dump( graph, open( dirname + filename + ".dump", "wb" ) )

def readSerialized(dirname, filename):
	graph = pickle.load( open( dirname + filename +".dump", "rb" ) )
	showGraph(graph)
	#G = generateGraph();

def modifyGraph(delete_road_factor, delete_way_factor, graph, show=False):

	edgesNum = len(graph.edges())/2
	delete_road_number = int(edgesNum * delete_road_factor)
	print delete_road_number

	print 'ROAD DELETION:'

	for x in range(0, delete_road_number):
		rand = random.randint(0, len(graph.edges()) - 1)
		# t1, t2 = edgeTuplesList.pop(rand)
		t1, t2 = graph.edges()[rand]

		print 'removing edge: (', t1, ',', t2, ')'
		graph.remove_edge(t1, t2)
		graph.remove_edge(t2, t1)
		if(not nx.is_strongly_connected(graph)):
			print 'is not strongly connected, reverting deletion'
			graph.add_edge(t1, t2)
			graph.add_edge(t2, t1)

	edgesNum = len(graph.edges())/2
	delete_way_number = int(edgesNum * delete_way_factor)

	print 'WAY DELETION:'

	for x in range(0, delete_way_number):
		rand = random.randint(0, len(graph.edges()) - 1)

		t1, t2 = graph.edges()[rand]

		print 'removing edge: (', t1, ',', t2, ')'
		graph.remove_edge(t1, t2)
		if(not nx.is_strongly_connected(graph)):
			print 'is not strongly connected, reverting deletion'
			graph.add_edge(t1, t2)

	if(show):
		showGraph(graph)

	return graph


if(len(sys.argv) != 4):
	if(len(sys.argv) != 2):

		print 'usage: python map_generator.py <nodes num> <roads to delete factor> <ways to delete factor>'
		print 'or to show generated map: python map_generator.py show'

		sys.exit(1)

if(len(sys.argv) == 2):
	if(sys.argv[1] == "read"):
		readSerialized(filedir, filename)
	sys.exit(1)

NODES_NUM = int(sys.argv[1])
ROADS_DEL_NUM = float(sys.argv[2])
WAY_DEL_NUM = float(sys.argv[3])

if(NODES_NUM < 0):
	print 'nodes num needs to be a positive number'
	sys.exit(1)

if( (ROADS_DEL_NUM < 0 or ROADS_DEL_NUM > 1) or (WAY_DEL_NUM < 0 or WAY_DEL_NUM > 1) ):
	print 'factors needs to be a float number between 0 and 1'
	sys.exit(1)	


print ROADS_DEL_NUM, WAY_DEL_NUM


random.seed(datetime.now())
xyPoints = [np.array([random.random(), random.random()]) for i in range(NODES_NUM)]
delaunay = triangulation.Delaunay2d(xyPoints)

graph = generateGraph(delaunay.getEdges(), points = map(lambda el: el.tolist(), delaunay.getPoints() ))

showGraph(graph)
graph = modifyGraph(ROADS_DEL_NUM, WAY_DEL_NUM, graph, show=False)

mappedPoints = map(lambda el: el.tolist(), delaunay.getPoints() )

generateShapefile(filedir, filename, graph, mappedPoints)

showGraph(graph)


# create the .prj file
init()
prj = open(filedir + filename + ".prj", "w")
# call the function and supply the epsg code
epsg = getWKT_PRJ("4326")
prj.write(epsg)
prj.close()

