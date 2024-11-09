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
public class ChassisControl extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor odom_l, odom_r, odom_h;
    private DcMotor leftFront, leftRear, rightFront, rightRear;
    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    private double globalAngle;

    private PIDController pidX;
    private PIDController pidY;
    private PIDController pidAngle;

    @Override
    public void runOpMode() {
        // Initialize hardware variables
        leftFront = hardwareMap.get(DcMotor.class, "lf");
        leftRear = hardwareMap.get(DcMotor.class, "lr");
        rightFront = hardwareMap.get(DcMotor.class, "rf");
        rightRear = hardwareMap.get(DcMotor.class, "rr");

        // Set odometry motors for tracking
        odom_l = leftFront;
        odom_r = leftRear;
        odom_h = rightFront;

        // Initialize the IMU
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);

        telemetry.addData("Status", "Program Initialized");
        telemetry.update();

        // Set encoder modes for odometry motors
        odom_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_h.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom_l.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_r.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        odom_h.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set tuning parameters for position and angle PID controllers
        double positionalKp = 1.0, positionalKi = 0.0, positionalKd = 0.1;
        double turningKp = 1.0, turningKi = 0.0, turningKd = 0.1;
        double maxPositionalSpeed = 0.8;
        double maxTurningSpeed = 0.5;

        pidX = new PIDController(positionalKp, positionalKi, positionalKd, maxPositionalSpeed, turningKp, turningKi, turningKd, maxTurningSpeed);
        pidY = new PIDController(positionalKp, positionalKi, positionalKd, maxPositionalSpeed, turningKp, turningKi, turningKd, maxTurningSpeed);
        pidAngle = new PIDController(turningKp, turningKi, turningKd, maxTurningSpeed, turningKp, turningKi, turningKd, maxTurningSpeed);

        // Set the mode for pidAngle to be turning
        pidAngle.setTurning(true);

        waitForStart();
        runtime.reset();

        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0;
        double global_xM = 0, global_yM = 0;
        double disM_encoderHtoCenter = 0.195;

        // Set target point
        double targetX = 1.0;
        double targetY = 0.0;
        double targetAngle = 0.0;

        pidX.setSetpoint(targetX);
        pidY.setSetpoint(targetY);
        pidAngle.setSetpoint(targetAngle);

        while (opModeIsActive() && !isStopRequested()) {
            // Get odometry encoder readings and convert to meters
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

            // Motor power distribution with positional and angle control
            double leftPower = powerX + powerY - powerAngle;
            double rightPower = powerX - powerY + powerAngle;

            leftFront.setPower(leftPower);
            leftRear.setPower(leftPower);
            rightFront.setPower(rightPower);
            rightRear.setPower(rightPower);

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
