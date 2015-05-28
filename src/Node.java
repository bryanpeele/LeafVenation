import processing.core.PApplet;
import java.util.ArrayList;

public class Node {
	PApplet parent;
	
	
	
	public float x;     // horizontal location of auxin
	public float y;     // vertical location of auxin
	public float xPos;
	public float yPos;
	
	public float x0;
	public float y0;
	
	public ArrayList<Auxin> localAuxins = new ArrayList<Auxin>();
	public ArrayList<Edge> connectedEdges = new ArrayList<Edge>();
	
	public boolean isTerminal;
	
	float size = 10;

	Node(PApplet p, float xA, float yA) {
		parent=p;
	    x = xA;
	    y = yA;
	    x0 = x;
	    y0 = y;
	    xPos = PApplet.map(x,-1,1,0,parent.width);
	    yPos = PApplet.map(y,0,2,parent.height,0);
	    isTerminal = true;
	  }
	
	// Draw node
	void display() {   
		updatePos();
		parent.fill(50,255,50);	    
	    parent.noStroke();    
	    parent.ellipse(xPos,yPos,size,size);
	}
	
	void updatePos() {
		xPos = PApplet.map(x,-1,1,0,parent.width);
	    yPos = PApplet.map(y,0,2,parent.height,0);
	}
	
}
