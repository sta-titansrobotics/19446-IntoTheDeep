package robotControl47_2425;

import androidx.annotation.AnyThread;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class sampleAuton extends LinearOpMode {
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    //private DcMotor hSlide;
    private Chassis chassis;
    ElapsedTime timer = new ElapsedTime();
    private Odometry odometry;
    private  VSlideController  vSliderSystem = null;


    @Override
    public void runOpMode() {
        vSliderSystem = new VSlideController(this);
        // MUST HAVE, DO NOT RUN THIS METHOD AGAIN AFTER AUTON INIT
        vSliderSystem.resetVSlidePos();

        chassis = new Chassis(this, 0.36 / 2, -0.36 / 2);
        chassis.startOdomThread();
        sleep(2000);
        telemetry.addLine("Ready");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        vSliderSystem.closeClaw();
        waitForStart();
        resetRuntime();

        vSliderSystem.tiltToPos(0.15);
        vSliderSystem.goToPos(900);

        chassis.p2pDrive(1.15, -0.13, 0, 2000, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.03, 0.04);
        timeout(chassis);
        vSliderSystem.tiltToPos(0.5);
        sleep(300);
        vSliderSystem.openClaw();

        for(int i = 0; i < 1; i++){
            vSliderSystem.goToPos(0);
            vSliderSystem.tiltToPos(0.7);
            vSliderSystem.pickupClaw();
            chassis.p2pDrive(-0.15, -0.9, 0, 4000, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.15, 2, 0.03, 0.04);
            timeout(chassis);
            chassis.p2pDrive(0, -1.05, 0, 500, 0.2, 1.1, 0.2, 0.6, 0.02, 2, 1.15, 2, 1.15, 2, 0.03, 0.04);
            timeout(chassis);
            vSliderSystem.closeClaw();
            sleep(50);
            vSliderSystem.tiltToPos(0.15);
            vSliderSystem.goToPos(900);
            chassis.p2pDrive(0.8, -0.1, 0, 1300, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.03, 0.04);
            timeout(chassis);

            chassis.p2pDrive(1.15, -0.1, 0, 1300, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.03, 0.04);
            timeout(chassis);
            vSliderSystem.tiltToPos(0.5);
            sleep(300);
            vSliderSystem.openClaw();
        }
        chassis.p2pDrive(0.3, -1.8, 0, 2000, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.03, 0.04);
        timeout(chassis);

//testtest

//        chassis.p2pDrive(0.75, -0.75, 100, 0.9, 1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);
//        timeout(3000, chassis);
////        // h slide intakes and outtakes
////
//        for(int i = 0; i < 3; i++){
//            // go into human player
//            chassis.p2pDrive(0.22, -0.9, 0, 0.9,1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);
//            timeout(5000, chassis);
//
//            // crash into wall, virtual point outside of field, increase dis and angle tolerance
//            // tolerance, timeout, min power
//            // put kp kd in function, max_speed fast is 1.1, slow is 0.5,
//
//            chassis.p2pDrive(0.85, -0.13, 0, 0.9, 1.15, 2, 2, 2, 0.03, 0.04, 0.4);
//            timeout(5000, chassis);
//        }
//        chassis.p2pDrive(0.25, -0.9, 0, 0.9, 1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);

//
//        chassis.p2pDrive(0.25, -0.9, 0, 0.7, 2, 2, 0.3, 0.3, 0.3);


//        chassis.p2pDrive(0.5, -0.4, -90, 0.6, 2, 2, 0.1, 0.1, 0.3);
//        timeout(1200, chassis);


        chassis.stopAllThreads();
        double endTime = getRuntime();
        while(opModeIsActive()){
            telemetry.addData("End Time", "%.2f", endTime);
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

    private void timeout(Chassis chassis){
        timer.reset();
//        while (opModeIsActive() && timer.milliseconds() < ms && chassis.isBusy){
//            // && chassis.isBusy && timer.milliseconds() < ms
//
//        }
        while(opModeIsActive() && chassis.isBusy){
            telemetry.addData("w", timer.milliseconds());
            telemetry.addData("isBusy", chassis.getBusyState());
            telemetry.addData("ang", chassis.getAngle());
            telemetry.addData("pos", chassis.getGlobalPos());
            telemetry.update();
            sleep(10);
        }
        sleep(100);

    }




}