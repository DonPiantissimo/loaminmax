package TucLoA;

public class Line {
  private final byte SELF = 0;
  private final byte OPP = 1;
  
  private int order;
  private byte formation[];
  private int moveDistance;
  
  private byte BOARD_SIZE;
  private byte EMPTY;
  
  private double value_pos = 1;
  private double value_neg = 1;
  private double value;
  
  public Line(int order, byte BOARD_SIZE, byte EMPTY, byte[] formation){
    this.order = order;
    this.BOARD_SIZE = BOARD_SIZE;
    this.EMPTY = EMPTY;
    System.arraycopy(this.formation,0,formation,0,BOARD_SIZE);

    findMoveDistance();
    value=getValue();

   
  }
  
  public void findMoveDistance(){
        moveDistance=0;
    for (int i=0;i<BOARD_SIZE;i++)
      if (formation[i]!=EMPTY)
        moveDistance++;
  }
  
  public int getValue(){
    if (order + moveDistance >= BOARD_SIZE)
      value_pos=0;
    else {
      if (formation[order+moveDistance]==OWN)
        value_pos=0;
      else {
        if (order+moveDistance==BOARD_SIZE-1)
          value_pos*=0.5;
        for (int i=order+1;i<order+moveDistance;i++)
          if (formation[order]==OPP){
            value_pos=0;
            break;
          }
        }
    }
    
    if (order - moveDistance < 0)
      value_pos=0;
    else {
      if (formation[order-moveDistance]==OWN)
        value_pos=0;
      else {
        if (order-moveDistance==0)
          value_pos*=0.5;
        for (int i=order-1;i>order-moveDistance;i--)
          if (formation[order]==OPP){
            value_pos=0;
            break;
          }
        }
    }
    
    return value_pos+value_neg;
  }
  
}
