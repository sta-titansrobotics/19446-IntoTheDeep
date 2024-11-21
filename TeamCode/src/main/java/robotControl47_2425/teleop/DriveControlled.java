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

            // Add telemetry data
            telemetry.addData("HSlide Position", hSlide.getCurrentPosition());
            telemetry.addData("HSlide Extended", hSlideExtended);
            telemetry.addData("VSlide Position", vSlideController.getCurrentPosition(vSlideController.getSlideMotor()));
            telemetry.addData("VSlide Extended", vSlideExtended);
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
        double y = -gamepad1.left_stick_y / 3; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1 / 3; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x / 3;

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