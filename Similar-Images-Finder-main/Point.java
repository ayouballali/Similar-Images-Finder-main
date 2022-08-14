package src;

import java.util.*;

public class Point {

//    private double x = 0;
//    private double y = 0;
	private double[] Histogram;
    public static final int HistogramSize = 5*5*5;
    private int cluster_number = 0;
    private int id;
    private static int count = 0;

    public Point(double[] Histogram)
    {
        this.id = count++;
        this.Histogram = Histogram;
    }

    public int getId() {
        return id;
    }

    //    public void setX(double x) {
//        this.x = x;
//    }
//    
//    public double getX()  {
//        return this.x;
//    }
    
//    public void setY(double y) {
//        this.y = y;
//    }

    public double[] getHistogram() {
        return this.Histogram;
    }

    public double getValue(int index) {
        return this.Histogram[index];
    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }
    
    public int getCluster() {
        return this.cluster_number;
    }
    
    //Calculates the distance between two points.
    protected static double distance(Point p, Point centroid) {
    	double dist = 0;
    	for (int i=0; i<p.Histogram.length; i++) {
    		dist += (p.Histogram[i] - centroid.Histogram[i]) * ( p.Histogram[i] - centroid.Histogram[i]);
    	}
        dist = Math.sqrt(dist);
        //---------------------------------------------------------
        double dist_ = EarthMoversDistance.compute(p.getHistogram(), centroid.getHistogram());
        System.out.println("(" + p.getId() + ", " + centroid.getId() + ") = " + dist + " , dist_ = " + dist_);
        return dist_;
    }
    
    //Creates random point
    @SuppressWarnings("null")
	protected static Point createRandomPoint() {
    	Random r = new Random();
        double[] H = new double[5*5*5];

    	double total = 128*128;
        int each = (int)total / HistogramSize;
        for(int i=0; i<5*5*5 && total>0 ;i++) {
//    		H[i] = r.nextInt(0,(int)total);
//    		total = total - H[i];
            H[i] = r.nextInt(0, each);
    	}
//    	double x = min + (max - min) * r.nextDouble();
//    	double y = min + (max - min) * r.nextDouble();
    	return new Point(H);
    }
    
    protected static List<Point> createRandomPoints(int min, int max, int number) {
    	List<Point> points = new ArrayList<>(number);
    	for(int i = 0; i < number; i++) {
            points.add(createRandomPoint());
        }
    	return points;
    }

    @Override
    public String toString() {
        return "Point{" +
                "Histogram=" + Arrays.toString(Histogram) +
                ", cluster_number=" + cluster_number +
                '}';
    }
}
