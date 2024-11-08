package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp
public class Odometry_Testing extends LinearOpMode {
    // test commit
    // Declare OpMode members for each of the 3 motors and IMU.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor odom_l, odom_r, odom_h, fl, fr, bl, br;
    private BNO055IMU imu;
    Orientation lastAngles = new Orientation();
    double globalAngle;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables.
        odom_l = hardwareMap.get(DcMotor.class, "odom_l");
        odom_r = hardwareMap.get(DcMotor.class, "odom_r");
        odom_h = hardwareMap.get(DcMotor.class, "odom_h");

        // Initialize the IMU
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);
        InitializeMortors();

        telemetry.addData("Status", "Program Initialized");
        telemetry.update();

        odom_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_h.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_l.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_r.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_h.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0, current_ang;
        double delta_encoder_l, delta_encoder_r, delta_encoder_h, delta_local_x, delta_local_y, delta_global_x, delta_global_y, delta_ang, phi;
        double global_xM = 0, global_yM = 0;
        double disM_encoderHtoCenter = 0.195;   // Distance from the horizontal encoder to the center of the robot in meters
        double disTrackWidth = 0.12;  // Distance between the left and right encoders (robot's track width)

        waitForStart();
        runtime.reset();

        while (opModeIsActive() && !isStopRequested()) {
            // Convert encoder ticks to meters
            double encoder_l = encoderToMetres(odom_l.getCurrentPosition());
            double encoder_r = encoderToMetres(odom_r.getCurrentPosition());
            double encoder_h = encoderToMetres(odom_h.getCurrentPosition());  // Inverted due to hardware setup

            telemetry.addData("l", encoder_l);
            telemetry.addData("r", encoder_r);
            telemetry.addData("h", encoder_h);

            // Get current angle from IMU in radians
            current_ang = Math.toRadians(-getAngle());

            // Calculate encoder changes since last loop iteration
            delta_encoder_l = encoder_l - prev_encoder_l;
            delta_encoder_r = encoder_r - prev_encoder_r;
            delta_encoder_h = encoder_h - prev_encoder_h;

            // Calculate angle change (heading delta) since last loop
            delta_ang = current_ang - prev_ang;

            // Calculate turning angle phi (rotation factor)
            phi = (delta_encoder_l - delta_encoder_r) / disTrackWidth;

            // Calculate the forward/backward movement in the robot's local frame
            delta_local_x = (delta_encoder_l + delta_encoder_r) / 2;

            // Calculate the lateral movement, considering the angular effect of turning
            double delta_perp_pos = delta_encoder_h - (disM_encoderHtoCenter * phi);  // Corrected for angular displacement effect
            delta_local_y = delta_perp_pos;  // Lateral movement due to rotation

            // Now we convert the local changes (delta_local_x, delta_local_y) to global coordinates:
            // 1. Apply rotation matrix based on the robot's current angle
            delta_global_x = delta_local_x * Math.cos(current_ang) - delta_local_y * Math.sin(current_ang);
            delta_global_y = delta_local_x * Math.sin(current_ang) + delta_local_y * Math.cos(current_ang);

            // Update global positions by adding the global deltas to the robot's position
            global_xM += delta_global_x;
            global_yM += delta_global_y;

            // Telemetry to monitor values
            telemetry.addData("x (cm)", global_xM * 100);  // Convert to cm
            telemetry.addData("y (cm)", global_yM * 100);  // Convert to cm
            telemetry.addData("Angle (degrees)", Math.toDegrees(current_ang));
            telemetry.addData("delta_encoder_l", delta_encoder_l);
            telemetry.addData("delta_encoder_r", delta_encoder_r);
            telemetry.addData("delta_encoder_h", delta_encoder_h);
            telemetry.addData("delta_local_x", delta_local_x);
            telemetry.addData("delta_local_y", delta_local_y);
            telemetry.addData("delta_global_x", delta_global_x);
            telemetry.addData("delta_global_y", delta_global_y);
            telemetry.update();

            // Update previous encoder and angle values for next iteration
            prev_encoder_l = encoder_l;
            prev_encoder_r = encoder_r;
            prev_encoder_h = encoder_h;
            prev_ang = current_ang;

            //=========================================================================
            telemetryDrive();
        }
    }

    // Method to convert encoder ticks to meters
    private double encoderToMetres(int ticks) {
        double wheelDiameter = 0.032; // Diameter of the wheel in meters (3.8 cm)
        double ticksPerRevolution = 2000.0; // Number of encoder ticks per wheel revolution
        double circumference = wheelDiameter * Math.PI; // Circumference of the wheel in meters
        return (ticks / ticksPerRevolution) * circumference; // Convert ticks to meters
    }

    // Method to get the current angle from the IMU
    private double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    public void InitializeMortors() {
        fl = hardwareMap.get(DcMotor.class, "odom_l"); //0
        fr = hardwareMap.get(DcMotor.class, "odom_h"); //2
        bl = hardwareMap.get(DcMotor.class, "odom_r"); //1
        br = hardwareMap.get(DcMotor.class, "br"); //3

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    public void telemetryDrive() {
        //Driving

        double y = -gamepad1.left_stick_y/3; // Remember, this is reversed!

        //STRAFING VARIABLE
        double x = gamepad1.left_stick_x * 1.1/3; // Counteract imperfect strafing

        //THIS IS THE TURNING VARIABLE
        double rx = gamepad1.right_stick_x/3;

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
