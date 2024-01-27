import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MapPanel extends JPanel {
    private InfoPanel infoPanel;
    private BufferedImage image;
    private SimpleGraph map;
    private AffineTransform at;
    private double scaleFactor = 0.5;
    private double xOffset = 0;
    private double yOffset = 0;
    private Point startPoint;
    private Point currentPoint;
    private Location hoveredLocation = null; 
    private Client client;

    public MapPanel(BufferedImage image, SimpleGraph map, InfoPanel infoPanel) {
        this.image = image;
        this.map = map;
        this.infoPanel = infoPanel;
        initComponent();
    }

    private void initComponent() {
        ClickListener clickListener = new ClickListener(infoPanel);
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
        if(client.getIsHeading() != null){
            drawPath(g2);
        }
        map.draw(g2);
        if(client!=null){
            client.draw(g2);
        }
        repaint();
    }

    public Location getHoveredLocation(){
        return hoveredLocation;
    }

    public void resetLocation() {
        final int IMAGE_WIDTH = 2026;
        final int IMAGE_HEIGHT = 1872;
        double xOnPanel = at.getTranslateX();
        double yOnPanel = at.getTranslateY();
        double imageScaleWidth = IMAGE_WIDTH * scaleFactor;
        double imageScaleHeight = IMAGE_HEIGHT * scaleFactor;
        double panelWidth = getSize().width;
        double panelHeight = getSize().height;
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
            if (yOnPanel + imageScaleHeight <= panelHeight) {
                yOffset = panelHeight - imageScaleHeight;
            }
        } else {
            if (yOnPanel <= 0) {
                yOffset = 0;
            }
            if(xOnPanel <= 0){
                xOffset = 0;
            }
        }
        if (xOnPanel >= 0) {
            xOffset = 0;
        }
        if (yOnPanel >= 0) {
            yOffset = 0;
        }
    }
    
    public void drawPath(Graphics2D g2){
        ArrayList<Location> pathToDraw = client.getCurrent().shortestPath(client.getIsHeading(),map);
        for (int i = 0; i < pathToDraw.size()-1; i++) {
            Location current = pathToDraw.get(i);
            Location next = pathToDraw.get(i+1);
            current.drawPath(g2, next);
        }
        repaint();
    }

    public void setClient(Client client){
        this.client = client;
    }
    private class WheeleListener implements MouseWheelListener {
        private double prevScale = 0.5;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // Zoom in if < 0, Zoom out if > 0
            if (e.getWheelRotation() < 0) {
                if (scaleFactor < 3) {
                    scaleFactor *= 1.1;
                }
            } else if (e.getWheelRotation() > 0) {
                if (scaleFactor > 0.5) {
                    scaleFactor /= 1.1;
                }
            }

            // offset the x and y values so that the map stays in place when zooming in and out
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

            } catch (NoninvertibleTransformException ex) {
                System.out.println("non invertible transform");
            }
            repaint();
        }
    }
}
