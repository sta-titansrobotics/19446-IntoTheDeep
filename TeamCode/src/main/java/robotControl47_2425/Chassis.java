package robotControl47_2425;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Chassis {
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private LinearOpMode opMode;
    private Odometry odometry;

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
    }

    // Mecanum drive movement with power inputs for x (strafe), y (forward/backward), and rotation (turn)
    public void mecanumDrive(double xPower, double yPower, double turnPower) {
        double flPower = yPower + xPower + turnPower;
        double frPower = yPower - xPower - turnPower;
        double blPower = yPower - xPower + turnPower;
        double brPower = yPower + xPower - turnPower;

        // Normalize the power values to ensure no value exceeds 1.0
        double maxPower = Math.max(1.0, Math.abs(flPower));
        maxPower = Math.max(maxPower, Math.abs(frPower));
        maxPower = Math.max(maxPower, Math.abs(blPower));
        maxPower = Math.max(maxPower, Math.abs(brPower));

        flPower /= maxPower;
        frPower /= maxPower;
        blPower /= maxPower;
        brPower /= maxPower;

        frontLeft.setPower(flPower);
        frontRight.setPower(frPower);
        backLeft.setPower(blPower);
        backRight.setPower(brPower);
    }

    // Basic movement functions for convenience
    public void moveForward(double power) {
        mecanumDrive(0, power, 0);
    }

    public void moveBackward(double power) {
        mecanumDrive(0, -power, 0);
    }

    public void strafeLeft(double power) {
        mecanumDrive(-power, 0, 0);
    }

    public void strafeRight(double power) {
        mecanumDrive(power, 0, 0);
    }

    public void turnLeft(double power) {
        mecanumDrive(0, 0, -power);
    }

    public void turnRight(double power) {
        mecanumDrive(0, 0, power);
    }

    // Stop all motors
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    // Update robot's position using odometry readings
    public void updatePosition() {
        odometry.updatePosition();
    }
}
