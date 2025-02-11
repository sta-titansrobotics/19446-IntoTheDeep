package robotControl47_2425.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class provsAutonTest extends LinearOpMode {
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


        chassis = new Chassis(this, 0, 0);
        chassis.startOdomThread();
        chassis.resetAngle();
        sleep(2000);
        telemetry.addLine("Ready");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        vSliderSystem.closeClaw();
        hSliderSystem.rampUp();
        hSliderSystem.intakeOff();
        waitForStart();
        resetRuntime();

        prepDropHighRung();
        // fwd: kp = 0.88, kd = 2.4
        // bwd: kp = 0.86, kd = 2.375
        //strafe: kp 1.254, kd 1.506
        // turn: kp = 0.007, kd = 0.01

        //S1

        chassis.p2pDrive(0.88, 0.3, 0, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        timeout(chassis);

        dropHighRung();
        prepPickup();

        //B1_1
        chassis.p2pDrive(0.57, -0.48, 140, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        timeout(chassis);


        hSliderSystem.goToPos(1500, 0.8);
        sleep(1500);

        //B1_2
        chassis.p2pDrive(0.5, -0.5, 55, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 4, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        timeout(chassis);

        //B2_1
        chassis.p2pDrive(0.65, -0.7, 140, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        hSliderSystem.goToPos(1000, 1);
        timeout(chassis);

        hSliderSystem.goToPos(1500, 1);
        sleep(200);

        //B2_2
        chassis.p2pDrive(0.5, -0.7, 55, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        timeout(chassis);

        //B3_1
        chassis.p2pDrive(0.7, -0.9, 110, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        hSliderSystem.goToPos(1000, 1);
        timeout(chassis);

        hSliderSystem.goToPos(1500, 1);
        sleep(200);

        //B3_2
        chassis.p2pDrive(0.5, -0.85, 55, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
        timeout(chassis);





//
//
        // 2 & 3
        //commented 5:21
        for (int i = 0; i < 2; i++) {

            prepPickup();
            chassis.p2pDrive(0.01, -0.855, 0, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
            timeout(chassis);
            pickup();

            prepDropHighRung();
//
            chassis.p2pDrive(0.88, 0.23-0.07*i, 55, 5000, 0.18, 0.7, 0.2, 0.5, 0.02, 2, 0.88, 2.4, 1.254, 1.506, 0.007, 0.01);
            timeout(chassis);


            dropHighRung();


        }


//        //oaihd
//        prepPickup();
//        hSliderSystem.goToPos(1500, 0.6);
//        chassis.p2pDrive(0.48, -0.65, 64, 1000, 0.6, 1.1, 0.2, 0.4, 0.1, 3, 1.5, 2, 1.5, 2, 0.01, 0.04);
//        timeout(chassis);


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
        vSliderSystem.goToPos(940);
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