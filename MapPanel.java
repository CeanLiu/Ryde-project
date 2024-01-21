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


public class MapPanel extends JPanel {
    private UserPanel userPanel;
    private BufferedImage image;
    private SimpleGraph map;
    private AffineTransform at;
    private double scaleFactor = 1;
    private double xOffset = 0;
    private double yOffset = 0;
    private Point startPoint;
    private Point currentPoint;
    private Location hoveredLocation; 

    public MapPanel(BufferedImage image, SimpleGraph map, UserPanel userPanel) {

        this.image = image;
        this.map = map;
        this.userPanel = userPanel;
        initComponent();

    }

    private void initComponent() {
        ClickListener clickListener = new ClickListener(userPanel);
        MouseMotionListener dragListener = new MouseMotionListener(this,userPanel);
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
                if (scaleFactor < 5) {
                    scaleFactor *= 1.1;
                }
            } else if (e.getWheelRotation() > 0) {
                if (scaleFactor > 0.8) {
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
        private UserPanel userPanel;
        public ClickListener(UserPanel userPanel){
            this.userPanel = userPanel;
        }
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            resetLocation();
            repaint();
        }

        public void mouseClicked(MouseEvent e){
            if(hoveredLocation != null){
                userPanel.finishChoose(hoveredLocation);
            }
        }
    }

    private class MouseMotionListener extends MouseMotionAdapter {
        private MapPanel mapPanel;
        private UserPanel userPanel;

        public MouseMotionListener(MapPanel mapPanel, UserPanel userPanel) {
            this.mapPanel = mapPanel;
            this.userPanel = userPanel;
        }

        public void mouseDragged(MouseEvent e) {
            currentPoint = e.getPoint();
            double moveX = currentPoint.getX() - startPoint.getX();
            double moveY = currentPoint.getY() - startPoint.getY();
            xOffset += moveX;
            yOffset += moveY;
            startPoint = currentPoint;
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            double mouseInPaneX = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double mouseInPaneY = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
            Point2D cursorLocation = new Point2D.Double(mouseInPaneX, mouseInPaneY);
            try {
                AffineTransform inverse = at.createInverse();
                inverse.transform(cursorLocation, cursorLocation);
                hoveredLocation = map.contains(cursorLocation);
                //this is where I change the textfield for userPanel
                userPanel.setTextField(hoveredLocation);
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
        System.out.println("\nx: " + xOnPanel + " y: " + yOnPanel + " \nwidth: " + imageScaleWidth + " height: "
                + imageScaleHeight + " \npanel width: " + panelWidth + " panel height: " + panelHeight);
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
