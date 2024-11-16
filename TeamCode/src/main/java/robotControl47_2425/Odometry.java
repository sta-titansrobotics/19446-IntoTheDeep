package robotControl47_2425;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Odometry {
    private DcMotor odomLeft, odomRight, odomHorizontal;
    private BNO055IMU imu;
    private RobotPos currentPosition;

    private double disM_encoderHtoCenter = -0.17; // Distance from the horizontal encoder to the center

    public Odometry(LinearOpMode opMode) {
        // Initialize hardware
        odomLeft = opMode.hardwareMap.get(DcMotor.class, "odomLeft");
        odomRight = opMode.hardwareMap.get(DcMotor.class, "odomRight");
        odomHorizontal = opMode.hardwareMap.get(DcMotor.class, "odomHorizontal");
        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");

        currentPosition = new RobotPos(0, 0, 0);
    }

    public void updatePosition() {
        // Implement position calculation logic as per the provided odometry class
        double encoderL = encoderToMetres(odomLeft.getCurrentPosition());
        double encoderR = encoderToMetres(odomRight.getCurrentPosition());
        double encoderH = encoderToMetres(odomHorizontal.getCurrentPosition());
        double currentAngle = Math.toRadians(getAngle());

        // Use encoder deltas and angle changes to update global position
        // ...
        // Update `currentPosition` accordingly
    }

    private double encoderToMetres(int ticks) {
        double wheelDiameter = 0.032; // 3.2 cm wheel diameter
        double ticksPerRevolution = 2000.0; // Encoder ticks per revolution
        return (ticks / ticksPerRevolution) * (wheelDiameter * Math.PI);
    }

    private double getAngle() {
        // Implement IMU angle reading as per provided code
        return 0; // Placeholder
    }

    public RobotPos getCurrentPosition() {
        return currentPosition;
    }
}
