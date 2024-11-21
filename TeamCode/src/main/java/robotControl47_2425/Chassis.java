package robotControl47_2425;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Chassis {
    private DcMotor lf, lr, rf, rr;
    private LinearOpMode opMode;
    private Odometry odometry;
    private RobotPos targetPosition;
    double global_yM, global_xM, globalAngRad;
    double encoder_ppr= 2000.0; //8192CPR or 8192 PPR for REV through bore encoder on REV website
    double wheel_radius_M = 0.032/2.0;// 0.036M for radius of goBilda omniwheel || 0.0175M for radius of "openodometry" omniwheel



    public Chassis(LinearOpMode opMode, Odometry odometry) {
        this.opMode = opMode;
        this.odometry = odometry;

        // Initialize motors from the hardware map
        lf = opMode.hardwareMap.get(DcMotor.class, "lf");
        rf = opMode.hardwareMap.get(DcMotor.class, "rf");
        lr = opMode.hardwareMap.get(DcMotor.class, "lr");
        rr = opMode.hardwareMap.get(DcMotor.class, "rr");

        // Set directions for motors
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set zero power behavior
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize target position
        targetPosition = new RobotPos(0, 0, 0);
    }

    private void toPoint(double target_x, double target_y, double target_ang, double max_speed, double kp, double kd, double turn_kp, double turn_kd, double turn_max_speed){
        double current_x = odometry.getCurrentPosition().x;
        double current_y = odometry.getCurrentPosition().y;

        double current_ang = odometry.getAngle();
        //put initial values first during init
        double prev_error_x = 0, prev_error_y = 0, prev_error_ang = 0;//init before use

        while (Math.sqrt(Math.pow(target_x - current_x, 2) + Math.pow(target_y - current_y, 2))>0.02 || Math.abs(target_ang-current_ang)>1){
            //condition uses formula for circle to create resolution
            //if current x and y is within circle with radius 0.02M
            //or if current angle is within a 2 degree resolution from target, stop
            current_x = odometry.getCurrentPosition().x;
            current_y = odometry.getCurrentPosition().y;
            current_ang = odometry.getAngle();

            double error_x = target_x - current_x;
            double error_y = target_y - current_y;
            double global_vel_x = (kp * error_x) + (kd * (error_x - prev_error_x));// PD (PID without the I) control
            double global_vel_y = (kp * error_y) + (kd * (error_y - prev_error_y));
            double local_vel_x = global_vel_x * Math.cos(Math.toRadians(odometry.getAngle())) + global_vel_y * Math.sin(Math.toRadians(odometry.getAngle()));

            double local_vel_y = -global_vel_x * Math.sin(Math.toRadians(odometry.getAngle())) + global_vel_y * Math.cos(Math.toRadians(odometry.getAngle()));// inverse matrix to calculate local velocities to global velocities

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

        }
        lf.setPower(0);
        rf.setPower(0);
        lr.setPower(0);
        rr.setPower(0);
    }
}
//sdkilo