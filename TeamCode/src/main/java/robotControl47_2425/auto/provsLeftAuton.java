package robotControl47_2425.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class provsLeftAuton extends LinearOpMode {
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

        chassis.p2pDrive(0.02, 1.14, 135, 4000, 0.2, 0.8, 0.15, 0.35, 0.04, 2, 0.92, 2.4, 1.3, 1.5, 0.007, 0.014);
        prepDropHighBasket();
        timeout(chassis);

        dropHighBasket();



        //P1
//
        chassis.p2pDrive(0.4, 0.91, 170, 4000, 0.2, 0.7, 0.15, 0.35, 0.016, 1, 0.88, 2.4, 1.3, 1.5, 0.007, 0.014);
        prepVTransfer();
        timeout(chassis);

        intake();

        //S2
        chassis.p2pDrive(0.02, 1.14, 135, 4000, 0.2, 0.8, 0.15, 0.35, 0.04, 2, 0.92, 2.4, 1.3, 1.5, 0.007, 0.014);
        transfer();
        prepDropHighBasket();
        timeout(chassis);
        dropHighBasket();

        //P2
        chassis.p2pDrive(0.4, 1.15, 173, 4000, 0.2, 0.7, 0.15, 0.35, 0.016, 1, 0.88, 2.4, 1.3, 1.5, 0.007, 0.014);
        prepVTransfer();
        timeout(chassis);

        intake();

        //S3
        chassis.p2pDrive(0.02, 1.14, 135, 4000, 0.2, 0.8, 0.15, 0.35, 0.04, 2, 0.92, 2.4, 1.3, 1.5, 0.007, 0.014);
        transfer();
        prepDropHighBasket();
        timeout(chassis);
        dropHighBasket();

        //P3
        chassis.p2pDrive(0.52, 0.925, 224, 4000, 0.2, 0.7, 0.15, 0.35, 0.016, 1, 0.88, 2.4, 1.3, 1.5, 0.007, 0.014);
        prepVTransfer();
        timeout(chassis);

        intake();

        //S4
        chassis.p2pDrive(0.03, 1.15, 135, 4000, 0.2, 0.8, 0.15, 0.35, 0.04, 2, 0.92, 2.4, 1.3, 1.5, 0.007, 0.014);
        transfer();
        prepDropHighBasket();
        timeout(chassis);

        dropHighBasket();

        // Park
        chassis.p2pDrive(1.42, 0.5, 90, 4000, 0.4, 1.1, 0.15, 0.4, 0.3, 3, 1.6, 2.4, 1, 1.5, 0.007, 0.014);
        sleep(300);
        vSliderSystem.goToPos(1100);
        vSliderSystem.tiltToPos(0.4);
        timeout(chassis);

        chassis.p2pDrive(1.42, 0.07, 90, 1000, 0.2, 0.8, 0.15, 0.4, 0.03, 3, 1.6, 2.4, 1, 1.5, 0.007, 0.014);
        timeout(chassis);
        vSliderSystem.goToPos(730);


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

    public void prepDropHighBasket() {
        vSliderSystem.closeClaw();
        sleep(100);
        vSliderSystem.tiltToPos(0.8);
        vSliderSystem.goToPos(2820);
        sleep(900);
    }

    public void dropHighBasket() {
        vSliderSystem.tiltToPos(0.93);
        sleep(200);
        vSliderSystem.openClaw();
        sleep(300);

    }

    public void prepVTransfer() {
        vSliderSystem.tiltToTransfer();
        sleep(200);
        vSliderSystem.goToPos(600);
        vSliderSystem.transferClaw();
    }



    public void transfer(){
        hSliderSystem.bootUp();
        // vslide up and tilt ready
        hSliderSystem.tiltTransfer();


        sleep(700);
        hSliderSystem.goToPos(0, 1);
        while (hSliderSystem.getCurrentPos() > 50 && opModeIsActive()){
            sleep(10);
        }

        hSliderSystem.outtake();
        sleep(80);
        hSliderSystem.idleIntake();
        vSliderSystem.goToPos(380);

        sleep(300);
        vSliderSystem.closeClaw();
        sleep(250);
    }

    public void intake(){
        hSliderSystem.tiltIntake();
        hSliderSystem.goToPos(700, 1);

        sleep(450);
        hSliderSystem.intake();
        hSliderSystem.goToPos(1440, 0.29);
        sleep(1700);
    }



}