package robotControl47_2425.Sliders;

import robotControl47_2425.Sliders.HSlide;

public class HSlideController {
    private final HSlide sliderH;
    private Thread slideThread;
    private volatile boolean opModeActive = true;

    public HSlideController(HSlide sliderH) {
        this.sliderH = sliderH;
    }

    public void start() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = sliderH.getCurrentPosition();

                if (currentPosition >= sliderH.getMaxPosition()) {
                    sliderH.goToPosition(0);
                    reached = true;
                } else if (!reached) {
                    sliderH.goToPosition(sliderH.getMaxPosition());
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        slideThread.start();
    }

    public void stop() {
        opModeActive = false;
        if (slideThread != null && slideThread.isAlive()) {
            slideThread.interrupt();
            try {
                slideThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}