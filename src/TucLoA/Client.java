package TucLoA;

import java.io.IOException;
//created by ntaklas
import java.util.Random;

public class Client {
	private Communication comm;
	private GamePosition gamePosition;
	private byte myColor;
	private Moves_and_Checks mac;
	
	private final byte NM_NEW_POSITION = 1;
	private final byte NM_COLOR_W = 2;
	private final byte NM_COLOR_B = 3;
	private final byte NM_REQUEST_MOVE = 4;
	private final byte NM_PREPARE_TO_RECEIVE_MOVE = 5;
	private final byte NM_REQUEST_NAME = 6;
	private final byte NM_QUIT = 7;
	
	private final double ENDGAME_START = 0.6;
	
	private byte minimaxMove[] = new byte[4];
	
	private PieceRank initRank[];
	private int pieceCount[];
	
	private boolean endgame = false;
	
	public Client(){
		try{
			comm = new Communication("127.0.0.1", 6001);
			gamePosition = new GamePosition();
			mac = new Moves_and_Checks();
		}catch (IOException e) {
			System.out.println("No server found!!");
			System.exit(1);
		}
		
		initRank = new PieceRank[2];
		pieceCount = new int[2];
	}
	
	
	public void run() {
		int msg;
		
		ClusterMap initCm;
		byte cl;
		double groupingRatio;
		
		try {
			while (true) {
				msg = comm.recvMsg();
				System.out.println("msg: "+msg);
				switch (msg) {
				case NM_REQUEST_NAME:
					comm.sendName("MyJAgent");
					break;
					
				case NM_NEW_POSITION:
					comm.getPosition(gamePosition);
					mac.printPosition(gamePosition);
					break;
					
				case NM_COLOR_W:
					myColor = gamePosition.WHITE;
					break;
					
				case NM_COLOR_B:
					myColor = gamePosition.BLACK;
					break;
				
				case NM_PREPARE_TO_RECEIVE_MOVE:
					byte b[] = comm.getMove();
					mac.doMove(gamePosition, b);
					mac.printPosition(gamePosition);
					break;
					
				case NM_REQUEST_MOVE:
					byte myMove[] = new byte[4];
					int i,j,k,l;
					Random rand = new Random(System.currentTimeMillis());
					
					if( !mac.canMove( gamePosition, myColor ) )
					{
						myMove[ 0 ] = -1;		//null move
					}
					else
					{
						initRank[gamePosition.getTurn()] = new PieceRank(gamePosition.BOARD_SIZE);
						initCm = new ClusterMap(gamePosition, initRank[gamePosition.getTurn()]);
						cl = initCm.mapping(gamePosition.getTurn());
						initCm.order(cl, gamePosition.getTurn());
						pieceCount[gamePosition.getTurn()] = initRank[gamePosition.getTurn()].makeOrder();
						
						groupingRatio = initRank[gamePosition.getTurn()].groupingRatio();
						
						System.out.println(groupingRatio);
						if (groupingRatio>ENDGAME_START)
							endgame=true;
						
						initRank[gamePosition.getOppTurn()] = new PieceRank(gamePosition.BOARD_SIZE);
						initCm = new ClusterMap(gamePosition, initRank[gamePosition.getOppTurn()]);
						cl = initCm.mapping(gamePosition.getOppTurn());
						initCm.order(cl,gamePosition.getOppTurn());
						pieceCount[gamePosition.getOppTurn()] = initRank[gamePosition.getOppTurn()].makeOrder();
						
						minimax(4,-1000);
						System.arraycopy(minimaxMove,0,myMove,0,4);

					}

					comm.sendMove( myMove );			//send our move
					mac.doMove( gamePosition, myMove );		//play our move on our position
					mac.printPosition( gamePosition );
					break;
					
				case NM_QUIT:
					System.out.println("The end!!!");
					System.exit(0);
					break;
					
				default:
					System.exit(1);
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR : Network problem!!!");
			System.exit(1);
		}
	}
	
	public int minimax(int order, int prune){
		byte myMove[] = new byte[4], tempMove[] = {-1,0,0,0};
		byte myColor = gamePosition.getTurn();
		int BOARD_SIZE = gamePosition.BOARD_SIZE, horPieces, verPieces, diagPosPieces, diagNegPieces, k, l,dist, minDist=1000,i, j;
		for (int piece=0;piece<pieceCount[myColor];piece++){
					i=initRank[myColor].getXpos(piece);
					j=initRank[myColor].getYpos(piece);
					
					//get horizontal moves
					horPieces = 0;
					for( k = 0; k < gamePosition.BOARD_SIZE ; k++ )
					{
						if( gamePosition.getBoard()[i][k] == gamePosition.WHITE || gamePosition.getBoard()[i][k] == gamePosition.BLACK )
							horPieces++;
					}
					
					//get vertical moves
					
					verPieces = 0;
					for( k = 0; k < gamePosition.BOARD_SIZE ; k++ )
					{
						if( gamePosition.getBoard()[k][j] == gamePosition.WHITE || gamePosition.getBoard()[k][j] == gamePosition.BLACK )
							verPieces++;
					}
					
					//get positive diagonal moves
					
					diagPosPieces = 0;
					if( i > j )
					{
						k = i - j;
						l = 0;
					}
					else
					{
						k = 0;
						l = j - i;
					}
	
					for( ; k < gamePosition.BOARD_SIZE && l < gamePosition.BOARD_SIZE ; k++, l++ )
					{
						if( gamePosition.getBoard()[k][l] == gamePosition.WHITE || gamePosition.getBoard()[k][l] == gamePosition.BLACK )
							diagPosPieces++;
					}
					
					//get negative diagonal moves
					
					diagNegPieces = 0;
					if( i + j > (gamePosition.BOARD_SIZE - 1) )
					{
						k = gamePosition.BOARD_SIZE-1;
						l = i + j - (gamePosition.BOARD_SIZE - 1);
					}
					else
					{
						k = i + j;
						l = 0;
					}
	
					for( ; k >= 0 && l < gamePosition.BOARD_SIZE ; k--, l++ )
					{
						if( gamePosition.getBoard()[k][l] == gamePosition.WHITE || gamePosition.getBoard()[k][l] == gamePosition.BLACK )
							diagNegPieces++;
					}
					
					myMove[0] = (byte) i;
					myMove[1] = (byte) j;
					
					//horizontal expand
					myMove[2] = (byte) i;
					myMove[3] = (byte) (j+horPieces);
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
					
					myMove[2] = (byte) i;
					myMove[3] = (byte) (j-horPieces);
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
					
					//vertical expand
					myMove[2] = (byte) (i+verPieces);
					myMove[3] = (byte) j;
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
	
					myMove[2] = (byte) (i-verPieces);
					myMove[3] = (byte) j;
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
					
					//positive diagonal expand
					myMove[2] = (byte) (i+diagPosPieces);
					myMove[3] = (byte) (j+diagPosPieces);
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
	
					myMove[2] = (byte) (i-diagPosPieces);
					myMove[3] = (byte) (j-diagPosPieces);
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
					
					//negative diagonal expand
					myMove[2] = (byte) (i-diagNegPieces);
					myMove[3] = (byte) (j+diagNegPieces);
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
	
					myMove[2] = (byte) (i+diagNegPieces);
					myMove[3] = (byte) (j-diagNegPieces);
					if ((dist=expandMove(myMove, order, -minDist))<minDist){
						if (dist<prune)
							return dist;
						minDist=dist;
						System.arraycopy(myMove,0,tempMove,0,4);
					}
				}
		System.arraycopy(tempMove, 0, minimaxMove, 0, 4);
		return minDist;
	}
	
	public int minimaxEdge(int prune){
		byte myMove[] = new byte[4], tempMove[] = new byte[4];
		byte myColor = gamePosition.getTurn(), oppColor = gamePosition.getOppTurn(), cl, cl_opp;
		int BOARD_SIZE = gamePosition.BOARD_SIZE, horPieces, verPieces, diagPosPieces, diagNegPieces, k, l,dist, minDist=1000, pieceDist, subPieceDist,i,j;
		ClusterMap cm = new ClusterMap(gamePosition), cmopp = new ClusterMap(gamePosition);
		cl=cm.mapping(myColor);
		cl_opp=cmopp.mapping(oppColor);
		int initOwnDist = cm.min_distance(cl, myColor);
		int initOppDist = cmopp.min_distance(cl, oppColor);
		int initDist = initOwnDist - initOppDist;
		int endgameDist;
		//cm.printCluster();
		for (int piece=0;piece<pieceCount[myColor];piece++){
			i=initRank[myColor].getXpos(piece);
			j=initRank[myColor].getYpos(piece);
					
					subPieceDist = initDist - cm.getPieces2()[i][j].distance;
					
					//get horizontal moves
					horPieces = 0;
					for( k = 0; k < gamePosition.BOARD_SIZE ; k++ )
					{
						if( gamePosition.getBoard()[i][k] == gamePosition.WHITE || gamePosition.getBoard()[i][k] == gamePosition.BLACK )
							horPieces++;
					}
					
					//get vertical moves
					
					verPieces = 0;
					for( k = 0; k < gamePosition.BOARD_SIZE ; k++ )
					{
						if( gamePosition.getBoard()[k][j] == gamePosition.WHITE || gamePosition.getBoard()[k][j] == gamePosition.BLACK )
							verPieces++;
					}
					
					//get positive diagonal moves
					
					diagPosPieces = 0;
					if( i > j )
					{
						k = i - j;
						l = 0;
					}
					else
					{
						k = 0;
						l = j - i;
					}
	
					for( ; k < gamePosition.BOARD_SIZE && l < gamePosition.BOARD_SIZE ; k++, l++ )
					{
						if( gamePosition.getBoard()[k][l] == gamePosition.WHITE || gamePosition.getBoard()[k][l] == gamePosition.BLACK )
							diagPosPieces++;
					}
					
					//get negative diagonal moves
					
					diagNegPieces = 0;
					if( i + j > (gamePosition.BOARD_SIZE - 1) )
					{
						k = gamePosition.BOARD_SIZE-1;
						l = i + j - (gamePosition.BOARD_SIZE - 1);
					}
					else
					{
						k = i + j;
						l = 0;
					}
	
					for( ; k >= 0 && l < gamePosition.BOARD_SIZE ; k--, l++ )
					{
						if( gamePosition.getBoard()[k][l] == gamePosition.WHITE || gamePosition.getBoard()[k][l] == gamePosition.BLACK )
							diagNegPieces++;
					}
				
					myMove[0] = (byte) i;
					myMove[1] = (byte) j;
					
					//horizontal expand
					myMove[2] = (byte) i;
					myMove[3] = (byte) (j+horPieces);
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
					
					myMove[2] = (byte) i;
					myMove[3] = (byte) (j-horPieces);
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
					
					//vertical expand
					myMove[2] = (byte) (i+verPieces);
					myMove[3] = (byte) j;
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
	
					myMove[2] = (byte) (i-verPieces);
					myMove[3] = (byte) j;
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
					
					//positive diagonal expand
					myMove[2] = (byte) (i+diagPosPieces);
					myMove[3] = (byte) (j+diagPosPieces);
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
	
					myMove[2] = (byte) (i-diagPosPieces);
					myMove[3] = (byte) (j-diagPosPieces);
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
					
					//negative diagonal expand
					myMove[2] = (byte) (i-diagNegPieces);
					myMove[3] = (byte) (j+diagNegPieces);
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
	
					myMove[2] = (byte) (i+diagNegPieces);
					myMove[3] = (byte) (j-diagNegPieces);
					if (mac.isLegalJump(gamePosition, myMove)){
						dist=subPieceDist + cm.cl_dist(cl, myMove[2], myMove[3]);
						if (dist+initOppDist==0)
							return -1000;
						if (dist<prune)
							return dist;
						if (gamePosition.getBoard()[myMove[2]][myMove[3]]==oppColor)
							dist+=cmopp.getPieces2()[myMove[2]][myMove[3]].color;
						if (dist<minDist){
							minDist=dist;
							System.arraycopy(myMove,0,tempMove,0,4);
						}
					}
				}
		System.arraycopy(tempMove, 0, minimaxMove, 0, 4);
		return minDist;
	}
	
	public int expandMove(byte moveToDo[], int order, int prune){
		boolean replaced, win=false;
		int dist=1000;
		if( mac.isLegalJump( gamePosition, moveToDo ) ){
			replaced=mac.doMoveCheck(gamePosition, moveToDo);
			if (endgame)
				win = mac.checkWin(gamePosition, gamePosition.getOppTurn());
			if (win)
				dist = -1000;
			else if (order>0)
				dist = -minimax(order-1, prune);
			else
				dist = -minimaxEdge(prune);
			mac.undoMove(gamePosition, moveToDo, replaced);
		}
		return dist;
	}
	
	
	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}

}
