import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DistanceVectorRouting {

	public HashMap<String, NetworkNode> nodes;
	
	public DistanceVectorRouting() {
		this.nodes = new HashMap<String, NetworkNode>();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] NodeList = {"a", "b", "c", "d", "e", "f"};
		
		DistanceVectorRouting DVR = new DistanceVectorRouting();
		
		for(String nodeName : NodeList){
			DVR.nodes.put(nodeName, new NetworkNode(nodeName, NodeList));
		}
		
		try{
			for(String nodeName : NodeList){
				BufferedReader reader = new BufferedReader(new FileReader(nodeName + ".txt"));
				
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
					
					NetworkNode node = DVR.nodes.get(nodeName);
					
					node.addNeighbourNode(neighbourNodeName, weight, neighbourNodeName);
					
				}
				
				System.out.println(DVR.nodes.get(nodeName));
				reader.close();
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
		
	}

}
