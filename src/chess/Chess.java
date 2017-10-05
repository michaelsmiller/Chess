package chess;

import java.util.ArrayList;

/**
 * @author Michael Miller
 * This is the overarching class which does not have much but which is important
 * for now and will probably be put away later
 */

public final class Chess
{
    public static final int COLUMNS = 8;
    public static final int ROWS = 8;
    //public static final ColorPackage SQUARE_COLORS = ColorPackage.STANDARD;
    
    /**
     * This just creates a list of all the pieces in your average start position
     * and their locations for the game to start
     * @return all 32 starting pieces with their correct original locations
     */
    public static ArrayList<Piece> startingPosition()
    {
        ArrayList<Piece> p = new ArrayList<>();
        int startingCol = 0;
        for (int col = startingCol; col<=startingCol+7; col++)
        {
            p.add(new Piece(Type.PAWN,PieceColor.WHITE,new Location(6,col)));
            p.add(new Piece(Type.PAWN,PieceColor.BLACK,new Location(1,col)));
        }
        p.add(new Piece(Type.ROOK,PieceColor.WHITE,new Location(7,startingCol)));
        p.add(new Piece(Type.ROOK,PieceColor.BLACK,new Location(0,startingCol)));
        p.add(new Piece(Type.ROOK,PieceColor.WHITE,new Location(7,startingCol+7)));
        p.add(new Piece(Type.ROOK,PieceColor.BLACK,new Location(0,startingCol+7)));
        
        p.add(new Piece(Type.KNIGHT,PieceColor.WHITE,new Location(7,startingCol+1)));
        p.add(new Piece(Type.KNIGHT,PieceColor.BLACK,new Location(0,startingCol+1)));
        p.add(new Piece(Type.KNIGHT,PieceColor.WHITE,new Location(7,startingCol+6)));
        p.add(new Piece(Type.KNIGHT,PieceColor.BLACK,new Location(0,startingCol+6)));
        
        p.add(new Piece(Type.BISHOP,PieceColor.WHITE,new Location(7,startingCol+2)));
        p.add(new Piece(Type.BISHOP,PieceColor.BLACK,new Location(0,startingCol+2)));
        p.add(new Piece(Type.BISHOP,PieceColor.WHITE,new Location(7,startingCol+5)));
        p.add(new Piece(Type.BISHOP,PieceColor.BLACK,new Location(0,startingCol+5)));
        
        p.add(new Piece(Type.QUEEN,PieceColor.WHITE,new Location(7,startingCol+3)));
        p.add(new Piece(Type.QUEEN,PieceColor.BLACK,new Location(0,startingCol+3)));
        p.add(new Piece(Type.KING,PieceColor.WHITE,new Location(7,startingCol+4)));
        p.add(new Piece(Type.KING,PieceColor.BLACK,new Location(0,startingCol+4)));
        return p;
    }
    
    public static ArrayList<Piece> stringToPieces(String str)
    {
        ArrayList<Piece> pieces = new ArrayList<>();
        if (str.length()==0)
            return pieces;
        int counter = 0;
        int i = 0;
        while (i<str.length())//moves through every piece in the notation
        {
            Type type;
            boolean hasMoved;
            boolean hasJustMoved = false;
            PieceColor color;
            Location loc;
            
            color = PieceColor.colorOf(str.charAt(i));//color
            i++;
            type = Type.typeOf(str.charAt(i));//type
            i++;
            hasMoved = str.charAt(i)=='t';//hasMoved
            i++;
            if (type==Type.PAWN)//hasJustMoved
            {
                hasJustMoved = str.charAt(i)=='t';
                i++;
            }
            //loc
            int row = Integer.parseInt(str.substring(i,i+1));
            i++;
            int col = Integer.parseInt(str.substring(i,i+1));
            i++;
            loc = new Location(row,col);
            Piece p = new Piece(type,color,loc);
            p.setHasMoved(hasMoved);
            p.setHasJustMoved(hasJustMoved);
            pieces.add(p);
            counter++;
        }
        return pieces;
    }
}