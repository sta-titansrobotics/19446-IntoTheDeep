package robotControl47_2425.PID_Parameter_Tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.auto.Chassis;
import robotControl47_2425.auto.Odometry;

/**
 * Used to tune the kp and kd value for the turning movement of the robot,
 *
 * Press gamepad1.a to move the robot back and forth, use the dpad to adjust the kpx and kdx values.
 *
 * @author Best
 */
@TeleOp
public class PIDTurnTuning extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    double kp = 0.02; // Proportional gain
    double kd = 0; // Derivative gain
    double desiredPosition = 0; // Desired position to move to
    int buttonAPressCount = 0;
    // Placeholder for error calculation
    double errorAng = 0;
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
                kp += 0.001; // Increase kp
                sleep(50);
            } else if (gamepad1.dpad_down) {
                kp -= 0.001; // Decrease kp
                sleep(50);
            }
            //forwards kp 0.888 kd 2.407   or 0.85 & 2.389 or 0.86&2.369
            //backwards kp 0.86 kd 2.375

            if (gamepad1.dpad_right) {
                kd += 0.001; // Increase kd
                sleep(50);
            } else if (gamepad1.dpad_left) {
                kd -= 0.001; // Decrease kd
                sleep(50);
            }

            // Check if button A is pressed
            if (gamepad1.a) {
                buttonAPressCount++;

                // Determine the desired position based on the number of button presses
                if (buttonAPressCount % 2 == 1) {
                    desiredPosition = 270; // Move forward by 0.8
                } else {
                    desiredPosition = 0; // Return to 0
                }

                // Drive the chassis to the desired position
                chassis.p2pDrive(0, 0, desiredPosition, 5000, 0.2, 1.1, 0.2, 0.6, 0.02, 1, 0, 0, 0, 0, kp, kd);
                timeout(chassis);
            }

            errorX = Math.abs(desiredPosition - chassis.getGlobalX());
            errorY = Math.abs(chassis.getGlobalY());
            errorAng = desiredPosition - chassis.getAngle();

            telemetry.addData("Error-X(cm) : ", errorX * 100);
            telemetry.addData("Error-Y(cm) : ", errorY * 100);
            telemetry.addData("error_ang (deg): ", errorAng);

            telemetry.addData("Motor Power: ", chassis.getMotorPower() );
            telemetry.addData("kp(dpad up/down) : ", kp);
            telemetry.addData("kd(dpad right/left : ", kd);

            telemetry.update();

        }

    }//sjfdjsa

    private void updateTelemetry() {
//
//        telemetry.addData("Front Left Power", fl.getPower());
//        telemetry.addData("Front Right Power", fr.getPower());
//        telemetry.addData("Back Left Power", bl.getPower());
//        telemetry.addData("Back Right Power", br.getPower());

        telemetry.addData("Position (m): ", chassis.getGlobalPos());
        telemetry.update();
    }

    //
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