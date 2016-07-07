package TucLoA;

public class GamePosition {
	private byte board[][];
	private byte turn;
	final byte BOARD_SIZE = 8;
	final byte WHITE = 0;
	final byte BLACK = 1;
	final byte EMPTY = 2;

	final int NUMBER_OF_PIECES = 12;	
	public int sumOfMinimalDistances[];
	private Line lineMap[][][];
	
	public GamePosition() {
		board = new byte[BOARD_SIZE][BOARD_SIZE];
		sumOfMinimalDistancesInit();
		lineMapInit();
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
	
	public Line[][] getLineMap(){
		return lineMap;
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
	
	public void lineMapInit(){
		Trinary[] formation = new Trinary[BOARD_SIZE-1];
		lineMap = new Line[BOARD_SIZE][3^(BOARD_SIZE-1)][BOARD_SIZE];
		formation[BOARD_SIZE-2] = new Trinary((byte)0);
		for (int i=BOARD_SIZE-3;i>=0;i--)
			formation[i] = new Trinary((byte)0,formation[i+1]);
		formation[BOARD_SIZE-2].setNext(formation[0]);
		for (int i=0;i<BOARD_SIZE;i++){
			for (int k=0;k<BOARD_SIZE-1;k++)
				formation[k].reset();
			for (int j=0;j<3^(BOARD_SIZE-1);j++){
				lineMap[j][i] = new Line(i, BOARD_SIZE, EMPTY, formation);
				formation[0].increment;
			}
		}
				
	}
	
	
	//WIP
	public void lineMapInit(){
		Trinary[][] formation = new Trinary[BOARD_SIZE+1][BOARD_SIZE-1];
		lineMap = new Line[BOARD_SIZE+1][3^(BOARD_SIZE-1)][BOARD_SIZE];
		for (int len=3;len<=BOARD_SIZE;len++){
			formation[len][len-2] = new Trinary((byte)0);
			for (int i=len-3;i>=0;i--)
				formation[len][i]= new Trinary ((byte)0,formation[len][i+1]);
			formation[len][len-2].setNext(formation[len][0]);
			for (int i=0;i<len;i++){
				for (int k=0;k<len-1;k++)
					formation[len][k].reset();
				for (int j=0;j<3^(len-1);j++){
					lineMap[len][j][i] = new Line(i, len, EMPTY, formation[len]);
					formation[len][0].increment;
				}
			}
		}
		
		
	}
	
	public double getPieceValue(int x, int y){
		int[] keys = new int[4];
		int form_key=0;
		int xydif;
		byte myColor = board[x][y];
		byte oppColor = 0;
		if (myColor==0) oppColor=(byte)1;
		
		double pieceValue=0;
		double tempValue;
		
		//horizontal
		for (int i=0;i<x;i++){
			if (board[i][y]==EMPTY)
				form_key+=2*(3^i);
			else if (board[i][y]==oppColor)
				form_key+=3^i;
		}
		for (int i=x+1;i<BOARD_SIZE;i++){
			if (board[i][y]==EMPTY)
				form_key+=2*(3^(i-1));
			else if (board[i][y]==oppColor)
				form_key+=3^(i-1);
		}
		keys[0]=form_key;
		
		//vertical
		form_key=0;
		for (int i=0;i<y;i++){
			if (board[x][i]==EMPTY)
				form_key+=2*(3^i);
			else if (board[x][i]==oppColor)
				form_key+=3^i;
		}
		for (int i=y+1;i<BOARD_SIZE;i++){
			if (board[x][i]==EMPTY)
				form_key+=2*(3^(i-1));
			else if (board[x][i]==oppColor)
				form_key+=3^(i-1);
		}
		keys[1]=form_key;
		
		//positive diagonal
		form_key=0;
		
		if (x<=y){
			xydif=y-x;
			for (int i=0;i<x;i++){
				if (board[i][i+xydif]==EMPTY)
					form_key+=2*(3^i);
				else if (board[i][i+xydif]==oppColor)
					form_key+=3^i;
			}
			for (int i=x+1;i+xydif<BOARD_SIZE;i++){
				if (board[i][i+xydif]==EMPTY)
					form_key+=2*(3^(i-1));
				else if (board[i][i+xydif]==oppColor)
					form_key+=3^(i-1);
			}
		}
		else {
			xydif=x-y
			for (int i=0;i<y;i++){
				if (board[i+xydif][i]==EMPTY)
					form_key+=2*(3^i);
				else if (board[i][i+xydif]==oppColor)
					form_key+=3^i;
			}
			for (int i=y+1;i+xydif<BOARD_SIZE;i++){
				if (board[i+xydif][i]==EMPTY)
					form_key+=2*(3^(i-1));
				else if (board[i+xydif][i]==oppColor)
					form_key+=3^(i-1);
			}
		}
		keys[2] = form_key;
		
		//negative diagonal
		form_key = 0;
		
		if (x+y<=BOARD_SIZE-1){
			xydif=x+y;
			for (int i=0;i<x;i++){
				if (board[i][-i+xydif]==EMPTY)
					form_key+=2*(3^i);
				else if (board[i][i+xydif]==oppColor)
					form_key+=3^i;
			}
			for (int i=x+1;-i+xydif>=0;i++){
				if (board[i][-i+xydif]==EMPTY)
					form_key+=2*(3^(i-1));
				else if (board[i][i+xydif]==oppColor)
					form_key+=3^(i-1);
			}
		}
		else {
			xydif = x+y
			for (int i=0;BOARD_SIZE-1-i>y;i++){
				if (board[xydif-BOARD_SIZE+1+i][BOARD_SIZE-1-i]==EMPTY)
					form_key+=2*(3^i);
				else if (board[xydif-BOARD_SIZE+1+i][BOARD_SIZE-1-i]==oppColor)
					form_key+=3^i;
			}
			for (int i=BOARD_SIZE-y;xydif-BOARD_SIZE+1+i<BOARD_SIZE;i++){
				if (board[xydif-BOARD_SIZE+1+i][BOARD_SIZE-1-i]==EMPTY)
					form_key+=2*(3^(i-1));
				else if (board[xydif-BOARD_SIZE+1+i][BOARD_SIZE-1-i]==oppColor)
					form_key+=3^(i-1);
			}
		}
		keys[3] = form_key;
		
		//Make lines, calculate value
		
		//horizontal
		tempValue=lineMap[BOARD_SIZE][keys[0]][x].value;
		if (x==0 || x==BOARD_SIZE)
			tempValue*=0.5;
		value+=tempValue;
		
		//vertical
		tempValue=lineMap[BOARD_SIZE][keys[1]][y].value;
		if (y==0 || y==BOARD_SIZE)
			tempValue*=0.5;
		value+=tempValue;
		
		//positive diagonal
		if (x==y)
			tempValue=lineMap[BOARD_SIZE-1-Math.abs(x-y)][keys[2]][x].diagValue;
		else
			tempValue=lineMap[BOARD_SIZE-1-Math.abs(x-y)][keys[2]][(x<y):x?y].value;
		value+=tempValue;
		
		//negative diagonal
		if (x+y==BOARD_SIZE-1)
			tempValue=lineMap[x+y][keys[3]][x].diagValue;
		else
			tempValue=lineMap[x+y][keys[3]][(x+y<=BOARD_SIZE-1):x?(BOARD_SIZE-1-y)].value;
		value+=tempValue;
			
		return value;
	}
}
