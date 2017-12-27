/*
 * Copyright Sergius Dell DevLab
 */
package SOM;
import java.util.*;
/**
 * Representation of Grid objects.
 * 
 * 
 * This class contains methods for representing
 * non-jagged, rectancular data arrays. Useful
 * methods such as mean, variance, and transpose
 * are defined. Since input validation is specific
 * for the Kohonen application, error handling and 
 * validation is handled there, so exceptions
 * will occur if problematic arrays are created
 * (e.g. no rows/columns, jagged, etc). The arrays
 * used to construct a grid object must have at least
 * two columns and must have at least as many rows as columns.
 * 
 * @credits David Shaub
 * @version 1.1.0
 * 
 * */
 
public class SomGrid {
	protected double [][] gridData;
	
	
	/**
	 * Single argument constructor
	 * 
	 * @param input The input array. It must have at least two rows,
	 * at least two columns, and at least as many rows as columns.
	 * 
	 * */
	public SomGrid(double [][] input)
	{
		// Enforce properties for Grid objects
		// Must have at least two rows
		if(input.length < 2)
		{
			throw new IllegalArgumentException();
		}
		// Must have at at least as many rows as columns
		else if(input.length < input[0].length)
		{
			throw new IllegalArgumentException();
		}
		// Must have at least two columns
		else if(input[0].length < 2)
		{
			throw new IllegalArgumentException();
		}
            // Don't allow jagged arrays
            for (double[] input1 : input) {
                if (input[0].length != input1.length) {
                    throw new IllegalArgumentException();
                }
            }
		
		this.gridData = input;
	}
	
	
	/**
	 * Extract the data for a given row and column
	 * from a Grid object.
	 * 
	 * @param row The row position
	 * @param column The column position
	 * 
	 * @return The value at the row and column position
	 * 
	 * */
	 public double getObs(int row, int column)
	 {
		 return this.gridData[row][column];
	 }
	
	/**
	 * Calculuate the mean of an array
	 * This method returns the mean of 
	 * an input array.
	 * 
	 * @param inputArray The array for calculation
	 * 
	 * @return The mean of the array
	 * 
	 * */
	public double mean(double[] inputArray)
	{
		double result = 0;
		for(int i = 0; i < inputArray.length; i++)
		{
			result += inputArray[i];
		}
		return result / inputArray.length;
	}
	
	
	/**
	 * Calculuate the variance of an array
	 * This method returns the variance of 
	 * an input array. A sample-size correction
	 * is not used.
	 * 
	 * @param inputArray The array for calculation
	 * 
	 * @return The variance of the array
	 * 
	 * */
	public double variance(double [] inputArray)
	{
		double xMean = mean(inputArray);
		double sumSq = 0;
		for(int i = 0; i < inputArray.length; i++)
		{
			sumSq += Math.pow((inputArray[i] - xMean), 2);
		}
		return sumSq / inputArray.length;
	}
	
	
	/**
	 * Test if the Grid object has zero variance.
	 * An important property to avoid in a data
	 * set for machine learning is columns with
	 * zero variance. These are columns that have
	 * only one unique value. This can be detected by 
	 * finding columns with no variance.
	 * 
	 * @return Whether the Grid object has at least
	 * one column with zero variance
	 * 
	 * */
	public boolean zeroVariance()
	{
		double[] currentColumn = new double[this.gridData.length];
		// Test each column
		for(int i = 0; i < this.gridData[0].length; i++)
		{
			// Extract the rows
			for(int j = 0; j < this.gridData.length; j++)
			{
				currentColumn[j] = this.gridData[j][i];
			}
			if(variance(currentColumn) == 0)
			{
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Standardize the values in a grid.
	 * This method standardizes the numbers
	 * in a grid by centering each column 
	 * around zero (i.e. forcing the column
	 * means to be zero) and giving each column
	 * unit variance (i.e. forcing the column
	 * variances to be one). This is a necessary
	 * operation for many algorithms that rely
	 * on distances measures so that the results
	 * of the algorithm are independent of the scale
	 * of the input data and units in which they are
	 * expressed. Note: if any column in the Grid object
	 * has zero variance, the scaling operation will
	 * not be performed.
	 * 
	 * 
	 * */
	public void scaleGrid()
	{
		// Don't scale if the columns have zero variance
		if(zeroVariance())
		{
			return;
		}
		int nrows = this.gridData.length;
		int ncols = this.gridData[0].length;
		double colMean;
		double colVar;
		ArrayList <Double[]> result = new ArrayList <>();
		double[] currentColumn = new double[nrows];
		// Repeat for every column
		for(int j = 0; j < this.gridData[0].length; j++)
		{
			// Find the mean and variance for the current column
			for(int i = 0; i < this.gridData.length; i++)
			{
				currentColumn[i] = this.gridData[i][j];
			}
			colMean = mean(currentColumn);
			colVar = variance(currentColumn);
			
			// Perform the scaling down the rows
			for(int k = 0; k < currentColumn.length; k ++)
			{
				gridData[k][j] = (currentColumn[k] - colMean) / Math.sqrt(colVar);
			}
		}
	}
	
	
	/**
	 * Calculuate the pair-wise distances
	 * between all points on the output Kohonen
	 * grid. The maximum, rectilinear distance
	 * is used with a rectangular geometry grid.
	 * The method works on a n * 2 Grid and produces
	 * a n * n output Grid with the distances. For example.
	 * the first rows contains the distances from the first
	 * pair in the implicit argument Grid to all of the other
	 * points. The second row of the output Grid similarly
	 * contains the distances to all the other points from
	 * the second point in the implicit argumetn Grid, etc.
	 * 
	 * @return A grid object representing the pairwise
	 * distances
	 * 
	 * */
	 public SomGrid distance()
	 {
		 int nRow = this.gridData.length;
		 double xDist;
		 double yDist;
		 double [][] distances = new double[nRow][nRow];
		 // Calculate for every point
		 // The result with be a matrix of dimensions nRow * nRow
		 for(int currentObs = 0; currentObs < nRow; currentObs++)
		 {
			 // Calculate on every row
			 for(int i = 0; i < nRow; i++)
			 {
				 // Calculate on every column
				 for(int j = 0; j < nRow; j++)
				 {
					 xDist = Math.abs(this.getObs(currentObs, 0) - this.getObs(j, 0));
					 yDist = Math.abs(this.getObs(currentObs, 1) - this.getObs(j, 1));
					 distances[i][j] = Math.max(xDist, yDist);
				 }
			 }
		 } 
		 return new SomGrid(distances);
	 }
	 
	 
	 /**
	  * A string representation of the object.
	  * 
	  * @return A string representation of the Grid object.
	  * 
	  * */
        @Override
	 public String toString()
	 {
		 String s = "";
            // Traverse the current row
            for (double[] gridData1 : this.gridData) {
                // Traverse all the columns
                for (int j = 0; j < this.gridData[0].length; j++) {
                    s += (gridData1[j] + " ");
                }
                s += "\n";
            }
		 return s;
	 }
}
