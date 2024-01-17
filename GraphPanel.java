import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;


class GraphPanel extends JPanel {
    JFrame frame2;
    BorderLayout panelLayout;
    JPanel panels, choicePanel;
    GraphicsPanel displayPanel;
    JTextArea info;
    JButton selectBoxButton, selectTruckButton, loadAllButton, loadOneButton, rotateButton;
    SimpleGraph map;
    final int FRAME_WIDTH = 1200;
    final int FRAME_HEIGHT = 1200;
    
    GraphPanel(SimpleGraph hi){
        this.map = hi;
    }

    public void runLoop(){
        frame2 = new JFrame("Display System");
        frame2.setResizable(true);
        frame2.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayPanel = new GraphicsPanel();
        displayPanel.setPreferredSize(new Dimension(1000, 1000));
        frame2.add(displayPanel, BorderLayout.CENTER);
        
        frame2.setVisible(true);
    }

public class GraphicsPanel extends JPanel{
    @Override
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      map.draw(g);
      }
}
}
