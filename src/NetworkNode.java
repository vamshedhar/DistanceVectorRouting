import java.util.HashMap;

public class NetworkNode {
	public String name;
	public HashMap<String, Double> neighbours;
	HashMap<String, DistanceVector> RoutingTable;
	Boolean routingTableChanged;
	
	
	public NetworkNode(String name) {
		this.name = name;
		this.routingTableChanged = true;
		this.neighbours = new HashMap<String, Double>();
		this.RoutingTable = new HashMap<String, DistanceVector>();
	}
	
	public NetworkNode(byte[] receiveData){

		String receivedData = new String(receiveData);

		String[] data = receivedData.split(",");

		this.routingTableChanged = true;
		
		this.name = data[0].trim();
		
		this.RoutingTable = new HashMap<String, DistanceVector>();
		
		DistanceVector vector = new DistanceVector();
		vector.distance = 0.0;
		this.RoutingTable.put(this.name, vector);
		
		for(int i = 1; i < data.length; i++){
			String[] tableEntry = data[i].split(":");
			Double distance = Double.parseDouble(tableEntry[1].trim());
			DistanceVector DV = new DistanceVector();
			DV.distance = distance;
			
			this.RoutingTable.put(tableEntry[0].trim(), DV);
		}
	}
	
	public void reset(){
		this.routingTableChanged = false;
	}
	
	public void addNeighbourNode(String forNode, double distance, String nextHop, Boolean create){
		this.routingTableChanged = true;

		double previousDistance = Double.POSITIVE_INFINITY;

		if(this.neighbours.get(forNode) != null){
			previousDistance = this.neighbours.get(forNode);
		}

		this.neighbours.put(forNode, distance);

		if(create || previousDistance != distance){
			this.updateRoutingTableEntry(forNode, distance, nextHop);
		}
	}
	
	public void updateRoutingTableEntry(String forNode, double distance, String nextHop){
		this.routingTableChanged = true;
		
		DistanceVector vector = this.RoutingTable.get(forNode);

		if(vector != null){
			vector.distance = distance;
			vector.next = nextHop;
		} else{
			this.RoutingTable.put(forNode, new DistanceVector(distance, nextHop));
		}
		
	}
	
	public String getRoutingTableString(String avoid){
		
		String data = "";
		
		for(String node : this.RoutingTable.keySet()){
			
			if(node.equals(this.name)){
				continue;
			}
			
			DistanceVector DV = this.RoutingTable.get(node);
			if(!DV.next.equals(avoid)){
				data += node + ":" + DV.distance + ",";
			}
		}

		if(data.equals("")){
			return this.name;
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
			// output += "shortest path " + this.name + "-" + node + ": the next hop is " + DV.next + " and the cost is " + DV.distance + "\n";
			output += this.name + "-" + node + ": " + DV.next + " and " + DV.distance + "\n";

		}
		
		return output;
	}
	
	
}
