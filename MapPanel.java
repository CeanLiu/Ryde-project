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
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.UIManager;


public class MapPanel extends JPanel {
    private Client client;
    private InfoPanel userPanel;
    private BufferedImage image;
    private SimpleGraph map;
    private AffineTransform at;
    private double scaleFactor = 1;
    private double xOffset = 0;
    private double yOffset = 0;
    private Point startPoint;
    private Point currentPoint;
    private Location hoveredLocation = null; 

    public MapPanel(BufferedImage image, SimpleGraph map, InfoPanel userPanel) {

        this.image = image;
        this.map = map;
        this.userPanel = userPanel;
        initComponent();

    }

    private void initComponent() {
        ClickListener clickListener = new ClickListener(userPanel);
        MouseMotionListener dragListener = new MouseMotionListener();
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
        client.drawPath(g2);
    }

    private class WheeleListener implements MouseWheelListener {
        private double prevScale = 1;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // Zoom in if < 0, Zoom out if > 0
            if (e.getWheelRotation() < 0) {
                if (scaleFactor < 5) {
                    scaleFactor *= 1.1;
                }
            } else if (e.getWheelRotation() > 0) {
                if (scaleFactor > 1) {
                    scaleFactor /= 1.1;
                }
            }

            // offset the x and y values so that the map stays in place when zooming in and
            // out
            double scaleChange = scaleFactor / prevScale;
            xOffset = e.getX()- scaleChange * (e.getX() - xOffset);
            yOffset = e.getY() - scaleChange * (e.getY() - yOffset);
            prevScale = scaleFactor;
            repaint();
        }
    }

    private class ClickListener extends MouseAdapter {
        private InfoPanel userPanel;
        public ClickListener(InfoPanel userPanel){
            this.userPanel = userPanel;
        }
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            System.out.println("\nX: " + e.getX() + " y: " + e.getY()+"\n");
        }

        public void mouseReleased(MouseEvent e) {
            if(hoveredLocation != null){
                userPanel.finishChoose(hoveredLocation);
                hoveredLocation = null;
            }
            resetLocation();
            repaint();
        }
    }

    private class MouseMotionListener extends MouseMotionAdapter {

        public void mouseDragged(MouseEvent e) {
            currentPoint = e.getPoint();
            if (startPoint == null) {
                startPoint = currentPoint;
            }
            double moveX = currentPoint.getX() - startPoint.getX();
            double moveY = currentPoint.getY() - startPoint.getY();
            xOffset += moveX;
            yOffset += moveY;
            startPoint = currentPoint;
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            Point2D cursorLocation = new Point2D.Double(e.getX(), e.getY());
            try {
                AffineTransform inverse = at.createInverse();
                inverse.transform(cursorLocation, cursorLocation);
                hoveredLocation = map.contains(cursorLocation);

                //this is where I change the textfield for userPanel
            } catch (NoninvertibleTransformException ex) {
                System.out.println("non invertible transofrm");
            }
            repaint();
        }
    }

    public Location getHoveredLocation(){
        return hoveredLocation;
    }

    public void resetLocation() {
        final int IMAGE_HEIGHT = 1024;
        final int IMAGE_WIDTH = 1024;
        double xOnPanel = at.getTranslateX();
        double yOnPanel = at.getTranslateY();
        double imageScaleWidth = IMAGE_WIDTH * scaleFactor;
        double imageScaleHeight = IMAGE_HEIGHT * scaleFactor;
        double panelWidth = getSize().width;
        double panelHeight = getSize().height;
        //System.out.println("\nx: " + xOnPanel + " y: " + yOnPanel + " \nwidth: " + imageScaleWidth + " height: " + imageScaleHeight + " \npanel width: " + panelWidth + " panel height: " + panelHeight);
        if (imageScaleWidth >= panelWidth) {
            if (xOnPanel + imageScaleWidth <= panelWidth) {
                xOffset = panelWidth - imageScaleWidth;
            }
        } else {
            if (xOnPanel <= 0) {
                xOffset = 0;
            }
        }
        if (imageScaleHeight >= panelHeight) {
            if (yOnPanel + imageScaleWidth <= panelHeight) {
                yOffset = panelHeight - imageScaleWidth;
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
