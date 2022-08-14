package src;

import java.util.ArrayList;
import java.util.List;
 
public class KMeans {
 
	//Number of Clusters. This metric should be related to the number of points
    private int NUM_CLUSTERS = 3;
    //Number of Points
    private int NUM_POINTS = 15;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;
    
    private List<Point> points;
    private List<Cluster> clusters;
    
    public KMeans(int numPoints, int numCluster) {
        NUM_POINTS = numPoints;
        NUM_CLUSTERS = numCluster;
    	this.points = new ArrayList<>();
    	this.clusters = new ArrayList<>();    	
    }


//    public static void main(String[] args) {
//    	KMeans kmeans = new KMeans();
//    	kmeans.init();
//    	kmeans.calculate();
//
//        System.out.println("====================================================");
//        kmeans.plotClusters();
//    }

    
    //Initializes the process
//    @SuppressWarnings("unchecked")
	public void init(ArrayList<Point> points) {
    	//Create Points
//    	points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);

        this.points = points;
    	//Create Clusters
    	//Set Random Centroids
    	for (int i = 0; i < NUM_CLUSTERS ; i++) {
    		Cluster cluster = new Cluster(i);
    		Point centroid = Point.createRandomPoint();
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	//Print Initial state
    	plotClusters();
    }
 
	public void plotClusters() {
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
    
	//The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	List <Point> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            //Calculate new centroids.
        	calculateCentroids();
        	
        	iteration++;
        	
        	List <Point> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
        	System.out.println("#################");
        	System.out.println("Iteration: " + iteration);
        	System.out.println("Centroid distances: " + distance);
//        	plotClusters();
        	        	
        	if(distance == 0) {
        		finish = true;
        	}
        }
    }
    
    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    private List<Point> getCentroids() {
    	List<Point> centroids = new ArrayList<>(NUM_CLUSTERS);
    	for(Cluster cluster : clusters) {
    		Point aux = cluster.getCentroid();
    		Point point = new Point(aux.getHistogram());
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(Point point : points) {
        	min = max;
            for(int i = 0; i < NUM_CLUSTERS; i++) {
            	Cluster c = (Cluster) clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            ((Cluster) clusters.get(cluster)).addPoint(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point> points = cluster.getPoints();
            int n_points = points.size();

            double[] sum = new double[Point.HistogramSize];
            for(Point point : points) {
                for(int i=0; i<Point.HistogramSize; i++) {
                    sum[i] += point.getValue(i);
                }
            }
            
            Point centroid = cluster.getCentroid();
            if(n_points > 0) {
                for(int i=0; i<sum.length; i++) {
                    sum[i] /= n_points;
                }
//            	double newX = sumX / n_points;
//            	double newY = sumY / n_points;
//                centroid.setX(newX);
//                centroid.setY(newY);
                cluster.setCentroid(new Point(sum));
            }
        }
    }
}







