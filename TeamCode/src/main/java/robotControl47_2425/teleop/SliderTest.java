package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
@TeleOp(name = "SliderTest", group = "Test")
public class SliderTest extends OpMode {

    private DcMotor slideVL, slideVR;
    private static final double SLIDER_POWER = 0.3;
    private static final int TARGET_POSITION = 150;

    @Override
    public void init() {
        // Initialize the slider motors
        slideVL = hardwareMap.get(DcMotor.class, "lvSlide");
        slideVR = hardwareMap.get(DcMotor.class, "rvSlide");

        // Set the motors to brake when power is zero
        slideVL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideVR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slideVL.setDirection(DcMotor.Direction.REVERSE);

        // Reset encoders
        slideVL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideVR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideVL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideVR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        // Move sliders to position 150 with gamepad1.a
        if (gamepad1.a) {
            moveSlidersToPosition(TARGET_POSITION);
        }

        // Move sliders back to position 0 with gamepad1.b
        if (gamepad1.b) {
            moveSlidersToPosition(0);
        }

        // Send telemetry message to signify slider motor power
        telemetry.addData("Slider Left Power", slideVL.getPower());
        telemetry.addData("Slider Right Power", slideVR.getPower());
        telemetry.update();
    }

    private void moveSlidersToPosition(int position) {
        slideVL.setTargetPosition(position);
        slideVR.setTargetPosition(position);

        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        slideVL.setPower(0);
        slideVR.setPower(0.3);

        while (slideVL.isBusy() && slideVR.isBusy()) {
            // Wait until target position is reached
        }

        slideVL.setPower(0);
        slideVR.setPower(0);

        slideVL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideVR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}