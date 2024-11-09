package Odometry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp
public class Odometry extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor odom_l, odom_r, odom_h;
    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    private double globalAngle;

    @Override
    public void runOpMode() {
        // Initialize hardware variables
        odom_l = hardwareMap.get(DcMotor.class, "lf");
        odom_r = hardwareMap.get(DcMotor.class, "lr");
        odom_h = hardwareMap.get(DcMotor.class, "rf");

        // Initialize the IMU
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);

        telemetry.addData("Status", "Program Initialized");
        telemetry.update();

        odom_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_h.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_l.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_r.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_h.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0;
        double delta_encoder_l, delta_encoder_r, delta_encoder_h;
        double delta_local_x, delta_local_y, delta_global_x, delta_global_y, delta_ang = 0;
        double global_xM = 0, global_yM = 0;
        double disM_encoderHtoCenter = 0.195; // Distance from the horizontal encoder to the center of the robot in meters

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
            double current_ang = Math.toRadians(getAngle());

            // Calculate encoder changes since last loop
            delta_encoder_l = encoder_l - prev_encoder_l;
            delta_encoder_r = encoder_r - prev_encoder_r;
            delta_encoder_h = encoder_h - prev_encoder_h;

            // Calculate change in heading angle since the last update
            delta_ang = current_ang - prev_ang;

            // Calculate the forward/backward movement in the robot's local frame
            delta_local_x = (delta_encoder_l + delta_encoder_r) / 2;

            // Adjust lateral movement to account for angular change effects
            delta_local_y = delta_encoder_h - (delta_ang * -disM_encoderHtoCenter);

            telemetry.addData("delta_local_y", delta_local_y);
            telemetry.addData("delta_ang", delta_ang);
            telemetry.addData("delta_encoder_h", delta_encoder_h);

            // Convert local changes (delta_local_x, delta_local_y) to global coordinates using rotation matrix
            delta_global_x = delta_local_x * Math.cos(current_ang) - delta_local_y * Math.sin(current_ang);
            delta_global_y = delta_local_x * Math.sin(current_ang) + delta_local_y * Math.cos(current_ang);

            // Update global positions
            global_xM += delta_global_x;
            global_yM += delta_global_y;

            // Telemetry display
            telemetry.addData("x (meters)", global_xM);
            telemetry.addData("y (meters)", global_yM);
            telemetry.addData("Angle (degrees)", Math.toDegrees(current_ang));
            telemetry.update();

            // Update previous values for the next loop iteration
            prev_encoder_l = encoder_l;
            prev_encoder_r = encoder_r;
            prev_encoder_h = encoder_h;
            prev_ang = current_ang;
        }
    }

    // Method to convert encoder ticks to meters
    private double encoderToMetres(int ticks) {
        double wheelDiameter = 0.032; // Diameter of the wheel in meters (3.2 cm)
        double ticksPerRevolution = 2000.0; // Number of encoder ticks per wheel revolution
        double circumference = wheelDiameter * Math.PI;
        return (ticks / ticksPerRevolution) * circumference;
    }

    // Method to get the current angle from the IMU
    private double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180) deltaAngle += 360;
        else if (deltaAngle > 180) deltaAngle -= 360;

        globalAngle += deltaAngle;
        lastAngles = angles;
        return globalAngle;
    }
}
