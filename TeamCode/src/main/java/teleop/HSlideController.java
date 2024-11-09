package teleop;

public class HSlideController {
    private final HSlide sliderV;
    private Thread slideThread;
    private volatile boolean opModeActive = true;

    public HSlideController(HSlide sliderV) {
        this.sliderV = sliderV;
    }

    public void start() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = sliderV.getCurrentPosition();

                if (currentPosition >= 1000) {
                    sliderV.goToPosition(0);
                    reached = true;
                } else if (!reached) {
                    sliderV.goToPosition(1000);
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