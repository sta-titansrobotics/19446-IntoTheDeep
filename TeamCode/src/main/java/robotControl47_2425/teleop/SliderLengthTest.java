package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "SliderLengthTest", group = "Test")
public class SliderLengthTest extends LinearOpMode {

    private DcMotor slideVL, slideVR;
    private int minPosition = Integer.MAX_VALUE;
    private int maxPosition = Integer.MIN_VALUE;

    @Override
    public void runOpMode() {
        slideVL = hardwareMap.get(DcMotor.class, "slideVL");
        slideVR = hardwareMap.get(DcMotor.class, "slideVR");

        slideVL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideVR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideVL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideVR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            double power = -gamepad1.left_stick_y;
            slideVL.setPower(power);
            slideVR.setPower(power);

            int currentPositionVL = slideVL.getCurrentPosition();
            int currentPositionVR = slideVR.getCurrentPosition();
            int averagePosition = (currentPositionVL + currentPositionVR) / 2;

            if (averagePosition < minPosition) {
                minPosition = averagePosition;
            }
            if (averagePosition > maxPosition) {
                maxPosition = averagePosition;
            }

            telemetry.addData("Current Position VL", currentPositionVL);
            telemetry.addData("Current Position VR", currentPositionVR);
            telemetry.addData("Average Position", averagePosition);
            telemetry.addData("Min Position", minPosition);
            telemetry.addData("Max Position", maxPosition);
            telemetry.update();
        }
    }
}