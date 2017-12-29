/*
 * Copyright Sergius Dell DevLab
 */
package SOM;

/**
 * A simple GUI for fitting self-organizing maps.
 *  
 * This file contains
 * the main method and constructs the necessary
 * windows for user input as well as reading in
 * the data and validating it. It then passes
 * the necesary data onto a SOM object where
 * the model fitting is performed before
 * displaying the results.
 * 
 * @credits David Shaub
 * 
 * */
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
public class SomKohonen extends JFrame
{
	// Instance variables
	private JButton fileChooser = new JButton("Input File");
	private ArrayList <Double[]> inputData = new ArrayList<>();
	private SomGrid trainData;
	private JTextField xDim = new JTextField(3);
	private JTextField yDim = new JTextField(3);
	private JTextField epochs = new JTextField(4);
	private JComboBox<String> colorBox = new JComboBox<>(new String[]{"Red", "Green", "Blue"});
	
	/**
	 * Read in the input csv data
	 * This function reads the input data
	 * from a csv for constructing the
	 * self-organizing map. The function also
	 * checks the data to make sure it is valid
	 * and satisfies the necessary conditions
	 * to construct a self-organizing map.
	 * 
	 * @param input The file object to read in
	 * 
	 * */
	public void readFile(File input)
	{
		// Open a Scanner
		Scanner inFile;
		try
		{
			inFile = new Scanner(input);
			inFile.useDelimiter("\n");
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Cannot read file. Select a readable file.");
			return;
		}
		
		// Treat the first line as a header and use it
		// to determine the number of columns
		int numColumns = 0;
		String currentLine = inFile.next();
		Scanner parser = new Scanner(currentLine);
		parser.useDelimiter(",");
		while(parser.hasNext())
		{
			parser.next();
			numColumns++;
		}
		// There should be at least two columns
		if(numColumns < 2)
		{
			JOptionPane.showMessageDialog(null, "The file should have at least two columns.");
			return;
		}
		// Now read the data
		Double [] currentRow = new Double[numColumns];
		int rowNum = 0;
		while(inFile.hasNext())
		{
			currentLine = inFile.next();
			parser = new Scanner(currentLine);
			parser.useDelimiter(",");
			try
			{
				currentRow = new Double[numColumns];
				// Ignore data in rows with more entries than in the header
				for(int i = 0; i < numColumns; i++)
				{
					currentRow[i] = Double.parseDouble(parser.next());
				}
				inputData.add(currentRow);
			}
			// Ensure the data are parsed to numeric
			catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(null, "The file should contain only numeric data.");
				return;
			}
			// Ensure a non-jagged array
			catch(NoSuchElementException nsee)
			{
				JOptionPane.showMessageDialog(null, "Every row should contain one number for every columns in the file header.");
				return;
			}
		}
		
		// Ensure # rows >= # cols
		if(inputData.size() < numColumns)
		{
			JOptionPane.showMessageDialog(null, "There must be at least as many data rows as columns in the file.");
			return;
		}
		
		// The data has passed validity checks, so convert to an array
		double[][] validData = new double[inputData.size()][numColumns];
		Double [] tmpArray;
		for(int i = 0; i < inputData.size(); i++)
		{
			tmpArray = inputData.get(i);
			for(int j = 0; j < numColumns; j++)
			{
				validData[i][j] = tmpArray[j];
			}
		}
		
		// Convert to a grid object
		int xVal;
		int yVal;
		int epochVal;
		try
		{
			xVal = Integer.parseInt(xDim.getText());
			yVal = Integer.parseInt(yDim.getText());
			epochVal = Integer.parseInt(epochs.getText());
			if(xVal <= 0 || yVal <= 0 || epochVal <= 0)
			{
				throw new NumberFormatException();
			}
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(null, "The grid dimensions and training epochs must be positive integers.");
			return;
		}
		// Create the SOM object
		long startTime = System.nanoTime();
		//JFrame status = new JFrame("Training the Kohonen network...");
		//status.setVisible(true);
		//status.setSize(100, 100);
		JOptionPane.showMessageDialog(null,"Training the Kohonen network. Please be patient.\nThis may take a while for large datasets, networks, and epochs.");
		SomLight training = new SomLight(validData, xVal, yVal, epochVal);
		// Train the Kohonen network
		training.train();
		long endTime = System.nanoTime();
		//System.out.println("" + (endTime - startTime / 1000000));
		// Plot the network as a heatmap
		//status.setVisible(false);
		plot(xVal, yVal, training.getNodes());
	}
	
	/**
	 * Plot the trained Kohonen network.
	 * 
	 * This method takes as inputs the x dimension
	 * of the map, the y dimension of the map, and 
	 * an array for the node labels of each observation
	 * and produces a grid heatmap showing the count
	 * of observations assigned to each node.
	 * 
	 * 
	 *
     * @param xDim
     * @param yDim
     * @param nodes */
	 public void plot(int xDim, int yDim, int [] nodes)
	 {
		 // Determine the number of observations assigned to each node
		 int [] counts = new int[xDim * yDim];
		 for(int i = 0; i < nodes.length; i++)
		 {
			 counts[nodes[i]] += 1;
		 }
		 // Determine the maximum and minimum counts for shading
		 int maxCount = 0;
		 int minCount = Integer.MAX_VALUE;
		 for(int i = 0; i < counts.length; i++)
		 {
			if(counts[i] < minCount)
			{
				minCount = counts[i];
			}
			if(counts[i] > maxCount)
			{
				maxCount = counts[i];
			}
			//System.out.println("Node " + i + ": " + counts[i]);
		 }
		 // Create the window
		 JFrame map = new JFrame ("Kohonen network");
		 // Set the output resolution, don't let it exceed 1024x768
		 //double aspectRatio = (double)xdim / yDim;
		 String col = colorBox.getSelectedItem().toString();
		 map.setSize(800, 600);
		 map.setLayout (new GridLayout(yDim, xDim));
		 
		 // Plot the counts
		 JButton [] jB = new JButton[counts.length];
		 // Set the colors
		 float r = 0;
		 float g = 0;
		 float b = 0;
		 // Fill top to bottom, left to right
		 // instead of left to right, top to bttom
		 int index;
		 int columnCount = 0;
		 for(int i = 0; i < counts.length; i++)
		 {
			 // Calculuate the element to use since we're
			 // filling from top to bottom, left to right
			 index = (i / xDim) + columnCount * yDim;
                     // Get the RGB values
                     switch (col) {
                         case "Red":
                             r = (float)counts[index] / maxCount;
                             break;
                         case "Green":
                             g = (float)counts[index] / maxCount;
                             break;
                         default:
                             b = (float)counts[index] / maxCount;
                             break;
                     }
			 jB[index] = new JButton("");
			 jB[index].setBackground(new Color(r, g, b));
			 jB[index].setOpaque(true);
			 map.add(jB[index]);
			 columnCount++;
			 // Reset the column count if it grows too large
			 if(columnCount >= xDim)
			 {
				 columnCount = 0;
			 }
		 }
		 map.setVisible(true); 
	 }
	
	class ButtonListener implements ActionListener 
	{
		/**
		 * Define the behavior for the ActionListener
		 * 
		 * @param e The ActionEvent object
		 * 
		 * */
                @Override
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource() == fileChooser)
			{
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fc.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					// Ensure the ArrayList is empty, e.g. user loads a file after first one failed
					inputData.clear();
					File inputFile = fc.getSelectedFile();
					readFile(inputFile);
				}
			}
		}
	}
	
	public SomKohonen()
	{
		// Create a frame for the background picture and the input
		JFrame fullWindow = new JFrame("kohonen: Self-Organizing Maps in Java");
		fullWindow.setSize (650, 245);
		fullWindow.setResizable(false);
		fullWindow.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		//Construct the JPanel for the parameters and input
		JPanel window = new JPanel();
		window.setLayout(new FlowLayout());
		
		
		//Color
		JLabel colorLabel = new JLabel("Shading");
		window.add(colorLabel);
		//String [] colors = new String[]{"Red", "Green", "Blue"};
		window.add(colorBox);
		
		//Epochs
		JLabel epochLabel = new JLabel("Training epochs");
		window.add(epochLabel);
		
		epochs.setText("20");
		window.add(epochs);
		
		//Grid size
		JLabel dimensionLabel = new JLabel("Grid dimensions");
		window.add(dimensionLabel);
		JPanel gridElements = new JPanel();
		xDim.setText("5");
		yDim.setText("5");
		window.add(xDim);
		window.add(yDim);
		
		//Input
		ButtonListener bl = new ButtonListener();
		fileChooser.addActionListener(bl);
		window.add(fileChooser);
		
		// Background picture
		JLabel background;
		try
		{
			// Converted to jpg from the CC 3.0 content at
			// https://en.wikipedia.org/wiki/Self-organizing_map#/media/File:Somtraining.svg
			background = new JLabel(new ImageIcon(javax.imageio.ImageIO.read(new File("img/background.jpg"))));
			fullWindow.add(background, BorderLayout.SOUTH);
		}
		catch(IOException e)
		{
		}
		
		
		fullWindow.add(window, BorderLayout.NORTH);
		//window.setVisible(true);
		fullWindow.setVisible(true);
	}
	
}
