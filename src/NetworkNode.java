import java.util.ArrayList;
import java.util.HashMap;

public class NetworkNode {
	public String name;
	public ArrayList<String> neighbours;
	HashMap<String, DistanceVector> RoutingTable;
	
	
	public NetworkNode(String name, String[] nodes) {
		this.name = name;
		this.neighbours = new ArrayList<String>();
		this.RoutingTable = new HashMap<String, DistanceVector>();
		for(String nodeName : nodes){
			RoutingTable.put(nodeName, new DistanceVector());
		}
	}
	
	public void addNeighbourNode(String forNode, double distance, String nextHop){
		this.neighbours.add(forNode);
		this.updateRoutingTableEntry(forNode, distance, nextHop);
	}
	
	public void updateRoutingTableEntry(String forNode, double distance, String nextHop){
		DistanceVector vector = this.RoutingTable.get(forNode);
		
		vector.distance = distance;
		vector.next = nextHop;
	}

	@Override
	public String toString() {
		
		String output = "";
		for(String node : RoutingTable.keySet()){
			if(node.equals(this.name)){
				continue;
			}
			
			DistanceVector DV = this.RoutingTable.get(node);
			output += "shortest path " + this.name + "-" + node + ": the next hop is " + DV.next + " and the cost is " + DV.distance + "\n";
		}
		
		return output;
	}
	
	
}
