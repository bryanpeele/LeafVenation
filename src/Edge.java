import processing.core.PApplet;

public class Edge {
	PApplet parent;
	
	public Node n0;
	public Node n1;
	public float size;
	
	
	Edge(PApplet p, Node node0, Node node1) {
		parent=p;
	    n0 = node0;
	    n1 = node1;
	    size = 1;
	 }
	
	void display() {
		parent.strokeWeight(size);
		parent.line(n0.xPos, n0.yPos, n1.xPos, n1.yPos);
	}
	
}
