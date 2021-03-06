package TucLoA;

public class Line {
  private final byte SELF = 0;
  private final byte OPP = 1;
  
  private int order;
  private byte formation[];
  private int moveDistance;
  
  private byte length;
  private byte EMPTY;
  
  public double value_pos = 1;
  public double value_neg = 1;
  public double value;
  
  public double diag_pos_value;
  public double diag_neg_value;
  
  public double diag_value;
  
  private boolean pos_end = false;
  private boolean neg_end = false;
  private boolean block_high = false;
  private boolean block_low = false;
  
  public Line(int order, byte length, byte EMPTY, Trinary[] formation){
    this.order = order;
    this.length = length;
    this.EMPTY = EMPTY;
    for (int i=0;i<order;i++){
      this.formation[i]=formation[i].digit;
    formation[order]=SELF;
    for (int i=order+1;i<length;i++)
      this.formation[i]=formation[i-1].digit
    }

    findMoveDistance();
    value=getValue();
    diag_value = getDiagValues();
   
  }
  
  public void findMoveDistance(){
        moveDistance=0;
    for (int i=0;i<length;i++)
      if (formation[i]!=EMPTY)
        moveDistance++;
  }
  
  public double getValue(){
    if (order + moveDistance >= length)
      value_pos=0;
    else {
      if (formation[order+moveDistance]==SELF)
        value_pos=0;
      else {
        if (order+moveDistance==length-1){
          value_pos*=0.5;
          pos_end=true;
        }
        for (int i=order+1;i<order+moveDistance;i++)
          if (formation[order]==OPP){
            value_pos=0;
            block_high = true;
            break;
          }
        }
    }
    if (formation[order+moveDistance]==OPP)
      value_pos*=2;
      
    if (order - moveDistance < 0)
      value_neg=0;
    else {
      if (formation[order-moveDistance]==SELF)
        value_neg=0;
      else {
        if (order-moveDistance==0){
          value_neg*=0.5;
          neg_end=true;
        }
        for (int i=order-1;i>order-moveDistance;i--)
          if (formation[order]==OPP){
            value_neg=0;
            block_low = true;
            break;
          }
        }
    }
    if (formation[order-moveDistance]==OPP)
      value_neg*=2;
    
    return value_pos+value_neg;
  }
  
  public double getDiagValues(){
    if (pos_end)
      diag_pos_value = value_pos*0.5;
    else diag_pos_value = value_pos;
    
    if (neg_end)
      diag_neg_value = value_neg*0.5;
    else diag_neg_value = value_neg;
    
    return diag_pos_value+diag_neg_value;
  }
  
  public boolean getBlock(){
    if (order<4)
      return block_low;
    return block_high;
  }
  
}
