import java.util.ArrayList;
/* AlphaBeta http://en.wikipedia.org/wiki/Alpha-beta_pruning and Heuristic search
 Alpha beta prunning depth of seven
 minMax algorithm has a search depth of 5
*/

public class OthelloAIStudent implements OthelloAI 
{
	private boolean isBlack;
	
	@Override
	public OthelloMove chooseMove(OthelloGameState state) 
	{
	  
	  isBlack = state.isBlackTurn(); //color
	  return alphaBeta(state, null, null, System.currentTimeMillis(), eval.GreedyWinCornersMobility, 6).move;
		
		
	}
	
	private enum eval
	{
		Greedy,
		GreedyWin, 
		GreedyWinCorners, 
		GreedyWinCornersMobility 
		
	}
	
	private class tallyScore
	{
		public OthelloMove move;
		int score;
		public tallyScore(OthelloMove move, int score)
		{
			this.move = move;
			this.score = score;
		}
	}

	/* naive minMax search 
	 *Attempts to find the max score, if it can't it will find during its turn
	 * */
	tallyScore minMaxSearch(OthelloGameState gameState, eval e, int depth)
	{
	    if (depth == 0 || gameState.gameIsOver())
	    	return new tallyScore(null, evaluate(gameState, e));
    	tallyScore topMove = new tallyScore(null, 0);
    	
        for(OthelloMove validMove : validMoves(gameState))
        {
     
			OthelloGameState segundoestado = gameState.clone(); 
			
			segundoestado.makeMove(validMove.getRow(), validMove.getColumn());
			
			int currentMove = minMaxSearch(segundoestado, e, depth-1).score;
			
            if(topMove.move == null || (isBlack == gameState.isBlackTurn() && currentMove > topMove.score)
            || (isBlack != gameState.isBlackTurn() && currentMove < topMove.score))
            {
            	topMove.move = validMove;
            	topMove.score = currentMove;
            }
            
        }
     
        return topMove;
	}
	
	
	tallyScore alphaBeta(OthelloGameState s, Integer alpha, Integer beta, long start, eval e, int depth)
	{ 
		if (depth == 0 || s.gameIsOver())
	    	return new tallyScore(null, evaluate(s, e));
		
		OthelloMove best = new OthelloMove(0,0); //start at the beginning
		
		//Maximize
    	if(isBlack == s.isBlackTurn())
    	{
    		//Traverse children
    		boolean shouldBreak = false;
    		for(OthelloMove validMove : validMoves(s))
            {
    			if(!shouldBreak)
    			{
    				//clone the state and make a move
	    			OthelloGameState s2 = s.clone();
	    			s2.makeMove(validMove.getRow(), validMove.getColumn());
	    			int current = alphaBeta(s2, alpha, beta, start, e, depth-1).score;
	    			
	    			//max score
	    			if(alpha == null || current > alpha)
	    			{
	    				best = validMove;
	    				alpha = current;
	    			}
	    			
	    			
	    			if(beta != null && alpha != null && beta <= alpha) 
	    				shouldBreak = true;
    			}
    			
    			if(System.currentTimeMillis() - start > 4500)
    			{
    			
    				if(alpha == null) alpha = 0;
    				if(best != null)
    					return new tallyScore(best, alpha);
    				else
    					return new tallyScore(validMove, alpha);
    			}
            }
    		return new tallyScore(best, alpha);
    	}
    	
    	//Minimize; same commentary as above, but find the minimum score
    	else
    	{
    		boolean shouldBreak = false;
    		for(OthelloMove validMove : validMoves(s))
            {
    			if(!shouldBreak)
    			{
	    			OthelloGameState s2 = s.clone();
	    			s2.makeMove(validMove.getRow(), validMove.getColumn());
	    			int current = alphaBeta(s2, alpha, beta, start, e, depth-1).score;
	    			if(beta == null || current < beta)
	    			{
	    				best = validMove;
	    				beta = current;
	    			}
	    			
	    			if(beta != null && alpha != null && beta <= alpha) 
	    				shouldBreak = true;
    			}
    			if( (System.currentTimeMillis() - start ) > 4500)
    			{;
    				if(beta == null) beta = 0;
    				if(best != null)
    					return new tallyScore(best, beta);
    				else
    					return new tallyScore(validMove, beta);
    			}
            }
    		return new tallyScore(best, beta);
    	}
	}
	
	
	ArrayList<OthelloMove> validMoves(OthelloGameState s)
	{
		ArrayList<OthelloMove> moves = new ArrayList<OthelloMove>();
		for(int i=0;i<8;i++)
        	for(int j=0;j<8;j++)
        		if(s.isValidMove(i, j))
        			moves.add(new OthelloMove(i, j));
		return moves;
	}
	
	int evaluate(OthelloGameState s, eval e)
	{
    	if(e == eval.Greedy)
    		return greedyEval(s);
    	else if(e == eval.GreedyWin)
    		return evalWin(s);
    	else if(e == eval.GreedyWinCorners)
    		return evalCorners(s);
    	else
    		return evalMobility(s);
	}
	
	int greedyEval(OthelloGameState s)
	{
		//If I am black, return (1) * (Black Score - White Score)
		//If I am white, return (-1) * (Black Score - White Score)
		return ( ( isBlack ? 1 : -1 ) * (s.getBlackScore() - s.getWhiteScore() ) );
	}
	

	int evalWin(OthelloGameState s)
	{
		
		int evaluation = greedyEval(s);
		
		if(s.gameIsOver())
		{
		
			if(isBlack)
			{
				if(s.getBlackScore() > s.getWhiteScore())
					evaluation += 111111111;
				if(s.getBlackScore() < s.getWhiteScore())
					evaluation -= 111111111;
			}
			
			else
			{
				if(s.getBlackScore() < s.getWhiteScore())
					evaluation += 999999999;
				if(s.getBlackScore() > s.getWhiteScore())
					evaluation -= 999999999;
			}
		}
		return evaluation;
	}
	
	
	int evalCorners(OthelloGameState s)
	{
		int evaluation = evalWin(s);

		//if I already know this game state is a win/loss, 
		//why bother checking anything else?
		if(Math.abs(evaluation) < 900000000)
		{
			if(s.getCell(0, 0) == OthelloCell.BLACK  && isBlack || s.getCell(0, 0) == OthelloCell.WHITE && !isBlack)
				evaluation +=10000;
			if(s.getCell(7, 0) == OthelloCell.BLACK  && isBlack || s.getCell(7, 0) == OthelloCell.WHITE && !isBlack)
				evaluation +=10000;
			if(s.getCell(0, 7) == OthelloCell.BLACK  && isBlack || s.getCell(0, 7) == OthelloCell.WHITE && !isBlack)
				evaluation +=10000;
			if(s.getCell(7, 7) == OthelloCell.BLACK  && isBlack || s.getCell(7, 7) == OthelloCell.WHITE && !isBlack)
				evaluation +=10000;
			
			if(s.getCell(0, 0) == OthelloCell.WHITE  && isBlack || s.getCell(0, 0) == OthelloCell.BLACK && !isBlack)
				evaluation -=20000;
			if(s.getCell(7, 0) == OthelloCell.WHITE  && isBlack || s.getCell(7, 0) == OthelloCell.BLACK && !isBlack)
				evaluation -=20000;
			if(s.getCell(0, 7) == OthelloCell.WHITE  && isBlack || s.getCell(0, 7) == OthelloCell.BLACK && !isBlack)
				evaluation -=20000;
			if(s.getCell(7, 7) == OthelloCell.WHITE  && isBlack || s.getCell(7, 7) == OthelloCell.BLACK && !isBlack)
				evaluation -=20000;
		}
		return evaluation;
	}

	
	int evalMobility(OthelloGameState s)
	{
		int evaluation = evalCorners(s);
		
		if(Math.abs(evaluation) < 900000000) 

			evaluation += ( isBlack == s.isBlackTurn() ? 1: -1) * validMoves(s).size() * 5;
		
		return evaluation;
	}

	
}
