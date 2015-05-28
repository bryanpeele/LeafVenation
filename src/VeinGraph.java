import processing.core.PApplet;

import java.util.ArrayList;
import java.io.PrintWriter;

public class VeinGraph {
	PApplet parent;
	
	public ArrayList<Node> nodes;
	public ArrayList<Edge> edges;
	LeafShape leaf;
	
	VeinGraph (PApplet p, ArrayList<Node> ns, ArrayList<Edge> es, LeafShape ls) {
		parent=p;
		nodes = ns;
		edges = es;
		leaf = ls;
	}
	
	void display() {
		//parent.stroke(150,50,150);
		parent.stroke(0);
		for (Edge edge : edges) {
			edge.display();
		}
	}
	
	public void clearAuxins() {
		for (Node node : nodes) {
			node.localAuxins.clear();
		}
	}
	
	public void assignAuxins(ArrayList<Auxin> as) {
		for (Auxin a : as) {
			float minDistance = 10;
			int closestNodeIndex = 0;
			for(Node n : nodes) {
				float distance = (float) Math.sqrt(Math.pow(a.x-n.x,2)+Math.pow(a.y-n.y,2));
				if (distance < minDistance) {
					minDistance = distance;
					closestNodeIndex = nodes.indexOf(n);
				}
			}
			nodes.get(closestNodeIndex).localAuxins.add(a);
		}
	}
	
	public void grow() {
		ArrayList<Node> nodesToAdd = new ArrayList<Node>();
		ArrayList<Edge> edgesToAdd = new ArrayList<Edge>();

		
		for (Node node : nodes) {	
			if (node.localAuxins.size() > 0) { 
				//Create a new vein node if influential (local) auxins exist
				// See Runions equation 1 for details
				//First find normal direction for new vein node
				
				node.isTerminal = false;
				
				SimplePoint normal = new SimplePoint(0,0);
				for(Auxin a : node.localAuxins) {
					float distance = (float) Math.sqrt(Math.pow(a.x-node.x,2)+Math.pow(a.y-node.y,2));
					normal.x += (a.x - node.x)/distance;
					normal.y += (a.y - node.y)/distance;
				}
				float normalMag = (float) Math.sqrt((normal.x*normal.x) + (normal.y*normal.y));
				normal.x = normal.x/normalMag;
				normal.y = normal.y/normalMag;
				
				if (normalMag > Constants.minEdgeLength) {
					SimplePoint newNodePos = new SimplePoint(0,0);
					newNodePos.x = node.x + (Constants.VeinGrowthLength*normal.x);
					newNodePos.y = node.y + (Constants.VeinGrowthLength*normal.y);
					
					Node newNode = new Node(parent, newNodePos.x, newNodePos.y);
					nodesToAdd.add(newNode);
					
					Edge newEdge = new Edge(parent, node, newNode);
					edgesToAdd.add(newEdge);
					
					if(!node.connectedEdges.contains(newEdge)) {node.connectedEdges.add(newEdge);}
					if(!newNode.connectedEdges.contains(newEdge)) {newNode.connectedEdges.add(newEdge);}
				}
			}
		}
		for(Node n : nodesToAdd) {nodes.add(n);}
		for(Edge e : edgesToAdd) {edges.add(e);}
	}
	
	public void assignEdgeWeights() {
		//Initialize all edges to one
		ArrayList<Edge> edgesToSolve = new ArrayList<Edge>();
		
		for (Edge edge : edges) {
			edge.size = 1;
			edgesToSolve.add(edge);
		}
		
		//Keep assignment for all terminal edges
		
		//Create list of terminal nodes
		ArrayList<Node> terminalNodes = new ArrayList<Node>();
		ArrayList<Edge> terminalEdges = new ArrayList<Edge>();
		for (Node node : nodes) {
			if (node.isTerminal) {
				terminalNodes.add(node);
				terminalEdges.add(node.connectedEdges.get(0));
			}
		}
		//Add terminal edges to solved edges and generate new list of working nodes
		ArrayList<Node> workingNodes = new ArrayList<Node>();
		for (Edge tE : terminalEdges) {
			edgesToSolve.remove(tE);
			if (!workingNodes.contains(tE.n0)) {workingNodes.add(tE.n0);}
		}
		
		while(edgesToSolve.size() > 0) {
			ArrayList<Node> newWorkingNodes = new ArrayList<Node>();
			for (Node node : workingNodes) {
				
				node.connectedEdges.get(0).size = 0;
				for (int index = 1; index < node.connectedEdges.size(); index++) {
					node.connectedEdges.get(0).size += Math.pow(node.connectedEdges.get(index).size, Constants.edgeScalingPower); //TODO add sclaing exponent
				}
				node.connectedEdges.get(0).size = (float) Math.pow(node.connectedEdges.get(0).size, 1/Constants.edgeScalingPower);
				edgesToSolve.remove(node.connectedEdges.get(0));
				if (!newWorkingNodes.contains(node.connectedEdges.get(0).n0)) {newWorkingNodes.add(node.connectedEdges.get(0).n0);}
				
				node.connectedEdges.get(0).size = PApplet.min(node.connectedEdges.get(0).size,Constants.maxEdgeWeight);
			}
			workingNodes.clear();
			workingNodes.addAll(newWorkingNodes);
		}
		
	}
	
	void exportGeometry(String filename) {
		PApplet.println("Output started");
		PrintWriter output = parent.createWriter(filename);
		float xGeom;
		float yGeom;
		float sizeGeom;
		
		
		//Use a bunch of spheres to make the vein structure
		//output.println("scale([1,1,2])");
		//output.println("union() {");
		
		for (Node node : nodes) {
			xGeom = Constants.geomScale * node.x;
			yGeom = Constants.geomScale * node.y;
			sizeGeom = node.connectedEdges.get(0).size / 10;
			output.println("translate(["+xGeom+","+yGeom+",0]) sphere("+sizeGeom+", $fn=10);");
	
					
		}
		//output.println("}");
		
		
		//Use a polygon to make the shape of the leaf
		output.println("linear_extrude(height = 0.1, center = true, convexity = 10, twist = 0)");
		output.println("polygon(points = [");
		for (Node node : leaf.nodes) {
			xGeom = Constants.geomScale * node.x;
			yGeom = Constants.geomScale * node.y;
			output.println("["+xGeom+","+yGeom+"],");
		}
		output.println("], paths = [ [");
		for (int i = 0; i < leaf.nodes.length; i++) {
			output.println(i+",");
		}
		output.println("] ] );");
		
		output.flush();
		output.close();
		PApplet.println("Output finished");
	}
	
	void exportGeometryFreeCAD(String filename) {
		PApplet.println("Output started");
		PrintWriter output = parent.createWriter(filename+".py");
		float xGeom;
		float yGeom;
		float sizeGeom;
		
		
		output.println("import Part");
		output.println("d = FreeCAD.newDocument(\""+filename+"\")");
		
		
		//Use a bunch of spheres to make the vein structure
		
		for (Node node : nodes) {
			xGeom = Constants.geomScale * node.x;
			yGeom = Constants.geomScale * node.y;
			sizeGeom = node.connectedEdges.get(0).size / 10;
			int i = nodes.indexOf(node);
			output.println("m"+i+" = Part.makeSphere("+sizeGeom+")");
			output.println("v"+i+" = FreeCAD.Vector("+xGeom+","+yGeom+",0)");
			output.println("m"+i+".translate(v"+i+")");
			output.println("s"+i+" = d.addObject(\"Part::Feature\",\"ms"+i+"\")");
			output.println("s"+i+".Shape = m"+i);
		}
		
		
		//Use a polygon to make the shape of the leaf
		output.println("lshape = Part.makePolygon([");
		for (Node node : leaf.nodes) {
			xGeom = Constants.geomScale * node.x;
			yGeom = Constants.geomScale * node.y;
			output.println("FreeCAD.Vector("+xGeom+","+yGeom+",0),");
		}
		xGeom = Constants.geomScale * nodes.get(0).x;
		yGeom = Constants.geomScale * nodes.get(0).y;
		output.println("FreeCAD.Vector("+xGeom+","+yGeom+",0),");
		output.println("])");		
		
		output.println("leafFace = Part.Face(lshape)");
		output.println("leaf = leafFace.extrude(FreeCAD.Vector(0,0,0.1))");
		output.println("shapeobj = d.addObject(\"Part::Feature\",\"MyShape\")");
		output.println("shapeobj.Shape = leaf");
		
		
		output.println("d.recompute()");
		output.flush();
		output.close();
		PApplet.println("Output finished");
		
	}
}
