package robotControl47_2425;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Chassis {
    private double global_xM = 0, global_yM = 0;
    private DcMotor lf, lr, rf, rr;
    private LinearOpMode opMode;
    private HardwareMap hardwareMap;

    private Telemetry telemetry;

    private volatile moveToPoint p2pThread = null;
    private volatile odom_thread odomTracking = new odom_thread();
    private boolean odomTrackingStartFlag = false;
    private double encoder_l, encoder_r, encoder_h;
    private double disM_encoderHtoCenter = 0.0755; // Distance from horizontal encoder to robot center in meters
    private double wheelDiameter = 0.032; // Diameter of the odometry wheel in meters
    private double ticksPerRevolution = 2000.0; // Encoder ticks per wheel revolution


    public boolean isBusy = false;
    private double globalAngle = 0;
    private Orientation lastAngles = new Orientation();
    private BNO055IMU imu;

    private Gamepad gamepad1;


    public Chassis(LinearOpMode opMode) {
        this.opMode = opMode;
        this.telemetry = opMode.telemetry;
        this.hardwareMap = opMode.hardwareMap;
        this.gamepad1 = opMode.gamepad1;

        initializeMotors();

        // Initialize target position
        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);

    }

    public void startOdomThread(){
        odomTracking.start();
    }

    public void initializeMotors() {
        lf = hardwareMap.get(DcMotor.class, "lf");
        rf = hardwareMap.get(DcMotor.class, "rf");
        lr = hardwareMap.get(DcMotor.class, "lr");
        rr = hardwareMap.get(DcMotor.class, "rr");

        lf.setDirection(DcMotor.Direction.REVERSE);
        lr.setDirection(DcMotor.Direction.REVERSE);

        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void telemetryDrive() {
        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;


        double frontLeftPower = (y + x + rx);
        double backLeftPower = (y - x + rx);
        double frontRightPower = (y - x - rx);
        double backRightPower = (y + x - rx);

        double denominator = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(backLeftPower), Math.max(Math.abs(frontRightPower), Math.max(Math.abs(backRightPower), 1))));

        lf.setPower(frontLeftPower / denominator);
        lr.setPower(backLeftPower / denominator);
        rf.setPower(frontRightPower / denominator);
        rr.setPower(backRightPower / denominator);
    }

    //=============================================================================================================================
    //-----------------------------------------------------------------------------------------------------------------------------

    private void toPoint(double target_x, double target_y, double target_ang, double max_speed, double kp, double kd, double turn_kp, double turn_kd, double turn_max_speed) throws InterruptedException {

        double current_x = global_xM;
        double current_y = global_yM;

        double current_ang = getAngle();
        //put initial values first during init
        double prev_error_x = 0, prev_error_y = 0, prev_error_ang = 0;//init before use

        while (!Thread.currentThread().isInterrupted()||Math.sqrt(Math.pow(target_x - current_x, 2) + Math.pow(target_y - current_y, 2)) > 0.015 || Math.abs(target_ang - current_ang) > 0.5) {

            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Thread interrupted, exiting...");
                break;
            }

            //condition uses formula for circle to create resolution
            //if current x and y is within circle with radius 0.02M
            //or if current angle is within a 2 degree resolution from target, stop
            current_x = global_xM;
            current_y = global_yM;
            current_ang = getAngle();

            double error_x = target_x - current_x;
            double error_y = target_y - current_y;
            double global_vel_x = (kp * error_x) + (kd * (error_x - prev_error_x));// PD (PID without the I) control
            double global_vel_y = (kp * error_y) + (kd * (error_y - prev_error_y));
            double local_vel_x = global_vel_x * Math.cos(Math.toRadians(getAngle())) + global_vel_y * Math.sin(Math.toRadians(getAngle()));

            double local_vel_y = -global_vel_x * Math.sin(Math.toRadians(getAngle())) + global_vel_y * Math.cos(Math.toRadians(getAngle()));// inverse matrix to calculate local velocities to global velocities

            double local_vel_max = Math.max(Math.abs(local_vel_x), Math.abs(local_vel_y));
            if (local_vel_max > max_speed) {
                local_vel_x = (local_vel_x / local_vel_max) * max_speed;
                local_vel_y = (local_vel_y / local_vel_max) * max_speed;
            }

            double error_ang = target_ang - current_ang;
            double correction_ang = (turn_kp * error_ang) + (turn_kd * (error_ang - prev_error_ang));//PD control
            if (Math.abs(correction_ang) > turn_max_speed) {
                correction_ang = turn_max_speed * Math.signum(correction_ang);
            }


            double lfPower = local_vel_x - local_vel_y - correction_ang;
            double rfPower = local_vel_x + local_vel_y + correction_ang;
            double lrPower = local_vel_x + local_vel_y - correction_ang;
            double rrPower = local_vel_x - local_vel_y + correction_ang;

            double maxNumber = Math.max(Math.max(Math.abs(lfPower), Math.abs(lrPower)), Math.max(Math.abs(rfPower), Math.abs(rrPower)));
            if (maxNumber > 1) {
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
            if (opMode.isStopRequested()) {
                break;
            }
            telemetry.addData("error (<2):", 100*Math.sqrt(Math.pow(target_x - current_x, 2) + Math.pow(target_y - current_y, 2)));
            telemetry.addData("angle error (<1):", Math.abs(target_ang - current_ang));
            Thread.sleep(10);
        }
        stop();

        isBusy = false;
        p2pThread.interrupt();
        p2pThread = null;

    }

    //========================================================================================================================

    private double getAngle() {

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
    //--------------------------------------------------------------------------------------------------

    private double encoderToMetres(int ticks) {
        return (ticks / ticksPerRevolution) * (wheelDiameter * Math.PI);
    }

    //--------------------------- Helper Methods -------------------------------------------------------------------------

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    // Stop all motors
    public void stop() {
        lf.setPower(0);
        lr.setPower(0);
        rf.setPower(0);
        rr.setPower(0);
    }
    public void stopAllThreads(){
        if (p2pThread != null){
            p2pThread.interrupt();
            p2pThread = null;
        }
        if (odomTracking != null){
            odomTracking.interrupt();
            odomTracking = null;
        }
    }

    public void p2pDrive(double target_x, double target_y, double target_ang, double max_speed, double kp, double kd, double turn_kp, double turn_kd, double turn_max_speed){

        isBusy = true;
        // Safely stop the previous thread if it exists
        if (p2pThread != null && p2pThread.isAlive()) {
            p2pThread.interrupt(); // Request interruption
            try {
                p2pThread.join(); // Wait for the thread to terminate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle this thread's interrupt state
            }
        }
        p2pThread = new moveToPoint(target_x, target_y, target_ang, max_speed, kp, kd, turn_kp, turn_kd, turn_max_speed);

        p2pThread.start();
    }
    private class moveToPoint extends Thread {
        double target_x, target_y, target_ang, max_speed, kp, kd, turn_kp, turn_kd, turn_max_speed;
        public moveToPoint(double target_x, double target_y, double target_ang, double max_speed, double kp, double kd, double turn_kp, double turn_kd, double turn_max_speed) {
            this.target_x = target_x;
            this.target_y = target_y;
            this.target_ang = target_ang;
            this.max_speed = max_speed;
            this.kp = kp;
            this.kd = kd;
            this.turn_kp = turn_kp;
            this.turn_kd = turn_kd;
            this.turn_max_speed = turn_max_speed;
        }

        public void run() {
            try {
                toPoint(target_x, target_y, target_ang, max_speed, kp, kd, turn_kp, turn_kd, turn_max_speed);
            } catch (Exception e) {

            }
        }
    }


    private class odom_thread extends Thread{
        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0, current_ang;
        double delta_encoder_l, delta_encoder_r, delta_encoder_h, delta_local_x, delta_local_y,
                delta_global_x, delta_global_y, delta_ang;
        public odom_thread(){
        }
        public void run(){
            while (opMode.opModeIsActive() && !opMode.isStopRequested()){
                try{
                    encoder_l = encoderToMetres(lr.getCurrentPosition());
                    encoder_r = encoderToMetres(-rr.getCurrentPosition()); //negative if using gobilda omniwheel bot, positive if using openodometry bot
                    encoder_h = encoderToMetres(lf.getCurrentPosition()); //negative if using gobilda omniwheel bot, positive if using openodometry bot

                    current_ang = Math.toRadians(getAngle()); //degrees to radians (either ways of calculating current angle work[imu or encoder])
                    //current_ang = Math.toRadians((encoder_r-encoder_l)/0.031) //(r-l) divided by distance (METRES) between the encoder wheels

                    delta_encoder_l = encoder_l - prev_encoder_l;
                    delta_encoder_r = encoder_r - prev_encoder_r;
                    delta_encoder_h = encoder_h - prev_encoder_h;
                    delta_ang = current_ang - prev_ang;

                    delta_local_x = (delta_encoder_l + delta_encoder_r) / 2;//find avrg between both odom wheels and convert ticks to M
                    //do not need arc formula because both "x" encoders cancel out offset


                    delta_local_y = delta_encoder_h - (delta_ang * disM_encoderHtoCenter); //use arc formula to subtract arc from horizontal encoder wheel


                    //* distance of h wheel to center

                    delta_global_x = delta_local_x * Math.cos(current_ang) - delta_local_y * Math.sin(current_ang);
                    delta_global_y = delta_local_x * Math.sin(current_ang) + delta_local_y * Math.cos(current_ang);

                    global_xM += delta_global_x;
                    global_yM += delta_global_y;
    //            telemetry.addData("L-encoder", encoder_l);
    //            telemetry.addData("R-encoder", encoder_r);
    //            telemetry.addData("H-encoder", encoder_h);
    //            telemetry.addData("ang", current_ang);
    //            telemetry.addData("global X (metres)", global_xM);
    //            telemetry.addData("global Y (metres)", global_yM);

                    prev_encoder_l = encoder_l;
                    prev_encoder_r = encoder_r;
                    prev_encoder_h = encoder_h;
                    prev_ang = current_ang;
                    Thread.sleep(10);
                }
                catch(Exception e){

                }
            }
        }
    }

    public String getGlobalPos(){
        return "Global X: " + (int)(global_xM * 10000)/10000.0 + " | Global Y: " + (int)(global_yM * 10000)/10000.0;
    }

    public boolean getBusyState(){
        return isBusy;
    }
}
