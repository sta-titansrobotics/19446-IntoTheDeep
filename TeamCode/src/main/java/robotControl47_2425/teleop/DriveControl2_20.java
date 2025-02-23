package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.auto.Chassis;
import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "46TeleOp", group = "Test")
public class DriveControl2_20 extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    private Chassis chassis;
    private HSlideController hSliderSystem = null;
    private VSlideController vSliderSystem = null;
    private boolean clawOpen = false;
    private boolean intakeUp = true;
    private boolean bootUp = true;
    boolean isVslideInTransferPos = false;


    //    private boolean armTiltedUp = false;
//    private boolean clawRolledUp = false;
    private long totalTime = 0;
    private int position_buttonA = 0;
    private boolean isHighBasket = false;

    @Override
    public void runOpMode() throws InterruptedException {

        hSliderSystem = new HSlideController(this);
        vSliderSystem = new VSlideController(this);

        chassis = new Chassis(this, 0, 0, "T");

        //   hSliderSystem.initialize();
        //   vSliderSystem.initializeMotors();

//        vSliderSystem.goToPosition(700);
//        long start = System.currentTimeMillis();
//        while(System.currentTimeMillis()-start<1000){
//
//        }
//        vSliderSystem.transferPos();
//        start = System.currentTimeMillis();
//        while(System.currentTimeMillis()-start<2000){
//
//        }
//        vSliderSystem.goToPosition(30);
        //====================================================


        waitForStart();
        chassis.startOdomThread();

        while (opModeIsActive() && !isStopRequested()) {
            telemetry.addData("ang", chassis.getAngle());
            totalTime = System.currentTimeMillis();
//            hSlideManualControl();
//            handleServoControl();
            updateTelemetry();
            chassis.telemetryDrive();
            vSliderCtrl();
            hSliderCtrl();
            hSlideMovement();

            if(gamepad1.start){
                toggleBoot();
            }

            sleep(50);
        }
        chassis.stopAllThreads();
    }


    private void vSliderCtrl() {
        if (gamepad2.dpad_up){
            prepPickup();
        }
        if (gamepad2.dpad_left){
            pickup();
            prepDropHighRung();
        }
        if (gamepad2.dpad_right){
            dropHighRung();
            prepPickup();
        }
        if(gamepad2.left_trigger>0.3){
            toggleClaw();
        }
        final int TRANSFER_POS = 600;
        final int TOLERANCE = 5;
        if(gamepad2.a){
            if (Math.abs(vSliderSystem.getCurrentPos() - TRANSFER_POS) > TOLERANCE) {
                vSliderSystem.goToPos(600);
                sleep(100);
            }
            else{
                transfer();
            }
        }
        if(gamepad2.x){
            dropHighBasket();
            prepVTransfer();
        }
        if(gamepad2.b){
            prepDropHighBasket();
        }

        //Manual
        if (gamepad2.left_stick_y>0.4) {
            vSliderSystem.goToPos(vSliderSystem.getCurrentPos() + 80);
        } else if (gamepad2.left_stick_y<-0.4) {
            vSliderSystem.goToPos(vSliderSystem.getCurrentPos() - 80);
        }
    }

    public void hSliderCtrl() {
        // Manual
//        if (gamepad2.right_stick_y<-0.4) {
//            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() - 80);
//        } else if (gamepad2.right_stick_y>0.4) {
//            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() + 80);
//        }
        if (gamepad1.dpad_up) {
            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() + 80);
        } else if (gamepad1.dpad_down) {
            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() - 80);
        }

        if (gamepad1.a){
            intake(550);
        }


        if (gamepad2.right_trigger > 0.3) {
            hSliderSystem.intake();
        } else if (gamepad1.right_bumper) {
            hSliderSystem.outtake();
        } else {
            hSliderSystem.idleIntake();
        }

        if(gamepad1.right_trigger>0.3){
            goToOuttake();
        }
        if(gamepad1.right_bumper){
            goToIntake();
        }
        if (gamepad1.left_bumper){
            toggleIntakeTilt();
        }
    }



    //X - HSlide OUT
    //Y - HSlide IN
    public void hSlideMovement() {
//        if (gamepad1.dpad_right) {
//            hSliderSystem.goToPos(550);
//            //hSliderSystem.goToPos(1500);
//        }
//
//        if (gamepad1.dpad_left) {
////                hSliderSystem.rampUp();
//
//            hSliderSystem.goToPos(0);
//
//        }
        if (gamepad1.left_trigger>0.3) {
            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() - 80);
        } else if (gamepad1.left_bumper) {
            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() + 80);
        }
    }


    private void updateTelemetry () {
        telemetry.addData("HSlide Position", hSliderSystem.getCurrentPos());
        telemetry.addData("VSlide Position", vSliderSystem.getCurrentPos());
        telemetry.addData("tiltPos", vSliderSystem.getTiltPos());
//
//        telemetry.addData("Front Left Power", fl.getPower());
//        telemetry.addData("Front Right Power", fr.getPower());
//        telemetry.addData("Back Left Power", bl.getPower());
//        telemetry.addData("Back Right Power", br.getPower());

        telemetry.addData("Position", chassis.getGlobalPos());
        telemetry.update();
    }

//    private void p2pMacro(){
//        if(gamepad1.b){
//            chassis.p2pDrive(0.73, -0.72, 135, 5000, 0.2, 0.7, 0.2, 0.6, 0.06, 4, 0.86, 2.38, 1.254, 1.506, 0.008, 0.02);
//        }
//        if(gamepad1.back){
//            //break
//        }
//    }

    void gamepad1Ctrl(){
        // transfer(ASSUMING ALREADY INTAKED)
        if (gamepad2.y){
            transfer();
            sleep(250);
            vSliderSystem.goToPos(2800);
            sleep(250);
            vSliderSystem.tiltToPos(0.8);
            sleep(150);
        }
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
        vSliderSystem.tiltToPos(0.7);
        sleep(100);
    }

    public void prepVTransfer() {
        vSliderSystem.tiltToTransfer();
        sleep(200);
        vSliderSystem.goToPos(600);
        vSliderSystem.transferClaw();
    }

    public void transfer(){
        hSliderSystem.bootUp();
        hSliderSystem.intake();
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
        vSliderSystem.goToPos(390);

        sleep(300);
        vSliderSystem.closeClaw();
        sleep(250);
    }

    public void intake(int midPos){
        hSliderSystem.goToPos(midPos, 1);
        sleep(700);
        hSliderSystem.intake();
        hSliderSystem.goToPos(1500, 0.2);
        sleep(500);
    }

    public void prepDropHighRung() {
        vSliderSystem.tiltToPos(0.95);
        vSliderSystem.goToPos(1400);
    }

    public void dropHighRung() {
        hSliderSystem.tiltTransfer();
        vSliderSystem.goToPos(1680);
        sleep(600);
        vSliderSystem.openClaw();
    }

    public void prepPickup() {
        vSliderSystem.goToPos(200);
        vSliderSystem.tiltToPos(0.33);
        vSliderSystem.transferClaw();
    }

    public void pickup() {
        vSliderSystem.closeClaw();
        sleep(250);
        vSliderSystem.tiltToPos(0.95);
        vSliderSystem.goToPos(1400);
    }

    private void toggleClaw () {
        if (!clawOpen) {
            vSliderSystem.openClaw();
        } else {
            vSliderSystem.closeClaw();
        }
        clawOpen = !clawOpen;

    }
    private void toggleIntakeTilt() {
        if (!intakeUp) {
            hSliderSystem.tiltIntake();
        } else {
            hSliderSystem.tiltTransfer();
        }
        intakeUp = !intakeUp;
        sleep(50);

    }
    private void goToOuttake(){
        hSliderSystem.goToPos(600, 1);
        sleep(400);
        hSliderSystem.tiltIntake();
    }

    private void goToIntake(){
        //not intaking just going back
        hSliderSystem.tiltTransfer();
        sleep(400);
        hSliderSystem.goToPos(0, 1);
    }

    private void toggleBoot () {
        if (!bootUp) {
            hSliderSystem.bootUp();
        } else {
            hSliderSystem.bootDown();
        }
        bootUp = !bootUp;

    }

}