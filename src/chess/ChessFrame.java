package chess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * @author Michael Miller
 * This is the frame that holds the board and the side panel.
 * It is useful only so the two can find each other in their methods.
 */
public class ChessFrame extends JFrame
{
    private Board board;
    private final SidePanel side;
    private final JTable notation;
    private final JTextField notifier;
    
    /**
     * This constructor takes in the amount of time on the clock and the board
     * to display everything.
     * @param b the board displayed
     * @param timeOnClock the number of milliseconds on the clock
     */
    public ChessFrame(Board b, long timeOnClock)
    {
         setTitle("Miller Chess");
         board = b;
         side = new SidePanel(this,timeOnClock);
         JPanel side1 = new JPanel();
         side1.setLayout(new GridLayout());
         side1.add(side);
         notation = new JTable(new DefaultTableModel(null,new Object[]{"White","Black"}));
         notation.setPreferredSize(new Dimension(80,800));
         JScrollPane scroll = new JScrollPane(notation);
         notation.setFillsViewportHeight(true);
         notation.setEnabled(false);
         /*for (int i = 0; i < 2; i++)
         {
             TableColumn col = notation.getColumnModel().getColumn(i);
             col.setPreferredWidth(10);
         }*/
         side1.add(scroll);
         side1.setPreferredSize(new Dimension(300,800));
         notifier = new JTextField("White to move");
         notifier.setEditable(false);
         
         setLayout(new BorderLayout());
         add(board,BorderLayout.CENTER);
         add(side1,BorderLayout.EAST);
         add(notifier,BorderLayout.SOUTH);
    }
    
    private String message(int i)
    {
        switch (i)
        {
            case 0: return board.colorGoing().toString() + " to move";
        }
        return null;
    }
    
    /**
     * This gets whether or not a draw is being offered
     * @return whether or not a draw is offered
     */
    public boolean drawOffered()
    {
        return side.drawOffered();
    }
    
    /**
     * This deselects the drawOffer checkBox
     */
    public void deselectDrawOffer()
    {
        side.deselectDraw();
    }
    
    /**
     * This displays the drawOffer checkBox and resign button
     * @param b whether or not the options are visible
     */
    public void setOptionVisibility(boolean b)
    {
        side.setOptionVisibility(b);
    }
    
    public JTable getNotation()
    {
        return notation;
    }
    
    /**
     * This gets the board
     * @return board the board.
     */
    public Board getBoard()
    {
        return board;
    }
    
    public SidePanel getSide()
    {
        return side;
    }
}