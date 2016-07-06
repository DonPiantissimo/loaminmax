package TucLoA;

public class GamePosition {
	private byte board[][];
	private byte turn;
	final byte BOARD_SIZE = 8;
	final byte WHITE = 0;
	final byte BLACK = 1;
	final byte EMPTY = 2;

	final int NUMBER_OF_PIECES = 12;	
	private int sumOfMinimalDistances[];
	
	public GamePosition() {
		board = new byte[BOARD_SIZE][BOARD_SIZE];
		sumOfMinimalDistancesInit();
	}
	
	public GamePosition(byte def){
		board = new byte[BOARD_SIZE][BOARD_SIZE];
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++)
				board[i][j]=def;
	}
	
	public byte[][] getBoard() {
		return board;
	}
	
	public byte getTurn() {
		return turn;
	}
	
	public byte getOppTurn(){
		if (turn==WHITE)
			return BLACK;
		return WHITE;
	}
	
	public void setTurn(byte t) {
		turn = t;
	}
	
	public void sumOfMinimalDistancesInit(){
		sumOfMinimalDistances = new int[NUMBER_OF_PIECES+1];
		sumOfMinimalDistances[0]=-1
		for (int i=1;i<=9;i++)
			sumOfMinimalDistances[i]=sumOfMinimalDistances[i-1]+1;
		for (int i=10;i<=14 && i<=NUMBER_OF_PIECES;i++)
			sumOfMinimalDistances[i]=sumOfMinimalDistances[i-1]+2;
	}
}
