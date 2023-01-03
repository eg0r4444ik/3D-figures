package cg;


import cg.draw.IDrawer;
import cg.draw.SimpleEdgeDrawer;
import cg.math.Vector3;
import cg.models.Helix3D;
import cg.models.Line3D;
import cg.models.Shamrock3D;
import cg.models.Torus3D;
import cg.screen.ScreenConverter;
import cg.third.Camera;
import cg.third.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawPanel extends JPanel
        implements CameraController.RepaintListener {
    private Scene scene;
    private ScreenConverter sc;
    private Camera cam;
    private CameraController camController;

    public DrawPanel() {
        super();
        sc = new ScreenConverter(-1, 1, 2, 2, 1, 1);
        cam = new Camera();
        camController = new CameraController(cam, sc);
        scene = new Scene(Color.WHITE.getRGB());

        scene.getModelsList().add(
                new Helix3D(3, 0.1, 0.5, 0.1));
//        scene.getModelsList().add(new Torus3D(0.1,0.5,0.1));


//        scene.getModelsList().add(new Shamrock3D(0.1,0.2,0.4));



//        scene.getModelsList().add(new Line3D(new Vector3(0, 0, 0), new Vector3(0, 0, 1)));
//        scene.getModelsList().add(new Line3D(new Vector3(0, 0, 0), new Vector3(0, 1, 0)));
//        scene.getModelsList().add(new Line3D(new Vector3(0, 0, 0), new Vector3(1, 0, 0)));

//        scene.getModelsList().add(new Line3D(new Vector3(0, 0, 0),
//                Matrix4Factories.rotationXYZ(-Math.PI/6, 2).mul(new Vector4(1, 0, 0, 0)).asVector3()));

        camController.addRepaintListener(this);
        addMouseListener(camController);
        addMouseMotionListener(camController);
        addMouseWheelListener(camController);
    }

    @Override
    public void paint(Graphics g) {
        sc.setScreenSize(getWidth(), getHeight());
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        IDrawer dr = new SimpleEdgeDrawer(sc, graphics);
        scene.drawScene(dr, cam);
        g.drawImage(bi, 0, 0, null);
        graphics.dispose();
    }

    @Override
    public void shouldRepaint() {
        repaint();
    }
}
