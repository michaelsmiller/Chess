package chess;

/**
 * @author Michael Miller
 * This is the main that gets some initial user input and then actually plays
 * the game.
 */

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class Game
{
    /*
     * To do:
     * add ai
     * add rank and file labels
     * add networking
     * add file saving
     * add position creating
     * add different sets of pieces
     */
    private static boolean preliminaryDone;
    
    public static void main(String[] args) throws InterruptedException
    {
        preliminaryDone=false;
        final JFrame start = new JFrame("Miller Chess");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        start.setSize(300,200);
        start.setLocation(dim.width/2-start.getSize().width/2, dim.height/2-start.getSize().height/2);
        start.setLayout(new GridLayout(3,1));
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    start.setVisible(false);
                    finishPreliminary();
                }
        });
        JLabel hourLabel = new JLabel("Hours");
        JLabel minuteLabel = new JLabel("Min");
        final JTextField minuteEnter = new JTextField("0",2);
        final JTextField hourEnter = new JTextField("0",2);
        final JCheckBox yes = new JCheckBox("With Chess Clock",true);
        yes.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (yes.isSelected())
                    {
                        minuteEnter.setEnabled(true);
                        hourEnter.setEnabled(true);
                    }
                    else
                    {
                        minuteEnter.setEnabled(false);
                        hourEnter.setEnabled(false);
                    }
                }
        });
        JPanel timePanel = new JPanel();
        timePanel.add(hourLabel);
        timePanel.add(hourEnter);
        timePanel.add(minuteLabel);
        timePanel.add(minuteEnter);
        start.add(timePanel);
        start.add(yes);
        start.add(ok);
        start.getRootPane().setDefaultButton(ok);
        start.setVisible(true);
        while (!preliminaryDone)
            Thread.sleep(100);
        JOptionPane.showMessageDialog(null,"Have a good game!");
        int timeOnClock;
        try
        {
            timeOnClock = Integer.parseInt(minuteEnter.getText()) + 
                    (Integer.parseInt(hourEnter.getText())*60);
            if (timeOnClock==0||timeOnClock>=600)
                timeOnClock= -1;
        }
        catch (Exception ex)
        {
            timeOnClock = -1;
        }
                
        ArrayList<Piece> pieces = Chess.startingPosition();
        Board b = new Board(pieces);
        
        ChessFrame f = new ChessFrame(b,timeOnClock);
        b.setFrame(f);
        b.setPreferredSize(new Dimension(800,800));
        f.setSize(1000,800);
        f.setVisible(true);
    }
    
    public static void finishPreliminary()
    {
        preliminaryDone = true;
    }
}