package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HSlideController {
    private final DcMotor slideH, intake;
    private Servo ramp, boot;
    private volatile boolean opModeActive = true;
    private static final int MAX_POSITION = 1500;

    private HardwareMap hardwareMap;
    private OpMode opMode;

    private long programStartTime;

    public HSlideController(OpMode opMode) {
        // Initialize hardware
        this.opMode = opMode;
        this.hardwareMap = opMode.hardwareMap;


        slideH = hardwareMap.get(DcMotor.class, "hSlide");
        slideH.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        slideH.setDirection(DcMotor.Direction.REVERSE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setDirection(DcMotor.Direction.FORWARD);

        //ramp = hardwareMap.get(Servo.class, "ramp");

        boot = hardwareMap.get(Servo.class, "boot");
    }

    public void resetHSlidePos() {
        slideH.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    // Generic method to move a motor to a specific position
    public void goToPos(int position) {
        position = Math.max(0, position);
        position = Math.min(MAX_POSITION, position);

        slideH.setTargetPosition(position);
        slideH.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideH.setPower(0.9);
    }
    public void goToPos(int position, double power) {
        position = Math.max(0, position);
        position = Math.min(MAX_POSITION, position);

        slideH.setTargetPosition(position);
        slideH.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideH.setPower(power);
    }

    // Get the current position of a motor
    public int getCurrentPos() {
        return slideH.getCurrentPosition();
    }

    /**
     * Brings the Hslide motor to 0 and reset the servo position to 0.5
     */
//    public void transferPos(){
//        long start = programStartTime;
//        while (programStartTime - start < 300){
//            ramp.setPosition(0.5);
//        }
//
//
//
//    }

    public void intaking(){
        intake.setPower(0.8);
    }

    public void outtaking(){
        intake.setPower(-0.8);
    }

    public void intakeOff(){
        intake.setPower(0);
    }

    public void rampUp(){
        ramp.setPosition(0.4);
        //ramp.setPosition(0.25);
    }

    public void rampHigh(){
        ramp.setPosition(0.25);
    }
    public void rampDown(){

        ramp.setPosition(0.55);
    }

//    public void rampHold(){
//        ramp.setDirection(Servo.Direction.FORWARD);
//    }
    public void bootUp(){
        boot.setPosition(0.3);
    }

    public void bootHalf(){
        boot.setPosition(0.8);
    }
    public void bootDown(){
        boot.setPosition(1);
    }

}

