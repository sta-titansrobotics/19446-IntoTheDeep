package robotControl47_2425.Sliders;

import robotControl47_2425.Sliders.VSlide;

public class VSlideController {
    private final VSlide sliderV1 , sliderV2;
    private Thread slideThread;
    private volatile boolean opModeActive = true; // Flag to safely stop the thread

    // Constructor
    public VSlideController(VSlide sliderV1, VSlide sliderV2) {
        this.sliderV1 = sliderV1;
        this.sliderV2 = sliderV2;
    }

    // Method to start the slide control thread
    public void start() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = sliderV1.getCurrentPosition();

                // Control logic for slide movement
                if (currentPosition >= sliderV1.getMaxPosition()) {
                    sliderV1.goToPosition(0);
                    sliderV2.goToPosition(0);
                    reached = true;
                } else if (!reached) {
                    sliderV1.goToPosition(sliderV1.getMaxPosition());
                    sliderV2.goToPosition(sliderV2.getMaxPosition());
                }

                // Pause briefly to prevent excessive CPU usage
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        slideThread.start();
    }

    // Method to stop the slide control thread
    public void stop() {
        opModeActive = false; // Signal the thread to stop
        if (slideThread != null && slideThread.isAlive()) {
            slideThread.interrupt(); // Interrupt the thread if it's running
            try {
                slideThread.join(); // Wait for the thread to finish safely
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}