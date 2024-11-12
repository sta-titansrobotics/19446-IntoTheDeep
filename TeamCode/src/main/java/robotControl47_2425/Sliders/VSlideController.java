package robotControl47_2425.Sliders;

import robotControl47_2425.Sliders.VSlide;

public class VSlideController {
    private final VSlide sliderV;
    private Thread slideThread;
    private volatile boolean opModeActive = true; // Flag to safely stop the thread

    // Constructor
    public VSlideController(VSlide sliderV) {
        this.sliderV = sliderV;
    }

    // Method to start the slide control thread
    public void start() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = sliderV.getCurrentPosition();

                // Control logic for slide movement
                if (currentPosition >= sliderV.getMaxPosition()) {
                    sliderV.goToPosition(0);
                    reached = true;
                } else if (!reached) {
                    sliderV.goToPosition(sliderV.getMaxPosition());
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