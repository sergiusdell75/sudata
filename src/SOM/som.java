package SOM;


import SOM.CNode;
import java.util.List;

/*
 * Copyright Sergius Dell DevLab
 */

/**
 * Kohonen Self Organizing Feature Map 
 * @author emil
 */
package SOM;
public class som {
      //the neurons representing the Self Organizing Map
  List<CNode>       m_SOM;

  //this holds the address of the winning node from the current iteration
  CNode []              m_pWinningNode; 

   //this is the topological 'radius' of the feature map
  double              m_dMapRadius;

   //used in the calculation of the neighbourhood width of influence
  double              m_dTimeConstant;

  //the number of training iterations
  int                 m_iNumIterations;

  //keeps track of what iteration the epoch method has reached
  int                 m_iIterationCount;

  //the current width of the winning node's area of influence
  double              m_dNeighbourhoodRadius;

  //how much the learning rate is adjusted for nodes within
  //the area of influence
  double              m_dInfluence;

  double              m_dLearningRate;

  //set true when training is finished
  boolean                m_bDone;

  //the height and width of the cells that the nodes occupy when 
  //rendered into 2D space.
  double              m_dCellWidth;
  double              m_dCellHeight;
}
