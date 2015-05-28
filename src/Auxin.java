import processing.core.PApplet;

import java.util.ArrayList;

public class Auxin {
	
	
	PApplet parent;
	
	float size = 5;
	float dir = 1;
	
	float x;     // horizontal location of auxin
	float y;     // vertical location of auxin
	float xPos;
	float yPos;
	
	Auxin(PApplet p, LeafShape ls, ArrayList<Auxin> as, VeinGraph vg) {
		parent=p;
	
	    boolean outside = true;
	    boolean tooCloseAuxins = true;
	    boolean tooCloseVein = true;
	    
	    while(outside || tooCloseAuxins || tooCloseVein){
	    	outside = true;
	    	tooCloseAuxins = true;
	    	tooCloseVein = true;
	    	
	    	// Test new location
			x = parent.random(-1,1);
		    y = parent.random(0,2);
	
		    //Test to see if point is inside leaf: TODO http://en.wikipedia.org/wiki/Point_in_polygon
		    float x1 = x;
		    float y1 = y;
		    
		    //Gurantee that ray crosses at least one edge, use midpoint of first edge
		    //float x2 = (ls.edges[0].n0.x+ls.edges[0].n1.x)/2;
		    //float y2 = (ls.edges[0].n0.y+ls.edges[0].n1.y)/2;;
		    float x2 = 1;
		    float y2 = y1;
		    float x3;
		    float y3;
		    float x4;
		    float y4;
	
		    int crossings = 0;
			for (int i = 0; i < ls.edges.length; i++) {
				//Setup points for edge
				x3 = ls.edges[i].n0.x;
				y3 = ls.edges[i].n0.y;
				x4 = ls.edges[i].n1.x;
				y4 = ls.edges[i].n1.y;
				
				//Find intersection
				SimplePoint intersection = getIntersection(x1,y1,x2,y2,x3,y3,x4,y4);
				
				//Check to see if intersection falls on edge
				if(onEdge(intersection, ls.edges[i]) && onRay(intersection, x1, y1, x2, y2)) {crossings += 1;}	
			}
			if(crossings%2 == 1) {outside = false;}
			
			
			// Check to see if too close to other auxins
			float minDistance = 10;
		    for (Auxin auxin : as) {
			      float distance = (float) Math.sqrt(Math.pow(x-auxin.x,2)+Math.pow(y-auxin.y,2));
			      if (distance < minDistance) {minDistance = distance;}
			}
		    if (minDistance > Constants.birthDistanceA) {tooCloseAuxins = false;}

		    
		    // Check to see if too close to a vein node
			minDistance = 10;
		    for (Node node : vg.nodes) {
			      float distance = (float) Math.sqrt(Math.pow(x-node.x,2)+Math.pow(y-node.y,2));
			      if (distance < minDistance) {minDistance = distance;}
			}
		    if (minDistance > Constants.birthDistanceV) {tooCloseVein = false;}
	    
	    
	    }
		
	    
	  }
	
	public boolean killAuxin(VeinGraph vg) {
		boolean kill = false;
		for (Node node : vg.nodes) {
			float distance = (float) Math.sqrt(Math.pow(x-node.x,2)+Math.pow(y-node.y,2));
			if (distance < Constants.killDistance) {return true;}
		}
		return kill;
	}
	
	// Get intersection
	public SimplePoint getIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		//find intersection: TODO http://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
		float denom = ((x1-x2)*(y3-y4)) - ((y1-y2)*(x3-x4));
		float xInt = ((x1*y2 - y1*x2)*(x3-x4)) - ((x1-x2)*(x3*y4-y3*x4));
		xInt = xInt/denom;
		float yInt = ((x1*y2 - y1*x2)*(y3-y4)) - ((y1-y2)*(x3*y4-y3*x4));
		yInt = yInt/denom;
		
		SimplePoint intersection = new SimplePoint(xInt,yInt);
		return intersection;
	}
	
	//Check to see if intersection is on edge (line segment)
	boolean onEdge(SimplePoint intersection, Edge edge) {
		boolean onEdge = true;
		float xInt = intersection.x;
		float x0 = edge.n0.x;
		float x1 = edge.n1.x;

		float yInt = intersection.y;
		float y0 = edge.n0.y;
		float y1 = edge.n1.y;
		
		
		if(xInt < PApplet.min(x0,x1) || xInt > PApplet.max(x0,x1) || yInt < PApplet.min(y0,y1) || yInt > PApplet.max(y0,y1)){
			onEdge = false;
		}
		
		return onEdge;
	}
	
	
	//Check to see if intersection is on ray
	boolean onRay(SimplePoint intersection, float x1, float y1, float x2, float y2) {
		boolean onRay = true;
		
		if(x2 > x1 && intersection.x < x1) {onRay = false;}
		if(x2 < x1 && intersection.x > x1) {onRay = false;}
		if(y2 > y1 && intersection.y < y1) {onRay = false;}
		if(y2 < y1 && intersection.y > y1) {onRay = false;}
		
		return onRay;
	}
		
	// Draw auxin
	void display() {    
		parent.fill(50,50,255);	    
	    parent.noStroke();
	    
	    xPos = PApplet.map(x,-1,1,0,parent.width);
	    yPos = PApplet.map(y,0,2,parent.height,0);
	    
	    parent.ellipse(xPos,yPos,size,size);
	}

	
	void grow() {
		if (size > 17) {
			dir = 0;
		}
		if (size < 13) {
			dir = 1;	
		}
		if (dir == 1) {
			size += 0.05;
		} 
		if (dir == 0){
			size -= 0.05;
		}
	}

}
