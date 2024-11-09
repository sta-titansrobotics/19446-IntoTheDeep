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

        // Reset and set encoder modes
        odom_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_h.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_l.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_r.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_h.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0;
        double global_xM = 0, global_yM = 0;
        double disM_encoderHtoCenter = -0.17; // Distance from the horizontal encoder to the center in meters

        waitForStart();
        runtime.reset();

        while (opModeIsActive() && !isStopRequested()) {
            // Convert encoder ticks to meters
            double encoder_l = encoderToMetres(odom_l.getCurrentPosition());
            double encoder_r = encoderToMetres(odom_r.getCurrentPosition());
            double encoder_h = encoderToMetres(odom_h.getCurrentPosition());

            // Get the current angle in radians
            double current_ang = Math.toRadians(getAngle());

            // Calculate encoder changes since last loop
            double delta_encoder_l = encoder_l - prev_encoder_l;
            double delta_encoder_r = encoder_r - prev_encoder_r;
            double delta_encoder_h = encoder_h - prev_encoder_h;

            // Calculate change in heading angle since the last update
            double delta_ang = current_ang - prev_ang;



                // Calculate forward/backward movement in the robot's local frame
                double delta_local_x = (delta_encoder_l + delta_encoder_r) / 2;

                // Adjust lateral movement to account for angular change effects
                double delta_local_y = delta_encoder_h - (delta_ang * disM_encoderHtoCenter);

                // Convert local changes to global coordinates using rotation matrix
                double delta_global_x = delta_local_x * Math.cos(current_ang) - delta_local_y * Math.sin(current_ang);
                double delta_global_y = delta_local_x * Math.sin(current_ang) + delta_local_y * Math.cos(current_ang);

                // Update global positions
                global_xM += delta_global_x;
                global_yM += delta_global_y;

                // Telemetry display for position updates
                telemetry.addData("Movement Status", "Updating Position");


            // Display telemetry data
            telemetry.addData("x (meters)", global_xM);
            telemetry.addData("y (meters)", global_yM);
            telemetry.addData("Angle (degrees)", Math.toDegrees(current_ang));
            telemetry.addData("Left Encoder (meters)", encoder_l);
            telemetry.addData("Right Encoder (meters)", encoder_r);
            telemetry.addData("Horizontal Encoder (meters)", encoder_h);
            telemetry.addData("Angle Change (degrees)", Math.toDegrees(delta_ang));
            telemetry.update();

            // Update previous values for the next loop iteration
            prev_encoder_l = encoder_l;
            prev_encoder_r = encoder_r;
            prev_encoder_h = encoder_h;
            prev_ang = current_ang;

            sleep(5); // Small delay for telemetry update
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
