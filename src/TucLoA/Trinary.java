package TucLoA;

public class Trinary {
  public byte digit;
  private Trinary next;
  private final byte LIMIT=3;
  
  public Trinary (byte digit, Trinary next){
    this.digit = digit;
    this.next = next;
  }
  
  public byte increment(){
    digit++;
    if (digit==LIMIT){
      digit=0;
      next.increment();
    }
    return digit;
  }
}
