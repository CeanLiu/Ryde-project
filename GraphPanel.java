import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

class GraphPanel extends JPanel {
    JFrame frame2;
    GraphicsPanel displayPanel;
    SimpleGraph map;
    final int FRAME_WIDTH = 1200;
    final int FRAME_HEIGHT = 1200;

    GraphPanel(SimpleGraph hi) {
        this.map = hi;
    }

    public void runLoop() {
        frame2 = new JFrame("Display System");
        frame2.setResizable(true);
        frame2.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayPanel = new GraphicsPanel();
        displayPanel.setPreferredSize(new Dimension(1000, 1000));

        // Add the mouse click listener to the panel
        MouseClickListener mouseClickListener = new MouseClickListener(this);
        displayPanel.addMouseListener(mouseClickListener);

        frame2.add(displayPanel, BorderLayout.CENTER);

        frame2.setVisible(true);
    }

    class GraphicsPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            map.draw(g);
        }
    }

    class MouseClickListener extends MouseAdapter {
        private GraphPanel graphPanel;

        public MouseClickListener(GraphPanel graphPanel) {
            this.graphPanel = graphPanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickPoint = e.getPoint();
            System.out.println("Mouse Clicked at: (" + clickPoint.getX() + ", " + clickPoint.getY() + ")");
        }
    }
}
