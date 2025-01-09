package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HSlideController {
    private final DcMotor slideH, intake;
    private final Servo ramp;
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
        slideH.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideH.setDirection(DcMotor.Direction.REVERSE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setDirection(DcMotor.Direction.FORWARD);

        ramp = hardwareMap.get(Servo.class, "ramp");


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
        slideH.setPower(1);
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
        ramp.setPosition(0);

    }
    public void rampDown(){
        ramp.setPosition(0.51);
    }
}
