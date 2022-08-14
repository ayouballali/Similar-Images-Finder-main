package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class ImageClassifier {
//	private ArrayList<bufferedImages> images;
//	private final File folder = new File("images");
//	private String path;

	public static ArrayList<BufferedImage> images;
	private ArrayList<String> imageNames;
	private final int histSize = 5;
	private final int linHistSize = histSize * histSize * histSize;
	private final int interval = (int)Math.ceil((double)256/5);

	ImageClassifier() {
		images = new ArrayList<>();
		imageNames = new ArrayList<String>();
		try {
			File folder = new File("coil-100");
			int count = 0;
			for(File file: Objects.requireNonNull(folder.listFiles())) {
//				if(++count > 100)
//					break;
				BufferedImage img = ImageIO.read(file);
				images.add(img);
				imageNames.add(file.getName());
			}
			for(int i=0; i<images.size(); i++) {
				images.set(i, resize(cropTheImage(i)));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void classify() {
		// image -> Histogram -> point
		ArrayList<Point> points = new ArrayList<>();
		int numImages = images.size();

		for(int i=0; i<numImages; i++) {
			points.add(new Point(LinearHist(i, true)));
//			System.out.println("image " + points.get(i).getId());
		}

		KMeans kmeans = new KMeans(numImages, 10);
		kmeans.init(points);
		kmeans.calculate();
		kmeans.plotClusters();
	}

	public double[] getDistances(BufferedImage img) {
		double[] imgHist = LinearHist(img, true);
		double[] res = new double[images.size()];
		for(int i=0; i<images.size(); i++) {
			res[i] = (normDist(imgHist, LinearHist(i, true)));
		}
		return res;
	}

	public ArrayList<String> findSimilar(BufferedImage img) {
		double[] distances = getDistances(img);
		HashMap<Integer, Double> mp = new HashMap<>();
		double mean = 0;
		for(int i=0; i<distances.length; i++) {
			mp.put(i, distances[i]);
			mean += distances[i];
		}
		mean /= distances.length;

		SortedSet<Map.Entry<Integer, Double>> s = new TreeSet<>(new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				double val1 = o1.getValue();
				double val2 = o2.getValue();
				if (val1 < val2)
					return 1;
				else if (val2 == val1)
					return 0;
				return -1;
			}
		});
		s.addAll(mp.entrySet());


		System.out.println("Mean(distances) = " + mean);
		System.out.println("\n\tAN IMAGE IS SELECTED IF THE DISTANCE IS LOWER THAN 0.5\n");

		int count = 0;
		ArrayList<String> similar = new ArrayList<>();
		for(Map.Entry<Integer, Double> en : s) {
			//if(en.getValue() > 0.5)
			//	break;
			System.out.println("ImageName = " + imageNames.get(en.getKey()) + ", Dist = " + en.getValue());
			similar.add(imageNames.get(en.getKey()));
			count++;
			if(count >= 30)
				break;
		}

		return similar;
	}


	public double[][][] Histogram(BufferedImage img) {
		double[][][] H = new double[histSize][histSize][histSize];
		int width = img.getWidth();
		int height = img.getHeight();

		double alpha;
		int r, g, b;
		Color c;
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				int rgb = img.getRGB(x, y);
				c = new Color(rgb);
//				if(img.getColorModel().getAlpha(y * width + x) == 0)
				if(isTransparent(rgb)) {
//					System.out.println("transparent");
					continue;
				}
				r = c.getRed();
				g = c.getGreen();
				b = c.getBlue();

				H[(int)(r/interval)][(int)(g/interval)][(int)(b/interval)]++;
			}
		}

		return H;
	}

	// get Histogram using index of images in folder
	public double[][][] Histogram(int index) {
		BufferedImage img = images.get(index);
		return Histogram(img);
	}
	public double[] LinearHist(BufferedImage img, boolean withBg) {
		double[][][] H;
		if(withBg)
			H = HistogramBg(img);
		else
			H = Histogram(img);
		int size = H.length * H[0].length * H[0][0].length;
		double[] linH = new double[size];
		int i = 0;
		for(int r=0; r<histSize; r++) {
			for(int g=0; g<histSize; g++) {
				for(int b=0; b<histSize; b++) {
					linH[i++] = H[r][g][b];
				}
			}
		}
		return linH;
	}

	public double[] LinearHist(int index, boolean withBg) {
		BufferedImage img = images.get(index);
		return LinearHist(img, withBg);
	}
	//--------- Histogram of an image width a background color (not transparent Image)

	public double[][][] HistogramBg(int index) {
		return HistogramBg(images.get(index));
	}
	// Histogram for images with background
	public double[][][] HistogramBg(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		Color bgColor = new Color(img.getRGB(0, 0));

		Color c;
		boolean inObject = false;
		int r, g, b, left, right, rgb;


		double[][][] H = new double[histSize][histSize][histSize];
		for (int y=0; y<height; y++) {
			// for every row of pixels look up the boundaries of the object using 2 pointers
			left = 0;
			right = width - 1;

			while(left < width) {
				rgb = img.getRGB(left, y);
				c = new Color(rgb);
				if(!c.equals(bgColor))
					break;
				left++;
			}

			while(right >= 0) {
				rgb = img.getRGB(right, y);
				c = new Color(rgb);
				if(!c.equals(bgColor))
					break;
				right--;
			}

			for(int x=left; x<=right; x++) {
				rgb = img.getRGB(x, y);
				c = new Color(rgb);

				if(isTransparent(rgb))
					continue;
				r = c.getRed();
				g = c.getGreen();
				b = c.getBlue();

				H[(int)(r/interval)][(int)(g/interval)][(int)(b/interval)]++;
			}
		}

		return H;
	}



	public double dist(double[] H1, double[] H2) {
        double dist = 0;
        for (int i = 0; i<H1.length; i++) {
            dist += ((H1[i] - H2[i])*(H1[i] - H2[i]));
//			System.out.println("dist++  = " + dist);
        }
		System.out.println("dist2 = " + dist);
        dist = Math.sqrt(dist);
        return dist;
    }




	// dot product
	public double dot(double[] H1, double[] H2) {
		double dotPro = 0;
		// normalization
		double H1_length, H2_length;
//		H1_length = dist(H1, new double[linHistSize]);
//		H2_length = dist(H2, new double[linHistSize]);
//
		H1_length = H2_length = 1;
        for (int i = 0; i<H1.length; i++) {
            dotPro += (H1[i]/H1_length) * (H2[i]/H2_length);
//			System.out.println("dist++  = " + dist);
        }
		return dotPro;
	}

	public double normDist(double[] H1, double[] H2) {
		return dot(H1, H2) / (dist(H1, new double[linHistSize]) * dist(H2, new double[linHistSize]));
	}

	public void printHist(int[][][] H) {
		for(int r=0; r<histSize; r++) {
			for(int g=0; g<histSize; g++) {
				for(int b=0; b<histSize; b++) {
					System.out.println("(" +r+ ", " +g+ ", " +b+ ")" + H[r][g][b]);
				}
			}
		}
	}

	public void printLinHist(int[] H) {
		for(int val : H) {
			System.out.print(val + " ");
		}
	}

	public void show(int index) {
		show(images.get(index));
	}

	public void show(BufferedImage img)  {
		try {
			ImageIcon imageIcon = new ImageIcon(img);
			JFrame jFrame = new JFrame();

			jFrame.setLayout(new FlowLayout());

			jFrame.setSize(500, 500);
			JLabel jLabel = new JLabel();

			jLabel.setIcon(imageIcon);
			jFrame.add(jLabel);
			jFrame.setVisible(true);

			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

    public static BufferedImage cropTheImage(int index) {
//		int xmin, ymin, xmax, xmin;
//		xmin = ymin = ymax = ymin = 0;
		BufferedImage img = images.get(index);

		int width = img.getWidth();
		int height = img.getHeight();

    	boolean upFound = false;
    	BufferedImage dest = null;
    	int p1=0,p2=0,p3=0,p4=0;
		p2 = Integer.MAX_VALUE;
    	Color c;
    	for (int i=0;i<height;i++) {
    		for(int j=0;j<width;j++) {
				int rgb = img.getRGB(j, i);
				c = new Color(rgb);

				if(isTransparent(rgb)) {
					continue;
				}

				if(!upFound) {
					p1 = i;
					upFound = true;
				}

				p2 = Math.min(p2, j);
				p3 = Math.max(p3, j);
				p4 = Math.max(p4, i);
    		}
    	}

		dest = img.getSubimage(p2, p1, p3-p2, p4-p1);

		// draw border
//		width = dest.getWidth();
//		height = dest.getHeight();
//		for(int x=0; x<width; x++) {
//			System.out.println(x);
//			dest.setRGB(x, 0, Color.green.getRGB());
//			dest.setRGB(x, height-1, Color.green.getRGB());
//		}
//		for(int y=0; y<height; y++) {
//			System.out.println(y);
//			dest.setRGB(0, y, Color.green.getRGB());
//			dest.setRGB(width-1, y, Color.green.getRGB());
//		}
//		System.out.println("p1 = " + p1 + ", p2 = " + p2 + ", p3 = " + p3 + ", p4 = " + p4);
//		System.out.println("width = " + (p3 - p2) + ", height = " + (p4 - p1));

		return dest;
    }

    public static BufferedImage resize(BufferedImage img) {
        // creates output image
		int scaledWidth = 128;
		int scaledHeight = 128;

        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
//		g2d.setComposite(AlphaComposite.Src);
        g2d.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
    	return outputImage;
    }


	public static boolean isTransparent(int pixel) {
	  return (pixel>>24) == 0x00;
	}

	public static boolean isTransparent(BufferedImage img, int x, int y) {
	  int pixel = img.getRGB(x,y);
	  return (pixel>>24) == 0x00;
	}

}


