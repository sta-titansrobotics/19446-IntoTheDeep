package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class VSlideController {
    private final DcMotor slideVL, slideVR;
    private final Servo tilt1Left;
    private final Servo tilt1Right;
    public final Servo tilt2;
    private final Servo roll;
    private final Servo claw;
    private final OpMode opMode;
    private Thread slideThread;
    private volatile boolean opModeActive = true; // Flag to safely stop the thread
    private static final int MAX_POSITION = 2200; // Updated maximum position
    private boolean isHighBasket = false; // Track the last called method

    // Constructor
    public VSlideController(HardwareMap hardwareMap, OpMode opMode) {
        this.opMode = opMode;

        // Initialize servos
        tilt1Left = hardwareMap.get(Servo.class, "tilt1L"); // expansion port 0
        tilt1Right = hardwareMap.get(Servo.class, "tilt1R"); // expansion port 1
        tilt2 = hardwareMap.get(Servo.class, "tilt2"); // expansion port 2
        roll = hardwareMap.get(Servo.class, "roll"); // expansion port 3
        claw = hardwareMap.get(Servo.class, "claw"); // expansion port 4

        tilt1Right.setDirection(Servo.Direction.REVERSE);

        // Initialize motors
        slideVL = hardwareMap.get(DcMotor.class, "lvSlide");
        slideVR = hardwareMap.get(DcMotor.class, "rvSlide");

        slideVL.setDirection(DcMotor.Direction.REVERSE);

        initializeMotors();
    }

    // Initialize motors
    public void initializeMotors() {
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
    private int getCurrentPosition(DcMotor motor) {
        return motor.getCurrentPosition();
    }

    public int getCurrentVPos() {
        return (getCurrentPosition(slideVR) + getCurrentPosition(slideVL)) / 2;
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
                int currentPosition = (getCurrentPosition(slideVR) + getCurrentPosition(slideVL)) / 2; // average of two sliders

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
        claw.setPosition(0.8);
        updateTelemetry();
    }

    public void closeClaw() {
        claw.setPosition(0.5);
        updateTelemetry();
    }

    public void rollClawUp() {
        roll.setPosition(0.156);
        updateTelemetry();
    }

    public void rollClawDown() {
        roll.setPosition(0.816);
        updateTelemetry();
    }

    public void tiltArmUp() {
        tilt1Left.setPosition(0.3);
        tilt1Right.setPosition(0.3);
        updateTelemetry();
    }

    public void tilt1ArmZero() {
        tilt1Left.setPosition(0.508);
        tilt1Right.setPosition(0.508);
        updateTelemetry();
    }

    public void transferPos() {
        tilt1ArmZero();
        tilt2.setPosition(0.505);
        openClaw();
        rollClawDown();
    }

    public void vSlideManualEg(int position) {
        position = Math.max(30, position);
        position = Math.min(MAX_POSITION, position);

        slideVL.setTargetPosition(position);
        slideVR.setTargetPosition(position);
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.5);
        slideVR.setPower(0.5);
    }

    // VSlideController.java
    public void tiltArmManualControl(double position) {

        position = Math.max(0.2, position);
        position = Math.min(0.95, position);

        tilt1Left.setPosition(position);
        tilt1Right.setPosition(position);
        updateTelemetry();
    }
    public void RollManualControl(double increment) {
        double newPosition = roll.getPosition() + increment;
        newPosition = Math.max(0.2, newPosition);
        newPosition = Math.min(0.8, newPosition);

        roll.setPosition(newPosition);
        updateTelemetry();
    }

    public void fdsa(double lol){
        tilt2.setPosition(lol);
    }

    public void VSlideHighBasket() {
        // Placeholder for high basket code
        slideVL.setTargetPosition(MAX_POSITION);
        slideVR.setTargetPosition(MAX_POSITION);
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.8);
        slideVR.setPower(0.8);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 700){

        }
        rollClawUp();
        tilt2.setPosition(0.6); //cave inwards
        // vSliderSystem.tiltArmUp();
        isHighBasket = true;
        tiltArmManualControl(1); //set it to max tilt
    }

    public void VSlideHighRung() {
        slideVL.setTargetPosition(1800); //PLACEHOLDER
        slideVR.setTargetPosition(1800); //PLACEHOLDER
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.5);
        slideVR.setPower(0.5);
        isHighBasket = false;

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 700){

        }

        tiltArmManualControl(0.78); //set it to max tilt
        tilt2.setPosition(0.4);
        rollClawDown();
    }

    public void pickUpFromWall(){
        slideVL.setTargetPosition(500); //PLACEHOLDER
        slideVR.setTargetPosition(500); //PLACEHOLDER
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.5);
        slideVR.setPower(0.5);

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 700) {

        }
        tiltArmManualControl(0.1);
        tilt2.setPosition(0.6);
        openClaw();
        start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 700) {

        }
        slideVL.setTargetPosition(0); //PLACEHOLDER
        slideVR.setTargetPosition(0); //PLACEHOLDER
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.5);
        slideVR.setPower(0.5);
    }


    public void tilt2Ctrl(int pos){
        if (pos < 0.8 && pos > 0.2){
            tilt2.setPosition(pos);
        }

    }
    public void vSlideDrop() {
        long start = System.currentTimeMillis();

            // Code for dropping from high rung
            // Placeholder for high rung drop code
            openClaw();
            vSlideManualEg(900);
            while (System.currentTimeMillis() - start < 800){

            }
    }


    // Update telemetry with motor and servo data
    private void updateTelemetry() {
        opMode.telemetry.addData("VSlide Left", getCurrentPosition(slideVL));
        opMode.telemetry.addData("VSlide Right", getCurrentPosition(slideVR));
        opMode.telemetry.addData("Claw", claw.getPosition());
        opMode.telemetry.addData("Roll", roll.getPosition());
        opMode.telemetry.addData("Tilt1 Left", tilt1Left.getPosition());
        opMode.telemetry.addData("Tilt1 Right", tilt1Right.getPosition());
        opMode.telemetry.addData("Tilt2", tilt2.getPosition());

        opMode.telemetry.update();
    }

    public DcMotor getSlideMotor() {
        return slideVL;
    }
}