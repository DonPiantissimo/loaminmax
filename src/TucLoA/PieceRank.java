package TucLoA;

public class PieceRank {
	int MAX_PIECE;
	int BOARD_SIZE;
	Piece pieces[][];
	Piece piecesOrder[];
	int rankCount[];
	int pieceCount = 0;
	
	public PieceRank(int bOARD_SIZE) {
		super();
		BOARD_SIZE = bOARD_SIZE;
		MAX_PIECE = (BOARD_SIZE-2)*2;
		rankCount = new int[BOARD_SIZE-1];
		for (int i=0;i<BOARD_SIZE-1;i++)
			rankCount[i]=0;
		pieces = new Piece[BOARD_SIZE-1][MAX_PIECE-1];
		piecesOrder = new Piece[MAX_PIECE];
	}
	
	public void setPiece(int xpos, int ypos, byte color, int distance){
		pieces[distance][rankCount[distance]] = new Piece(xpos, ypos, color, distance);
		rankCount[distance]++;
	}
	
	public void setPiece(Piece copy){
		int distance = copy.distance;
		pieces[distance][rankCount[distance]] = new Piece(copy);
		rankCount[distance]++;
	}
	
	public int makeOrder(){
		
		for (int i=0;i<BOARD_SIZE-1;i++)
			for (int j=0;j<rankCount[i];j++){
				piecesOrder[pieceCount]=new Piece(pieces[i][j]);
				pieceCount++;
			}
		
		return pieceCount;
	}
	
	public byte getXpos(int pos){
		return (byte)piecesOrder[pos].xpos;
	}
	
	public byte getYpos(int pos){
		return (byte)piecesOrder[pos].ypos;
	}

	public int getPieceCount() {
		return pieceCount;
	}
	
	public double groupingRatio(){
		return (double)rankCount[0]/(double)pieceCount;
	}

}
