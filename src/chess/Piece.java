package chess;

/**
 * @author Michael Miller
 * This is the piece of the game, the one that can move and that has a type
 * and can check and take others and checkmate and roll around in mud...I think
 */

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Piece implements Comparable
{
    private PieceColor color;
    private Type type;
    private final Location origin;
    private boolean hasMoved;
    private boolean hasJustMoved;
    
    /**
     * Constructor for a piece that instantiates all the instance variables
     * @param t the type
     * @param c the color
     * @param original the value of origin
     */
    public Piece(Type t, PieceColor c, Location original)
    {
        type = t;
        color = c;
        origin = original;
        hasMoved = false;
        hasJustMoved = false;
    }
    
    /**
     * Constructor to deep copy another piece
     * @param other the one being copied
     */
    public Piece(Piece other)
    {
        type = other.type;
        color = other.color;
        origin = other.origin;
        hasMoved = other.hasMoved;
        hasJustMoved = other.hasJustMoved;
    }
    
    /**
     * This gets the original location of the piece
     * @return origin the origin
     */
    public Location getOrigin()
    {
        return origin;
    }
    
    /**
     * Gets the color, which is represented in booleans
     * @return white the color
     */
    public PieceColor getColor()
    {
        return color;
    }
    
    /**
     * This gets whether or not the piece is in fact white
     * @return whether or not it is white
     */
    public boolean white()
    {
        return color==PieceColor.WHITE;
    }
    
    /**
     * Gets the Type of the piece
     * @return type the type
     */
    public Type getType()
    {
        return type;
    }
    
    /**
     * Gives the piece a new type, probably only used for promotions of pawns
     * @param t the new type
     */
    public void setType(Type t)
    {
        type = t;
    }
    
    /**
     * Sets the color of the piece. There might be a sick zombie chess game
     * that involves this, somehow, someday
     * @param p the color
     */
    public void setColor(PieceColor p)
    {
        color = p;
    }
    
    /**
     * Sets whether or not the piece just moved
     * @param b the new value of hasjustmoved
     */
    public void setHasJustMoved(boolean b)
    {
        hasJustMoved = b;
    }
    
    /**
     * Sets whether or not the piece ever moved
     * @param b whether or not the piece hasmoved
     */
    public void setHasMoved(boolean b)
    {
        hasMoved = b;
    }
    
    /**
     * Gets whether the piece just moved
     * @return hasJustMoved the value of that
     */
    public boolean hasJustMoved()
    {
        return hasJustMoved;
    }
    
    /**
     * Gets hasMoved
     * @return hasMoved whether the piece has moved
     */
    public boolean hasMoved()
    {
        return hasMoved;
    }
    
    /**
     * Gets the image associated with this piece's type and color
     * @return the image of the piece
     */
    public Image getImage()
    {
        try
        {
            return ImageIO.read(getClass().getResourceAsStream("/Pictures/"+imageName()));
        }
        catch (IOException ex)
        {}
        return null;
    }
    
    /**
     * Gets the name of the file where the image is based on the piece
     * @return the string name of the image
     */
    private String imageName()
    {
        String s;
        if (color==PieceColor.WHITE)
            s = "W";
        else
            s = "B";
        s+=type+".png";
        return s;
    }
    
    /**
     * Determines whether two pieces are the same one
     * @param other the other piece
     * @return whether they are the same
     */
    @Override
    public boolean equals(Object other)
    {
        Piece a = (Piece) other;
        return type==a.type && sameColor(a) && origin.equals(a.origin);
    }
    
    /**
     * Determines whether two pieces have the same color
     * @param other the other piece
     * @return whether they the same side
     */
    public boolean sameColor(Piece other)
    {
        return color==other.color;
    }
    
    /**
     * Determines whether the piece is a king, kind of unnecessary but this
     * method is used pretty often so its fine
     * @return whether type==king
     */
    public boolean isKing()
    {
        return type==Type.KING;
    }
    
    /**
     * This gets the string representation of the piece: whether or not it has
     * moved and maybe whether it just moved depending on some stuff.
     * @return the string rep of the piece
     */
    @Override
    public String toString()
    {
        String str = ""+color.toNotation()+type.toNotation();
        if (type==Type.PAWN)
            str+=(""+hasJustMoved).substring(0,1);
        str+=(""+hasMoved).substring(0,1);
        return str;
    }
    
    /**
     * Gives a numerical comparison between two pieces
     * @param other the piece being compared with
     * @return the comparison value, >0 if the piece is better than the other.
     */
    @Override
    public int compareTo(Object other)
    {
        int thisValue = type.getValue();
        int otherValue = ((Piece)other).type.getValue();
        return thisValue-otherValue;
    }
}

/**
 * @author Michael Miller
 * This little class represents the type that a piece is (kings, knight, etc.)
 */
enum Type
{
    PAWN("Pawn",1), KNIGHT("Knight",3), BISHOP("Bishop",4), 
    ROOK("Rook",5), KING("King",1000), QUEEN("Queen",10);
    
    private final String name;
    private final int value;
    
    /**
     * The constructor that takes in n and instantiates name
     * @param n the name
     */
    Type(String n, int v)
    {
        name = n;
        value = v;
    }
    
    /**
     * Gets the name
     * @return the name
     */
    @Override
    public String toString()
    {
        return name;
    }
    
    public String toNotation()
    {
        if (this==KNIGHT)
            return "N";
        else 
            return toString().substring(0,1);
    }
    
    /**
     * Gets the value
     * @return value the value
     */
    public int getValue()
    {
        return value;
    }
    
    public static Type typeOf(String n)
    {
        for (Type t: values())
            if (t.toString().equals(n))
                return t;
        return null;
    }
    
    public static Type typeOf(char n)
    {
        for (Type t: values())
            if (t.toNotation().charAt(0)==n)
                return t;
        return null;
    }
}

/**
 * This little enum represents the color of the piece a lot of this stuff is
 * not used as of now
 * @author Michael Miller
 */
enum PieceColor
{
    WHITE("White",0), BLACK("Black",1);
    private final String name;
    private final int moveOrder;
    
    /**
     * This makes a new piececolor and sets the parameters
     * @param n the name of the color
     * @param m the moveOrder of the color
     */
    PieceColor(String n, int m)
    {
        name = n;
        moveOrder = m;
    }
    
    /**
     * This gets the string rep of the little color
     * @return name the name of the thing
     */
    @Override
    public String toString()
    {
        return name;
    }
    
    /**
     * This gets the move order of the color
     * @return moveOrder the move order
     */
    public int getMoveOrder()
    {
        return moveOrder;
    }
    
    public String toNotation()
    {
        return toString().substring(0,1);
    }
    
    public static PieceColor colorOf(String n)
    {
        for (PieceColor c: values())
            if (c.toString().equals(n))
                return c;
        return null;
    }
    
    public static PieceColor colorOf(char n)
    {
        for (PieceColor c: values())
            if (c.toString().charAt(0)==n)
                return c;
        return null;
    }
}