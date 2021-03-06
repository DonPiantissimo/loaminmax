package TucLoA;

public class Trinary {
  public byte digit;
  private Trinary next;
  private final byte LIMIT=3;
  
  public Trinary (byte digit, Trinary next){
    this.digit = digit;
    this.next = next;
  }
  
  public Trinary (byte digit){
    this.digit = digit;
  }
  
  public void setNext (Trinary next){
    this.next=next;
  }
  
  public byte increment(){
    digit++;
    if (digit==LIMIT){
      digit=0;
      next.increment();
    }
    return digit;
  }
  
  public void reset(){
    digit=0;
  }
}
