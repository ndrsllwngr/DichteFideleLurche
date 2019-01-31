package view.client;

import controller.Register;
import javafx.scene.shape.Rectangle;

public class HelpAnimation extends Thread {
    final int mill;
    final Rectangle rectangle;

    public HelpAnimation(int mill, Rectangle rectangle) {
        this.mill = mill;
        this.rectangle = rectangle;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(mill);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Register.getViewClientController().clearAnimation(rectangle);
    }
}
