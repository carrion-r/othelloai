import java.util.ArrayList;

public class OthelloAIOne implements OthelloAI 
{
	private class MoveAndScore
	{
		public OthelloMove move;
		public int score;
		public MoveAndScore(OthelloMove move, int score)
		{
			this.move = move;
			this.score = score;
		}
	}
	
	@Override
	public OthelloMove chooseMove(OthelloGameState state) 
	{
		// TODO Auto-generated method stub
		return search(state, 4).move;
	}

	MoveAndScore search(OthelloGameState s, int depth)
	{
	    if (depth == 0 || s.gameIsOver())
	        return new MoveAndScore(null, s.getBlackScore() - s.getWhiteScore());
	    else
	    {
        	MoveAndScore bestMove = new MoveAndScore(null, 0);
            for(OthelloMove validMove : validMoves(s))
            {
    			OthelloGameState s2 = s.clone();
    			s2.makeMove(validMove.getRow(), validMove.getColumn());
    			int currentMove = search(s2, depth-1).score;
                if(bestMove.move == null 
                || (s.isBlackTurn() && currentMove > bestMove.score)
                || (!s.isBlackTurn() && currentMove < bestMove.score))
                {
                	bestMove.move = validMove;
                	bestMove.score = currentMove;
                }
                
            }
            return bestMove;
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
}
