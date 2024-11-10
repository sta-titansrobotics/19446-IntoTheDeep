package Odometry;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;


/**
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Forward:    Driving forward and backwards               Left-joystick Forward/Backwards
 * 2) Strafe:  Strafing right and left                     Left-joystick Right and Left
 * 3) Turn:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backwards when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="odom_calc", group="Linear Opmode")
//@Disabled
public class P2P extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    DcMotor lf, rf, lr, rr;
    double encoder_l, encoder_r, encoder_h;
    double global_yM, global_xM, globalAngRad;
    double encoder_ppr= 2000.0; //8192CPR or 8192 PPR for REV through bore encoder on REV website
    double wheel_radius_M = 0.032/2.0;// 0.036M for radius of goBilda omniwheel || 0.0175M for radius of "openodometry" omniwheel

    boolean isBusy;

    double disM_encoderHtoCenter = -0.17;// distance from horizontal odom wheel to the center of the robot
    //0.065M for goBilda odom wheel bot || 0.055M for openodometry wheel bot


    BNO055IMU               imu;
    Orientation             lastAngles = new Orientation();
    double                  globalAngle;

    boolean running = false;

    private NormalizedColorSensor colorSensor = null;


    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        lf  = hardwareMap.get(DcMotor.class, "lf");
        lr  = hardwareMap.get(DcMotor.class, "lr");
        rf = hardwareMap.get(DcMotor.class, "rf");
        rr = hardwareMap.get(DcMotor.class, "rr");
        //colorSensor = hardwareMap.get(NormalizedColorSensor.class, "base_color");
        //arm = hardwareMap.get(DcMotor.class, "arm");
        //extend = hardwareMap.get(DcMotor.class, "lift");

        // Most robots need the motors on one side to be reversed to drive forward.
        // When you first test your robot, push the left joystick forward
        // and flip the direction ( FORWARD <-> REVERSE ) of any wheel that runs backwards
        lf.setDirection(DcMotor.Direction.REVERSE);
        lr.setDirection(DcMotor.Direction.REVERSE);
        rf.setDirection(DcMotor.Direction.FORWARD);
        rr.setDirection(DcMotor.Direction.FORWARD);
        //arm.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Orientation             lastAngles = new Orientation();
        double                  globalAngle;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor).enableLight(true);
        }

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();
        resetAngle();
        odom_thread calcThread = new odom_thread();
        odom_toPoint runToPoint = new odom_toPoint(0.2, -0.2, 135, 0.2, 2.7, 3.5, 0.03, 0.067, 0.3);
        odom_toPoint backToPoint = new odom_toPoint(0, 0, 0, 0.3, 2.7, 3.5, 0.03, 0.067, 0.3);

        calcThread.start();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.

            double Vgx   = -0.3 * gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double Vgy =  0.3 * gamepad1.left_stick_x;
            double Vrotate     =  0.3 * gamepad1.right_stick_x;

//            double Vrx = Math.cos(DegtoRad(getAngle())) * Vgy - Math.sin(DegtoRad(getAngle())) * Vgx;
//            double Vry = Math.sin(DegtoRad(getAngle()))* Vgy + Math.cos(DegtoRad(getAngle())) * Vgx;

            double lfPower = Vgx - Vgy + Vrotate;//local vel to global frame vel
            double rfPower = Vgx - Vgy - Vrotate;
            double lrPower = Vgx + Vgy + Vrotate;
            double rrPower = Vgx + Vgy - Vrotate;
//            telemetry.addData("Left Front Power", lfPower);
//            telemetry.addData("Right Front Power", rfPower);
//            telemetry.addData("Left Rear Power", lrPower);
//            telemetry.addData("Right Rear Power", rrPower);
//            telemetry.update(); // Ensures all data is sent to the telemetry screen


            double maxNumber = Math.max(Math.max(Math.abs(lfPower), Math.abs(lrPower)), Math.max(Math.abs(rfPower), Math.abs(rrPower)));
            if(maxNumber > 0.5){
                lfPower /= maxNumber;
                lrPower /= maxNumber;
                rfPower /= maxNumber;
                rrPower /= maxNumber;
            }
            if (!isBusy){
                lf.setPower(lfPower);
                rf.setPower(rfPower);
                lr.setPower(lrPower);
                rr.setPower(rrPower);
            }




            if (gamepad1.a && !isBusy){
                isBusy = true;
                runToPoint.start();
                while(runToPoint.isAlive()){idle();}
            }
            if (gamepad1.b && !isBusy){
                isBusy = true;
                backToPoint.start();
                while(backToPoint.isAlive()){idle();}
            }
        }
    }

    private double DegtoRad(double degrees){
        double output = degrees*3.14/180;
        return output;
    }

    private double getAngle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

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

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }
    private class odom_thread extends Thread{
        public odom_thread(){
        }
        public void run(){
            try{
                odom_pos_est();
            }
            catch(Exception e){}
        }
    }
    private void odom_pos_est(){
        double prev_encoder_l = 0, prev_encoder_r = 0, prev_encoder_h = 0, prev_ang = 0, current_ang;
        double delta_encoder_l, delta_encoder_r, delta_encoder_h, delta_local_x, delta_local_y,
                delta_global_x, delta_global_y, delta_ang;
        while (!isStopRequested() && opModeIsActive()){
            encoder_l = encoderToMetres(-lf.getCurrentPosition());
            encoder_r = encoderToMetres(-lr.getCurrentPosition()); //negative if using gobilda omniwheel bot, positive if using openodometry bot
            encoder_h = encoderToMetres(rf.getCurrentPosition()); //negative if using gobilda omniwheel bot, positive if using openodometry bot

            current_ang = Math.toRadians(getAngle()); //degrees to radians (either ways of calculating current angle work[imu or encoder])
            //current_ang = Math.toRadians((encoder_r-encoder_l)/0.031) //(r-l) divided by distance (METRES) between the encoder wheels

            delta_encoder_l = encoder_l - prev_encoder_l;
            delta_encoder_r = encoder_r - prev_encoder_r;
            delta_encoder_h = encoder_h - prev_encoder_h;
            delta_ang = current_ang - prev_ang;

            delta_local_x = (delta_encoder_l+delta_encoder_r)/2;//find avrg between both odom wheels and convert ticks to M
            //do not need arc formula because both "x" encoders cancel out offset


            delta_local_y = delta_encoder_h - (delta_ang * disM_encoderHtoCenter); //use arc formula to subtract arc from horizontal encoder wheel


            //* distance of h wheel to center

            delta_global_x = delta_local_x*Math.cos(current_ang)-delta_local_y*Math.sin(current_ang);
            delta_global_y = delta_local_x*Math.sin(current_ang)+delta_local_y*Math.cos(current_ang);

            global_xM += delta_global_x;
            global_yM += delta_global_y;
            telemetry.addData("L-encoder", encoder_l);
            telemetry.addData("R-encoder", encoder_r);
            telemetry.addData("H-encoder", encoder_h);
            telemetry.addData("ang", current_ang);
            telemetry.addData("x", global_xM);
            telemetry.addData("y", global_yM);
            telemetry.update();

            prev_encoder_l = encoder_l;
            prev_encoder_r = encoder_r;
            prev_encoder_h = encoder_h;
            prev_ang = current_ang;

        }
    }
    private double encoderToMetres(double ticks){
        double dis = (ticks/encoder_ppr)*2*3.1415*wheel_radius_M; //circumference * ticks/ppr
        return dis;
    }
    private class odom_toPoint extends Thread{
        double target_x, target_y, target_ang, max_speed, kp, kd, turn_kp, turn_kd, turn_max_speed;
        public odom_toPoint(double target_x, double target_y, double target_ang, double max_speed, double kp, double kd, double turn_kp, double turn_kd, double turn_max_speed){
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
        public void run(){
            try{
                toPoint(target_x, target_y, target_ang, max_speed, kp, kd, turn_kp, turn_kd, turn_max_speed);
            }
            catch (Exception e){}
        }
    }
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
            telemetry.addData("global vel_y", global_vel_y);
            double local_vel_x = global_vel_x * Math.cos(Math.toRadians(getAngle())) + global_vel_y * Math.sin(Math.toRadians(getAngle()));

            double local_vel_y = -global_vel_x * Math.sin(Math.toRadians(getAngle())) + global_vel_y * Math.cos(Math.toRadians(getAngle()));// inverse matrix to calculate local velocities to global velocities
            telemetry.addData("local vel_y", local_vel_y);
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
}