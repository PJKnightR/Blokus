import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.imageio.*;
import java.io.File;

public class Blokus
{
   public static final int NUM_PLAYERS = 2;
   
   public static class BlokusFrame extends JFrame
   {
      private BlokusBoard board;
      private BlokusPlayer[] players;
      private int turn = -1;
      
      private int pieceIndex;
      private Point selected;
      
      private JPanel mainPanel;
      private JPanel sidePanel;
      private JPanel piecesPanel;
      private JPanel boardPanel;
      private JLabel label;
      private ImageIcon boardImage;
      private JButton surrender;
      
      public BlokusFrame()
      {
         super("Blokus");
         board = new BlokusBoard();
         players = new BlokusPlayer[NUM_PLAYERS];
         players[0] = new BlokusPlayer(BlokusBoard.BLUE);
         //players[1] = new BlokusPlayer(BlokusBoard.RED);
         players[1] = new BlokusPlayer(BlokusBoard.YELLOW);
         //players[3] = new BlokusPlayer(BlokusBoard.GREEN);
         
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         initializeGUI();
         startNewTurn();
      }
      
      private void initializeGUI()
      {
         class BoardClickListener implements MouseListener, MouseMotionListener, MouseWheelListener
         {
            public void mouseClicked(MouseEvent e)
            {
               if (e.getButton() == MouseEvent.BUTTON3)
               {
                  flipPiece();
               }
               else
               {
                  try
                  {
                     board.placePiece(players[turn].pieces.get(
                        pieceIndex), selected.x - BlokusPiece.SHAPE_SIZE / 2, 
                        selected.y - BlokusPiece.SHAPE_SIZE / 2, players[turn].firstMove);
                     drawBoard();
                     players[turn].pieces.remove(pieceIndex);
                     players[turn].firstMove = false;
                     players[turn].canPlay = players[turn].pieces.size() != 0;
                     startNewTurn();
                  }
                  catch (BlokusBoard.IllegalMoveException ex)
                  {
                     displayMessage(ex.getMessage(), "Illegal Move!");
                  }
               }
            }
            
            public void mousePressed(MouseEvent e)
            {
               
            }
            
            public void mouseReleased(MouseEvent e)
            {
               
            }
            
            public void mouseEntered(MouseEvent e)
            {
               
            }
            
            public void mouseExited(MouseEvent e)
            {
               selected = null;
               board.resetOverlay();
               drawBoard();
            }
            
            public void mouseDragged(MouseEvent e)
            {
            
            }
            
            public void mouseMoved(MouseEvent e)
            {
               Point p = board.getCoordinates(e.getPoint(), BlokusBoard.DEFAULT_RESOLUTION);
               if (!p.equals(selected))
               {
                  selected = p;
                  board.overlay(players[turn].pieces.get(pieceIndex), selected.x, selected.y);
                  drawBoard();
               }
            }
            
            public void mouseWheelMoved(MouseWheelEvent e)
            {
               if (e.getWheelRotation() > 0)
               {
                  rotateClockwise();
               }
               else
               {
                  rotateCounterClockwise();
               }
            }
         }
         
         class SurrenderListener implements ActionListener
         {
            public void actionPerformed(ActionEvent event)
            {
               players[turn].canPlay = false;
               startNewTurn();
            }
         }
         
         mainPanel = new JPanel();
         piecesPanel = new JPanel();
         piecesPanel.setLayout(new BoxLayout(piecesPanel, BoxLayout.PAGE_AXIS));
         
         JScrollPane jsp = new JScrollPane(piecesPanel);
         jsp.getVerticalScrollBar().setUnitIncrement(BlokusPiece.DEFAULT_RESOLUTION / 3);
         //ugly
         jsp.setPreferredSize(new Dimension(BlokusPiece.DEFAULT_RESOLUTION - 80, BlokusBoard.DEFAULT_RESOLUTION - 30));

         
         surrender = new JButton("Surrender");
         surrender.setPreferredSize(new Dimension(BlokusPiece.DEFAULT_RESOLUTION, 30));
         surrender.addActionListener(new SurrenderListener());
         
         sidePanel = new JPanel();
         sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
         
         
         boardPanel = new JPanel();
         
         boardImage = new ImageIcon(board.render());
         
         label = new JLabel(boardImage);
         BoardClickListener bcl = new BoardClickListener();
         label.addMouseListener(bcl);
         label.addMouseMotionListener(bcl);
         label.addMouseWheelListener(bcl);
         
         boardPanel.add(label);
         
         sidePanel.add(jsp);
         sidePanel.add(surrender);
         mainPanel.add(sidePanel);
         mainPanel.add(boardPanel);
         
         getContentPane().add(mainPanel);
         pack();
         setVisible(true);
      }
      
      private void rotateClockwise()
      {
         players[turn].pieces.get(pieceIndex).rotateClockwise();
         board.overlay(players[turn].pieces.get(pieceIndex), selected.x, selected.y);
         drawBoard();
      }
      
      private void rotateCounterClockwise()
      {
         players[turn].pieces.get(pieceIndex).rotateCounterClockwise();
         board.overlay(players[turn].pieces.get(pieceIndex), selected.x, selected.y);
         drawBoard();
      }
      
      private void flipPiece()
      {
         players[turn].pieces.get(pieceIndex).flipOver();
         board.overlay(players[turn].pieces.get(pieceIndex), selected.x, selected.y);
         drawBoard();
      }
      
      private void drawBoard()
      {
         boardImage.setImage(board.render());
         label.repaint();
      }
      
      private void drawBorder()
      {
         JComponent piece = (JComponent) piecesPanel.getComponent(pieceIndex);
         piece.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
      }
      
      private void clearBorder()
      {
         JComponent piece = (JComponent) piecesPanel.getComponent(pieceIndex);
         piece.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      }
      
      private void displayMessage(String message, String title)
      {
         JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
      }
      
      private class PieceLabelClickListener implements MouseListener
      {
         public void mouseClicked(MouseEvent e)
         {
            
            BlokusPieceLabel bp = (BlokusPieceLabel) e.getComponent();
            clearBorder();
            pieceIndex = bp.pieceIndex;
            drawBorder();
         }
         
         public void mousePressed(MouseEvent e)
         {
            
         }
         
         public void mouseReleased(MouseEvent e)
         {
            
         }
         
         public void mouseEntered(MouseEvent e)
         {
            
         }
         
         public void mouseExited(MouseEvent e)
         {
            
         }
      }
      
      private void startNewTurn()
      {
         turn++;
         turn %= NUM_PLAYERS;
         
         if (isGameOver())
         {
            gameOver();
         }
         
         if (!players[turn].canPlay)
         {
            startNewTurn();
            return;
         }
         
         piecesPanel.removeAll();
         
         for (int i = 0; i < players[turn].pieces.size(); i++)
         {
            BlokusPieceLabel pieceLabel = 
               new BlokusPieceLabel(i, players[turn].pieces.get(i), BlokusPiece.DEFAULT_RESOLUTION);
            pieceLabel.addMouseListener(new PieceLabelClickListener());
            pieceLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            piecesPanel.add(pieceLabel);
         }
         
         pieceIndex = 0;
         drawBorder();
         piecesPanel.repaint();
         
         pack();
      }
      
      private boolean isGameOver()
      {
         for (int i = 0; i < NUM_PLAYERS; i++)
         {
            if (players[i].canPlay) return false;
         }
         return true;
      }
      
      private void gameOver()
      {
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < NUM_PLAYERS; i++)
         {
            sb.append(BlokusBoard.getColorName(getPlayerColor(i)));
            sb.append(": ");
            sb.append(players[i].getScore());
            sb.append("\n");
         }
         JOptionPane.showMessageDialog(this, sb.toString(), "Game Over", JOptionPane.INFORMATION_MESSAGE );
         System.exit(0);
      }
      
      private int getPlayerColor(int index)
      {
         switch (index)
         {
            case 0: return BlokusBoard.BLUE;
            case 1: return BlokusBoard.YELLOW;
            case 2: return BlokusBoard.RED;
            case 3: return BlokusBoard.GREEN;
            default: return 0;
         }
      }
      
   }
   
   public static class BlokusPieceLabel extends JLabel
   {  
      public int pieceIndex;
      
      public BlokusPieceLabel(int pieceIndex, BlokusPiece bp, int size)
      {
         super(new ImageIcon(bp.render(size)));
         this.pieceIndex = pieceIndex;
      }
   }
   
   public static void main(String[] args)
   {
      BlokusFrame bf = new BlokusFrame();
   }
}
