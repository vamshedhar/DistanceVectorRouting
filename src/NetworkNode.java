import java.util.HashMap;

public class NetworkNode {
	public String name;
	public HashMap<String, Double> neighbours;
	HashMap<String, DistanceVector> RoutingTable;
	Boolean routingTableChanged;
	
	
	public NetworkNode(String name, String[] nodes) {
		this.name = name;
		this.routingTableChanged = false;
		this.neighbours = new HashMap<String, Double>();
		this.RoutingTable = new HashMap<String, DistanceVector>();
		for(String nodeName : nodes){
			DistanceVector DV = new DistanceVector();
			
			if(nodeName.equals(this.name)){
				DV.distance = 0;
			}
			
			RoutingTable.put(nodeName, DV);
		}
	}
	
	public NetworkNode(String receivedData){
		String[] data = receivedData.split(",");
		
		this.name = data[0];
		
		this.RoutingTable = new HashMap<String, DistanceVector>();
		
		DistanceVector vector = new DistanceVector();
		vector.distance = 0.0;
		this.RoutingTable.put(name, vector);
		
		for(int i = 1; i < data.length; i++){
			String[] tableEntry = data[i].split(":");
			Double distance = Double.parseDouble(tableEntry[1]);
			DistanceVector DV = new DistanceVector();
			DV.distance = distance;
			
			this.RoutingTable.put(tableEntry[0], DV);
		}
	}
	
	public void reset(){
		this.routingTableChanged = false;
	}
	
	public void addNeighbourNode(String forNode, double distance, String nextHop){
		this.routingTableChanged = true;
		this.neighbours.put(forNode, distance);
		this.updateRoutingTableEntry(forNode, distance, nextHop);
	}
	
	public void updateRoutingTableEntry(String forNode, double distance, String nextHop){
		this.routingTableChanged = true;
		
		DistanceVector vector = this.RoutingTable.get(forNode);
		
		vector.distance = distance;
		vector.next = nextHop;
	}
	
	public String getRoutingTableString(){
		
		String data = "";
		
		for(String node : this.RoutingTable.keySet()){
			
			if(node.equals(this.name)){
				continue;
			}
			
			DistanceVector DV = this.RoutingTable.get(node);
			data += node + ":" + DV.distance + ",";
		}
		
		return this.name + "," + data.substring(0, data.length() - 1);
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
