package robotControl47_2425.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutoStrafeRight", group = "Test")
public class AutoStrafeRight extends LinearOpMode {

    private DcMotor fl, fr, bl, br;

    @Override
    public void runOpMode() {
        // Initialize motors
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

        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        // Set initial power to strafe right
        double power = 0.5;
        fl.setPower(power);
        fr.setPower(-power);
        bl.setPower(-power);
        br.setPower(power);

        // Proportional control constant
        double kP = 0.01;

        // Strafe for a fixed duration (e.g., 2 seconds)
        long startTime = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis() - startTime < 5000) {
            // Calculate the error (difference in encoder values)
            int leftError = fl.getCurrentPosition() - bl.getCurrentPosition();
            int rightError = fr.getCurrentPosition() - br.getCurrentPosition();

            // Calculate the correction
            double correction = kP * (leftError - rightError);

            // Adjust motor powers
            fl.setPower(power - correction);
            fr.setPower(-power - correction);
            bl.setPower(-power + correction);
            br.setPower(power + correction);
        }

        // Stop all motors
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
}