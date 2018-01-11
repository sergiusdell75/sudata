/*
 * Copyright Sergius Dell DevLab
 */
package SOM;

/**
 *
 * @author emil
 */
import java.io.*;
/**
 * Performs principal component analysis on a set of data and returns the resulting data set. The
 * QR algorithm is used to find the eigenvalues and orthonormal eigenvectors of the covariance
 * matrix of the data set. The eigenvectors corresponding to the largest eigenvalues are the
 * principal components. The data file should be in the same directory as the PCA.class file.
 * All numbers should be tab-delimited. The first line of the data should be two numbers: the 
 * number of rows R followed by the number of columns C. After that, there should be R lines of 
 * C tab-delimited values. The columns would most likely represent the dimensions of measure; the
 * rows would each represent a single data point.
 * @credits	Kushal Ranjan
 */
public class PCALight {

    private double[][] data;
    private double [][] scores;
    private int numComps;
    private final boolean debug;
    private final boolean verbose;
    int n1,n2,n3;
    
    public PCALight(boolean debug, boolean verbose) {
        this.numComps = 1;
        this.data = null;
        this.scores=null;
        this.debug=debug;
        this.verbose=verbose;
    }
    
    /**
     *
     * @param n1 fast dimension of data
     * @param n2 slow dimension of data
     */
    public void setNs(int n1, int n2) {
        this.n1=n1;
        this.n2=n2;
    }
    public void setData(double [][] data){
        int length = data.length;
        this.data = new double[length][data[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, data[i].length);
        }
    }
    public void setNumComps(int numComps){
        this.numComps=numComps;
    }

    /**
     *
     * @return
     */
    public double [][] getScores(){return this.scores;};
    
    public void process() {
        scores = PCAdata.PCANIPALS(data, numComps);
	if (debug) { 
            String filename_din="./debug_pcadata_raw";
            String filename_sc="/debug_pcadata_scores";
            saveResults(data,filename_din);
            saveResults(scores,filename_sc);
        }
		
        if (verbose) 
           System.out.println("In PCA part" + PCAmatrix.numMults + " multiplications performed.");
    }
	
	/**
	 * Uses the file given by filename to construct a table for use by the application.
	 * All numbers should be tab-delimited.
	 * The first line of the data should be two numbers: the number of rows R followed by the
	 * number of columns C. After that, there should be R lines of C tab-delimited values.
	 * @param filename	the name of the file containing the data
	 * @return			a double[][] containing the data in filename
	 * @throws IOException	if an error occurs while reading the file
	 */
	private static double[][] parseData(String filename) throws IOException{
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(new File(filename)));
		} catch(FileNotFoundException e) {
			System.err.println("File " + filename + " not found.");
		}
		String firstLine = in.readLine();
		String[] dims = firstLine.split(","); // <# points> <#dimensions>
		double[][] data = new double[Integer.parseInt(dims[1])][Integer.parseInt(dims[0])];
		for(int j = 0; j < data[0].length; j++) {
			String text = in.readLine();
			String[] vals = text.split(",");
			for(int i = 0; i < data.length; i++)  {
				data[i][j] = Double.parseDouble(vals[i]);
			}
		}
		try {
			in.close();
		} catch(IOException e) {
			System.err.println(e);
		}
		return data;
	}
	
	/**
	 * Saves the results of PCA to a file. The filename has "_processed" appended to it before
	 * the extension.
	 * @param results	double[][] of PCA results
	 * @param filename	original filename of data
	 */
	private static void saveResults(double[][] results, String newFilename) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File(newFilename)));
		} catch(IOException e) {
			System.err.println("Error trying to write new file.");
		}
		for(int i = 0; i < results[0].length; i++) {
			for(int j = 0; j < results.length; j++) {
				try {
					out.write("" + results[j][i]);
					if(j != results.length - 1) {
						out.write(",");
					} else {
						out.write("\n");
					}
				} catch(IOException e) {
					System.err.println("Error trying to write new file.");
				}
			}
		}
	}
}