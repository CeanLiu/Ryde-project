import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GraphPanel extends JPanel {

    private BufferedImage image;
    private SimpleGraph map;
    private AffineTransform at;
    private double scaleFactor = 1;
    private double xOffset = 0;
    private double yOffset = 0;
    private Point startPoint;
    private Point currentPoint;

    public GraphPanel(BufferedImage image, SimpleGraph map) {

        this.image = image;
        this.map = map;
        initComponent();

    }

    private void initComponent() {
        ClickListener clickListener = new ClickListener();
        DragListener dragListener = new DragListener();
        WheeleListener wheeleListener = new WheeleListener();
        this.addMouseListener(clickListener);
        this.addMouseMotionListener(dragListener);
        this.addMouseWheelListener(wheeleListener);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        at = new AffineTransform();
        at.translate(xOffset, yOffset);
        at.scale(scaleFactor, scaleFactor);
        g2.transform(at);
        g2.drawImage(image, 0, 0, this);
        map.draw(g2);

    }

    private class WheeleListener implements MouseWheelListener {
        private double prevScale = 1;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // Zoom in if < 0, Zoom out if > 0
            if (e.getWheelRotation() < 0) {
                if (scaleFactor < 5){
                    scaleFactor *= 1.1;
                }
            } else if (e.getWheelRotation() > 0) {
                if (scaleFactor > 0.8){
                    scaleFactor /= 1.1;
                }
            }
            // get the coordinates of the cursor on the panel
            double mouseInPaneX = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double mouseInPaneY = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

            // offset the x and y values so that the map stays in place when zooming in and
            // out
            double scaleChange = scaleFactor / prevScale;
            xOffset = mouseInPaneX - scaleChange * (mouseInPaneX - xOffset);
            yOffset = mouseInPaneY - scaleChange * (mouseInPaneY - yOffset);
            prevScale = scaleFactor;
            repaint();
        }
    }

    private class ClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            resetLocation();
            repaint();
        }
    }

    private class DragListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            currentPoint = e.getPoint();
            if (true) {
                double moveX = currentPoint.getX() - startPoint.getX();
                double moveY = currentPoint.getY() - startPoint.getY();
                xOffset += moveX;
                yOffset += moveY;
                startPoint = currentPoint;
            }
            repaint();
        }

    }

    public void resetLocation() {
        final int IMAGE_HEIGHT = 1024;
        final int IMAGE_WIDTH = 1024;
        // double xOnPanel = at.getTranslateX();
        // double yOnPanel = at.getTranslateY();
        // double imageScaleWidth = IMAGE_WIDTH * scaleFactor;
        // double imageScaleHeight = IMAGE_HEIGHT * scaleFactor;
        // double panelWidth = getSize().width;
        // double panelHeight = getSize().height;
        int xOnPanel = (int) at.getTranslateX();
int yOnPanel = (int) at.getTranslateY();
int imageScaleWidth = (int) (IMAGE_WIDTH * scaleFactor);
int imageScaleHeight = (int) (IMAGE_HEIGHT * scaleFactor);
int panelWidth = (int) getSize().getWidth();
int panelHeight = (int) getSize().getHeight();

        System.out.println("\nx: " + xOnPanel + " y: " + yOnPanel + " \nwidth: " +imageScaleWidth + " height: " + imageScaleHeight + " \npanel width: " + panelWidth + " panel height: " + panelHeight);
        if (imageScaleWidth >= panelWidth) {
            if (xOnPanel + imageScaleWidth <= panelWidth) {
                xOffset =  panelWidth-imageScaleWidth;
            }
        } else {
            if (xOnPanel <= 0) {
                xOffset = 0;
            }
        }
        if (imageScaleHeight >= panelHeight) {
            if (yOnPanel + imageScaleWidth <= panelHeight) {
                yOffset = panelHeight-imageScaleWidth;
            }
        } else {
            if (yOnPanel <= 0) {
                yOffset = 0;
            }
        }
        if (xOnPanel >= 0) {
            xOffset = 0;
        }
        if (yOnPanel >= 0) {
            yOffset = 0;
        }

    }

}
