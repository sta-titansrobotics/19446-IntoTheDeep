package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Chassis {

    // Drive Motors
    private DcMotor leftFrontMotor, leftRearMotor, rightFrontMotor, rightRearMotor;

    // Encoders for Odometry (could be separate from motors)
    private DcMotor leftEncoder, rightEncoder, strafeEncoder;

    // Odometry variables to track position (X, Y, Heading)
    private double xPos = 0;
    private double yPos = 0;
    private double heading = 0;

    // PID constants for straight driving
    private double kp = 0.01, ki = 0.0, kd = 0.0; // Straight PID constants
    private double turnKp = 0.01, turnKi = 0.0, turnKd = 0.0; // Turn PID constants

    // Target position and heading for GoToPos command
    private double targetX = 0;
    private double targetY = 0;
    private double targetHeading = 0;  // Desired heading after reaching the target

    // Odometry constants
    private final double TICKS_PER_REV = 537.7;  // Encoder ticks per revolution (adjust for your encoder)
    private final double WHEEL_DIAMETER = 9.6;   // Wheel diameter in cm (adjust based on your wheels)
    private final double TRACK_WIDTH = 40.0;     // Distance between left and right wheels in cm (adjust accordingly)

    private boolean running = true; // Flag to control PID loops

    // Timer for PID control
    private ElapsedTime runtime = new ElapsedTime();

    // Telemetry for displaying data on driver station
    private Telemetry telemetry;

    // Constructor: Initialize motors, encoders, and telemetry
    public Chassis(HardwareMap hardwareMap, String leftFrontID, String leftRearID, String rightFrontID, String rightRearID,
                   String leftEncoderID, String rightEncoderID, String strafeEncoderID, Telemetry telemetry) {
        this.telemetry = telemetry;

        // Initialize drive motors
        leftFrontMotor = hardwareMap.get(DcMotor.class, leftFrontID);
        leftRearMotor = hardwareMap.get(DcMotor.class, leftRearID);
        rightFrontMotor = hardwareMap.get(DcMotor.class, rightFrontID);
        rightRearMotor = hardwareMap.get(DcMotor.class, rightRearID);

        // Initialize encoders
        leftEncoder = hardwareMap.get(DcMotor.class, leftEncoderID);
        rightEncoder = hardwareMap.get(DcMotor.class, rightEncoderID);
        strafeEncoder = hardwareMap.get(DcMotor.class, strafeEncoderID);

        // Reset encoders to start with a clean slate
        resetEncoders();
    }

    // Method to reset all encoders
    public void resetEncoders() {
        leftEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        strafeEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        strafeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // Method to convert encoder ticks to distance in centimeters
    private double encoderTicksToCm(int ticks) {
        double circumference = Math.PI * WHEEL_DIAMETER;  // Circumference of the wheel
        return (ticks / TICKS_PER_REV) * circumference;   // Distance traveled based on ticks
    }

    // Update odometry based on encoder values
    public void updateOdometry() {
        // Get encoder values
        int leftTicks = leftEncoder.getCurrentPosition();
        int rightTicks = rightEncoder.getCurrentPosition();
        int strafeTicks = strafeEncoder.getCurrentPosition();

        // Convert encoder ticks to distance in cm
        double leftDistance = encoderTicksToCm(leftTicks);
        double rightDistance = encoderTicksToCm(rightTicks);
        double strafeDistance = encoderTicksToCm(strafeTicks);

        // Calculate change in heading (based on wheel separation - TRACK_WIDTH)
        double deltaHeading = (rightDistance - leftDistance) / TRACK_WIDTH;
        heading += deltaHeading;

        // Calculate forward movement
        double deltaDistance = (leftDistance + rightDistance) / 2.0;

        // Update X and Y positions based on forward and lateral movement
        xPos += deltaDistance * Math.cos(heading) + strafeDistance * Math.sin(heading);
        yPos += deltaDistance * Math.sin(heading) - strafeDistance * Math.cos(heading);
    }

    // Method to return the odometry data (x, y, heading) - Global Command
    public double[] GetOdomPos() {
        updateOdometry();  // Ensure the odometry is updated before returning
        return new double[]{xPos, yPos, heading};
    }

    // Method to display odometry data on the driver station via telemetry
    public void printOdometry() {
        double[] odomPos = GetOdomPos();
        telemetry.addData("X Position (cm)", odomPos[0]);
        telemetry.addData("Y Position (cm)", odomPos[1]);
        telemetry.addData("Heading (rad)", odomPos[2]);
        telemetry.update();
    }

    // GoToPos Command (Global Command): Point-to-point movement using PID and heading correction
    public void GoToPos(double targetX, double targetY, double targetHeading) {
        this.targetX = targetX;      // Set the target X coordinate
        this.targetY = targetY;      // Set the target Y coordinate
        this.targetHeading = targetHeading;  // Set the target heading

        double lastErrorX = 0, integralX = 0;
        double lastErrorY = 0, integralY = 0;

        double tolerance = 1.0;  // Define how close we need to be to the target in cm

        while (running) {
            updateOdometry();  // Continuously update the robot's position

            // Calculate the errors for X and Y directions
            double errorX = targetX - xPos;
            double errorY = targetY - yPos;

            // PID calculation for X axis
            integralX += errorX * runtime.seconds();
            double derivativeX = (errorX - lastErrorX) / runtime.seconds();
            double outputX = (kp * errorX) + (ki * integralX) + (kd * derivativeX);
            lastErrorX = errorX;

            // PID calculation for Y axis
            integralY += errorY * runtime.seconds();
            double derivativeY = (errorY - lastErrorY) / runtime.seconds();
            double outputY = (kp * errorY) + (ki * integralY) + (kd * derivativeY);
            lastErrorY = errorY;

            // Heading correction using PID for turning
            double headingError = targetHeading - heading;
            double turnIntegral = 0;
            double lastHeadingError = 0;
            turnIntegral += headingError * runtime.seconds();
            double turnDerivative = (headingError - lastHeadingError) / runtime.seconds();
            double turnOutput = (turnKp * headingError) + (turnKi * turnIntegral) + (turnKd * turnDerivative);
            lastHeadingError = headingError;

            // Combine straight movement and turn for motor power
            double leftPower = (outputY - outputX) - turnOutput;
            double rightPower = (outputY + outputX) + turnOutput;

            applyMotorPower(leftPower, rightPower);  // Adjust motors based on PID outputs

            // Check if the robot is close enough to the target and heading
            if (Math.abs(errorX) < tolerance && Math.abs(errorY) < tolerance && Math.abs(headingError) < 0.05) {
                stopMotors();  // Stop the motors once we are at the target
                break;
            }

            runtime.reset();  // Reset the timer for the next loop
        }
    }

    // Method to apply motor power to both sides (for straight and turn control)
    private void applyMotorPower(double leftPower, double rightPower) {
        leftFrontMotor.setPower(leftPower);
        leftRearMotor.setPower(leftPower);
        rightFrontMotor.setPower(rightPower);
        rightRearMotor.setPower(rightPower);
    }

    // Stop all motors
    public void stopMotors() {
        leftFrontMotor.setPower(0);
        leftRearMotor.setPower(0);
        rightFrontMotor.setPower(0);
        rightRearMotor.setPower(0);
    }

    // Methods to set PID tuning values for straight driving and turning
    public void setStraightPID(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void setTurnPID(double turnKp, double turnKi, double turnKd) {
        this.turnKp = turnKp;
        this.turnKi = turnKi;
        this.turnKd = turnKd;
    }
}
