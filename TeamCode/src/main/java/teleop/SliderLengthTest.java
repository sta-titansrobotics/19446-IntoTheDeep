package teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "SliderLengthTest", group = "Test")
public class SliderLengthTest extends LinearOpMode {

    private DcMotor slideMotor;
    private int minPosition = Integer.MAX_VALUE;
    private int maxPosition = Integer.MIN_VALUE;

    @Override
    public void runOpMode() {
        slideMotor = hardwareMap.get(DcMotor.class, "vSlide");

        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            double power = -gamepad1.left_stick_y;
            slideMotor.setPower(power);

            int currentPosition = slideMotor.getCurrentPosition();
            if (currentPosition < minPosition) {
                minPosition = currentPosition;
            }
            if (currentPosition > maxPosition) {
                maxPosition = currentPosition;
            }

            telemetry.addData("Current Position", currentPosition);
            telemetry.addData("Min Position", minPosition);
            telemetry.addData("Max Position", maxPosition);
            telemetry.update();

            // H SLIDE: 0 TO -2000
        }
    }
}