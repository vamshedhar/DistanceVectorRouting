import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class DistanceVectorRouting {
	
	public NetworkNode currentNode;
	
	public DistanceVectorRouting(NetworkNode currentNode) {
		
		this.currentNode = currentNode;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] NodeList = {"a", "b", "c", "d", "e", "f"};

		// String[] NodeList = {"x", "y", "z"};
		
		String filename = args[0];
		
		String currentNodeName = filename.split(".txt")[0];
		
		NetworkNode currentNode = new NetworkNode(currentNodeName, NodeList);
		
		DistanceVectorRouting DVR = new DistanceVectorRouting(currentNode);
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String line = reader.readLine();
			while((line = reader.readLine()) != null) {
				
				StringTokenizer st = new StringTokenizer(line);
				
				if(st.countTokens() != 2){
					System.err.println("Invalid data!");
					reader.close();
					return;
				}
				
				String neighbourNodeName = st.nextToken();
				Double weight = Double.parseDouble(st.nextToken());
				
				currentNode.addNeighbourNode(neighbourNodeName, weight, neighbourNodeName);
				
			}
			
			System.out.println(DVR.currentNode);
			reader.close();
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
		
		// Create a Socket
		try {
			int currentPort = 8000 + (int) currentNodeName.charAt(0);
			DatagramSocket clientSocket = new DatagramSocket(currentPort);
			
			InetAddress IPAddress = InetAddress.getByName("localhost");
			
			while(true){
				if(DVR.currentNode.routingTableChanged){

					System.out.println(DVR.currentNode);

					System.out.println();
					
					String routingTable = currentNode.getRoutingTableString();
					
					byte[] data = routingTable.getBytes();
				
					for(String neighbour : DVR.currentNode.neighbours.keySet()){
						int receiverPort = 8000 + (int) neighbour.charAt(0);
						
						System.out.println("Sending Data to " + neighbour);
						System.out.println(routingTable);
						
						DatagramPacket dataPacket = new DatagramPacket(data, data.length, IPAddress, receiverPort);
						clientSocket.send(dataPacket);
					}
					
					DVR.currentNode.reset();
				}
				
				byte[] receivedPacket = new byte[1024];
				DatagramPacket receiveDatagramPacket = new DatagramPacket(receivedPacket, receivedPacket.length);
				
				clientSocket.receive(receiveDatagramPacket);
				byte[] receiveData = receiveDatagramPacket.getData();
				
				String receivedRoutingTable = new String(receiveData);
				
				NetworkNode receivedNode = new NetworkNode(receivedRoutingTable);
				
				System.out.println(receivedNode);
				
				for(String node : receivedNode.RoutingTable.keySet()){
					Double actualDistance = DVR.currentNode.RoutingTable.get(node).distance;
					Double calculatedDistance = receivedNode.RoutingTable.get(node).distance + DVR.currentNode.neighbours.get(receivedNode.name);
					if(actualDistance > calculatedDistance){
						DVR.currentNode.updateRoutingTableEntry(node, calculatedDistance, receivedNode.name);
					}
				}
			}
			
			
			
		} catch (SocketException e) {
			e.printStackTrace();
		}  catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}

}
