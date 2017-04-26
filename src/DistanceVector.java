
public class DistanceVector {
	public double distance;
	public String next;
	
	public DistanceVector() {
		this.distance = Double.POSITIVE_INFINITY;
		this.next = null;
	}
	
	public DistanceVector(double distance, String next) {
		this.distance = distance;
		this.next = next;
	}
}
