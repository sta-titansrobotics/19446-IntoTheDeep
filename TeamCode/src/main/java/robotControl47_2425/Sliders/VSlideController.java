package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class VSlideController {
    private final DcMotor slideVL, slideVR;
    private final Servo servo1, servo2, servo3, servo4, servo5, claw;
    private final OpMode opMode;
    private Thread slideThread;
    private volatile boolean opModeActive = true; // Flag to safely stop the thread
    private static final int MAX_POSITION = 2200; // Updated maximum position

    // Constructor
    public VSlideController(HardwareMap hardwareMap, OpMode opMode) {
        this.opMode = opMode;

        // Initialize servos
        claw = hardwareMap.get(Servo.class, "claw");
        servo1 = hardwareMap.get(Servo.class, "servo1");
        servo2 = hardwareMap.get(Servo.class, "servo2");
        servo3 = hardwareMap.get(Servo.class, "servo3");
        servo4 = hardwareMap.get(Servo.class, "servo4");
        servo5 = hardwareMap.get(Servo.class, "servo5");

        //Initialzie motors
        slideVL = hardwareMap.get(DcMotor.class, "vSlide1");
        slideVR = hardwareMap.get(DcMotor.class, "vSlide2");

        initializeMotors();
    }



    // Initialize motors
    private void initializeMotors() {
        slideVL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideVR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideVL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideVR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // Method to move motors to a position
    public void goToPosition(int position) {
        position = Math.max(0, position);
        position = Math.min(MAX_POSITION, position);

        slideVL.setTargetPosition(position);
        slideVR.setTargetPosition(position);
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.5);
        slideVR.setPower(0.5);
    }

    // Method to reset motors to position 0
    public void resetPosition() {
        goToPosition(0);
    }

    // Get current position of the first motor
    public int getCurrentPosition(DcMotor motor) {
        return motor.getCurrentPosition();
    }

    // Get max position
    public int getMaxPosition() {
        return MAX_POSITION;
    }

    // Method to start the slide control thread
    public void start() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = (getCurrentPosition(slideVR)+getCurrentPosition(slideVL))/2; //average of two sliders

                // Control logic for slide movement
                if (currentPosition >= MAX_POSITION) {
                    resetPosition();
                    reached = true;
                } else if (!reached) {
                    goToPosition(MAX_POSITION);
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

    // Servo control methods
    public void openClaw() {
        claw.setPosition(0.9);
        updateTelemetry();
    }

    public void closeClaw() {
        claw.setPosition(0.5);
        updateTelemetry();
    }

    public void rollClawUp() {
        claw.setPosition(0.156);
        updateTelemetry();
    }

    public void rollClawDown() {
        claw.setPosition(0.816);
        updateTelemetry();
    }

    // Update telemetry with motor and servo data
    private void updateTelemetry() {
        opMode.telemetry.addData("VSlide Left", getCurrentPosition(slideVL));
        opMode.telemetry.addData("VSlide Right", getCurrentPosition(slideVR));
        opMode.telemetry.addData("Claw Position", claw.getPosition());
        opMode.telemetry.addData("Servo1 Position", servo1.getPosition());
        opMode.telemetry.addData("Servo2 Position", servo2.getPosition());
        opMode.telemetry.addData("Servo3 Position", servo3.getPosition());
        opMode.telemetry.addData("Servo4 Position", servo4.getPosition());
        opMode.telemetry.addData("Servo5 Position", servo5.getPosition());
        opMode.telemetry.update();
    }
}
