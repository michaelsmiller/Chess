package chess;

/**
 * @author Michael Miller
 * One day this will have a bunch of features that include a clock and a
 * notation keeper.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SidePanel extends JPanel
{
    private final JCheckBox drawOffer;
    private final ChessFrame frame;
    private final JButton resign;
    private final JButton undo;
    private long whiteClockTime;
    private long blackClockTime;
    private final long whiteClockInitialTime;
    private final long blackClockInitialTime;
    private long startTime;
    
    /**
     * The constructor that makes this SidePanel great
     * @param f the frame this is a part of
     * @param timeOnClock the...you know...time on clock
     */
    public SidePanel(ChessFrame f, long timeOnClock)
    {
        setLayout(new GridLayout(8,1));
        frame = f;
        whiteClockInitialTime = timeOnClock*60000;
        blackClockInitialTime = timeOnClock*60000;
        if (timeOnClock>0)
        {
            whiteClockTime = whiteClockInitialTime;
            blackClockTime = blackClockInitialTime;
        }
        if (whiteClockTime!=0)
        {
            setStartTime();
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run()
            {
                updateUI();
            }
                     },500,500);//6, 6);
        }
        //timer.stop() stops.
        drawOffer = new JCheckBox("Offer Draw?");
        drawOffer.setToolTipText("Offers your opponent draw after you move");
        add(new JLabel(" "));
        add(new JLabel(" "));
        add(drawOffer);
        //add(new JLabel(" "));
        add(new JLabel(" "));
        drawOffer.setEnabled(false);
        resign = new JButton("Resign");
        resign.setToolTipText("Resign");
        resign.setEnabled(false);
        resign.addActionListener(new ActionListener()
        {
             @Override
             public void actionPerformed(ActionEvent e)
             {
                   int choice = JOptionPane.showConfirmDialog(null,"Are you sure"
                            + " you want to resign?");
                   if (choice==JOptionPane.YES_OPTION)
                    {
                       String message;
                       if (frame.getBoard().colorGoing()==PieceColor.WHITE)
                           message = "White Resigned! Black Wins";
                       else
                           message = "Black Resigned! White Wins";
                       JOptionPane.showMessageDialog(null,message);
                       System.exit(0);
                    }
             }
        });
        undo = new JButton("Undo");
        undo.setToolTipText("Resets the position to 1 move ago");
        undo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (frame.getBoard().getTurn()>0)
                    frame.getBoard().undoMove();
            }
        });
        
        add(resign);
        add(undo);
        add(new JLabel(" "));
        add(new JLabel(" "));
    }
    
    /**
     * This figures out how the timer is displayed
     * @param g the Graphics doing everything
     */
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        g.setFont(new Font("Sanseriff",Font.BOLD,30));
        if (whiteClockTime!=0 && blackClockTime!=0)
        {
            if (frame.getBoard().colorGoing()==PieceColor.WHITE)
                whiteClockTime = whiteClockInitialTime-
                (System.currentTimeMillis()-startTime)+(blackClockInitialTime-blackClockTime);
            else
                blackClockTime = blackClockInitialTime-
                (System.currentTimeMillis()-startTime)+(whiteClockInitialTime-whiteClockTime);
            if (whiteClockTime<120000)
                g.setColor(Color.RED);
            else g.setColor(Color.BLACK);
            g.drawString(toDate(whiteClockTime),17,650);
            if (blackClockTime<120000)
                g.setColor(Color.RED);
            else g.setColor(Color.BLACK);
            g.drawString(toDate(blackClockTime),17,50);
            long whiteSec = whiteClockTime/1000;
            long blackSec = blackClockTime/1000;
            if (blackSec==0||whiteSec==0)
            {
                String message;
                if (blackSec==0)
                    message = "White Won on Time";
                else
                    message = "Black Won on Time";
                JOptionPane.showMessageDialog(null,message);
                System.exit(0);
            }
                
        }
    }
    
    /**
     * This takes the milliseconds and converts to proper visual format
     * @param time the milliseconds we want to convert
     * @return the nice minutes and seconds we want
     */
    /*private static String toDate(long time)
    {
        Date date = new Date(time);
        DateFormat formatter;
        if (time<60*1000*60)
            formatter = new SimpleDateFormat("m:ss");
        else
            formatter = new SimpleDateFormat("h:mm:ss");
        return formatter.format(date);
    }*/
    
    private static String toDate(long time)
    {
        int t = (int)time/1000;
        if (time<60*1000*60)
            return t/60+":"+formatNumber(t%60);
        else
        {
            int t1 = (t%3600-t%60)/60;
            return t/3600+":"+formatNumber(t1)+":"+formatNumber(t%60);
        }
    }
    
    private static String formatNumber(int n)
    {
        String s = n+"";
        if (n<10)
            return "0"+n;
        else return ""+n;
    }
    
    /**
     * This sets the start time to the current time
     */
    public final void setStartTime()
    {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * This figures out if there is a draw offered yet or not
     * @return whether or not the check box for draw is selected
     */
    public boolean drawOffered()
    {
        return drawOffer.isSelected();
    }
    
    /**
     * this deselects the check box for drawing
     */
    public void deselectDraw()
    {
        drawOffer.setSelected(false);
    }
    
    /**
     * this gets the frame the side panel is a part of
     * @return the frame
     */
    public ChessFrame getFrame()
    {
        return frame;
    }
    
    /**
     * Lets the users actually use drawOffer or the resign button,
     * should be used after ten cycles of
     * moves have gone by.
     * @param b whether or not the thing is visible
     */
    public void setOptionVisibility(boolean b)
    {
        drawOffer.setEnabled(b);
        resign.setEnabled(b);
    }
    
    public JCheckBox getDrawBox()
    {
        return drawOffer;
    }
}