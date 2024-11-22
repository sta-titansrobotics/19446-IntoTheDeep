package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "46TeleOp", group = "Test")
public class DriveControlled extends LinearOpMode {

    private HSlideController hSliderSystem;
    private VSlideController vSliderSystem;
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
        hSliderSystem = new HSlideController(hardwareMap, this);
        vSliderSystem = new VSlideController(hardwareMap, this);

        hSliderSystem.initialize();
        vSliderSystem.initializeMotors();

        InitializeMotors();

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            hSlideManualControl();
            handleServoControl();
            updateTelemetry();
            telemetryDrive();
            vSliderCtrl();
            armSyncCtrl();
            tiltArmCtrl();
        }
    }

    private void armSyncCtrl() {
        if (gamepad2.a) {
            vSliderSystem.transferPos();
        }
    }

    private void hSlideManualControl() {
        if (gamepad1.a && !previousAState) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() + 15);
        }
        previousAState = gamepad1.a;

        if (gamepad1.b && !previousBState) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() - 15);
        }
        previousBState = gamepad1.b;
    }

    private void handleServoControl() {
        if (gamepad1.x && !previousXState) {
            toggleClaw();
        }
        previousXState = gamepad1.x;

        if ((gamepad1.y && !previousYState) || (gamepad1.dpad_up && !previousDpadUpState) || (gamepad1.dpad_down && !previousDpadDownState)) {
            toggleArmTilt();
        }
        previousYState = gamepad1.y;
        previousDpadUpState = gamepad1.dpad_up;
        previousDpadDownState = gamepad1.dpad_down;

        if ((gamepad1.dpad_left && !previousDpadLeftState) || (gamepad1.dpad_right && !previousDpadRightState)) {
            toggleClawRoll();
        }
        previousDpadLeftState = gamepad1.dpad_left;
        previousDpadRightState = gamepad1.dpad_right;
    }

    private void toggleHSlide() {
        if (!hSlideExtended) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos());
        } else {
            hSliderSystem.goToPosition(0);
        }
        hSlideExtended = !hSlideExtended;
    }

    private void vSliderCtrl() {
        if (gamepad1.right_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(10);
        } else if (gamepad1.left_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(-10);
        }
    }

    private void toggleVSlide() {
        if (!vSlideExtended) {
            vSliderSystem.goToPosition(vSliderSystem.getMaxPosition());
        } else {
            vSliderSystem.goToPosition(0);
        }
        vSlideExtended = !vSlideExtended;
    }

    private void toggleClaw() {
        if (!clawOpen) {
            vSliderSystem.openClaw();
        } else {
            vSliderSystem.closeClaw();
        }
        clawOpen = !clawOpen;
    }

    private void toggleArmTilt() {
        if (!armTiltedUp) {
            vSliderSystem.tiltArmUp();
        } else {
            vSliderSystem.tilt1ArmZero();
        }
        armTiltedUp = !armTiltedUp;
    }

    private void toggleClawRoll() {
        if (!clawRolledUp) {
            vSliderSystem.rollClawUp();
        } else {
            vSliderSystem.rollClawDown();
        }
        clawRolledUp = !clawRolledUp;
    }

    private void updateTelemetry() {
        telemetry.addData("HSlide Position", hSliderSystem.getCurrentPos());
        telemetry.addData("HSlide Extended", hSlideExtended);
        telemetry.addData("VSlide Position", vSliderSystem.getCurrentVPos());
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
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
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

    void gamepad1Ctrl() {
        if (gamepad1.right_trigger > 0.2) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() + 10);
        }
        if (gamepad1.left_trigger > 0.2) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() - 10);
        }
        if (gamepad1.right_bumper) {
            hSliderSystem.intaking();
        } else if (gamepad1.left_bumper) {
            hSliderSystem.outtaking();
        } else {
            hSliderSystem.setIntakePower(0);
        }

        if (gamepad1.a) {
            hSliderSystem.goToPosition(1000);
            hSliderSystem.rampDown();
        }

        if (gamepad1.b) {
            hSliderSystem.goToPosition(0);
            hSliderSystem.rampUp();
        }
    }

    void gamepad2Ctrl() {
        if (gamepad1.right_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(10);
        } else if (gamepad1.left_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(-10);
        }
    }

    // DriveControlled.java
    private void tiltArmCtrl() {
        if (gamepad1.dpad_up) {
            vSliderSystem.tiltArmManualControl(0.01);
        } else if (gamepad1.dpad_down) {
            vSliderSystem.tiltArmManualControl(-0.01);
        }
    }
}