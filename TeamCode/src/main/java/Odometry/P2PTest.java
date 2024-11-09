package Odometry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous(name="P2PTest", group="Tests")
public class P2PTest extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor odom_l, odom_r, odom_h;
    private DcMotor leftFront, leftRear, rightFront, rightRear;
    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    private double globalAngle;

    private PIDController pidX;
    private PIDController pidY;
    private PIDController pidAngle;

    // Parameters for control
    private double maxSpeed = 0.8; // Maximum translational speed
    private double maxTurnSpeed = 0.5; // Maximum turn speed
    private double kp = 1.0, kd = 0.1; // PID constants for movement
    private double turnKp = 1.0, turnKd = 0.1; // PID constants for turning

    @Override
    public void runOpMode() {
        // Initialize hardware variables
        odom_l = hardwareMap.get(DcMotor.class, "lf");
        odom_r = hardwareMap.get(DcMotor.class, "lr");
        odom_h = hardwareMap.get(DcMotor.class, "rf");
        leftFront = hardwareMap.get(DcMotor.class, "lf");
        leftRear = hardwareMap.get(DcMotor.class, "lr");
        rightFront = hardwareMap.get(DcMotor.class, "rf");
        rightRear = hardwareMap.get(DcMotor.class, "rr");

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

        // Set up PID controllers with separate parameters for positional and turning control
        pidX = new PIDController(kp, 0.0, kd, maxSpeed, turnKp, 0.0, turnKd, maxTurnSpeed);
        pidY = new PIDController(kp, 0.0, kd, maxSpeed, turnKp, 0.0, turnKd, maxTurnSpeed);
        pidAngle = new PIDController(turnKp, 0.0, turnKd, maxTurnSpeed, turnKp, 0.0, turnKd, maxTurnSpeed);

        // Enable turning mode for pidAngle
        pidAngle.setTurning(true);

        waitForStart();
        runtime.reset();

        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0;
        double global_xM = 0, global_yM = 0;
        double disM_encoderHtoCenter = 0.195;

        // Set autonomous target points
        double targetX = 1.0; // Target X position in meters
        double targetY = 1.0; // Target Y position in meters
        double targetAngle = Math.toRadians(90); // Target orientation in radians

        // Set PID controller setpoints
        pidX.setSetpoint(targetX);
        pidY.setSetpoint(targetY);
        pidAngle.setSetpoint(targetAngle);

        // Define tolerance thresholds for position and angle to consider as "reached"
        double positionTolerance = 0.05; // 5 cm tolerance for position
        double angleTolerance = Math.toRadians(2); // 2 degree tolerance for angle

        while (opModeIsActive() && !isStopRequested()) {
            // Read encoder values in meters
            double encoder_l = encoderToMetres(odom_l.getCurrentPosition());
            double encoder_r = encoderToMetres(odom_r.getCurrentPosition());
            double encoder_h = encoderToMetres(odom_h.getCurrentPosition());

            // Get current angle in radians
            double current_ang = Math.toRadians(getAngle());

            // Calculate change in encoder readings and angle
            double delta_encoder_l = encoder_l - prev_encoder_l;
            double delta_encoder_r = encoder_r - prev_encoder_r;
            double delta_encoder_h = encoder_h - prev_encoder_h;
            double delta_ang = current_ang - prev_ang;

            // Calculate movement in robot's local frame
            double delta_local_x = (delta_encoder_l + delta_encoder_r) / 2;
            double delta_local_y = delta_encoder_h - (delta_ang * disM_encoderHtoCenter);

            // Convert local frame changes to global coordinates
            double delta_global_x = delta_local_x * Math.cos(current_ang) - delta_local_y * Math.sin(current_ang);
            double delta_global_y = delta_local_x * Math.sin(current_ang) + delta_local_y * Math.cos(current_ang);

            // Update global positions
            global_xM += delta_global_x;
            global_yM += delta_global_y;

            // PID control for X, Y, and angle
            double powerX = pidX.calculate(global_xM);
            double powerY = pidY.calculate(global_yM);
            double powerAngle = pidAngle.calculate(current_ang);

            // Limit motor powers within defined speed constraints
            powerX = Math.min(maxSpeed, Math.abs(powerX)) * Math.signum(powerX);
            powerY = Math.min(maxSpeed, Math.abs(powerY)) * Math.signum(powerY);
            powerAngle = Math.min(maxTurnSpeed, Math.abs(powerAngle)) * Math.signum(powerAngle);

            // Calculate motor powers
            double leftPower = powerX + powerY - powerAngle;
            double rightPower = powerX - powerY + powerAngle;

            // Set motor powers
            leftFront.setPower(leftPower);
            leftRear.setPower(leftPower);
            rightFront.setPower(rightPower);
            rightRear.setPower(rightPower);

            // Display telemetry data
            telemetry.addData("x (meters)", global_xM);
            telemetry.addData("y (meters)", global_yM);
            telemetry.addData("Angle (degrees)", Math.toDegrees(current_ang));
            telemetry.addData("Target x (meters)", targetX);
            telemetry.addData("Target y (meters)", targetY);
            telemetry.addData("Target angle (degrees)", Math.toDegrees(targetAngle));
            telemetry.update();

            // Update previous values for the next iteration
            prev_encoder_l = encoder_l;
            prev_encoder_r = encoder_r;
            prev_encoder_h = encoder_h;
            prev_ang = current_ang;

            // Check if robot has reached the target position and orientation
            if (Math.abs(targetX - global_xM) < positionTolerance &&
                    Math.abs(targetY - global_yM) < positionTolerance &&
                    Math.abs(targetAngle - current_ang) < angleTolerance) {
                break; // Stop loop if target is reached
            }
        }

        // Stop all motors after reaching the target
        leftFront.setPower(0);
        leftRear.setPower(0);
        rightFront.setPower(0);
        rightRear.setPower(0);

        telemetry.addData("Status", "Target Reached");
        telemetry.update();
    }

    private double encoderToMetres(int ticks) {
        double wheelDiameter = 0.032;
        double ticksPerRevolution = 2000.0;
        double circumference = wheelDiameter * Math.PI;
        return (ticks / ticksPerRevolution) * circumference;
    }

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
