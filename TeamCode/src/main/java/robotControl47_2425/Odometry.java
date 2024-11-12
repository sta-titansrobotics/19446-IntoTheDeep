package robotControl47_2425;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

public class Odometry {

    LinearOpMode mainTask = null;
    RobotPos pos = null;
    Chassis chassis = null;

    double encoder_l, encoder_r, encoder_h;
    double global_yM, global_xM, globalAngRad;
    double encoder_ppr= 2000.0; //8192CPR or 8192 PPR for REV through bore encoder on REV website
    double wheel_radius_M = 0.032/2.0;// 0.036M for radius of goBilda omniwheel || 0.0175M for radius of "openodometry" omniwheel

    boolean isBusy;

    double disM_encoderHtoCenter = -0.17;// distance from horizontal odom wheel to the center of the robot
    //0.065M for goBilda odom wheel bot || 0.055M for openodometry wheel bot

    BNO055IMU imu;
    Orientation lastAngles = new Orientation();
    double                  globalAngle;
    Telemetry telemetry = null;

    public Odometry(LinearOpMode mainTask, Chassis chassis, RobotPos pos, boolean auto) {
        this.mainTask = mainTask;
        this.chassis = chassis;
        this.pos.setPosition(pos);   // set starting the robot position


        this.telemetry = this.mainTask.telemetry;
        //now using the general IMU initialize
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
        imu = mainTask.hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);
    }







}
