package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HSlideController {
    private final DcMotor sliderH, intakeMotor;
    private final Servo ramp;
    private Thread slideThread;
    private volatile boolean opModeActive = true;
    private static final int MAX_POSITION = 2000;

    public HSlideController(HardwareMap hardwareMap, OpMode opMode) {
        // Initialize hardware
        sliderH = hardwareMap.get(DcMotor.class, "hslide");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        ramp = hardwareMap.get(Servo.class, "ramp");

        // Initialize components
        initialize();

    }
    private void initialize() {
        // Initialize slider motor
        sliderH.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sliderH.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //sliderH.setDirection(DcMotor.Direction.REVERSE);

        // Initialize intake motor
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);

        // Initialize intake servo
        ramp.setPosition(0.5); // Default to 0.5 position
    }

    // Start a thread for the horizontal slide functionality
    public void startSlideControl() {
        slideThread = new Thread(() -> {
            boolean reached = false;
            while (opModeActive && !Thread.currentThread().isInterrupted()) {
                int currentPosition = sliderH.getCurrentPosition();

                if (currentPosition >= MAX_POSITION) {
                    goToPosition(0, sliderH);
                    reached = true;
                } else if (!reached) {
                    goToPosition(MAX_POSITION, sliderH);
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

    // Generic method to reset a motor's position
    public void resetPosition(DcMotor motor) {
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    // Generic method to move a motor to a specific position
    public void goToPosition(int position, DcMotor motor) {
        position = Math.max(0, position);
        position = Math.min(MAX_POSITION, position);

        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    // Get the current position of a motor
    public int getCurrentPosition(DcMotor motor) {
        return motor.getCurrentPosition();
    }

    /**
     * Brings the Hslide motor to 0 and reset the servo position to 0.5
     */
    public void initHSlideController(){
        resetPosition(sliderH);
        ramp.setPosition(0.5);
    }

    // Stop the slide thread
    public void stopSlideControl() {
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

    // Intake motor control methods
    public void setIntakePower(double power) {
        intakeMotor.setPower(power);
    }
}
