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

    //-----------------------------------------------------------------------------------------------
    private void toPoint(double target_x, double target_y, double target_ang, double max_speed, double kp, double kd, double turn_kp, double turn_kd, double turn_max_speed){
        double current_x = global_xM;
        double current_y = global_yM;

        double current_ang = getAngle();
        //put initial values first during init
        double prev_error_x = 0, prev_error_y = 0, prev_error_ang = 0;//init before use

        while (Math.sqrt(Math.pow(target_x - current_x, 2) + Math.pow(target_y - current_y, 2))>0.02 || Math.abs(target_ang-current_ang)>1){
            //condition uses formula for circle to create resolution
            //if current x and y is within circle with radius 0.02M
            //or if current angle is within a 2 degree resolution from target, stop
            current_x = global_xM;
            current_y = global_yM;
            telemetry.addData("current_y", current_y);
            current_ang = getAngle();

            double error_x = target_x - current_x;
            double error_y = target_y - current_y;
            double global_vel_x = (kp * error_x) + (kd * (error_x - prev_error_x));// PD (PID without the I) control
            double global_vel_y = (kp * error_y) + (kd * (error_y - prev_error_y));
            opMode.telemetry.addData("global vel_y", global_vel_y);
            double local_vel_x = global_vel_x * Math.cos(Math.toRadians(getAngle())) + global_vel_y * Math.sin(Math.toRadians(getAngle()));

            double local_vel_y = -global_vel_x * Math.sin(Math.toRadians(getAngle())) + global_vel_y * Math.cos(Math.toRadians(getAngle()));// inverse matrix to calculate local velocities to global velocities
            opMode.telemetry.addData("local vel_x", local_vel_x);telemetry.addData("local vel_y", local_vel_y);
            double local_vel_max = Math.max(Math.abs(local_vel_x), Math.abs(local_vel_y));
            if (local_vel_max > max_speed){
                local_vel_x = (local_vel_x/local_vel_max)*max_speed;
                local_vel_y = (local_vel_y/local_vel_max)*max_speed;
            }

            double error_ang = target_ang - current_ang;
            double correction_ang = (turn_kp * error_ang) + (turn_kd * (error_ang - prev_error_ang));//PD control
            if (Math.abs(correction_ang) > turn_max_speed){
                correction_ang = turn_max_speed*Math.signum(correction_ang);
            }



            double lfPower = local_vel_x + local_vel_y - correction_ang;
            double rfPower = local_vel_x + local_vel_y + correction_ang;
            double lrPower = local_vel_x - local_vel_y - correction_ang;
            double rrPower = local_vel_x - local_vel_y + correction_ang;

            double maxNumber = Math.max(Math.max(Math.abs(lfPower), Math.abs(lrPower)), Math.max(Math.abs(rfPower), Math.abs(rrPower)));
            if(maxNumber > 1){
                lfPower /= maxNumber;
                lrPower /= maxNumber;
                rfPower /= maxNumber;
                rrPower /= maxNumber;
            }


            lf.setPower(lfPower);
            rf.setPower(rfPower);
            lr.setPower(lrPower);
            rr.setPower(rrPower);

            prev_error_x = error_x;
            prev_error_y = error_y;
            prev_error_ang = error_ang;
            if (isStopRequested()){
                break;
            }
        }
        lf.setPower(0);
        rf.setPower(0);
        lr.setPower(0);
        rr.setPower(0);
        isBusy = false;
    }
    //---------------------------------------------------------------------------------------------

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
