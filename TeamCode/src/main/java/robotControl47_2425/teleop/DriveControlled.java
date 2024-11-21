package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import robotControl47_2425.Sliders.HSlide;
import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "TeleopSliderTest", group = "Test")
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
            // Control HSlide with gamepad1.a
            if (gamepad1.a && !previousAState) {
                if (!hSlideExtended) {
                    hSlide.goToPosition(hSlide.getMaxPosition());
                } else {
                    hSlide.goToPosition(0);
                }
                hSlideExtended = !hSlideExtended;
            }
            previousAState = gamepad1.a;

            // Control VSlide with gamepad1.b
            if (gamepad1.b && !previousBState) {
                if (!vSlideExtended) {
                    vSlideController.goToPosition(vSlideController.getMaxPosition());
                } else {
                    vSlideController.goToPosition(0);
                }
                vSlideExtended = !vSlideExtended;
            }
            previousBState = gamepad1.b;

            // Gamepad macros for servos
            // Toggle claw open/close with gamepad1.x
            if (gamepad1.x && !previousXState) {
                if (!clawOpen) {
                    vSlideController.openClaw();
                } else {
                    vSlideController.closeClaw();
                }
                clawOpen = !clawOpen;
            }
            previousXState = gamepad1.x;

            // Toggle arm tilt up/down with gamepad1.y
            if (gamepad1.y && !previousYState) {
                if (!armTiltedUp) {
                    vSlideController.tiltArmUp();
                } else {
                    vSlideController.tiltArmDown();
                }
                armTiltedUp = !armTiltedUp;
            }
            previousYState = gamepad1.y;

            // Toggle arm tilt up/down with gamepad1.dpad_up
            if (gamepad1.dpad_up && !previousDpadUpState) {
                if (!armTiltedUp) {
                    vSlideController.tiltArmUp();
                } else {
                    vSlideController.tiltArmDown();
                }
                armTiltedUp = !armTiltedUp;
            }
            previousDpadUpState = gamepad1.dpad_up;

            // Toggle arm tilt up/down with gamepad1.dpad_down
            if (gamepad1.dpad_down && !previousDpadDownState) {
                if (!armTiltedUp) {
                    vSlideController.tiltArmUp();
                } else {
                    vSlideController.tiltArmDown();
                }
                armTiltedUp = !armTiltedUp;
            }
            previousDpadDownState = gamepad1.dpad_down;

            // Toggle claw roll up/down with gamepad1.dpad_left
            if (gamepad1.dpad_left && !previousDpadLeftState) {
                if (!clawRolledUp) {
                    vSlideController.rollClawUp();
                } else {
                    vSlideController.rollClawDown();
                }
                clawRolledUp = !clawRolledUp;
            }
            previousDpadLeftState = gamepad1.dpad_left;

            // Toggle claw roll up/down with gamepad1.dpad_right
            if (gamepad1.dpad_right && !previousDpadRightState) {
                if (!clawRolledUp) {
                    vSlideController.rollClawUp();
                } else {
                    vSlideController.rollClawDown();
                }
                clawRolledUp = !clawRolledUp;
            }
            previousDpadRightState = gamepad1.dpad_right;

            // Add telemetry data
            telemetry.addData("HSlide Position", hSlide.getCurrentPosition());
            telemetry.addData("HSlide Extended", hSlideExtended);
            telemetry.addData("VSlide Position", vSlideController.getCurrentPosition(vSlideController.getSlideMotor()));
            telemetry.addData("VSlide Extended", vSlideExtended);

            // Add telemetry for motor powers
            telemetry.addData("Front Left Power", fl.getPower());
            telemetry.addData("Front Right Power", fr.getPower());
            telemetry.addData("Back Left Power", bl.getPower());
            telemetry.addData("Back Right Power", br.getPower());

            telemetry.update();

            telemetryDrive();
        }
    }

    public void InitializeMotors() {
        fl = hardwareMap.get(DcMotor.class, "fl");
        fr = hardwareMap.get(DcMotor.class, "fr");
        bl = hardwareMap.get(DcMotor.class, "bl");
        br = hardwareMap.get(DcMotor.class, "br");

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void telemetryDrive() {
        // Driving
        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        fl.setPower(frontLeftPower);
        bl.setPower(backLeftPower);
        fr.setPower(frontRightPower);
        br.setPower(backRightPower);
    }
}