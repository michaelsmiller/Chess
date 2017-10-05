
package chess;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * @author Michael Miller
 * This is the physical representation of where pieces reside and is what
 * is displayed in graphics
 */
public class Square extends JComponent {

    private final Location location;
    private Piece piece;
    private final Color color;
    private final Board board;
    private boolean selected;
    
    public static final ColorPackage COLORS = ColorPackage.STANDARD;

    /**
     * This is the constructor that takes in stuff and instantiates the
     * variables without location involved
     * @param row the row of location
     * @param col the col of location
     * @param b the board
     */
    public Square(int row, int col, Board b) {
        this(new Location(row,col),b);
    }

    /**
     * A similar constructor that uses loc instead of row and col
     * @param loc the location
     * @param b the board
     */
    public Square(Location loc, Board b) {
        int row = loc.getRow();
        int col = loc.getCol();
        location = loc;
        piece = null;
        selected = false;
        addMouseListener(new SquareListener());
        board = b;
        if ((row + col) % 2 == 0) {
            color = COLORS.getWhite();
        } else {
            color = COLORS.getBlack();
        }
        setToolTipText(Location.LocToNot(location));
    }

    /**
     * Makes a square in the image of another square
     * @param a the other square
     */
    public Square(Square a) {
        location = new Location(a.location);
        if (a.piece == null) 
            piece = null;
        else 
            piece = new Piece(a.piece);
        color = a.color;
        selected = a.selected;
        addMouseListener(new SquareListener());
        board = a.board;
        setToolTipText(Location.LocToNot(location));
    }

    /**
     * Sets whether this square is selected
     * @param b 
     */
    public void setSelected(boolean b) {
        selected = b;
    }

    /**
     * Sets the piece that resides here or null if there ain't none
     * @param p the piece
     */
    public void setPiece(Piece p) {
        piece = p;
    }

    /**
     * Gets the piece that resides here or null
     * @return the piece
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Gets the board this square is a part of
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the piece residing here and removes it at the same time
     * @return the piece
     */
    public Piece removePiece() {
        Piece p = getPiece();
        setPiece(null);
        return p;
    }

    /**
     * Gets the location of this little 'ol square
     * @return the location
     */
    public Location getLoc() {
        return location;
    }

    /**
     * Determines whether or not this does not have a piece
     * @return whether piece==null
     */
    public boolean isEmpty() {
        return piece == null;
    }

    /**
     * This gets all the graphics going, displaying the piece and the square,
     * showing if it is highlighted or not.
     * @param g Graphics that work in mysterious ways
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        double width = board.getWidth() / Chess.COLUMNS;
        double height = board.getHeight() / Chess.ROWS;
        Rectangle2D.Double outline = new Rectangle2D.Double(0, 0, width, height);
        if (selected) {
            g2.setStroke(new BasicStroke(9));
        } else {
            g2.setStroke(new BasicStroke(2));
        }
        g2.setColor(color);
        g2.fill(outline);
        g2.setColor(Color.BLACK);
        if (selected) {
            g2.setColor(Color.YELLOW);
        }
        g2.draw(outline);
        if (piece != null) {
            double subWidth = width * 5.0 / 6;
            double subHeight = height * 5.0 / 6;
            g2.drawImage(piece.getImage(), (int) (width - subWidth) / 2, (int) (height - subHeight) / 2,
                    (int) subWidth, (int) subHeight, null);
        }
    }

    /**
     * Takes all the squares in board, and deselects them. This really should
     * be a method in board. I know. I'll do that at some point.
     */
    public void deselectSquares() {
        for (Square[] s1 : board.getSquares()) {
            for (Square s : s1) {
                s.setSelected(false);
            }
        }
    }
    
    /**
     * This takes just returns the information of the piece if there is one.
     * @return String s the string representation of the square in its piece.
     */
    @Override
    public String toString()
    {
        String s = "";
        if (piece!=null)
            s+=piece+""+location;
        return s;
    }
    

    /**
     * This beautiful little class does what I want when the mouse is clicked
     */
    class SquareListener implements MouseListener {

        /**
         * This makes moves and sets selected and such when the square is
         * clicked by the mouse, allowing the user to control the flow of the
         * game.
         * @param event the mouseEvent of clicking
         */
        @Override
        public void mouseClicked(MouseEvent event) {
            //JOptionPane.showMessageDialog(null,board.toString());
            if (board.firstSelected() == null) {//this click is first selection
                if (piece!=null&&board.colorGoing()!=piece.getColor())
                    return;
                board.setFirstSelected(getPiece());
            } else {//piece to move already selected, so this is what happens when a piece is moved
                if (selected) 
                {
                    board.movePiece(board.getLocation(board.firstSelected()),getLoc());
                    deselectSquares();
                    board.updateUI();
                    if (board.checkmate()) //informs of checkmate
                    {
                        String message;
                        if (board.checkmate(PieceColor.WHITE))
                            message = "Checkmate! Black Wins!";
                        else
                            message = "Checkmate! White Wins!";
                        JOptionPane.showMessageDialog(null,message);
                        System.exit(0);
                    }
                    else if (board.check()) //informs of check
                    {
                        PieceColor color = board.inCheck();
                        JOptionPane.showMessageDialog(null,color+" is in check!");
                    }
                
                    else if (board.getFrame().drawOffered()) //offered draw
                    {
                        //board.updateUI();
                        board.getFrame().deselectDrawOffer();
                        String color = "";
                        for (PieceColor c: PieceColor.values())
                            if (c!=board.colorGoing())
                                color=c.toString();
                        String message = color+" offered Draw\nDo you accept?";
                        int accept=JOptionPane.showConfirmDialog(null,message);
                        if (accept==JOptionPane.YES_OPTION)
                        {
                            JOptionPane.showMessageDialog(null,"Draw offered and accepted.");
                            System.exit(0);
                        }
                    }
                }
                
                board.setFirstSelected(null);
            }
            deselectSquares();
            if (board.firstSelected() != null && board.firstSelected().equals(getPiece())) {
                board.selectMoveLocations(board.getMoveLocations(getPiece()));
            }
            board.updateUI();
            checkForDraw();
            if (board.getTurn()>=20)//This gets offer draw and resign selected
                board.getFrame().setOptionVisibility(true);
        }
        
        /**
         * Nothing at all. They just have to be here so that everything will
         * compile because they need to implemented as well.
         * @param event the event described in the method name
         */
        @Override
        public void mousePressed(MouseEvent event) {
        }
        @Override
        public void mouseExited(MouseEvent event) {
        }
        @Override
        public void mouseReleased(MouseEvent event) {
        }
        @Override
        public void mouseEntered(MouseEvent event) {
        }
    }
    
    private void checkForDraw()
    {
        if (board.draw()!=Board.NOT_DRAW)//checks for a draw or checkmate
            {
                int draw = board.draw();
                String message;
                if (draw==Board.STALEMATE)
                    message = "Stalemate! Draw!";
                else if (draw==Board.THREE_MOVE)
                    message = "Three Turn Repetition! Draw!";
                else if (draw==Board.TWO_KINGS)
                    message = "No mating material! Draw!";
                else //fifty move draw
                    message = "You have gone fifty moves without taking or\n"
                            + "moving a pawn. The game is a draw.";
                JOptionPane.showMessageDialog(null,message);
                System.exit(0);
            }
    }
}

enum ColorPackage
{
    STANDARD(Color.WHITE,Color.GREEN.darker().darker().darker()),
    WINTER(new Color(0,174,249),new Color(60,0,149)),
    SEASONS(new Color(4,53,237),new Color(255,255,0)),
    CHRISTMAS(Color.GREEN.darker(),Color.RED.brighter()),
    WEIRD(Color.YELLOW,new Color(130,20,255)),
    PINK(new Color(250,0,250),new Color(130,20,255).darker());
    
    private final Color white;
    private final Color black;
    
    ColorPackage(Color w,Color b)
    {
        white = w;
        black = b;
    }
    
    public Color getWhite()
    {
        return white;
    }
    
    public Color getBlack()
    {
        return black;
    }
}