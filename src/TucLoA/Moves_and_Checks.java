package TucLoA;

public class Moves_and_Checks {
	
	
	void printBoard( GamePosition gamePosition ){

		/* Print the upper section */
		System.out.print( "   " );
		for(int i = 0; i < gamePosition.BOARD_SIZE; i++ )
			System.out.print( i + " " );
		System.out.print( "\n +" );
		for(int i = 0; i < 2 * gamePosition.BOARD_SIZE + 1; i++ )
			System.out.print( "-" );
		System.out.print( "+\n" );

		/* Print board */
		for(int i = 0; i < gamePosition.BOARD_SIZE; i++ ){
			System.out.print( i + "| ");
			for(int j = 0; j < gamePosition.BOARD_SIZE; j++ )
				switch( gamePosition.getBoard()[ i ][ j ] ){
					case 0:
						System.out.print( "W " );
						break;
					case 1:
						System.out.print( "B " );
						break;
					case 2:
						System.out.print( ". " );
						break;
					case 3:
						System.out.print( "# " );
						break;
					default:
						System.out.print("ERROR: Unknown character in board (printBoard)\n");
						System.exit( 1 );
				}
			System.out.print( "|"+ i +"\n"  );
		}

		/* Print the lower section */
		System.out.print( " +" );
		for(int i = 0; i < 2 * gamePosition.BOARD_SIZE + 1; i++ )
			System.out.print( "-" );
		System.out.print( "+\n" );
		System.out.print( "   " );
		for(int i = 0; i < gamePosition.BOARD_SIZE; i++ )
			System.out.print( i +" ");
		System.out.print( "\n" );

	}

	void printPosition( GamePosition gamePosition ){
		//board
		printBoard( gamePosition );

		//turn
		if( gamePosition.getTurn() == gamePosition.WHITE )
			System.out.print( "Turn: WHITE" );
		else if( gamePosition.getTurn() == gamePosition.BLACK )
			System.out.print( "Turn: BLACK" );
		else
			System.out.print( "Turn: -" );

		System.out.print( "\n" );

	}
	
	public void doMove( GamePosition gamePosition, byte moveToDo[] ){
		

		if( moveToDo[ 0 ] != -1 )
		{
			gamePosition.getBoard()[ moveToDo[ 0 ] ][  moveToDo[ 1 ]  ] = gamePosition.EMPTY;	//remove piece
			gamePosition.getBoard()[ moveToDo[ 2 ] ][  moveToDo[ 3 ]  ] = gamePosition.getTurn();	//place piece
		
		}
			/* change turn - even in case of a null move */
		if(gamePosition.getTurn() == gamePosition.WHITE)
			gamePosition.setTurn(gamePosition.BLACK);
		else
			gamePosition.setTurn(gamePosition.WHITE);
	
	}
	
	public boolean doMoveCheck( GamePosition gamePosition, byte moveToDo[] ){
		
		boolean replaced=false;
		if( moveToDo[ 0 ] != -1 )
		{
			gamePosition.getBoard()[ moveToDo[ 0 ] ][  moveToDo[ 1 ]  ] = gamePosition.EMPTY;	//remove piece
			if (gamePosition.getBoard()[ moveToDo[ 2 ] ][  moveToDo[ 3 ]  ] != gamePosition.EMPTY)
				replaced = true; //check if a piece was replaced
			gamePosition.getBoard()[ moveToDo[ 2 ] ][  moveToDo[ 3 ]  ] = gamePosition.getTurn();	//place piece
		
		}
			/* change turn - even in case of a null move */
		if(gamePosition.getTurn() == gamePosition.WHITE)
			gamePosition.setTurn(gamePosition.BLACK);
		else
			gamePosition.setTurn(gamePosition.WHITE);
		return replaced;
	
	}
	
	public boolean undoMove( GamePosition gamePosition, byte moveToDo[], boolean replaced ){
		
		if( moveToDo[ 0 ] != -1 )
		{
			gamePosition.getBoard()[ moveToDo[ 0 ] ][  moveToDo[ 1 ]  ] = gamePosition.getOppTurn();	//return piece to initial position
			if (replaced)
				gamePosition.getBoard()[ moveToDo[ 2 ] ][  moveToDo[ 3 ]  ] = gamePosition.getTurn();	//return captured piece
			else
				gamePosition.getBoard()[ moveToDo[ 2 ] ][  moveToDo[ 3 ]  ] = gamePosition.EMPTY;
		
		}
			/* change turn - even in case of a null move */
		if(gamePosition.getTurn() == gamePosition.WHITE)
			gamePosition.setTurn(gamePosition.BLACK);
		else
			gamePosition.setTurn(gamePosition.WHITE);
		return replaced;
	
	}
	

		
	public boolean isLegalJump( GamePosition gamePosition, byte moveToCheck[] ){

		int i,j;
		int stepX, stepY;
		int pieces = 0;
		int pathLength = 0;
		boolean insidePathFlag = false;

		if( moveToCheck[ 0 ] == -1 )
		{
			return false;
		}

		/* first coordinates must be inside board */
		if( ( moveToCheck[ 0 ] < 0 ) || ( moveToCheck[ 0 ] >= gamePosition.BOARD_SIZE ) )
			return false;
		if( ( moveToCheck[ 1 ] < 0 ) || ( moveToCheck[ 1 ] >= gamePosition.BOARD_SIZE ) )
			return false;

		/* piece must be ours */
		if( gamePosition.getBoard()[ moveToCheck[ 0 ] ][ moveToCheck[ 1 ] ] != gamePosition.getTurn() )
			return false;


		/* second coordinates must be inside board */
		if( ( moveToCheck[ 2 ] < 0 ) || ( moveToCheck[ 2] >= gamePosition.BOARD_SIZE ) )
			return false;
		if( ( moveToCheck[ 3 ] < 0 ) || ( moveToCheck[ 3 ] >= gamePosition.BOARD_SIZE ) )
			return false;

		/* square must be empty or enemy - also takes care of the possibility the two coordinates pointing at the same tile */
		if( (gamePosition.getBoard()[ moveToCheck[ 2 ] ][ moveToCheck[ 3 ] ] != gamePosition.EMPTY) && (gamePosition.getBoard()[ moveToCheck[ 2 ] ][ moveToCheck[ 3 ] ] != 1-gamePosition.getTurn() ) )
			return false;


		//check all four directions for a match
		if( moveToCheck[ 0 ] == moveToCheck[ 2 ] )
		// if horizontal	(x1 == x2)
		{
			stepX = 0;
			stepY = 1;
			
			i = moveToCheck[ 0 ];
			j = 0;
		}
		else if( moveToCheck[ 1 ] == moveToCheck[ 3 ] )
		//if vertical	(y1 == y2)
		{
			stepX = 1;
			stepY = 0;
			
			i = 0;
			j = moveToCheck[ 1 ];
		}
		else if( (moveToCheck[ 0 ] - moveToCheck[ 1 ]) == (moveToCheck[ 2 ] - moveToCheck[ 3 ]) )
		//if first diagonal	(x1-y1 == x2-y2)
		{
			stepX = 1;
			stepY = 1;

			if( moveToCheck[ 0 ] > moveToCheck[ 1 ] )
			{
				i = moveToCheck[ 0 ] - moveToCheck[ 1 ];
				j = 0;
			}
			else
			{
				i = 0;
				j = moveToCheck[ 1 ] - moveToCheck[ 0 ];
			}
		}
		else if( (moveToCheck[ 0 ] + moveToCheck[ 1 ]) == (moveToCheck[ 2 ] + moveToCheck[ 3 ]) )
		//if second diagonal	(x1+y1 == x2+y2)
		{
			stepX = -1;
			stepY = 1;

			if( (moveToCheck[ 0 ] + moveToCheck[ 1 ]) > (gamePosition.BOARD_SIZE-1) )
			{
				i = gamePosition.BOARD_SIZE - 1;
				j = (moveToCheck[ 0 ] + moveToCheck[ 1 ]) - (gamePosition.BOARD_SIZE - 1);
			}
			else
			{
				i = moveToCheck[ 0 ] + moveToCheck[ 1 ];
				j = 0;
			}
		}
		else	//not in the same line
			return false;


		do	//for all the line of "action" (movement)
		{
			// count pieces
			if( gamePosition.getBoard()[i][j] == gamePosition.WHITE || gamePosition.getBoard()[i][j] == gamePosition.BLACK )
				pieces++;
			
			// if we reached source or destination tile
			if( (i == moveToCheck[ 0 ] && j == moveToCheck[ 1 ]) || (i == moveToCheck[ 2 ] && j == moveToCheck[ 3 ]) )
			{
				if(	insidePathFlag == false )
					insidePathFlag = true;
				else
					insidePathFlag = false;
			}
			
			if( insidePathFlag )
			{
				// if we find enemy inside the path and it's not the destination tile.. then jump is illegal
				if( ( gamePosition.getBoard()[i][j] == 1- gamePosition.getTurn() ) && (i != moveToCheck[ 2 ] || j != moveToCheck[ 3 ]) )
					return false;
				pathLength++;
			}
			
			i += stepX;
			j += stepY;
		}
		while( i >= 0 && i < gamePosition.BOARD_SIZE && j >= 0 && j < gamePosition.BOARD_SIZE );	//while still inside board
		
		if( pathLength == pieces )
			return true;
		else
			return false;

	}

	
	public boolean canMove( GamePosition gamePosition, byte player ){
		byte i, j;
		byte k, l;
		byte pieces;
		byte tempMove[] = new byte[4];

		for( i = 0; i < gamePosition.BOARD_SIZE; i++ )
		{
			for( j = 0; j < gamePosition.BOARD_SIZE; j++ )
			{
				if( gamePosition.getBoard()[ i ][ j ] == player )		//when we find a piece of ours
				{
					tempMove[0] = i;
					tempMove[1] = j;
					
					// check all four directions for a legal move
					
					// horizontal
					pieces = 0;
					for( k = 0; k < gamePosition.BOARD_SIZE ; k++ )
					{
						if( gamePosition.getBoard()[i][k] == gamePosition.WHITE || gamePosition.getBoard()[i][k] == gamePosition.BLACK )
							pieces++;
					}
					
					tempMove[2] = i;
					tempMove[3] = (byte) (j+pieces);
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					tempMove[2] = i;
					tempMove[3] = (byte) (j-pieces);
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					
					// vertical
					pieces = 0;
					for( k = 0; k < gamePosition.BOARD_SIZE ; k++ )
					{
						if( gamePosition.getBoard()[k][j] == gamePosition.WHITE || gamePosition.getBoard()[k][j] == gamePosition.BLACK )
							pieces++;
					}
					
					tempMove[2] = (byte) (i+pieces);
					tempMove[3] = j;
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					tempMove[2] = (byte) (i-pieces);
					tempMove[3] = j;
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					
					// first diagonal
					pieces = 0;
					if( i > j )
					{
						k = (byte) (i - j);
						l = 0;
					}
					else
					{
						k = 0;
						l = (byte) (j - i);
					}
					
					for( ; k < gamePosition.BOARD_SIZE && l < gamePosition.BOARD_SIZE ; k++, l++ )
					{
						if( gamePosition.getBoard()[k][l] == gamePosition.WHITE || gamePosition.getBoard()[k][l] == gamePosition.BLACK )
							pieces++;
					}
					
					tempMove[2] = (byte) (i+pieces);
					tempMove[3] = (byte) (j+pieces);
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					tempMove[2] = (byte) (i-pieces);
					tempMove[3] = (byte) (j-pieces);
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					
					// second diagonal
					pieces = 0;
					if( i + j > (gamePosition.BOARD_SIZE - 1) )
					{
						k = (byte) (gamePosition.BOARD_SIZE-1);
						l = (byte) (i + j - (gamePosition.BOARD_SIZE - 1));
					}
					else
					{
						k = (byte) (i + j);
						l = 0;
					}
					
					for( ; k >= 0 && l < gamePosition.BOARD_SIZE ; k--, l++ )
					{
						if( gamePosition.getBoard()[k][l] == gamePosition.WHITE || gamePosition.getBoard()[k][l] == gamePosition.BLACK )
							pieces++;
					}
					
					tempMove[2] = (byte) (i-pieces);
					tempMove[3] = (byte) (j+pieces);
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
					tempMove[2] = (byte) (i+pieces);
					tempMove[3] = (byte) (j-pieces);
					
					if( isLegalJump( gamePosition, tempMove ) )
						return true;
					
				}
			}
		}

		return false;

	}
	
	
	public boolean isLegal(GamePosition gamePosition, byte moveToCheck[] )
	{

		if( !canMove( gamePosition, gamePosition.getTurn() ) )	//if that player cannot move, the only legal move is null
		{
			if( moveToCheck[ 0 ] == -1 )
				return true;
			else
				return false;
		}
		else
			return isLegalJump( gamePosition, moveToCheck );

	}
	
	public boolean checkWin(GamePosition gamePosition, byte color){
		ClusterMap cm;
		cm = new ClusterMap(gamePosition);
		cm.mapping(color);
		if (cm.getCluster_num()==1)
			return true;
		return false;
	}
	
}
