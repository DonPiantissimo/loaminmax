package TucLoA;

public class ClusterMap {
	GamePosition gameposition, clusterposition;
	private byte BOARD_SIZE;
	private Piece pieces2[][];	
	private byte cluster_num = 0;
	private byte EMPTY;
	private byte cmEMPTY;
	PieceRank rank;
	
	
	public ClusterMap (GamePosition gamepos){
		gameposition = gamepos;
		BOARD_SIZE = gameposition.BOARD_SIZE;
		EMPTY = gameposition.EMPTY;
		cmEMPTY = 0;
		clusterposition = new GamePosition(cmEMPTY);
		
		pieces2 = new Piece[BOARD_SIZE][BOARD_SIZE];
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++)
				pieces2[i][j] = new Piece(i,j,EMPTY,-1);
	}
	
	public ClusterMap (GamePosition gamepos, PieceRank rank){
		gameposition = gamepos;
		this.rank = rank;
		BOARD_SIZE = gameposition.BOARD_SIZE;
		EMPTY = gameposition.EMPTY;
		cmEMPTY = 0;
		clusterposition = new GamePosition(cmEMPTY);
		
		pieces2 = new Piece[BOARD_SIZE][BOARD_SIZE];
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++)
				pieces2[i][j] = new Piece(i,j,EMPTY,-1);
	}
	
	public byte mapping(){
		byte max_cl=1;
		byte cl_pop;
		byte max_cl_pop = 0;
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++){
				if (clusterposition.getBoard()[i][j]==0 && gameposition.getBoard()[i][j]!=EMPTY){
					cluster_num++;
					clusterposition.getBoard()[i][j]=cluster_num;
					cl_pop=markCluster(cluster_num, i, j);
					if (cl_pop>max_cl_pop){
						max_cl_pop=cl_pop;
						max_cl=cluster_num;
					}
					
				}
			}
		return max_cl;
	}
	
	public byte mapping(byte colour){
		byte max_cl=1;
		byte cl_pop;
		byte max_cl_pop = 0;
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++){
				if (clusterposition.getBoard()[i][j]==0 && gameposition.getBoard()[i][j]==colour){
					cluster_num++;
					clusterposition.getBoard()[i][j]=cluster_num;
					cl_pop=markCluster(cluster_num, i, j);
					if (cl_pop>max_cl_pop){
						max_cl_pop=cl_pop;
						max_cl=cluster_num;
					}
					
				}
			}
		return max_cl;
	}
	
	public int min_distance(byte cl, byte colour){
		int dist;
		int tot_dist=0;
		byte cl_id;
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++){
				cl_id=clusterposition.getBoard()[i][j];
				if (cl_id!=0 && cl_id!=cl && gameposition.getBoard()[i][j]==colour){
					dist=cl_dist(cl,i,j);
					tot_dist+=dist;
					pieces2[i][j].setPiece(colour,dist);
				}
					
			}
		return tot_dist;
	}	
	
	public void order(byte cl, byte colour){
		int dist;
		byte cl_id;
		for (int i=0;i<BOARD_SIZE;i++)
			for (int j=0;j<BOARD_SIZE;j++){
				cl_id=clusterposition.getBoard()[i][j];
				if (gameposition.getBoard()[i][j]==colour){
					if (cl_id==cl){
						//System.out.println("Will set piece with "+i+","+j+","+colour+","+0);
						rank.setPiece(i,j,colour,0);
					}
					else if (cl_id!=0){
						dist=cl_dist(cl,i,j);
						//System.out.println("Will set piece with "+i+","+j+","+colour+","+dist);
						rank.setPiece(i,j,colour,dist);
					}
				}
			}
		
	}
	
	public int cl_dist(byte cl,int xpos,int ypos){
		for (int i=1;i<BOARD_SIZE-1;i++){
			
			for (int k=(ypos-i>0)?-i:0;k<i+1 && k+ypos<BOARD_SIZE;k++){
				if ((xpos+i<BOARD_SIZE && clusterposition.getBoard()[xpos+i][ypos+k]==cl) || (xpos-i>=0 && clusterposition.getBoard()[xpos-i][ypos+k]==cl))
					return i-1;
			}
		
			for (int k=(xpos-i+1>0)?-i+1:0;k<i && k+xpos<BOARD_SIZE;k++){
				if ((ypos+i<BOARD_SIZE && clusterposition.getBoard()[xpos+k][ypos+i]==cl) || (ypos-i>=0 && clusterposition.getBoard()[xpos+k][ypos-i]==cl))
					return i-1;
			}
			
			}
		return BOARD_SIZE-2;
			
	}
	
	public byte markCluster(byte cl, int xpos, int ypos){
		byte colour = gameposition.getBoard()[xpos][ypos];
		byte cl_pop=1;
		
		if (xpos>0 && ypos>0 && gameposition.getBoard()[xpos-1][ypos-1]==colour && clusterposition.getBoard()[xpos-1][ypos-1]==0){
			clusterposition.getBoard()[xpos-1][ypos-1]=cl;
			cl_pop+=markCluster(cl, xpos-1, ypos-1);
		}
		
		if (xpos>0 && gameposition.getBoard()[xpos-1][ypos]==colour && clusterposition.getBoard()[xpos-1][ypos]==0){
			clusterposition.getBoard()[xpos-1][ypos]=cl;
			cl_pop+=markCluster(cl, xpos-1, ypos);
		}
		
		if (xpos>0 && ypos<BOARD_SIZE-1 && gameposition.getBoard()[xpos-1][ypos+1]==colour && clusterposition.getBoard()[xpos-1][ypos+1]==0){
			clusterposition.getBoard()[xpos-1][ypos+1]=cl;
			cl_pop+=markCluster(cl, xpos-1, ypos+1);
		}
		
		if (ypos>0 && gameposition.getBoard()[xpos][ypos-1]==colour && clusterposition.getBoard()[xpos][ypos-1]==0){
			clusterposition.getBoard()[xpos][ypos-1]=cl;
			cl_pop+=markCluster(cl, xpos, ypos-1);
		}
		
		if (ypos<BOARD_SIZE-1 && gameposition.getBoard()[xpos][ypos+1]==colour && clusterposition.getBoard()[xpos][ypos+1]==0){
			clusterposition.getBoard()[xpos][ypos+1]=cl;
			cl_pop+=markCluster(cl, xpos, ypos+1);
		}
		
		if (xpos<BOARD_SIZE-1 && ypos>0 && gameposition.getBoard()[xpos+1][ypos-1]==colour && clusterposition.getBoard()[xpos+1][ypos-1]==0){
			clusterposition.getBoard()[xpos+1][ypos-1]=cl;
			cl_pop+=markCluster(cl, xpos+1, ypos-1);
		}
		
		if (xpos<BOARD_SIZE-1 && gameposition.getBoard()[xpos+1][ypos]==colour && clusterposition.getBoard()[xpos+1][ypos]==0){
			clusterposition.getBoard()[xpos+1][ypos]=cl;
			cl_pop+=markCluster(cl, xpos+1, ypos);
		}
		
		if (xpos<BOARD_SIZE-1 && ypos<BOARD_SIZE-1 && gameposition.getBoard()[xpos+1][ypos+1]==colour && clusterposition.getBoard()[xpos+1][ypos+1]==0){
			clusterposition.getBoard()[xpos+1][ypos+1]=cl;
			cl_pop+=markCluster(cl, xpos+1, ypos+1);
		}
		
		return cl_pop;
		
	}

	public Piece[][] getPieces2() {
		return pieces2;
	}

	public void setPieces2(Piece[][] pieces2) {
		this.pieces2 = pieces2;
	}
	
	void printCluster(){

		/* Print the upper section */
		System.out.print( "   " );
		for(int i = 0; i < BOARD_SIZE; i++ )
			System.out.print( i + " " );
		System.out.print( "\n +" );
		for(int i = 0; i < 2 * BOARD_SIZE + 1; i++ )
			System.out.print( "-" );
		System.out.print( "+\n" );

		/* Print board */
		for(int i = 0; i < BOARD_SIZE; i++ ){
			System.out.print( i + "| ");
			for(int j = 0; j < BOARD_SIZE; j++ )
				System.out.print(clusterposition.getBoard()[ i ][ j ]+" ");
			System.out.print( "|"+ i +"\n"  );
		}

		/* Print the lower section */
		System.out.print( " +" );
		for(int i = 0; i < 2 * BOARD_SIZE + 1; i++ )
			System.out.print( "-" );
		System.out.print( "+\n" );
		System.out.print( "   " );
		for(int i = 0; i < BOARD_SIZE; i++ )
			System.out.print( i +" ");
		System.out.print( "\n" );

	}

	public byte getCluster_num() {
		return cluster_num;
	}
	
	
	
}
