package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;


public class HSlideController {
    private final DcMotor slideH;
    private Servo boot;
    private Servo tiltL, tiltR;
    // tiltL transfer pos is 0
    // tiltR transfer pos is 1 without reverse direction
    private CRServo intakeL, intakeR;
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


        //ramp = hardwareMap.get(Servo.class, "ramp");

        boot = hardwareMap.get(Servo.class, "boot");
        tiltL = hardwareMap.get(Servo.class, "inTiltL");
        tiltR = hardwareMap.get(Servo.class, "inTiltR");

        intakeL = hardwareMap.get(CRServo.class, "inL");
        intakeR = hardwareMap.get(CRServo.class, "inR");
        intakeL.setDirection(CRServo.Direction.REVERSE);

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


    public void bootUp(){
        boot.setPosition(0.3);
    }

    public void bootHalf(){
        boot.setPosition(0.8);
    }
    public void bootDown(){
        boot.setPosition(1);
    }


    public void intake(){
        intakeL.setPower(0.8);
        intakeR.setPower(0.8);
    }
    public void idleIntake(){
        intakeL.setPower(0);
        intakeR.setPower(0);
    }
    public void outtake(){
        intakeL.setPower(-0.8);
        intakeR.setPower(-0.8);
    }

    public void tiltTransfer(){
        tiltL.setPosition(0.03);
        tiltR.setPosition(0.96);
    }

    public void tiltIntake(){
        tiltL.setPosition(0.74);
        tiltR.setPosition(0.23);
    }
}

