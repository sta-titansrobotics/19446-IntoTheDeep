package robotControl47_2425.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class provsRightAuton extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    //private DcMotor hSlide;
    private Chassis chassis;
    private Odometry odometry;
    private VSlideController vSliderSystem = null;
    private HSlideController hSliderSystem = null;


    @Override
    public void runOpMode() {
        vSliderSystem = new VSlideController(this);
        hSliderSystem = new HSlideController(this);

        // MUST HAVE, DO NOT RUN THIS METHOD AGAIN AFTER AUTON INIT
        hSliderSystem.resetHSlidePos();
        vSliderSystem.resetVSlidePos();


        chassis = new Chassis(this, 0, 0, "A");
        chassis.startOdomThread();
        chassis.resetAngle();
        sleep(2000);
        telemetry.addLine("Ready");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        vSliderSystem.closeClaw();
//        hSliderSystem.rampUp();
        hSliderSystem.idleIntake();
        hSliderSystem.bootUp();
        hSliderSystem.tiltInit();
        waitForStart();
        resetRuntime();


        // fwd: kp = 0.88, kd = 2.4
        // bwd: kp = 0.86, kd = 2.375
        //strafe: kp 1.254, kd 1.506
        // turn: kp = 0.007, kd = 0.01

        //S1
        prepDropHighRung();
        chassis.p2pDrive(0.93, 0.27, 0, 2500, 0.2, 0.7, 0.2, 0.4, 0.02, 2, 0.84, 3, 1.254, 1.506, 0.004, 0.01);

        sleep(1800);
        dropHighRung();
        prepPickup();


        //B1_1
        hSliderSystem.tiltTransfer();

        chassis.p2pDrive(0.68, -0.475, 139, 4000, 0.2, 0.8, 0.2, 0.4, 0.03, 4, 1.3, 1.4, 1.3, 1.65, 0.01, 0.02);
        hSliderSystem.goToPos(300, 0.2);
        hSliderSystem.bootDown();
        timeout(chassis);

        hSliderSystem.goToPos(1440, 1);

        sleep(600);
//
        //B1_2

        chassis.p2pDrive(0.2, -0.55, 85, 4000, 0.2, 0.6, 0.3, 0.9, 0.15, 15, 1.5, 2.4, 1.3, 1.5, 0.015, 0.05);
        sleep(100);
        hSliderSystem.goToPos(300, 0.4);
        timeout(chassis);

        hSliderSystem.bootUp();

//
        //B2_1

        chassis.p2pDrive(0.824, -0.68, 124, 4000, 0.2, 0.8, 0.2, 0.4, 0.04, 4, 1.4, 1.5, 1.4, 1.4, 0.011, 0.02);

        timeout(chassis);

        hSliderSystem.goToPos(1470, 1);
        hSliderSystem.bootDown();
        sleep(600);

//        //B2_2
        chassis.p2pDrive(0.34, -0.8, 76.1, 4000, 0.2, 0.6, 0.2, 1.0, 0.15, 14, 1.5, 2.4, 1.3, 1.5, 0.015, 0.05);

        hSliderSystem.goToPos(600, 0.2);
        timeout(chassis);


        hSliderSystem.bootUp();
        hSliderSystem.goToPos(0, 1);
        prepPickup();
        chassis.p2pDrive(-.02, -0.8, 0, 1500, 0.25, 0.7, 0.2, 0.3, 0.03, 3, 0.88, 3, 1.254, 1.506, 0.007, 0.014);

        sleep(1400);
        pickup();
//
////
//        //B3_1
//        hSliderSystem.bootUp();
//        chassis.p2pDrive(0.7, -0.98, 135, 5000, 0.18, 0.7, 0.2, 0.6, 0.04, 3, 0.88, 2.4, 1.254, 1.506, 0.009, 0.02);
//        hSliderSystem.goToPos(1500, 0.5);
//        timeout(chassis);
////
//        hSliderSystem.bootDown();
//        sleep(300);
////
////        //B3_2
//
//        chassis.p2pDrive(0.3, -1, 80, 5000, 0.18, 0.6, 0.3, 0.8, 0.07, 10, 0.88, 2.4, 1.254, 1.506, 0.008, 0.017);
//        hSliderSystem.goToPos(400, 0.1);
//        timeout(chassis);
////
//        hSliderSystem.goToPos(0, 1);
//        hSliderSystem.bootUp();
//
////        sleep(300);
////
////
//        prepPickup();
//        chassis.p2pDrive(0.034, -0.8, 0, 1500, 0.22, 0.7, 0.2, 0.3, 0.03, 3, 0.88, 3, 1.254, 1.506, 0.007, 0.014);
//        timeout(chassis);
//        pickup();
////
////
//////
//////
////        // 2 & 3
////        //commented 5:21
        for (int i = 0; i < 2; i++) {
            prepDropHighRung();
//
            chassis.p2pDrive(1  , 0.22-0.06*i, 0, 5000, 0.25, 1.1, 0.2, 0.3, 0.04, 2, 0.85, 3, 1.8, 1.7, 0.009, 0.02);
//l

            sleep(2700);

            dropHighRung();


            prepPickup();
            chassis.p2pDrive(-.04, -0.8, 0, 3000, 0.25, 0.7, 0.2, 0.3, 0.03, 3, 0.88, 2, 1.7, 2, 0.007, 0.014);

            sleep(2500);
            pickup();
        }
        prepDropHighRung();
//
        chassis.p2pDrive(1  , 0.10, 0, 5000, 0.25, 1.1, 0.2, 0.3, 0.04, 2, 0.84, 2.9, 1.8, 1.6, 0.009, 0.02);


        sleep(2600);

        dropHighRung();
        prepPickup();
        chassis.p2pDrive(0.3, -0.5, 64, 3000, 0.25, 0.7, 0.2, 0.5, 0.1, 3, 0.88, 2, 1.7, 2, 0.007, 0.014);
        hSliderSystem.goToPos(1400, 0.5);
        timeout(chassis);

        chassis.stopAllThreads();
        double endTime = getRuntime();
        while (opModeIsActive()) {
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

    public void prepDropHighRung() {
        vSliderSystem.tiltToPos(0.95);
        vSliderSystem.goToPos(1240);
    }

    public void dropHighRung() {
        hSliderSystem.tiltTransfer();
        vSliderSystem.goToPos(1720);
        sleep(400);
        vSliderSystem.openClaw();
    }

    public void prepPickup() {
        vSliderSystem.goToPos(200);
        vSliderSystem.tiltToPos(0.33);
        vSliderSystem.pickupClaw();
    }

    public void pickup() {
        vSliderSystem.closeClaw();
        sleep(250);
    }


}