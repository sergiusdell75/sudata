/*
 * Copyright Sergius Dell DevLab
 */
package SOM;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author emil
 */

    public class Neuron
    {
        private List<Double> weights=null;
        private Point coordinate;
        private int iteration;
        private int wightsdimension;
        private int sigma0;
        private final double alpha0;
        private double tau1;
        private final int tau2;

        private double h(Point winnerCoordinate, Functions f) {
            double result = 0;
            double distance = 0;
            switch (f)
            {
                case Discrete:
                    {
                        distance = Math.abs(this.coordinate.getX() - winnerCoordinate.getX()) 
                                + Math.abs(this.coordinate.getY() - winnerCoordinate.getY());
                        switch ((int)distance)
                        {
                            case 0:
                                result = 1;
                                break;
                            case 1:
                                result = 0.5f;
                                break;
                            case 2:
                                result = 0.25f;
                                break;
                            case 3:
                                result = 0.125f;
                                break;
                        }
                        break;
                    }
                case Gaus:
                    {
                        distance = Math.sqrt(Math.pow((winnerCoordinate.getX() - coordinate.getX()), 2) + 
                                    Math.pow((winnerCoordinate.getY() - coordinate.getY()), 2));
                        result = Math.exp(-(distance * distance) / (Math.pow(Sigma(iteration), 2)));
                        break;
                    }
                case MexicanHat:
                    {
                        distance = Math.sqrt(Math.pow((winnerCoordinate.getX() - coordinate.getX()), 2) + Math.pow((winnerCoordinate.getY() - coordinate.getY()), 2));
                        result = Math.exp(-(distance * distance) / Math.pow(Sigma(iteration), 2)) * (1 - (2 / Math.pow(Sigma(iteration), 2)) * (distance * distance));                        
                        break;
                    }
                case FrenchHat:
                    {
                        int a = 2;
                        distance = Math.abs(this.coordinate.getX() - winnerCoordinate.getX())
                                + Math.abs(this.coordinate.getY() - winnerCoordinate.getY());
                        if (distance <= a) result = 1;
                        else
                            if (distance < a && distance <= 3 * a) result = -1 / 3;
                            else
                                if (distance > 3 * a) result = 0;
                        break;
                    }
            }
            return result;
        }

        private void InitializeVariables(int sigma0)
        {
            iteration = 1;
            this.sigma0 = sigma0;
            tau1 = 1000 / Math.log(sigma0);
        }

        private double Alpha(int t)
        {
            double value = alpha0 * Math.exp(-t/tau2);
            return value;
        }

        private double Sigma(int t)
        {
            double value = sigma0 * Math.exp(-t/tau1);
            return value;
        }

        public List<Double> getWeights() { return weights; }
        public void setWeights (List<Double> value){ 
            weights = value;
            wightsdimension = weights.size();
        }

        public Point getCoordinate() { return coordinate; }
        public void setCoordinate(Point value){
          coordinate = value;
        }

        public int getIteration(){ return iteration; }       
        public Neuron(int x, int y, int sigma0)
        {
        this.alpha0 = 0.1;
        this.tau2 = 1000;
            coordinate.setLocation(x,y);
            InitializeVariables(sigma0);
        }

        public Neuron(Point coordinate, int sigma0)
        {
        this.alpha0 = 0.1;
        this.tau2 = 1000;
            this.coordinate = coordinate;
            InitializeVariables(sigma0);
        }

        public double ModifyWights(List<Double> pattern, Point winnerCoordinate, 
                int iteration, Functions f)
        {
            double avgDelta = 0;
            double tmp=0;
            double modificationValue =0;
            weights = new ArrayList(wightsdimension);
            for (int i = 0; i < wightsdimension; i++)
            {
                modificationValue = Alpha(iteration) * h(winnerCoordinate, f) * (pattern.get(i) - weights.get(i));
                tmp=weights.get(i) + modificationValue;
                weights.set(i, tmp);
                avgDelta += modificationValue;
            }
            avgDelta = avgDelta / wightsdimension;
            return avgDelta;
        }

        public double getNorm() {
                double norm = 0;
                norm = weights.stream().map((d) -> d).reduce(norm, (accumulator, _item) -> accumulator + _item);
                norm = norm / this.wightsdimension;
                return norm;
            }
        }