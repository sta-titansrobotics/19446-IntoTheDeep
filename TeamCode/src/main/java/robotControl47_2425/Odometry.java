package robotControl47_2425;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robocol.TelemetryMessage;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Odometry {
    private DcMotor odomLeft, odomRight, odomHorizontal;
    private BNO055IMU imu;


    private double prevEncoderL = 0, prevEncoderR = 0, prevEncoderH = 0, prevAngle = 0;
    private Orientation lastAngles = new Orientation();
    private double globalAngle = 0;
    private Telemetry telemetry;
    private double global_y =0, global_x =0;

    public Odometry(LinearOpMode opMode, Telemetry tm) {
        // Initialize hardware
        odomLeft = opMode.hardwareMap.get(DcMotor.class, "lf"); //expansionhub port 0
        odomRight = opMode.hardwareMap.get(DcMotor.class, "rr"); //control hub port 2
        odomHorizontal = opMode.hardwareMap.get(DcMotor.class, "rf"); //control hub port 3
        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);
        telemetry = tm;


    }

    public void updatePosition() {
        // Convert encoder ticks to meters
        double encoderL = encoderToMetres(-odomLeft.getCurrentPosition());
        double encoderR = encoderToMetres(odomRight.getCurrentPosition());
        double encoderH = encoderToMetres(odomHorizontal.getCurrentPosition());

        // Get the current angle in radians
        double currentAngle = Math.toRadians(getAngle());

        // Calculate encoder changes since the last update
        double deltaEncoderL = encoderL - prevEncoderL;
        double deltaEncoderR = encoderR - prevEncoderR;
        double deltaEncoderH = encoderH - prevEncoderH;

        // Calculate change in heading angle since the last update
        double deltaAngle = currentAngle - prevAngle;

        // Calculate forward/backward movement in the robot's local frame
        double deltaLocalX = (deltaEncoderL + deltaEncoderR) / 2;

        // Adjust lateral movement to account for angular change effects
        // Distance from the horizontal encoder to the center
        double disM_encoderHtoCenter = -0.17;
        double deltaLocalY = deltaEncoderH - (deltaAngle * disM_encoderHtoCenter);

        // Convert local changes to global coordinates using rotation matrix
        double deltaGlobalX = deltaLocalX * Math.cos(currentAngle) - deltaLocalY * Math.sin(currentAngle);
        double deltaGlobalY = deltaLocalX * Math.sin(currentAngle) + deltaLocalY * Math.cos(currentAngle);

        // Update global position
        global_x+= deltaGlobalX;
        global_y += deltaGlobalY;
        globalAngle = Math.toDegrees(currentAngle);

        // Update previous values for the next update
        prevEncoderL = encoderL;
        prevEncoderR = encoderR;
        prevEncoderH = encoderH;
        prevAngle = currentAngle;

        telemetry.addData("L-encoder", odomLeft);
        telemetry.addData("R-encoder", odomRight);
        telemetry.addData("H-encoder", odomHorizontal);
        telemetry.addData("ang", currentAngle);
        telemetry.addData("global X (metres)", global_x);
        telemetry.addData("global Y (metres)", global_y);
        telemetry.update();
    }

    private double encoderToMetres(int ticks) {
        double wheelDiameter = 0.032; // 3.2 cm wheel diameter
        double ticksPerRevolution = 2000.0; // Encoder ticks per revolution
        return (ticks / ticksPerRevolution) * (wheelDiameter * Math.PI);
    }

    public double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180) deltaAngle += 360;
        else if (deltaAngle > 180) deltaAngle -= 360;

        globalAngle += deltaAngle;
        lastAngles = angles;
        return globalAngle;
    }

    public double getGlobalX() {
        return global_x;
    }
    public double getGlobalY() {
        return global_y;
    }
    public double getGlobalAngle() {
        return globalAngle;
    }




}
