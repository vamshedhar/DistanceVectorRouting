import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class DistanceVectorRouting {
	
	public NetworkNode currentNode;
	
	public DistanceVectorRouting(NetworkNode currentNode) {
		
		this.currentNode = currentNode;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] NodeList = {"a", "b", "c", "d", "e", "f"};
		
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
		
	}

}
