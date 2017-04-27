import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DistanceVectorRouting {
	
	public NetworkNode currentNode;
	public int iteration;
	public static long TIMEOUT = 10000;
	
	public DistanceVectorRouting(NetworkNode currentNode) {
		
		this.currentNode = currentNode;
		this.iteration = 1;
	}
	
	public static void main(String[] args) throws Exception {
		String filename = args[0];
		
		String currentNodeName = filename.split(".txt")[0];
		
		NetworkNode currentNode = new NetworkNode(currentNodeName);
		
		DistanceVectorRouting DVR = new DistanceVectorRouting(currentNode);

		long startTime = System.currentTimeMillis();
		
		// Create a Socket
		try {
			int currentPort = 8000 + (int) currentNodeName.charAt(0);
			DatagramSocket clientSocket = new DatagramSocket(currentPort);
			
			InetAddress IPAddress = InetAddress.getByName("localhost");
			
			while(true){

				if(DVR.currentNode.routingTableChanged){

					try{
						BufferedReader reader = new BufferedReader(new FileReader(filename));
						
						String line = reader.readLine();

						HashMap<String, Double> neighbours = new HashMap<String, Double>();

						while((line = reader.readLine()) != null) {
							
							StringTokenizer st = new StringTokenizer(line);
							
							if(st.countTokens() != 2){
								System.err.println("Invalid data!");
								reader.close();
								return;
							}
							
							String neighbourNodeName = st.nextToken().trim();
							Double weight = Double.parseDouble(st.nextToken().trim());

							neighbours.put(neighbourNodeName, weight);
						}
						reader.close();
						
						Set<String> newNeighbours = new HashSet<String>();
						newNeighbours.addAll(neighbours.keySet());
						newNeighbours.addAll(DVR.currentNode.neighbours.keySet());

						for(String neighbourNodeName : newNeighbours){
							Double weight = neighbours.get(neighbourNodeName);
							
							if(weight == null){
								weight = Double.POSITIVE_INFINITY;
							}
							
							DVR.currentNode.addNeighbourNode(neighbourNodeName, weight, neighbourNodeName, DVR.iteration == 1);
						}
						
					} catch(FileNotFoundException e){
						System.err.println("File Not Found!");
						return;
					} catch(NumberFormatException e){
						System.err.println("Invalid Data!");
						return;
					} catch(IOException e){
						System.err.println("Invalid Data!");
						return;
					}

					System.out.println("Sending data to neighbours: Iteration " + DVR.iteration++);

					System.out.println(DVR.currentNode);

					System.out.println();
				
					for(String neighbour : DVR.currentNode.neighbours.keySet()){

						if(DVR.currentNode.neighbours.get(neighbour) == Double.POSITIVE_INFINITY){
							continue;
						}

						String routingTable = currentNode.getRoutingTableString(neighbour);
					
						byte[] data = routingTable.getBytes();

						int receiverPort = 8000 + (int) neighbour.charAt(0);
						
						DatagramPacket dataPacket = new DatagramPacket(data, data.length, IPAddress, receiverPort);
						clientSocket.send(dataPacket);
					}
					
					DVR.currentNode.reset();
				}

				try{
					long TIMER = TIMEOUT - (System.currentTimeMillis() - startTime);

					if (TIMER < 0) {
						throw new SocketTimeoutException();
					}

					byte[] receivedPacket = new byte[1024];
					DatagramPacket receiveDatagramPacket = new DatagramPacket(receivedPacket, receivedPacket.length);

					clientSocket.setSoTimeout((int) TIMER);
					
					clientSocket.receive(receiveDatagramPacket);
					byte[] receiveData = receiveDatagramPacket.getData();
					
					NetworkNode receivedNode = new NetworkNode(receiveData);
					
					for(String node : receivedNode.RoutingTable.keySet()){

						DistanceVector tableEntry = DVR.currentNode.RoutingTable.get(node);

						Double actualDistance = Double.POSITIVE_INFINITY;
						String nextHop = null;

						if(tableEntry != null){
							actualDistance = tableEntry.distance;
							nextHop = tableEntry.next;
						}
						
						Double calculatedDistance = receivedNode.RoutingTable.get(node).distance + DVR.currentNode.neighbours.get(receivedNode.name);

						if(actualDistance > calculatedDistance){
							DVR.currentNode.updateRoutingTableEntry(node, calculatedDistance, receivedNode.name);
						} else if(nextHop != null && nextHop.equals(receivedNode.name) && !actualDistance.equals(calculatedDistance)){
							DVR.currentNode.updateRoutingTableEntry(node, calculatedDistance, receivedNode.name);
						}
					}
				} catch(SocketTimeoutException e){
					DVR.currentNode.routingTableChanged = true;
					startTime = System.currentTimeMillis();
				}
				
				
			}
			
			
			
		} catch (SocketException e) {
			e.printStackTrace();
		}  catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}

}
