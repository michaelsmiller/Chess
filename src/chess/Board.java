package chess;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author Michael Miller
 * This is the jpanel where all the squares reside and 
 * where most of the work in the program is done for 
 * interactions between pieces and such.
 */

public class Board extends JPanel
{
    private final Square[][] squares;
    private Piece firstSelected;
    private final boolean mainBoard;
    private int turn;
    private int fiftyMove;
    private ArrayList<String> positions;
    private ChessFrame frame;
    
    //move types
    public static final int ILLEGAL_MOVE = -1;
    public static final int KNIGHT_MOVE = 0;
    public static final int ROOK_MOVE = 1;
    public static final int BISHOP_MOVE = 2;
    public static final int QUEEN_MOVE = 3;
    public static final int KING_MOVE = 4;
    public static final int KINGSIDE_CASTLING = 5;
    public static final int QUEENSIDE_CASTLING = 10;
    public static final int PAWN_MOVE = 6;
    public static final int PAWN_FIRST_MOVE = 7;
    public static final int PAWN_CAPTURE = 8;
    public static final int EN_PASSANT = 9;
    
    //draw types
    public static final int STALEMATE = 0;
    public static final int THREE_MOVE = 1;
    public static final int FIFTY_MOVE = 2;
    public static final int TWO_KINGS = 3;
    public static final int NOT_DRAW = -1;
    
    /**
     * This constructor takes in some pieces and puts them in
     * their correct Squares by using their origin fields, instantiating
     * squares according to the number of rows and cols and firstSelected as null
     * @param pieces the pieces the game starts with
     */
    public Board(ArrayList<Piece> pieces)
    {
        squares = new Square[Chess.ROWS][Chess.COLUMNS];
        setLayout(new GridLayout(Chess.ROWS,Chess.COLUMNS,0,0));
        firstSelected = null;
        mainBoard=true;
        turn = 0;
        fiftyMove = 0;
        for (int i = 0; i < Chess.ROWS; i++)
            for (int j = 0; j < Chess.COLUMNS; j++)
            {
                squares[i][j] = new Square(i,j,this);
                for (Piece p: pieces)
                    if (p.getOrigin().equals(new Location(i,j)))
                        squares[i][j].setPiece(p);
                add(squares[i][j]);
            }
        positions = new ArrayList<>();
        positions.add(toString());
    }
    
    public Board(String position)
    {
        this(Chess.stringToPieces(getPiecePositionFromPos(position)));
        turn = getTurnFromPos(position);
        fiftyMove = getFiftyMoveFromPos(position);
        if (turn<20)
            frame.setOptionVisibility(false);
    }
    
    /**
     * This is the constructor that takes in a Board and copies everything about
     * that board into a new board, used for figuring out legality of moves
     * @param a 
     */
    public Board(Board a)
    {
        mainBoard=false;
        squares = new Square[Chess.ROWS][Chess.COLUMNS];
        setLayout(new GridLayout(Chess.ROWS,Chess.COLUMNS,0,0));
        if (a.firstSelected==null)
            firstSelected=null;
        else
            firstSelected = new Piece(a.firstSelected);
        for (int i = 0; i < Chess.ROWS; i++)
            for (int j = 0; j < Chess.COLUMNS; j++)
                squares[i][j] = new Square(a.squares[i][j]);
    }
    
    public void setTurn(int t)
    {
        turn=t;
    }
    
    /**
     * This gets the piece that is on the first square selected by the user,
     * as opposed to the second.
     * @return firstSelected the piece on the first clicked square
     */
    public Piece firstSelected()
    {
        return firstSelected;
    }
    
    /**
     * This sets the new piece to be considered the firstSelected, to move
     * @param s the piece to be selected
     */
    public void setFirstSelected(Piece s)
    {
        firstSelected = s;
    }
    
    public void setFiftyMove(int f)
    {
        fiftyMove=f;
    }
    
    /**
     * This sets the frame that the board will be a part of.
     * @param f the frame this is a part of.
     */
    public void setFrame(ChessFrame f)
    {
        frame = f;
    }
    
    /**
     * This gets the chessframe this is a part of
     * @return the chess frame this is a part of
     */
    public ChessFrame getFrame()
    {
        return frame;
    }
    
    /**
     * This gets a piece in squares using location
     * @param loc the location of the piece
     * @return the piece required
     */
    public Piece getPiece(Location loc)
    {
        if (!loc.isValid())
            return null;
        Square s = getSquare(loc);
        return s.getPiece();
    }
    
    /**
     * Gets the number turn it is (1 move = 1 turn, not 1 cycle of moves)
     * @return turn the turn number
     */
    public int getTurn()
    {
        return turn;
    }
    
    /**
     * This gets the square at a given location
     * @param loc the location of the square
     * @return the square
     */
    public Square getSquare(Location loc)
    {
        return squares[loc.getRow()][loc.getCol()];
    }
    
    /**
     * This sets the value of the Piece at a given location
     * @param p the piece to be put at that location
     * @param loc the location of the piece
     */
    public void setPiece(Piece p, Location loc)
    {
        squares[loc.getRow()][loc.getCol()].setPiece(p);
    }
    
    /**
     * This gets the location of a given piece
     * @param p the piece
     * @return the location or null if nothing there
     */
    public Location getLocation(Piece p)
    {
        if (p==null)
            return null;
        for (Square[] s1: squares)
        {
            for (Square s: s1)
            {
                Piece p1 = s.getPiece();
                if (p1!=null && p1.equals(p))
                    return s.getLoc();
            }
        }
        return null;
    }
    
    /**
     * This gets a piece at a given location and removes it from that location
     * @param loc the location of the piece
     * @return the piece removed
     */
    private Piece removePiece(Location loc)
    {
        Square s = getSquare(loc);
        return s.removePiece();
    }
    
    /**
     * This moves a piece to a location where it can move to
     * @param a the location of the piece
     * @param b the destination
     */
    public void movePiece(Location a, Location b)
    {
        Square s1 = getSquare(a);
        Square s2 = getSquare(b);
        Piece p = s1.getPiece();
        if (p==null)
            return;
        int moveType = canMoveTo(p,b);
        boolean taking = getPiece(b)!=null;
        boolean fiftyMoveBreak = getPiece(b)!=null||p.getType()==Type.PAWN;
        s2.setPiece(s1.removePiece());
        p.setHasMoved(true);
        if (p.getType()==Type.PAWN)
        {
            if (mainBoard&&((p.white()&&getLocation(p).getRow()==0)
                || !p.white()&&getLocation(p).getRow()==7))
                    promotePawn(p);
            else if (moveType==EN_PASSANT)
            {
                int row = a.getRow();
                int col = b.getCol();
                removePiece(new Location(row,col));
                taking = true;
            }
        }
        else if (moveType==KINGSIDE_CASTLING||moveType==QUEENSIDE_CASTLING)
        {
            Location rookLoc;
            Location rookDest;
            if (moveType==KINGSIDE_CASTLING)
            {
                rookLoc = b.farther(Direction.EAST);
                rookDest = b.farther(Direction.WEST);
            }
            else
            {
                rookLoc = new Location(b.getRow(),b.getCol()-2);
                rookDest = b.farther(Direction.EAST);
            }
            //movePiece(getLocation(rook),rookDest);
            getSquare(rookDest).setPiece(removePiece(rookLoc)); //moves the rook
        }
        if (mainBoard)
        {
            turn++;
            if (fiftyMoveBreak)
                fiftyMove= 0;
            else fiftyMove++;
        }
        for (Piece piece: getPieces())
            piece.setHasJustMoved(false);
        p.setHasJustMoved(true);
        if (mainBoard)
        {
            positions.add(toString());
            
            //this is all for the notation on the side
            notateMove(p,b,moveType,taking);
            /*JTable not = frame.getNotation();
            DefaultTableModel d = (DefaultTableModel)not.getModel();
            String notation;
            PieceColor color = p.getColor();
            if (color==PieceColor.WHITE)
                notation = turn/2+1+". "; //the turn number first, on the left
            else notation = "";
            if (moveType==QUEENSIDE_CASTLING)
                notation += "0-0-0";
            else if (moveType==KINGSIDE_CASTLING)
                notation += "0-0";
            else //normal move
            {
                if (p.getType()!=Type.PAWN)
                    notation+=p.getType().toNotation(); //the type of piece (K,N,R,etc)
                if (taking)
                    notation+="x"; //this is if the move involves taking
                notation+=Location.LocToNot(b);
            }
            if (checkmate(colorGoing())) //notates # for checkmate
                notation+="#";
            else if (inCheck(colorGoing())) //notates + for check
                notation+="+";
            
            if (color==PieceColor.WHITE)
                d.addRow(new Object[]{notation,""});
            else 
                d.setValueAt(notation, (turn-1)/2, 1);*/
        }
    }
    
    private void notateMove(Piece p, Location loc, int moveType, boolean taking)
    {
        JTable not = frame.getNotation();
            DefaultTableModel d = (DefaultTableModel)not.getModel();
            String notation;
            PieceColor color = p.getColor();
            if (color==PieceColor.WHITE)
                notation = turn/2+1+". "; //the turn number first, on the left
            else notation = "";
            if (moveType==QUEENSIDE_CASTLING)
                notation += "0-0-0";
            else if (moveType==KINGSIDE_CASTLING)
                notation += "0-0";
            else //normal move
            {
                if (p.getType()!=Type.PAWN)
                    notation+=p.getType().toNotation(); //the type of piece (K,N,R,etc)
                if (taking)
                    notation+="x"; //this is if the move involves taking
                notation+=Location.LocToNot(loc);
            }
            if (checkmate(colorGoing())) //notates # for checkmate
                notation+="#";
            else if (inCheck(colorGoing())) //notates + for check
                notation+="+";
            
            if (color==PieceColor.WHITE)
                d.addRow(new String[]{notation,""});
            else 
                d.setValueAt(notation, (turn-1)/2, 1);
    }
    
    /**
     * Allows the user to choose what to promote the pawn to,
     * given that it can be promoted and all that, then promotes it.
     * @param p the pawn
     */
    private void promotePawn(Piece p)
    {
        JRadioButton knight = new JRadioButton("Knight");
        JRadioButton bishop = new JRadioButton("Bishop");
        JRadioButton rook = new JRadioButton("Rook");
        JRadioButton queen = new JRadioButton("Queen", true); //queen is the default choice
        ButtonGroup group = new ButtonGroup();
        group.add(knight);
        group.add(bishop);
        group.add(rook);
        group.add(queen);
        JRadioButton[] buttons = {knight,bishop,rook,queen};
        JOptionPane.showConfirmDialog(null,
                buttons,"Choose what the pawn promotes to",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
        Type type = Type.PAWN;
        for (JRadioButton button: buttons)
            if (button.isSelected())
                for (Type t: Type.values())
                    if (t.toString().equals(button.getText()))
                        type = t;
        p.setType(type);
    }
    
    /**
     * gets all the locations on the board
     * @return the locations on the board
     */
    public ArrayList<Location> getLocations()
    {
        ArrayList<Location> locs = new ArrayList<>();
        for (int row = 0; row < squares.length; row++)
        {
            for (int col = 0; col < squares[0].length; col++)
            {
                locs.add(new Location(row,col));
            }
        }
        return locs;
    }
    
    /**
     * Gets the adjacent locations to a given location
     * @param loc the center of those adjacent locs
     * @return locations the locations adjacent to loc
     */
    public ArrayList<Location> getAdjacentLocations(Location loc)
    {
        ArrayList<Location> locations = new ArrayList<>();
        for (Direction d: Direction.values())
        {
            if (loc.farther(d).isValid())
                locations.add(loc.farther(d));
        }
        return locations;
    }
    
    /**
     * Gets the empty adjacent locations to loc
     * @param loc the one around which all this is centered
     * @return locations the locations
     */
    public ArrayList<Location> getEmptyAdjacentLocations(Location loc)
    {
        ArrayList<Location> locations = getAdjacentLocations(loc);
        for (Direction d: Direction.values())
        {
             Location temp = loc.farther(d);
             if (getPiece(temp)==null)
                locations.add(temp);
        }
        return locations;
    }
    
    /**
     * Gets all the squares in this board
     * @return squares the squares
     */
    public Square[][] getSquares()
    {
        return squares;
    }
    
    /**
     * Gets all the pieces in the board
     * @return pieces the pieces
     */
    public ArrayList<Piece> getPieces()
    {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Square[] s1: squares)
        {
            for (Square s: s1)
            {
                Piece p = s.getPiece();
                if (p!=null)
                    pieces.add(p);
            }
        }
        return pieces;
    }
    
    /**
     * This gets all the pieces of the specified color on the board
     * @param color the color of the pieces
     * @return pieces the pieces specified
     */
    public ArrayList<Piece> getPieces(PieceColor color)
    {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Square[] s1: squares)
        {
            for (Square s: s1)
            {
                Piece p = s.getPiece();
                if (p!=null&&p.getColor()==color)
                    pieces.add(p);
            }
        }
        return pieces;
    }
    
    /**
     * This stuff returns the set of locations the piece could conceivably move
     * to.
     * @param origin the location of the piece
     * @return the max range of the locations the piece can move to on the board.
     */
    private ArrayList<Location> getCandidateLocations(Location origin)
    {
        ArrayList<Location> locs = new ArrayList<>();
        Piece p = getPiece(origin);
        if (p==null)
            return locs;
        switch (p.getType())
        {
            case QUEEN:case ROOK:case BISHOP:
            locs = getLocations();break;
            case KNIGHT:case PAWN:case KING:
            locs = getLocationsWithin(getLocation(p),2);
        }
        return locs;
    }
    
    /**
     * This gets all the locations within n squares of loc
     * @param loc the starting point
     * @param n the radius of the search
     * @return the locations within n of loc
     */
    private ArrayList<Location> getLocationsWithin(Location loc, int n)
    {
        ArrayList<Location> locs = new ArrayList<>();
        if (loc==null||!loc.isValid()||n<=0)
            return locs;
        int thisRow = loc.getRow();
        int thisCol = loc.getCol();
        for (int row=thisRow-n;row<=thisRow+n;row++)
        {
            for (int col=thisCol-n;col<=thisCol+n;col++)
            {
                Location temp = new Location(row,col);
                if (temp.isValid())
                    locs.add(temp);
            }
        }
        return locs;
    }
    
    /**
     * Finds the possible locations a given piece can move to
     * @param p the piece in question
     * @return locs all the locations the piece can go to
     */
    public ArrayList<Location> getMoveLocations(Piece p)
    {
        Location loc = getLocation(p);
        ArrayList<Location> locs = getCandidateLocations(loc);
        if (p==null)
            return null;
        for (int i = 0; i < locs.size(); i++)
        {
            if (canMoveTo(p,locs.get(i))==ILLEGAL_MOVE)
            {
                locs.remove(i);
                i--;
            }
            else
            {
                Board b = new Board(this);
                b.movePiece(loc,locs.get(i));
                if (b.findKing(p.getColor())!=null&&b.inCheck(p.getColor()))
                {
                    locs.remove(i);
                    i--;
                }
            }
        }
        return locs;
    }
    
    /**
     * Sets all of the squares at given locations as selected
     * @param locs the locations of the squares now being selected
     */
    public void selectMoveLocations(ArrayList<Location> locs)
    {
        if (locs==null||locs.isEmpty())
            return;
        for (Location loc: locs)
            getSquare(loc).setSelected(true);
    }
    
    /**
     * Determines if a piece can move to a destination regardless of whether
     * that results in a check or not
     * @param p the piece moving
     * @param dest the location its going to
     * @return whether or not p can go to dest
     */
    public int canMoveTo(Piece p, Location dest)
    {
        if (p==null||dest==null||!dest.isValid())
            return ILLEGAL_MOVE;
        Location loc = getLocation(p);
        if (dest.equals(loc))
            return ILLEGAL_MOVE;
        int thisCol = loc.getCol();
        int thisRow = loc.getRow();
        int otherCol = dest.getCol();
        int otherRow = dest.getRow();
        int rowDiff = Math.abs(thisRow-otherRow);
        int colDiff = Math.abs(thisCol-otherCol);
        Piece other = getPiece(dest);
        if (other!=null&&other.sameColor(p))
            return ILLEGAL_MOVE;
        switch (p.getType())
        {
            case KING:
            if (rowDiff==0&&colDiff==2) //castling
            {
                if (p.hasMoved())
                    return ILLEGAL_MOVE;
                Piece rook = null;
                Direction dir = null;
                if (thisCol > otherCol) //queenside
                {
                    dir = Direction.WEST;
                    rook = getPiece(new Location(thisRow,thisCol-4));
                }
                else //kingside
                {
                    dir = Direction.EAST;
                    rook = getPiece(new Location(thisRow,thisCol+3));
                }
                if (rook==null||rook.getType()!=Type.ROOK||!p.sameColor(rook)||rook.hasMoved())
                    return ILLEGAL_MOVE;
                Location next = loc;
                for (int i=thisCol; i!=getLocation(rook).getCol(); i=next.getCol())
                {
                    if ((!next.equals(loc)&&getPiece(next)!=null)||enemyCanAttack(p.getColor(),next))
                        return ILLEGAL_MOVE;
                    next = next.farther(dir);
                }
                if (thisCol > otherCol)
                    return QUEENSIDE_CASTLING;
                else return KINGSIDE_CASTLING;
            }
            else //normal king move
                if (adjacent(loc,dest)
                && (other==null || !p.sameColor(other)))
                    return KING_MOVE;
                else return ILLEGAL_MOVE;
            
            case PAWN:
            if (rowDiff>2||rowDiff<1||colDiff>1||(rowDiff==2&&colDiff>0)
            ||(p.white()&&otherRow>thisRow)||(!p.white()&&otherRow<thisRow))
                return ILLEGAL_MOVE;
            else if (rowDiff==2) //first move
            {
                if (p.hasMoved())
                    return ILLEGAL_MOVE;
                Location temp = loc.closerTo(dest);
                if (getPiece(temp)==null&&other==null&&((p.white()&&thisRow==6)||(!p.white()&&thisRow==1)))
                    return PAWN_FIRST_MOVE;
                else return ILLEGAL_MOVE;
            }
            else if (colDiff==1 && other!=null) //taking
            {
                if (p.sameColor(other))
                    return ILLEGAL_MOVE;
                else return PAWN_CAPTURE;
            }
            else if (colDiff==1 && other==null) //en passant
            {
                int diff = otherCol-thisCol;
                Location otherLoc = new Location(thisRow,thisCol+diff);
                Piece otherPiece = getPiece(otherLoc);
                if (otherPiece!=null&&otherPiece.hasJustMoved()
                    &&otherPiece.getType()==Type.PAWN&&!otherPiece.sameColor(p))
                    return EN_PASSANT;
                else return ILLEGAL_MOVE;
            }
            else if (rowDiff==1) //normal move
            {
                if (other==null)
                    return PAWN_MOVE;
                else return ILLEGAL_MOVE;
            }
            break;
            
            case ROOK:case QUEEN: case BISHOP:case KNIGHT:
            if (!canAttack(p,dest))
                return ILLEGAL_MOVE;
            else
            {
                switch (p.getType())
                {
                    case ROOK:return ROOK_MOVE;
                    case QUEEN:return QUEEN_MOVE;
                    case BISHOP:return BISHOP_MOVE;
                    case KNIGHT:return KNIGHT_MOVE;
                }
            }
        }
        return ILLEGAL_MOVE; //will never happen
    }
    
    /**
     * checks if p can threaten to attack a certain square, regardless of whether 
     * it can actually move there at the present.
     * @param p the piece moving
     * @param loc the location moving to
     * @return whether or not p threatens loc
     */
    private boolean canAttack(Piece p, Location loc)
    {
        int thisRow = getLocation(p).getRow();
        int thisCol = getLocation(p).getCol();
        int otherRow = loc.getRow();
        int otherCol = loc.getCol();
        int rowDiff = Math.abs(otherRow-thisRow);
        int colDiff = Math.abs(otherCol-thisCol);
        switch (p.getType())
        {
            case PAWN:
            return rowDiff==1&&colDiff==1 &&
                ((p.white()&&otherRow<thisRow)||(!p.white()&&otherRow>thisRow));
            
            case KING:
            return adjacent(getLocation(p),loc);
            
            case KNIGHT:
            return rowDiff>0 && colDiff>0 && rowDiff+colDiff==3;
            
            //rook, bishop, queen are identical, except for their preconditions
            case ROOK:case BISHOP:case QUEEN:
            if ((p.getType()==Type.ROOK&&rowDiff>0&&colDiff>0)
                ||(p.getType()==Type.BISHOP&&rowDiff!=colDiff)
                ||(p.getType()==Type.QUEEN&&rowDiff>0&&colDiff>0&&rowDiff!=colDiff))
                return false;
            Location next = getLocation(p).closerTo(loc);
            while (!next.equals(loc))
            {
                if (getPiece(next)!=null) //checks for piece in the way
                    return false;
                next = next.closerTo(loc);
            }
            return true;
        }
        return false; //will never happen because all piece types covered
    }
    
    /**
     * Checks if an piece of the opposite color of color can attack a square.
     * @param allyColor the color being attacked
     * @param loc the location being attacked
     * @return whether or not the opposite color can attack loc
     */
    private boolean enemyCanAttack(PieceColor allyColor, Location loc)
    {
        if (!loc.isValid())
            return false;
        for (Piece other: getPieces())
        {
            if (other.getColor()!=allyColor && canAttack(other,loc))
                return true;
        }
        return false;
    }
    
    /**
     * Gets the king of a certain color or null if there is none
     * @param color the color of the king
     * @return the king
     */
    private Piece findKing(PieceColor color)
    {
        for (Piece p: getPieces())
        {
            if (p.isKing() && p.getColor()==color)
                return p;
        }
        return null;
    }
    
    /**
     * Determines whether a king of a color is in check
     * @param color the king's color
     * @return whether or not the king is in check
     */
    public boolean inCheck(PieceColor color)
    {
        Piece king = findKing(color);
        if (king==null)
            return false;
        Location loc = getLocation(king);
        return enemyCanAttack(color,loc);
    }
    
    public boolean check()
    {
        return inCheck(PieceColor.WHITE)||inCheck(PieceColor.BLACK);
    }
    
    public PieceColor inCheck()
    {
        PieceColor white = PieceColor.WHITE;
        PieceColor black = PieceColor.BLACK;
        if (inCheck(white))
            return white;
        else if (inCheck(black))
            return black;
        else return null;
    }
    
    /**
     * This checks if a specific color is checkmated
     * @param color the color being mated
     * @return whether or not it is in mate
     */
    public boolean checkmate(PieceColor color)
    {
        Piece king = findKing(color);
        if (king==null||!getMoveLocations(king).isEmpty()||!inCheck(color))
            return false;
        for (Piece p: getPieces(color))
            if (!p.equals(king)&&!getMoveLocations(p).isEmpty())
                return false;
        return true;
    }
    
    /**
     * This figures out whether or not there is any sort of checkmate on the board
     * @return whether or not white or black is checkmated
     */
    public boolean checkmate()
    {
        return checkmate(PieceColor.BLACK)||checkmate(PieceColor.WHITE);
    }
    
    /**
     * Determines whether two locations are adjacent
     * @param a the first location
     * @param b the second location
     * @return whether they are adjacent
     */
    private boolean adjacent(Location a, Location b)
    {
        for (Location loc: getAdjacentLocations(a))
        {
            if (loc.equals(b))
                return true;
        }
        return false;
    }
    
    /**
     * This determines whether or not the game is over based on the position
     * @return whether or not it is a gameover
     */
    public boolean gameOver()
    {
        return checkmate()||draw()!=NOT_DRAW;
    }
    
    /**
     * This gets the piececolor whose turn it is currently.
     * @return the piece whose move it is.
     */
    public PieceColor colorGoing()
    {
        if (turn%2==0)
            return PieceColor.WHITE;
        else return PieceColor.BLACK;
    }
    
    /**
     * This gets the type of draw currently happening
     * @return the type of draw occurring or NOT_DRAW if there isn't one.
     */
    public int draw()
    {
        //Lack of mating material draw
        if (getPieces().size()<3)
            return TWO_KINGS;
        
        //stalemate
        PieceColor color = colorGoing();
        if (inCheck(color))
            return NOT_DRAW;
        boolean draw = true;
        for (Piece p: getPieces(color))
            if (!getMoveLocations(p).isEmpty())
                draw = false;
        if (draw)
            return STALEMATE;
        
        //Fifty move draw
        if (fiftyMove>=100)
            return FIFTY_MOVE;
        
        //three move repetition
        String pos = positions.get(positions.size()-1);
        int counter = 0;
        for (String temp: positions)
        {
            if (temp.substring(4).equals(pos.substring(4)))
                counter++;
        }
        if (counter>=3)
            return THREE_MOVE;
        
        return NOT_DRAW;
    }
    
    /**
     * This turns the position into the position one move ago
     */
    public void undoMove()
    {
        positions.remove(positions.size()-1);
        String position = positions.get(positions.size()-1);
        ArrayList<Piece> pieces = Chess.stringToPieces(position.substring(4));
        setTurn(Integer.parseInt(position.substring(0,2)));
        setFiftyMove(Integer.parseInt(position.substring(2,4)));
        for (int i = 0; i < Chess.ROWS; i++)
            for (int j = 0; j < Chess.COLUMNS; j++)
            {
                squares[i][j].setPiece(null);
                for (Piece p: pieces)
                    if (p.getOrigin().equals(new Location(i,j)))
                        squares[i][j].setPiece(p);
            }
        setFirstSelected(null);
        squares[0][0].deselectSquares();
        if (getTurn()<20)
            frame.setOptionVisibility(false);
        
        DefaultTableModel d = (DefaultTableModel)frame.getNotation().getModel();
        if (colorGoing()==PieceColor.BLACK)
            d.setValueAt("",turn/2,1);
        else
            d.removeRow((turn+1)/2);
        frame.getSide().getDrawBox().setSelected(false);
        updateUI();
    }
    
    /**
     * This gets the string representation of the board with all the pieces
     * on it and is used to recreate positions and for three-move draw
     * @return the string representation of the board
     */
    @Override
    public final String toString()
    {
        String str = formatInt(turn)+formatInt(fiftyMove);
        //int counter = 0;
        for (Square[] s1: getSquares())
        {
            for (Square s: s1)
            {
                if (!s.isEmpty())
                {
                    str+=""+s;
                    //counter++;
                    /*if (counter%4==0)
                        str+="\n";*/
                }
            }
        }
        return str;
    }
    
    private static String formatInt(int n)
    {
        if (n<10)
            return "0"+n;
        else return ""+n;
    }
    
    private static int getTurnFromPos(String position)
    {
        if (position.length()<2)
            System.out.println("For some reason, the board toString didn't get copied right");
        return Integer.parseInt(position.substring(0,2));
    }
    
    private static int getFiftyMoveFromPos(String position)
    {
        if (position.length()<4)
            System.out.println("For some reason, the board toString didn't get copied right");
        return Integer.parseInt(position.substring(2,4));
    }
    
    private static String getPiecePositionFromPos(String position)
    {
        return position.substring(4);
    }
}