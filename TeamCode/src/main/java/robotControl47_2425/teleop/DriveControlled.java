package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "46TeleOp", group = "Test")
public class DriveControlled extends LinearOpMode {

    private HSlide hSlide;
    private VSlideController vSlideController;
    private boolean hSlideExtended = false;
    private boolean vSlideExtended = false;
    private boolean previousAState = false;
    private boolean previousBState = false;
    private boolean previousXState = false;
    private boolean previousYState = false;
    private boolean previousDpadUpState = false;
    private boolean previousDpadDownState = false;
    private boolean previousDpadLeftState = false;
    private boolean previousDpadRightState = false;
    private boolean clawOpen = false;
    private boolean armTiltedUp = false;
    private boolean clawRolledUp = false;
    private DcMotor fl, fr, bl, br;

    @Override
    public void runOpMode() {
        DcMotor hSlideMotor = hardwareMap.get(DcMotor.class, "hSlide");

        hSlide = new HSlide(hSlideMotor);
        vSlideController = new VSlideController(hardwareMap, this);

        hSlide.initialize();
        vSlideController.initializeMotors();

        InitializeMotors();

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            hSlideControl();
            handleServoControl();
            updateTelemetry();
            telemetryDrive();
            vSliderCtrl();
            armSyncCtrl();
        }
    }

    private void armSyncCtrl(){
        // bring vslider into transfer pos
        if (gamepad2.a){

        }
    }

    private void hSlideControl() {
        // Control HSlide with gamepad1.a
        if (gamepad1.a && !previousAState) {
            toggleHSlide();
        }
        previousAState = gamepad1.a;

        // Control VSlide with gamepad1.b
        if (gamepad1.b && !previousBState) {
            toggleVSlide();
        }
        previousBState = gamepad1.b;
    }

    private void handleServoControl() {
        // Toggle claw open/close with gamepad1.x
        if (gamepad1.x && !previousXState) {
            toggleClaw();
        }
        previousXState = gamepad1.x;

        // Toggle arm tilt up/down with gamepad1.y or dpad_up/down
        if ((gamepad1.y && !previousYState) || (gamepad1.dpad_up && !previousDpadUpState) || (gamepad1.dpad_down && !previousDpadDownState)) {
            toggleArmTilt();
        }
        previousYState = gamepad1.y;
        previousDpadUpState = gamepad1.dpad_up;
        previousDpadDownState = gamepad1.dpad_down;

        // Toggle claw roll up/down with gamepad1.dpad_left/right
        if ((gamepad1.dpad_left && !previousDpadLeftState) || (gamepad1.dpad_right && !previousDpadRightState)) {
            toggleClawRoll();
        }
        previousDpadLeftState = gamepad1.dpad_left;
        previousDpadRightState = gamepad1.dpad_right;
    }

    private void toggleHSlide() {
        if (!hSlideExtended) {
            hSlide.goToPosition(hSlide.getMaxPosition());
        } else {
            hSlide.goToPosition(0);
        }
        hSlideExtended = !hSlideExtended;
    }

    private void vSliderCtrl(){
        if (gamepad1.right_trigger > 0.2){
            vSlideController.goToPosition(vSlideController.getCurrentVPos() + 10);
        }
    }

    private void toggleVSlide() {
        if (!vSlideExtended) {
            vSlideController.goToPosition(vSlideController.getMaxPosition());
        } else {
            vSlideController.goToPosition(0);
        }
        vSlideExtended = !vSlideExtended;
    }

    private void toggleClaw() {
        if (!clawOpen) {
            vSlideController.openClaw();
        } else {
            vSlideController.closeClaw();
        }
        clawOpen = !clawOpen;
    }

    private void toggleArmTilt() {
        if (!armTiltedUp) {
            vSlideController.tiltArmUp();
        } else {
            vSlideController.tiltArmDown();
        }
        armTiltedUp = !armTiltedUp;
    }

    private void toggleClawRoll() {
        if (!clawRolledUp) {
            vSlideController.rollClawUp();
        } else {
            vSlideController.rollClawDown();
        }
        clawRolledUp = !clawRolledUp;
    }

    private void updateTelemetry() {
        telemetry.addData("HSlide Position", hSlide.getCurrentPosition());
        telemetry.addData("HSlide Extended", hSlideExtended);
        telemetry.addData("VSlide Position", vSlideController.getCurrentVPos());
        telemetry.addData("VSlide Extended", vSlideExtended);

        telemetry.addData("Front Left Power", fl.getPower());
        telemetry.addData("Front Right Power", fr.getPower());
        telemetry.addData("Back Left Power", bl.getPower());
        telemetry.addData("Back Right Power", br.getPower());

        telemetry.update();
    }

    public void InitializeMotors() {
        fl = hardwareMap.get(DcMotor.class, "lf");
        fr = hardwareMap.get(DcMotor.class, "rf");
        bl = hardwareMap.get(DcMotor.class, "lr");
        br = hardwareMap.get(DcMotor.class, "rr");

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void telemetryDrive() {
        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;


        double frontLeftPower = (y + x + rx);
        double backLeftPower = (y - x + rx);
        double frontRightPower = (y - x - rx);
        double backRightPower = (y + x - rx);

        double denominator = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(backLeftPower), Math.max(Math.abs(frontRightPower), Math.max(Math.abs(backRightPower), 1))));

        fl.setPower(frontLeftPower / denominator);
        bl.setPower(backLeftPower / denominator);
        fr.setPower(frontRightPower / denominator);
        br.setPower(backRightPower / denominator);
    }
}