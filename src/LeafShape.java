import processing.core.PApplet;

import processing.core.PConstants;

public class LeafShape {
	PApplet parent;
	
	public Node[] nodes;
	public Edge[] edges;
	
	float sigma = 1;
	
	LeafShape(PApplet p, Node[] n, Edge[] e) {
		parent=p;
		nodes = n;
		edges = e;
	    
	 }
	
	
	void display() {
		//PShape s;
		//s = parent.createShape();
		parent.beginShape();
		
		parent.stroke(150,150,150);
		//parent.fill(100);
		parent.fill(220);
		parent.strokeWeight(2);
		for (int i = 0; i < nodes.length; i++) {
			parent.vertex(nodes[i].xPos, nodes[i].yPos);
		}
		parent.endShape(PConstants.CLOSE);
		//parent.shape(s);
		
	}
	
	void grow() {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].x = nodes[i].x0 * sigma;
			nodes[i].y = nodes[i].y0 * sigma;
			nodes[i].updatePos();
			sigma += Constants.LeafGrowthRate;
		}
	}
	
}
