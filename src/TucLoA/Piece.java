package TucLoA;

public class Piece {
	byte color;
	int distance;
	int xpos;
	int ypos;
	
	public Piece(int xpos, int ypos, byte color, int distance) {
		super();
		this.color = color;
		this.distance = distance;
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public void setPiece(byte color, int distance){
		this.color = color;
		this.distance = distance;
	}
	
	public Piece(Piece copy){
		color=copy.color;
		distance=copy.distance;
		xpos=copy.xpos;
		ypos=copy.ypos;
	}
}
