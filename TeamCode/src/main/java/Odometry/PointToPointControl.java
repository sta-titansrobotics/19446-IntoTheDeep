package Odometry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp
public class PointToPointControl extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor odom_l, odom_r, odom_h;
    private DcMotor leftDrive, rightDrive;
    private Servo someServo;
    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    private double globalAngle;

    private PIDController pidX;
    private PIDController pidY;
    private PIDController pidAngle;

    @Override
    public void runOpMode() {
        // Initialize hardware variables
        odom_l = hardwareMap.get(DcMotor.class, "lf");
        odom_r = hardwareMap.get(DcMotor.class, "lr");
        odom_h = hardwareMap.get(DcMotor.class, "rf");
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        someServo = hardwareMap.get(Servo.class, "some_servo");

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

        pidX = new PIDController(1.0, 0.0, 0.0); // Tuning required
        pidY = new PIDController(1.0, 0.0, 0.0); // Tuning required
        pidAngle = new PIDController(1.0, 0.0, 0.0); // Tuning required

        waitForStart();
        runtime.reset();

        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0;
        double global_xM = 0, global_yM = 0;
        double disM_encoderHtoCenter = 0.195; // Distance from the horizontal encoder to the center of the robot in meters

        // Dynamic target point and heading input
        double targetX = 1.0; // Initial target x-coordinate
        double targetY = 1.0; // Initial target y-coordinate
        double targetAngle = Math.toRadians(90); // Initial target heading in radians

        // Set setpoints for PID controllers
        pidX.setSetpoint(targetX);
        pidY.setSetpoint(targetY);
        pidAngle.setSetpoint(targetAngle);

        while (opModeIsActive() && !isStopRequested()) {
            // Allow dynamic target input using gamepad buttons
            if (gamepad1.x) {
                targetX += 0.1; // Increment targetX by 0.1 meters
                pidX.setSetpoint(targetX);
            }
            if (gamepad1.y) {
                targetY += 0.1; // Increment targetY by 0.1 meters
                pidY.setSetpoint(targetY);
            }
            if (gamepad1.a) {
                targetAngle += Math.toRadians(5); // Increment targetAngle by 5 degrees
                pidAngle.setSetpoint(targetAngle);
            }
            if (gamepad1.b) {
                targetAngle -= Math.toRadians(5); // Decrement targetAngle by 5 degrees
                pidAngle.setSetpoint(targetAngle);
            }

            // Convert encoder ticks to meters
            double encoder_l = encoderToMetres(odom_l.getCurrentPosition());
            double encoder_r = encoderToMetres(odom_r.getCurrentPosition());
            double encoder_h = encoderToMetres(odom_h.getCurrentPosition());

            // Get current angle from IMU in radians
            double current_ang = Math.toRadians(getAngle());

            // Calculate encoder changes since last loop
            double delta_encoder_l = encoder_l - prev_encoder_l;
            double delta_encoder_r = encoder_r - prev_encoder_r;
            double delta_encoder_h = encoder_h - prev_encoder_h;

            // Calculate change in heading angle since the last update
            double delta_ang = current_ang - prev_ang;

            // Calculate the forward/backward movement in the robot's local frame
            double delta_local_x = (delta_encoder_l + delta_encoder_r) / 2;
            double delta_local_y = delta_encoder_h - (delta_ang * disM_encoderHtoCenter);

            // Convert local changes (delta_local_x, delta_local_y) to global coordinates using rotation matrix
            double delta_global_x = delta_local_x * Math.cos(current_ang) - delta_local_y * Math.sin(current_ang);
            double delta_global_y = delta_local_x * Math.sin(current_ang) + delta_local_y * Math.cos(current_ang);

            // Update global positions
            global_xM += delta_global_x;
            global_yM += delta_global_y;

            // PID control for X, Y, and angle
            double powerX = pidX.calculate(global_xM);
            double powerY = pidY.calculate(global_yM);
            double powerAngle = pidAngle.calculate(current_ang);

            // Control motors accordingly
            double leftPower = powerX + powerY - powerAngle;
            double rightPower = powerX - powerY + powerAngle;

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            // Telemetry display
            telemetry.addData("x (meters)", global_xM);
            telemetry.addData("y (meters)", global_yM);
            telemetry.addData("Angle (degrees)", Math.toDegrees(current_ang));
            telemetry.addData("Target x (meters)", targetX);
            telemetry.addData("Target y (meters)", targetY);
            telemetry.addData("Target angle (degrees)", Math.toDegrees(targetAngle));
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
