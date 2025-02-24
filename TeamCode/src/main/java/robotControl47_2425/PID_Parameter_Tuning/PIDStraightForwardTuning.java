package robotControl47_2425.PID_Parameter_Tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.auto.Chassis;
import robotControl47_2425.auto.Odometry;


/**
 * Used to tune the kpx and kdx value for the robot, note the robot may have different optimal values
 * for forward and backward movement independently because of the weight
 *
 * Press gamepad1.a to move the robot back and forth, use the dpad to adjust the kpx and kdx values.
 *
 * @author Best
 */
//@TeleOp
public class PIDStraightForwardTuning extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    double kp = 0.9; // Proportional gain
    double kd = 2; // Derivative gain
    double desiredPosition = 0; // Desired position to move to
    int buttonAPressCount = 0;
    // Placeholder for error calculation
    double errorX = 0;
    double errorY = 0;
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    //private DcMotor hSlide;
    private Chassis chassis;
    private Odometry odometry;

    @Override
    public void runOpMode() {

        chassis = new Chassis(this, 0, 0, "T");
        chassis.startOdomThread();
        chassis.resetAngle();
        sleep(2000);
        telemetry.addLine("Ready");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)

        waitForStart();
        resetRuntime();

        //   prepDropHighRung();
        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                kp += 0.002; // Increase kp
                sleep(20);
            } else if (gamepad1.dpad_down) {
                kp -= 0.002; // Decrease kp
                sleep(20);
            }
            //forwards kp 0.888 kd 2.407   or 0.85 & 2.389 or 0.86&2.369
            //backwards kp 0.86 kd 2.375

            if (gamepad1.dpad_right) {
                kd += 0.001; // Increase kd
                sleep(20);
            } else if (gamepad1.dpad_left) {
                kd -= 0.001; // Decrease kd
                sleep(20);
            }

            // Check if button A is pressed
            if (gamepad1.a) {
                buttonAPressCount++;

                // Determine the desired position based on the number of button presses
                if (buttonAPressCount % 2 == 1) {
                    desiredPosition = 1.4; // Move forward by 0.8
                } else {
                    desiredPosition = 0; // Return to 0
                }

                // Drive the chassis to the desired position
                chassis.p2pDrive(desiredPosition, 0, 0, 5000, 0.2, 1.1, 0.2, 0.5, 0.02, 2, kp, kd, 0, 0, 0, 0);
                timeout(chassis);

            }

            errorX = Math.abs(desiredPosition - chassis.getGlobalX());
            errorY = Math.abs(chassis.getGlobalY());

            telemetry.addData("Error-X(cm) : ", errorX * 100);
            telemetry.addData("Error-Y(cm) : ", errorY * 100);
            telemetry.addData("Motor Power: ", chassis.getMotorPower() );


            telemetry.addData("kp(dpad up/down) : ", kp);
            telemetry.addData("kd(dpad right/left : ", kd);

            telemetry.update();

        }

    }
    private void timeout(Chassis chassis) {
        timer.reset();
//        while (opModeIsActive() && timer.milliseconds() < ms && chassis.isBusy){
//            // && chassis.isBusy && timer.milliseconds() < ms
//
//        }
        while (opModeIsActive() && chassis.isBusy) {
            telemetry.addData("w", timer.milliseconds());
            telemetry.addData("isBusy", chassis.getBusyState());
            telemetry.addData("ang", chassis.getAngle());
            telemetry.addData("pos", chassis.getGlobalPos());
            telemetry.update();
            sleep(10);
        }
        sleep(30);

    }


}