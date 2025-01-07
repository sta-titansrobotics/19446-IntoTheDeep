package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class VSlideController {
    private DcMotor slideL, slideR;
    private Servo tiltL, tiltR, clawL, clawR;
    private final OpMode opMode;
    private final HardwareMap hardwareMap;
    private Thread slideThread;
    private volatile boolean opModeActive = true; // Flag to safely stop the thread
    private static final int MAX_POSITION = 2400; // Updated maximum position
    private boolean isHighBasket = false; // Track the last called method

    // Constructor
    public VSlideController(OpMode opMode) {
        this.opMode = opMode;
        this.hardwareMap = opMode.hardwareMap;

        // Initialize motors
        slideL = hardwareMap.get(DcMotor.class, "lvSlide");
        slideR = hardwareMap.get(DcMotor.class, "rvSlide");

        slideR.setDirection(DcMotor.Direction.REVERSE);

        slideL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        initializeServos();
        //resetVSlidePos();
        //should only initialize for auto, the resets carry into teleOp
    }

    //should only initialize for auto, the resets carry into teleOp
    public void resetVSlidePos() {
        slideL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void initializeServos(){
        // Initialize servos
        tiltL = hardwareMap.get(Servo.class, "tiltL"); // expansion port 0
        tiltR = hardwareMap.get(Servo.class, "tiltR"); // expansion port 1
        clawL = hardwareMap.get(Servo.class, "clawL"); // expansion port 3
        clawR = hardwareMap.get(Servo.class, "clawR"); // expansion port 4

        tiltR.setDirection(Servo.Direction.REVERSE);
    }

    // Method to move motors to a position
    public void stepCtrl(int step) {
        int targetPos = Math.min(MAX_POSITION, Math.max(20, getCurrentPos() + step));

        slideL.setTargetPosition(targetPos);
        slideR.setTargetPosition(targetPos);
        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideL.setPower(0.2);
        slideR.setPower(0.2);
    }

    public void goToPos(int pos){
        int targetPos = Math.min(MAX_POSITION, Math.max(20, pos));

        slideL.setTargetPosition(targetPos);
        slideR.setTargetPosition(targetPos);
        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideL.setPower(0.4);
        slideR.setPower(0.4);
    }
    public void openClaw() {
        clawL.setPosition(0.4);
        clawR.setPosition(0.63);
    }

    public void closeClaw() {
        clawL.setPosition(0.53);
        clawR.setPosition(0.5);
    }

    public void tiltToPos(double pos){
        pos = pos > 0.14 ? (pos < 1 ? pos : 1) : 0.14;
        tiltL.setPosition(pos);
        tiltR.setPosition(pos);
    }

    public double getTiltPos(){
        return tiltL.getPosition();
    }

    public void tiltStepCtrl (double step){

        double targetPos = Math.min(1.0, Math.max(0.1, tiltL.getPosition() + step));
        tiltL.setPosition(targetPos);
//        tiltR.setPosition(targetPos);
    }




    public int getCurrentPos() {
        return (slideR.getCurrentPosition() + slideL.getCurrentPosition()) / 2;
    }



    // Method to start the slide control thread
//    public void start() {
//        slideThread = new Thread(() -> {
//            boolean reached = false;
//            while (opModeActive && !Thread.currentThread().isInterrupted()) {
//                int currentPosition = getCurrentPos(); // average of two sliders
//
//                // Control logic for slide movement
//                if (currentPosition >= MAX_POSITION) {
//                    resetPosition();
//                    reached = true;
//                } else if (!reached) {
//                    goToPosition(MAX_POSITION);
//                }
//
//                // Pause briefly to prevent excessive CPU usage
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        });
//
//        slideThread.start();
//    }
//
//    // Method to stop the slide control thread
//    public void stop() {
//        opModeActive = false; // Signal the thread to stop
//        if (slideThread != null && slideThread.isAlive()) {
//            slideThread.interrupt(); // Interrupt the thread if it's running
//            try {
//                slideThread.join(); // Wait for the thread to finish safely
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
    // Servo control methods

//
//    public void rollClawUp() {
//        roll.setPosition(0.156);
//        updateTelemetry();
//    }
//
//    public void rollClawDown() {
//        roll.setPosition(0.816);
//        updateTelemetry();
//    }
//
//    public void tiltArmUp() {
//        tilt1Left.setPosition(0.3);
//        tilt1Right.setPosition(0.3);
//        updateTelemetry();
//    }
//
//    public void tilt1ArmZero() {
//        tilt1Left.setPosition(0.5065);
//        tilt1Right.setPosition(0.5065);
//        updateTelemetry();
//    }
//
//    public void transferPos() {
//        tilt1ArmZero();
//        tilt2.setPosition(0.505);
//        openClaw();
//        rollClawDown();
//    }
//
//
//    // VSlideController.java
//    public void tiltArmManualControl(double position) {
//
//        position = Math.max(0.2, position);
//        position = Math.min(0.95, position);
//
//        tilt1Left.setPosition(position);
//        tilt1Right.setPosition(position);
//        updateTelemetry();
//    }
//    public void RollManualControl(double increment) {
//        double newPosition = roll.getPosition() + increment;
//        newPosition = Math.max(0.2, newPosition);
//        newPosition = Math.min(0.8, newPosition);
//
//        roll.setPosition(newPosition);
//        updateTelemetry();
//    }
//
//    public void fdsa(double lol){
//        tilt2.setPosition(lol);
//    }
//
//    public void VSlideHighBasket() {
//        // Placeholder for high basket code
//        slideL.setTargetPosition(MAX_POSITION);
//        slideR.setTargetPosition(MAX_POSITION);
//        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideL.setPower(0.8);
//        slideR.setPower(0.8);
//        long start = System.currentTimeMillis();
//        while (System.currentTimeMillis() - start < 700){
//
//        }
//        rollClawUp();
//        tilt2.setPosition(0.6); //cave inwards
//        // vSliderSystem.tiltArmUp();
//        isHighBasket = true;
//        tiltArmManualControl(1); //set it to max tilt
//    }
//
//    public void VSlideHighRung() {
//        slideL.setTargetPosition(1750);
//        slideR.setTargetPosition(1750);
//        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideL.setPower(0.5);
//        slideR.setPower(0.5);
//        rollClawUp();
//        isHighBasket = false;
//        long start = System.currentTimeMillis();
//        while (System.currentTimeMillis() - start < 700){
//
//        }
//
//        tiltArmManualControl(0.78); //set it to max tilt
//        tilt2.setPosition(0.4);
//        rollClawUp();
//    }
//
//    public void pickUpFromWall(){
//        openClaw();
//        long start = System.currentTimeMillis();
//        while(System.currentTimeMillis()-start<200){
//
//        }
//        slideL.setTargetPosition(500); //PLACEHOLDER
//        slideR.setTargetPosition(500); //PLACEHOLDER
//        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideL.setPower(0.5);
//        slideR.setPower(0.5);
//
//        start = System.currentTimeMillis();
//        while (System.currentTimeMillis() - start < 700) {
//
//        }
//        tiltArmManualControl(0.1);
//        tilt2.setPosition(0.6);
//        openClaw();
//        rollClawDown();
//        start = System.currentTimeMillis();
//        while (System.currentTimeMillis() - start < 700) {
//
//        }
//        slideL.setTargetPosition(0); //PLACEHOLDER
//        slideR.setTargetPosition(0); //PLACEHOLDER
//        slideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        slideL.setPower(0.5);
//        slideR.setPower(0.5);
//    }
//
//
//    public void tilt2Ctrl(int pos){
//        if (pos < 0.8 && pos > 0.2){
//            tilt2.setPosition(pos);
//        }
//
//    }
//    public void vSlideDrop() {
//        long start = System.currentTimeMillis();
//
//            // Code for dropping from high rung
//            // Placeholder for high rung drop code
//            openClaw();
//            vSlideManualEg(900);
//            while (System.currentTimeMillis() - start < 800){
//
//            }
//    }
//
//
//    // Update telemetry with motor and servo data
//    private void updateTelemetry() {
//        opMode.telemetry.addData("VSlide Left", getCurrentPosition(slideL));
//        opMode.telemetry.addData("VSlide Right", getCurrentPosition(slideR));
//        opMode.telemetry.addData("Claw", claw.getPosition());
//        opMode.telemetry.addData("Roll", roll.getPosition());
//        opMode.telemetry.addData("Tilt1 Left", tilt1Left.getPosition());
//        opMode.telemetry.addData("Tilt1 Right", tilt1Right.getPosition());
//        opMode.telemetry.addData("Tilt2", tilt2.getPosition());
//
//        opMode.telemetry.update();
//    }
//
//    public DcMotor getSlideMotor() {
//        return slideL;
//    }
}