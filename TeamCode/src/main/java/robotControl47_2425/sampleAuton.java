package robotControl47_2425;

import androidx.annotation.AnyThread;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class sampleAuton extends LinearOpMode {
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


        chassis = new Chassis(this, 0.36 / 2, -0.36 / 2);
        chassis.startOdomThread();
        chassis.resetAngle();
        sleep(2000);
        telemetry.addLine("Ready");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        vSliderSystem.closeClaw();
        hSliderSystem.rampUp();
        waitForStart();
        resetRuntime();

        prepDropHighRung();

        chassis.p2pDrive(1.08, 0.2, 0, 2000, 0.18, 0.7, 0.2, 0.4, 0.02, 2, 1, 2, 1, 2, 0.02, 0.04);
        timeout(chassis);

        dropHighRung();
        prepPickup();

        //point 2
        chassis.p2pDrive(0.8, -0.86, 0, 1700, 0.8, 1.1, 0.2, 0.5, 0.06, 3, 1.15, 2, 1.25, 2, 0.02, 0.04);
        timeout(chassis);

        //point 3
        chassis.p2pDrive(1.5, -0.97, 0, 1500, 0.4, 1.1, 0.2, 0.5, 0.04, 3, 1.4, 2, 1, 2, 0.025, 0.04);
        timeout(chassis);

        //push floor 1
        chassis.p2pDrive(1.48, -1.2, 0, 1000, 0.4, 0.6, 0.2, 0.5, 0.06, 2, 1.3, 2, 1.4, 2, 0.02, 0.04);
        timeout(chassis);
//        sleep(200);

        //verify angle
//        chassis.p2pDrive(1.48, -1.25, 0, 500, 0.2, 0.4, 0.2, 0.3, 0.02, 2, 1.5, 2, 1.15, 2, 0.02, 0.04);
//        timeout(chassis);

//      hSlidePushFloor();
        chassis.p2pDrive(0.4, -1.22, 0, 1000, 0.25, 1.1, 0.2, 0.2, 0.02, 2, 1.6, 2, 1.3, 2, 0.03, 0.04);
        timeout(chassis);

//      hSlideReturn();
        chassis.p2pDrive(1.3, -1, 0, 1200, 0.6, 1.1, 0.2, 0.4, 0.1, 2, 1.5, 2, 2.5, 2, 0.03, 0.04);
        timeout(chassis);


        //push floor 2
        chassis.p2pDrive(1.48, -1.44, 0, 1200, 0.25, 1.1, 0.2, 0.5, 0.035, 2, 1.3, 2, 1.5, 2, 0.02, 0.04);
        timeout(chassis);
//        sleep(200);

        //verify angle

//        chassis.p2pDrive(1.49, -1.5, 0, 300, 0.2, 0.5, 0.2, 0.3, 0.04, 2, 1.3, 2, 1.15, 2, 0.02, 0.04);
//        timeout(chassis);

//        hSlidePushFloor();
//        sleep(200);
        chassis.p2pDrive(0.5, -1.45, 15, 1500, 0.25, 1.1, 0.2, 0.2, 0.1, 2, 1.5, 2, 1.15, 2, 0.02, 0.04);
        timeout(chassis);
//        hSlideReturn();


        // first rung specimen

        prepPickup();
        chassis.p2pDrive(0.4, -1, 15, 1000, 0.8, 1.1, 0.2, 0.5, 0.07, 1, 1.7, 2, 1.4, 2, 0.02, 0.04);
        timeout(chassis);


        chassis.p2pDrive(0.16, -1, 0, 1300, 0.3, 0.6, 0.2, 0.4, 0.02, 2, 1.5, 2, 1.5, 2, 0.02, 0.04);
        timeout(chassis);
        pickup();

        prepDropHighRung();
//        chassis.p2pDrive(0.75, 0.1, 0, 1000, 0.8, 1.1, 0.2, 0.4, 0.1, 2, 1.35, 2, 1.4, 2, 0.01, 0.04);
//        timeout(chassis);
        chassis.p2pDrive(1.09, 0.15, 0, 2000, 0.2, 0.9, 0.2, 0.4, 0.02, 2, 1.2, 2, 1.6, 2, 0.02, 0.04);
        timeout(chassis);

        dropHighRung();

        // for human player
//        sleep(100);

        // 2 & 3
        //commented 5:21
        for (int i = 0; i < 2; i++) {

            prepPickup();
            chassis.p2pDrive(0.3, -1.01, 0, 1700, 0.4, 1.1, 0.2, 0.4, 0.03, 2, 0.6, 2, 1, 2, 0.025, 0.04);
            timeout(chassis);

            chassis.p2pDrive(0.155, -1.01, 0, 1400, 0.3, 0.3, 0.2, 0.4, 0.01, 2, 0.6, 2, 0.8, 2, 0.025, 0.04);
            timeout(chassis);
            pickup();

            prepDropHighRung();
//            chassis.p2pDrive(0.75, -0.06, 0, 800, 0.8, 1.1, 0.2, 0.4, 0.1, 2, 1.35, 2, 1.6, 2, 0.01, 0.04);
//            timeout(chassis);
            chassis.p2pDrive(1.1, 0.12-0.06*i, 0, 2050, 0.18, 0.9, 0.3, 0.4,     0.01, 2, 0.69, 2, 1.1, 2, 0.02, 0.04);
            timeout(chassis);


            dropHighRung();


        }


        //oaihd
        prepPickup();
        hSliderSystem.goToPos(1500, 0.6);
        chassis.p2pDrive(0.48, -0.65, 64, 1000, 0.6, 1.1, 0.2, 0.4, 0.1, 3, 1.5, 2, 1.5, 2, 0.01, 0.04);
        timeout(chassis);


//        for(int i = 0; i < 1; i++){
//
//            chassis.p2pDrive(-0.1, -1.25, 0, 1500, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.15, 2, 0.02, 0.04);
//            timeout(chassis);
//            chassis.p2pDrive(0, -1.3, 0, 500, 0.2, 0.7, 0.2, 0.6, 0.02, 2, 1.15, 2, 1.15, 2, 0.02, 0.04);
//            timeout(chassis);
//            vSliderSystem.closeClaw();
//
//            vSliderSystem.tiltToPos(0.15);
//            vSliderSystem.goToPos(930);
//            chassis.p2pDrive(0.7, 0.15, 0, 2000, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.02, 0.04);
//            timeout(chassis);
//
//            chassis.p2pDrive(1.15, 0.15, 0, 2000, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.02, 0.04);
//            timeout(chassis);
//            vSliderSystem.tiltToPos(0.5);
//            sleep(300);
//            vSliderSystem.openClaw();
//        }
        //chassis.p2pDrive(0.3, -1.8, 0, 2000, 0.2, 1.1, 0.2, 0.7, 0.02, 2, 1.15, 2, 1.4, 2, 0.02, 0.04);
        timeout(chassis);

//testtest

//        chassis.p2pDrive(0.75, -0.75, 100, 0.9, 1.15, 1.3, 2, 2, 0.02, 0.04, 0.4);
//        timeout(3000, chassis);
////        // h slide intakes and outtakes
////
//        for(int i = 0; i < 3; i++){
//            // go into human player
//            chassis.p2pDrive(0.22, -0.9, 0, 0.9,1.15, 1.3, 2, 2, 0.02, 0.04, 0.4);
//            timeout(5000, chassis);
//
//            // crash into wall, virtual point outside of field, increase dis and angle tolerance
//            // tolerance, timeout, min power
//            // put kp kd in function, max_speed fast is 1.1, slow is 0.5,
//
//            chassis.p2pDrive(0.85, -0.13, 0, 0.9, 1.15, 2, 2, 2, 0.02, 0.04, 0.4);
//            timeout(5000, chassis);
//        }
//        chassis.p2pDrive(0.25, -0.9, 0, 0.9, 1.15, 1.3, 2, 2, 0.02, 0.04, 0.4);

//
//        chassis.p2pDrive(0.25, -0.9, 0, 0.7, 2, 2, 0.3, 0.3, 0.3);


//        chassis.p2pDrive(0.5, -0.4, -90, 0.6, 2, 2, 0.1, 0.1, 0.3);
//        timeout(1200, chassis);
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
        vSliderSystem.tiltToPos(0.15);
        vSliderSystem.goToPos(930);
    }

    public void dropHighRung() {
        vSliderSystem.tiltToPos(0.5);
        sleep(300);
        vSliderSystem.openClaw();
    }

    public void prepPickup() {
        vSliderSystem.goToPos(0);
        vSliderSystem.tiltToPos(0.73);
        vSliderSystem.pickupClaw();
    }

    public void pickup() {
        vSliderSystem.closeClaw();
        sleep(400);
    }

    public void hSlidePushFloor() {
        hSliderSystem.goToPos(1500);
        hSliderSystem.rampDown();
        hSliderSystem.outtaking();
    }

    public void hSlideReturn() {
        hSliderSystem.intakeOff();
        hSliderSystem.goToPos(0);
        hSliderSystem.rampUp();
    }


}