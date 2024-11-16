package robotControl47_2425;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Chassis {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private LinearOpMode opMode;
    private Odometry odometry;
    private RobotPos targetPosition;

    public Chassis(LinearOpMode opMode, Odometry odometry) {
        this.opMode = opMode;
        this.odometry = odometry;

        // Initialize motors from the hardware map
        frontLeft = opMode.hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = opMode.hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = opMode.hardwareMap.get(DcMotor.class, "backLeft");
        backRight = opMode.hardwareMap.get(DcMotor.class, "backRight");

        // Set directions for motors
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set zero power behavior
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize target position
        targetPosition = new RobotPos(0, 0, 0);
    }

    // Method to move to a specific target position
    public void moveToPosition(double x, double y, double targetAngle) {

        x = cmToEncoderTicks(x);
        y = cmToEncoderTicks(y);
        targetPosition.setPosition(x, y, targetAngle);

        double kP = 0.1; // Proportional constant for movement correction
        double angleTolerance = 2.0; // Tolerance for angle in degrees
        double positionTolerance = 0.02; // Tolerance for position in meters

        while (opMode.opModeIsActive()) {
            // Update odometry to get current position
            odometry.updatePosition();
            RobotPos currentPosition = odometry.getCurrentPosition();

            // Calculate errors
            double errorX = targetPosition.x - currentPosition.x;
            double errorY = targetPosition.y - currentPosition.y;
            double errorAngle = targetPosition.angle - currentPosition.angle;

            // Normalize angle error
            if (errorAngle > 180) errorAngle -= 360;
            if (errorAngle < -180) errorAngle += 360;

            // Break loop if position and angle are within tolerance
            if (Math.hypot(errorX, errorY) < positionTolerance && Math.abs(errorAngle) < angleTolerance) {
                stop();
                break;
            }

            // Proportional control
            double forwardPower = kP * Math.hypot(errorX, errorY);
            double strafePower = kP * Math.atan2(errorY, errorX);
            double turnPower = kP * (errorAngle / 180.0);

            // Motor power calculations
            double powerFL = forwardPower + strafePower - turnPower;
            double powerFR = forwardPower - strafePower + turnPower;
            double powerBL = forwardPower - strafePower - turnPower;
            double powerBR = forwardPower + strafePower + turnPower;

            // Normalize motor powers
            double maxPower = Math.max(Math.max(Math.abs(powerFL), Math.abs(powerFR)),
                    Math.max(Math.abs(powerBL), Math.abs(powerBR)));
            if (maxPower > 1.0) {
                powerFL /= maxPower;
                powerFR /= maxPower;
                powerBL /= maxPower;
                powerBR /= maxPower;
            }

            // Set motor powers
            frontLeft.setPower(powerFL);
            frontRight.setPower(powerFR);
            backLeft.setPower(powerBL);
            backRight.setPower(powerBR);

            // Telemetry for debugging
            opMode.telemetry.addData("Target Position", "(%.2f, %.2f, %.2f)", x, y, targetAngle);
            opMode.telemetry.addData("Current Position", "(%.2f, %.2f, %.2f)", currentPosition.x, currentPosition.y, currentPosition.angle);
            opMode.telemetry.addData("Errors", "X: %.2f, Y: %.2f, Angle: %.2f", errorX, errorY, errorAngle);
            opMode.telemetry.update();
        }
    }

    // Stop all motors
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    private double encoderToCM(int ticks) {
        double wheelDiameter = 0.032; // Diameter of the wheel in meters (3.2 cm)
        double ticksPerRevolution = 2000.0; // Number of encoder ticks per wheel revolution
        double circumference = wheelDiameter * Math.PI;
        return (ticks / ticksPerRevolution) * circumference * 100;
    }

    private int cmToEncoderTicks(double cm) {
        double wheelDiameter = 0.032; // Diameter of the wheel in meters (3.2 cm)
        double ticksPerRevolution = 2000.0; // Number of encoder ticks per wheel revolution
        double circumference = wheelDiameter * Math.PI * 100; // Convert circumference to cm
        return (int) ((cm / circumference) * ticksPerRevolution);
    }
}
