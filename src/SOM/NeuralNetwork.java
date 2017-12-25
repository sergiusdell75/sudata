/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
package SOM;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.AbstractList; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


 class SortedList<E> extends AbstractList<E> {

    private ArrayList<E> internalList = new ArrayList<E>();

    // Note that add(E e) in AbstractList is calling this one
    @Override 
    public void add(int position, E e) {
        internalList.add(e);
        Collections.sort(internalList, null);
    }

    @Override
    public E get(int i) {
        return internalList.get(i);
    }

    @Override
    public int size() {
        return internalList.size();
    }

}
    public class NeuralNetwork implements EventArgs
    {
        private Neuron [][] outputLayer= null;
        public Neuron [][] getOutputLayer() { return outputLayer; }
        public void setOutputLayer (Neuron [][]value) { outputLayer = value; }
        private int inputLayerDimension;
        private int outputLayerDimension;
        private int numberOfPatterns;
        private List<List<Double>> patterns;
        private List<String> classes;
        private SortedList<String> existentClasses;
        private List<Color> usedColors;
        private boolean normalize;
        private int numberOfIterations;
        private int currentIteration;

        private Functions function;
        private double epsilon;
        private double currentEpsilon;

        private double CalculateNormOfVectors(List<Double> vector1, List<Double> vector2)
        {
            double value = 0;
            for(int i=0; i<vector1.size(); i++)
                value += Math.pow((vector1.get(i) - vector2.get(i)), 2);
            value = Math.sqrt(value);
            return value;
        }

        private void NormalizeInputPattern(List<Double> pattern){
            double nn = 0;
            double tmp=0.0;
            for (int i = 0; i < inputLayerDimension; i++)
            {
                nn += (pattern.get(i) * pattern.get(i));
            }
            nn = Math.sqrt(nn);
            for (int i = 0; i < inputLayerDimension; i++)
            {
                tmp=pattern.get(i)/nn;
                pattern.set(i,tmp);
            }
        }

        private void StartEpoch(List<Double> pattern)
        {
            Neuron Winner = this.FindWinner(pattern);
            currentEpsilon = 0;
            for (int i = 0; i < outputLayerDimension; i++)
                for (int j = 0; j < outputLayerDimension; j++)
                { 
                    currentEpsilon += outputLayer[i][j].ModifyWights(pattern, Winner.getCoordinate(), currentIteration, function);                   
                }
            currentIteration++;
            currentEpsilon = Math.abs(currentEpsilon / (outputLayerDimension * outputLayerDimension));
            EndEpochEventArgs e = new EndEpochEventArgs();
            OnEndEpochEvent(e);
        }

        public boolean getNormalize(){ return normalize; }
        public void setNormalize(boolean value){ normalize = value; }

        public List<List<Double>> getPatterns()
        {
            return patterns;
        }

        public List<String> getClasses()
        {
            return classes;
        }

        public int getInputLayerDimension()
        {
           return inputLayerDimension;
        }

        public int getOutputLayerDimension()
        {
            return outputLayerDimension;
        }

        public double getCurrentDelta()
        {
            return currentEpsilon;
        }

        public SortedList<String> getExistentClasses()
        {
           return existentClasses;
        }

        public List<Color> getUsedColors()
        {
            return usedColors;
        }

        private int NumberOfClasses()
        {
            existentClasses = new SortedList<>();
            existentClasses.add(0, classes.get(0));
            int k = 0;
            int d = 2;
            for (int i = 1; i < classes.size(); i++)
            {
                k=0;
                for (int j = 0; j < existentClasses.size(); j++)
                    if (existentClasses.IndexOfKey(classes[i])!=-1) k++;
                if (k == 0)
                {
                    existentClasses.add(d, classes.get(i));
                    d++;
                }
            }
            return existentClasses.size();
        }

        public Color[][] ColorSOFM()
        {
            Color[][] colorMatrix = new Color[outputLayerDimension][outputLayerDimension];
            int numOfClasses = NumberOfClasses();
            List<Color> goodColors = new ArrayList<>();
            goodColors.add(Color.black);
            goodColors.add(Color.red);
            goodColors.add(Color.blue);
            goodColors.add(Color.green);
            goodColors.add(Color.yellow);            
            usedColors = new ArrayList<>(numOfClasses);
            usedColors.add(goodColors.get(0));
            int k = 0;
            int randomColor = 0;
            Random r = new Random();
            while (usedColors.size() != numOfClasses)
            {
                k = 0;
                randomColor = r.Next(goodColors.size());
                for (Color cl : usedColors)
                    if (cl == goodColors[randomColor]) k++;
                if (k == 0) usedColors.Add(goodColors[randomColor]);
            }
            for (int i = 0; i < outputLayerDimension; i++)
                for (int j = 0; j < outputLayerDimension; j++)
                    colorMatrix[i][j] = Color.FromKnownColor(KnownColor.ButtonFace);

            for (int i = 0; i < patterns.Count; i++)
            {
               Neuron n = FindWinner(patterns[i]);
                colorMatrix[n.Coordinate.X,n.Coordinate.Y] = usedColors[existentClasses[classes[i]]-1];
            }
            return colorMatrix;
        }

        public NeuralNetwork(int m, int numberOfIterations, double epsilon, Functions f)
        {
            outputLayerDimension = m;
            currentIteration = 1;
            this.numberOfIterations = numberOfIterations;
            function = f;
            this.epsilon = epsilon;
            currentEpsilon = 100;
        }

        public Neuron FindWinner(List<Double> pattern)
        {
            List<Double> norms = new ArrayList<>(outputLayerDimension * outputLayerDimension);
            double D = 0;
            Neuron Winner = outputLayer[0][0];
            double min = CalculateNormOfVectors(pattern, outputLayer[0][0].getWeights());
            for (int i = 0; i < outputLayerDimension; i++)
                for (int j = 0; j < outputLayerDimension; j++)
                {
                    D = CalculateNormOfVectors(pattern, outputLayer[i][j].getWeights());
                    if (D < min)
                    {
                        min = D;
                        Winner = outputLayer[i][j];
                    }
                }
            return Winner;
        }

        public void StartLearning()
        {
            int iterations = 0;
            while (iterations<=numberOfIterations && currentEpsilon > epsilon)
            {
                List<List<Double>> patternsToLearn = new ArrayList<>(numberOfPatterns);
                patterns.stream().forEach((pArray) -> {
                    patternsToLearn.add(pArray);
                });
                Random randomPattern = new Random();
                List<Double> pattern = new ArrayList<>(inputLayerDimension);
                for (int i = 0; i < numberOfPatterns; i++)
                {
                    pattern = patternsToLearn[randomPattern.Next(numberOfPatterns - i)];

                    StartEpoch(pattern);

                    patternsToLearn.Remove(pattern);
                }
                iterations++;
                OnEndIterationEvent(new EventArgs());
            }
        }

        public void ReadDataFromFile(string inputDataFileName) throws IOException
        {
            InputStreamReader sr = new InputStreamReader(inputDataFileName);
            String line = sr.readLine();
            int k = 0;
            for (int i = 0; i < line.Length; i++)
            {
                if (line[i] == ' ') k++;
            }
  
            inputLayerDimension = k;
            int sigma0 = outputLayerDimension;
            
            outputLayer = new Neuron[outputLayerDimension, outputLayerDimension];
            Random r = new Random();
            for (int i = 0; i < outputLayerDimension; i++)
                for (int j = 0; j < outputLayerDimension; j++)
                {
                    outputLayer[i] [j] = new Neuron(i, j, sigma0);
                    outputLayer[i] [j].setWeights() = new ArrayList<double>(inputLayerDimension);
                    for (k = 0; k < inputLayerDimension; k++)
                    {
                        outputLayer[i, j].Weights.Add(r.NextDouble());
                    }
                }

            k = 0;
            while (line != null)
            {
                line = sr.ReadLine();
                k++;
            }
            patterns = new ArrayList<>(k);
            classes = new ArrayList<>(k);
            numberOfPatterns = k;

            List<Double> pattern;

            sr = new InputStreamReader(inputDataFileName);
            line = sr.ReadLine();
           
            while (line != null)
            {
                int startPos = 0;
                int endPos = 0;
                int j = 0;
                pattern = new ArrayList<>(inputLayerDimension);
                for (int ind = 0; ind < line.Length; ind++)
                {
                    if (line[ind] == ' ' && j != inputLayerDimension)
                    {
                        endPos = ind;
                        pattern.Add(Convert.ToDouble(line.Substring(startPos, endPos - startPos)));
                        startPos = ind + 1;
                        j++;
                    }
                    if (j > inputLayerDimension) 
                        throw new IOException("Wrong file format. Check input data file, and try again");
                }
                if (normalize) this.NormalizeInputPattern(pattern);
                patterns.add(pattern);
                startPos = line.LastIndexOf(' ');
                classes.Add(line.Substring(startPos));
                line = sr.ReadLine();
            }
        }

        public EndEpochEventHandler EndEpochEvent;
        public event EndIterationEventHandler EndIterationEvent;

        protected virtual void OnEndEpochEvent(EndEpochEventArgs e)
        {
            if (EndEpochEvent != null)
                EndEpochEvent(this, e);
        }

        protected virtual void OnEndIterationEvent(EventArgs e)
        {
            if (EndIterationEvent != null)
                EndIterationEvent(this, e);
        }
    }
}
