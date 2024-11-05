package auto;

public class SlideController {
    private final HSlide sliderH;
    private Thread slideThread;
    private volatile boolean opModeActive = true; // Flag to safely stop the thread

    // Constructor
    public SlideController(HSlide sliderH) {
        this.sliderH = sliderH;
    }

    // Method to start the slide control thread
    public void start() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = sliderH.getCurrentPosition();

                // Control logic for slide movement
                if (currentPosition >= 1000) {
                    sliderH.goToPosition(0);
                    reached = true;
                } else if (!reached) {
                    sliderH.goToPosition(1000);
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
