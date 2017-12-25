package SOM;


import java.util.List;

/*
 * Copyright Sergius Dell DevLab
 */

/**
 * class to describe a node for a Kohonen Self Organising Map
 * @author emil
 * @credits Mat Buckland, fup@ai-junkie.com
 */
public class CNode {

  List<Double>    m_dWeights;

  //its position within the lattice
  double            m_dX,
                    m_dY;

  //the edges of this node's cell. Each node, when draw to the client 
  //area, is represented as a rectangular cell. The color of the cell 
  //is set to the RGB value its weights represent.
  int               m_iLeft;
  int               m_iTop;
  int               m_iRight;
  int               m_iBottom;

    CNode(int lft, int rgt, int top, int bot, int NumWeights){
        this.m_iLeft=lft;
        this.m_iRight=rgt;
        this.m_iBottom=bot;
        this.m_iTop=top;
                                                                    }

    for (int w=0; w<NumWeights; ++w)
    {
      m_dWeights.push_back(RandFloat());
    }

    //calculate the node's center
    m_dX = m_iLeft + (double)(m_iRight - m_iLeft)/2;
    m_dY = m_iTop +  (double)(m_iBottom - m_iTop)/2;
  }

   

    public void   Render(HDC surface);

    public double CalculateDistance(const vector<double> &InputVector);

    public void   AdjustWeights(const vector<double> &vec,
                              const double         LearningRate,
                              const double         Influence);

  double X()const{return m_dX;}
  double Y()const{return m_dY;}

};


//------------------------------ Render ----------------------------------
//
//------------------------------------------------------------------------
void Render(HDC surface)
{
 
  //create a brush and pen of the correct color
  int red   = (int)(m_dWeights[0] * 255);
  int green = (int)(m_dWeights[1] * 255);
  int blue  = (int)(m_dWeights[2] * 255);

  HBRUSH brush = CreateSolidBrush(RGB(red, green, blue));
  HPEN   pen   = CreatePen(PS_SOLID, 1, RGB(red, green, blue));

  HBRUSH OldBrush = (HBRUSH)SelectObject(surface, brush);
  HPEN   OldPen   = (HPEN)SelectObject(surface, pen);

  Rectangle(surface, m_iLeft, m_iTop, m_iRight, m_iBottom);

  SelectObject(surface, OldBrush);
  SelectObject(surface, OldPen);

  DeleteObject(brush);
  DeleteObject(pen);

}


//-------------------------- CalculateDistance ---------------------------
//
//  returns the euclidean distance (squared) between the node's weights 
//  and the input vector
//------------------------------------------------------------------------
double CNode::CalculateDistance(const vector<double> &InputVector)
{
  double distance = 0;
  
  for (int i=0; i<m_dWeights.size(); ++i)
  {
    distance += (InputVector[i] - m_dWeights[i]) * 
                (InputVector[i] - m_dWeights[i]);
  }

  return distance;
}

//-------------------------- AdjustWeights -------------------------------
//
//  Given a learning rate and a target vector this function adjusts
//  the node's weights accordingly
//------------------------------------------------------------------------
void CNode::AdjustWeights(const vector<double> &target,
                          const double LearningRate,
                          const double Influence)
{
  for (int w=0; w<target.size(); ++w)
  {
    m_dWeights[w] += LearningRate * Influence * (target[w] - m_dWeights[w]);
  }
}



#endif



    
    
}
