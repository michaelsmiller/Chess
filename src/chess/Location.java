package chess;

/**
 * @author Michael Miller
 * This is the abstract conception of places I use in this program.
 * It holds noting but ints but is powerful in that it is used everywhere
 */
public class Location implements Comparable
{
    private int row;
    private int col;
    
    /**
     * This constructor instantiates row and col
     * @param x1 row value
     * @param y1 col value
     */
    public Location(int x1, int y1)
    {
        row = x1;
        col = y1;
    }
    
    /**
     * Constructor based on an existing location
     * @param loc the copyee
     */
    public Location(Location loc)
    {
        row = loc.row;
        col = loc.col;
    }
    
    /**
     * Gets the row
     * @return the row
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * Gets the col
     * @return the col
     */
    public int getCol()
    {
        return col;
    }
    
    /**
     * Sets the row
     * @param r the new value of row
     */
    public void setRow(int r)
    {
        row = r;
    }
    
    /**
     * Sets the col
     * @param c the new value of col
     */
    public void setCol(int c)
    {
        col = c;
    }
    
    /**
     * This gets the location farther in a direction
     * @param direction the given direction
     * @return the location farther along
     */
    public Location farther(Direction direction)
    {
        Location loc = new Location(this);
        double up = Math.sin(direction.getAngle());
        double right = Math.cos(direction.getAngle());
        up = (int)(up*100);
        right = (int)(right*100);
        
        if (up>0)
            loc.setRow(loc.row-1);
        else if (up<0)
            loc.setRow(loc.row+1);
        
        if (right>0)
            loc.setCol(loc.col+1);
        else if (right<0)
            loc.setCol(loc.col-1);
        return loc;
    }
    
    /**
     * Gets the location one unit closer to a given loc
     * @param loc
     * @return 
     */
    public Location closerTo(Location loc)
    {
        int thisRow = getRow();
        int thisCol = getCol();
        Location other = new Location(this);
        int otherRow = loc.getRow();
        int otherCol = loc.getCol();
        
        if (otherCol>thisCol)
            other.col++;
        else if (otherCol<thisCol)
            other.col--;
        
        if (otherRow>thisRow)
            other.row++;
        else if (otherRow<thisRow)
            other.row--;
        return other;
    }
    
    /**
     * This determines whether two locations are equal
     * @param other the other location
     * @return whether their rows and cols are the same
     */
    @Override
    public boolean equals(Object other)
    {
        Location a = (Location) other;
        return row==a.row && col==a.col;
    }
    
    /**
     * Determines whether this location is on the board
     * @return whether this location is valid
     */
    public boolean isValid()
    {
        return row<Chess.ROWS && row>=0 && col<Chess.COLUMNS && col>=0;
    }
    
    /**
     * Gets a num that compares this to another location
     * @param other the other location
     * @return the representation of their relationship in space and time
     * in the continuum of chess on this planet earth
     */
    @Override
    public int compareTo(Object other)
    {
        Location loc = (Location) other;
        int otherRow = loc.getRow();
        int otherCol = loc.getCol();
        int thisRow = getRow();
        int thisCol = getCol();
        
        if (otherRow>thisRow)
            return -1;
        if (otherRow<thisRow)
            return 1;
        if (otherCol>thisCol)
            return -1;
        if (otherCol<thisCol)
            return 1;
        return 0;
    }
    
    public boolean sameRow(Location a)
    {
        return row==a.row;
    }
    
    public boolean sameCol(Location a)
    {
        return col==a.col;
    }
    
    /**
     * Gets the string representation of this location
     * @return the string representation
     */
    @Override
    public String toString()
    {
        return row+""+col;
    }
    
    /**
     * Converts from algebraic notation format to a location, not used yet
     * @param str the location written
     * @return the location meant
     */
    public static Location NotToLoc(String str)
    {
        if (str.length()!=2)
            return null;
        String letter = str.substring(0,1);
        String num = str.substring(1);
        int col = letter.compareTo("a");
        int row = Chess.ROWS-Integer.parseInt(num);
        return new Location(row,col);
    }
    
    public static String LocToNot(Location loc)
    {
        int row = 8-loc.getRow();
        int col = loc.getCol();
        char c = (char)(col+0x61);
        String output=""+c+row;
        return output;
    }
}

/**
 * @author Michael Miller
 * This little class has a set number of values and describes direction,
 * of all the random things it could.
 */
enum Direction
{
    EAST(0), NORTHEAST(Math.PI/4), NORTH(Math.PI/2), NORTHWEST(3*Math.PI/4), 
    WEST(Math.PI), SOUTHWEST(5*Math.PI/4), SOUTH(3*Math.PI/2), SOUTHEAST(7*Math.PI/4);
    
    private double angle;
    
    /**
     * Constructor that instantiates angle
     * @param a the value of angle
     */
    Direction(double a)
    {
        angle = a;
    }
    
    /**
     * Gets the angle
     * @return the angle
     */
    public double getAngle()
    {
        return angle;
    }
}