import java.util.LinkedList;

public class BlokusPlayer
{
   public LinkedList<BlokusPiece> pieces;
   public boolean firstMove = true;
   public boolean canPlay = true;
   
   public BlokusPlayer(int color)
   {
      
      int[][][] shapes = BlokusPiece.getAllShapes();
      
      pieces = new LinkedList<BlokusPiece>();
      
      for (int i = 0; i < shapes.length; i++)
      {
         pieces.add(new BlokusPiece(shapes[i], color));
      }
   }
   
   public int getScore()
   {
      int total = 0;
      for (BlokusPiece bp : pieces)
      {
         total += bp.getPoints();
      }
      return total;
   }
}
