import processing.core.*;
import java.util.ArrayList;


public class LeafVenationBuilder extends PApplet{


	  //	An array of stripes
	  ArrayList<Auxin> auxins = new ArrayList<Auxin>();
	  Node[] leafNodes = new Node[18];
	  Edge[] leafEdges = new Edge[18];
	  LeafShape shape;
	  
	  
	  ArrayList<Node> veinNodes = new ArrayList<Node>();
	  ArrayList<Edge> veinEdges = new ArrayList<Edge>();
	  VeinGraph vein;
	  
	  int frame = 1;
	  
	  public static void main(String args[]) {
	    PApplet.main(new String[] { "--present", "LeafVenationBuilder" });
	  }	
	  
	  public void setup() {
	    size(850,850);
	    frameRate(Constants.FPS);

	    //Simple Leaf (hexagon)
	    leafNodes[0] = new Node(this,(float)	0.000000,	(float) 0.000000);
	    leafNodes[1] = new Node(this,(float) 	0.017358,	(float)	0.002264);
	    leafNodes[2] = new Node(this,(float)	0.030566,	(float)	0.009057);
	    leafNodes[3] = new Node(this,(float)	0.041132,	(float)	0.019623);
	    leafNodes[4] = new Node(this,(float)	0.044528,	(float) 0.029434);
	    leafNodes[5] = new Node(this,(float)	0.043774,	(float)	0.045283);
	    leafNodes[6] = new Node(this,(float)	0.036604,	(float)	0.066038);
	    leafNodes[7] = new Node(this,(float)	0.024906,	(float) 0.079623);
	    leafNodes[8] = new Node(this,(float)	0.012453,	(float)	0.090189);
	    leafNodes[9] = new Node(this,(float)	0.000000,	(float)	0.100000);
	    
	    leafNodes[17] = new Node(this,(float) 	-0.017358,	(float)	0.002264);
	    leafNodes[16] = new Node(this,(float)	-0.030566,	(float)	0.009057);
	    leafNodes[15] = new Node(this,(float)	-0.041132,	(float)	0.019623);
	    leafNodes[14] = new Node(this,(float)	-0.044528,	(float) 0.029434);
	    leafNodes[13] = new Node(this,(float)	-0.043774,	(float)	0.045283);
	    leafNodes[12] = new Node(this,(float)	-0.036604,	(float)	0.066038);
	    leafNodes[11] = new Node(this,(float)	-0.024906,	(float) 0.079623);
	    leafNodes[10] = new Node(this,(float)	-0.012453,	(float)	0.090189);
	    
	    
	    leafEdges[0] = new Edge(this,leafNodes[0],leafNodes[1]);
	    leafEdges[1] = new Edge(this,leafNodes[1],leafNodes[2]);
	    leafEdges[2] = new Edge(this,leafNodes[2],leafNodes[3]);
	    leafEdges[3] = new Edge(this,leafNodes[3],leafNodes[4]);
	    leafEdges[4] = new Edge(this,leafNodes[4],leafNodes[5]);
	    leafEdges[5] = new Edge(this,leafNodes[5],leafNodes[6]);
	    leafEdges[6] = new Edge(this,leafNodes[6],leafNodes[7]);
	    leafEdges[7] = new Edge(this,leafNodes[7],leafNodes[8]);
	    leafEdges[8] = new Edge(this,leafNodes[8],leafNodes[9]);
	    leafEdges[9] = new Edge(this,leafNodes[9],leafNodes[10]);
	    leafEdges[10] = new Edge(this,leafNodes[10],leafNodes[11]);
	    leafEdges[11] = new Edge(this,leafNodes[11],leafNodes[12]);
	    leafEdges[12] = new Edge(this,leafNodes[12],leafNodes[13]);
	    leafEdges[13] = new Edge(this,leafNodes[13],leafNodes[14]);
	    leafEdges[14] = new Edge(this,leafNodes[14],leafNodes[15]);
	    leafEdges[15] = new Edge(this,leafNodes[15],leafNodes[16]);
	    leafEdges[16] = new Edge(this,leafNodes[16],leafNodes[17]);
	    leafEdges[17] = new Edge(this,leafNodes[17],leafNodes[0]);
	    shape = new LeafShape(this,leafNodes,leafEdges);
	    
	    

	    
	    // Add initial vein node at origin
	    veinNodes.add(new Node(this, 0, 0));
	    veinNodes.get(0).isTerminal=false;
	    veinNodes.add(new Node(this, 0, (float) 0.01));
	    veinNodes.get(1).isTerminal=true;
	    veinEdges.add(new Edge(this, veinNodes.get(0), veinNodes.get(1)));
	    veinNodes.get(0).connectedEdges.add(veinEdges.get(0));
	    veinNodes.get(1).connectedEdges.add(veinEdges.get(0));
	    vein = new VeinGraph(this, veinNodes, veinEdges, shape);
	    
	    // Initialize auxin(s)
	    for (int i = 0; i < 1; i++) { //TODO clean up number of initial auxins
	      auxins.add(new Auxin(this, shape, auxins, vein));
	    }
	    
	  }

	  public void draw() {
		smooth(4);
	    background(30);
	    
	    textSize(26);
	    fill(250);
	    text("Frame: "+frame, 10, 120); 
	    text("Leaf Growth Rate: "+Constants.LeafGrowthRate,10, 40);
	    text("Vein Growth Rate: "+Constants.VeinGrowthLength,10, 80);
	   
	    if (frame < Constants.numberLeafFrames) { 
	    	shape.grow();
	    	for(int i = 0; i < Constants.auxinsPerIteration; i++) {
		    	Auxin newestAuxin = new Auxin(this, shape, auxins, vein);
		    	auxins.add(newestAuxin);

		    }
	
	    
	    }
	    
	    
	    shape.display();
	    	    
	    //for each vein node, remove list of influential auxins
	    vein.clearAuxins();
	    vein.assignAuxins(auxins);
	    vein.grow();
	    
	    
	    
	    //kill auxins that are too close to a vein node
	    
	    ArrayList<Auxin> auxinsToKill = new ArrayList<Auxin>();
	    for (Auxin auxin : auxins) {
	    	if(auxin.killAuxin(vein)) {
	    		auxinsToKill.add(auxin);
	    	}
	    }
	    for (Auxin auxin : auxinsToKill) {
	    	auxins.remove(auxin);
	    }
	    

	    // display remaining auxins
	    for (Auxin auxin : auxins) {
  	      auxin.display();
	    }

	    vein.assignEdgeWeights();
	    vein.display();
	    
	    
	    frame += 1;
	    
	    if (frame > Constants.numberFrames) {noLoop();}
	    
	    saveFrame("veinGrowth-######.png");
	    
	  }
	  
	  
	  public void keyPressed() {
		  if (key == 'E' || key == 'e') {
			  //vein.exportGeometry("test.scad");
			  vein.exportGeometryFreeCAD("test");
		  }
	  }
}
