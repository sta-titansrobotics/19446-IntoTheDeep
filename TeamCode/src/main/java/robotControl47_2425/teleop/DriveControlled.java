package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Chassis;
import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "46TeleOp", group = "Test")
public class DriveControlled extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    private Chassis chassis;
    private HSlideController hSliderSystem = null;
    private VSlideController vSliderSystem = null;
    //    private boolean hSlideExtended = false;
//    private boolean vSlideExtended = false;
//    private boolean previousAState = false;
//    private boolean previousBState = false;
//    private boolean previousXState = false;
//    private boolean previousYState = false;
//    private boolean previousDpadUpState = false;
//    private boolean previousDpadDownState = false;
//    private boolean previousDpadLeftState = false;
//    private boolean previousDpadRightState = false;
    private boolean clawOpen = false;
    private boolean rampUp = true;

    //    private boolean armTiltedUp = false;
//    private boolean clawRolledUp = false;
    private long totalTime = 0;
    private int position_buttonA = 0;
    private boolean isHighBasket = false;

    @Override
    public void runOpMode() throws InterruptedException {

        hSliderSystem = new HSlideController(this);
        vSliderSystem = new VSlideController(this);


        chassis = new Chassis(this, 0.36 / 2, -0.36 / 2);

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
            //chassis.telemetryStrafe();
            vSliderCtrl();
            hSliderCtrl();
            prepDropHighRung();
            dropHighRung();
            prepPickup();
            hSlideMovement();


            if (gamepad1.a) {
                toggleClaw();
            }
            sleep(50);
        }
        chassis.stopAllThreads();
    }

    private void vSliderCtrl() {
//        if (gamepad2.left_trigger > 0.3) {
//            vSliderSystem.stepCtrl(-100);
//        } else if (gamepad2.right_trigger>0.3) {
//            vSliderSystem.stepCtrl(100);
//        }


        if(gamepad2.dpad_down){
            //grabs from transfer pos
            hSliderSystem.goToPos(78);
            sleep(600);
            vSliderSystem.openSlightClaw();
            vSliderSystem.goToPos(350);
            sleep(600);
            vSliderSystem.tiltToPos(1);
            sleep(800);
            vSliderSystem.goToPos(0);
            sleep(600);
            vSliderSystem.closeClaw();
        }
        telemetry.addData("tiltPos", vSliderSystem.getTiltPos());

        //smaller the tilt value, the higher the servo, where tiltpotision of 1 is straight downwards.
        if(gamepad2.dpad_up){
            vSliderSystem.goToPos(2900);
            sleep(100);
            vSliderSystem.tiltToPos(0.65);
        }

        if(gamepad2.b){
            toggleRamp();
            sleep(75);
        }

        if(gamepad2.dpad_left){
            vSliderSystem.openClaw();
        }

        if(gamepad2.dpad_right){
            vSliderSystem.closeClaw();
        }

        //double y = gamepad2.left_stick_y; // reversed NOT same as auto
        if (gamepad2.left_stick_y>0.4) {
            vSliderSystem.goToPos(vSliderSystem.getCurrentPos() + 50);
        } else if (gamepad2.left_stick_y<-0.4) {
            vSliderSystem.goToPos(vSliderSystem.getCurrentPos() - 50);
        }


//        if (gamepad1.dpad_down) {
//            vSliderSystem.tiltStepCtrl(-0.05);
//        } else if (gamepad1.dpad_up) {
//            vSliderSystem.tiltStepCtrl(0.05);
//        }

    }

    public void hSliderCtrl() {
        if (gamepad1.dpad_down) {
            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() - 20);
        } else if (gamepad1.dpad_up) {
            hSliderSystem.goToPos(hSliderSystem.getCurrentPos() + 20);
        }

        if (gamepad1.right_trigger > 0.2) {
            hSliderSystem.outtaking();
        } else if (gamepad1.right_bumper) {
            hSliderSystem.intaking();
        } else {
            hSliderSystem.intakeOff();
        }
    }

        private void toggleClaw () {
            if (!clawOpen) {
                vSliderSystem.openClaw();
            } else {
                vSliderSystem.closeClaw();
            }
            clawOpen = !clawOpen;

        }

        private void toggleRamp () {
            if (!rampUp) {
                hSliderSystem.rampHigh();
            } else {
                hSliderSystem.rampDown();
            }
            rampUp = !rampUp;

        }
        public void prepDropHighRung () {
        if(gamepad2.x) {
            vSliderSystem.closeClaw();
            sleep(400);
            vSliderSystem.tiltToPos(0.15);
            vSliderSystem.goToPos(930);
        }
        }
        public void dropHighRung () {
        if(gamepad2.y) {
            vSliderSystem.tiltToPos(0.5);
            sleep(300);
            vSliderSystem.openClaw();
        }
        }

        //This is the macro for auto pick up from sidways walls. Don't use for tele-Op (maybe)
        public void prepPickup () {
            if (gamepad2.a) {

                vSliderSystem.goToPos(0);
                vSliderSystem.tiltToPos(0.73);
                vSliderSystem.openClaw();
                sleep(50);
            }
        }

        //X - HSlide OUT
        //Y - HSlide IN
        public void hSlideMovement() {
            if (gamepad1.x) {
                hSliderSystem.goToPos(1500);
                hSliderSystem.rampUp();
            }

            if (gamepad1.y) {
                hSliderSystem.rampUp();

                hSliderSystem.goToPos(0);

            }
            if (gamepad2.right_trigger > 0.2) {
                hSliderSystem.goToPos(hSliderSystem.getCurrentPos() - 80);
            } else if (gamepad2.left_trigger > 0.2) {
                hSliderSystem.goToPos(hSliderSystem.getCurrentPos() + 80);
            }
            if (gamepad2.b){
                hSliderSystem.rampUp();
            }
        }

//    private void hSlideManualControl() {
//        // Control HSlide with gamepad1.a
//        if (gamepad1.a && !previousAState) {
//            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() + 15);
//        }
//        previousAState = gamepad1.a;
//
//        // Control VSlide with gamepad1.b
//        if (gamepad1.b && !previousBState) {
//            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() - 15);
//        }
//        previousBState = gamepad1.b;
//    }

//    private void handleServoControl() {
//        // Toggle claw open/close with gamepad1.x
//        if (gamepad1.x && !previousXState) {
////            toggleClaw();
//        }
//        previousXState = gamepad1.x;
//
//        // Toggle arm tilt up/down with gamepad1.y or dpad_up/down
//        if ((gamepad1.y && !previousYState) || (gamepad1.dpad_up && !previousDpadUpState) || (gamepad1.dpad_down && !previousDpadDownState)) {
////            toggleArmTilt();
//        }
//        previousYState = gamepad1.y;
//        previousDpadUpState = gamepad1.dpad_up;
//        previousDpadDownState = gamepad1.dpad_down;
//
//        // Toggle claw roll up/down with gamepad1.dpad_left/right
//        if ((gamepad1.dpad_left && !previousDpadLeftState) || (gamepad1.dpad_right && !previousDpadRightState)) {
////            toggleClawRoll();
//        }
//        previousDpadLeftState = gamepad1.dpad_left;
//        previousDpadRightState = gamepad1.dpad_right;
//    }
//
//    private void toggleHSlide() {
//        if (!hSlideExtended) {
//            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos());
//        } else {
//            hSliderSystem.goToPosition(0);
//        }
//        hSlideExtended = !hSlideExtended;
//    }


//
//    private void toggleVSlide() {
//        if (!vSlideExtended) {
//            vSliderSystem.goToPosition(vSliderSystem.getMaxPosition());
//        } else {
//            vSliderSystem.goToPosition(0);
//        }
//        vSlideExtended = !vSlideExtended;
//    }
//
//
//
//    private void toggleArmTilt() {
//        if (!armTiltedUp) {
//            vSliderSystem.tiltArmUp();
//        } else {
//            vSliderSystem.tilt1ArmZero();
//        }
//        armTiltedUp = !armTiltedUp;
//    }
//
//    private void toggleClawRoll() {
//        if (!clawRolledUp) {
//            vSliderSystem.rollClawUp();
//        } else {
//            vSliderSystem.rollClawDown();
//        }
//        clawRolledUp = !clawRolledUp;
//    }

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


//
//    void gamepad1Ctrl(){
//        // manual h-slider
//        if (gamepad1.right_trigger > 0.2){
//            hSliderSystem.rampUp();
//            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() + 50);
//        }
//        else if (gamepad1.left_trigger > 0.2){
//            hSliderSystem.rampUp();
//            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() - 50);
//        }
//        else if(hSliderSystem.getCurrentPos()>300){
//            hSliderSystem.rampDown();
//        }
//        // intake
//        if(gamepad1.right_bumper){
//            hSliderSystem.intaking();
//        }
//        else if(gamepad1.left_bumper){
//            hSliderSystem.outtaking();
//        }
//        else{
//            hSliderSystem.setIntakePower(0);
//        }
//
//        if(gamepad1.dpad_up){
//            hSliderSystem.rampUp();
//        }
//
//        if(gamepad1.dpad_left){
//            hSliderSystem.rampDown();
//        }
//
//        // automatic
//        if (gamepad1.a){
//            vSliderSystem.goToPosition(500);
//            long start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 1000){
//
//            }
//            vSliderSystem.transferPos();
//            hSliderSystem.rampUp();
//            start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 300){
//
//            }
//            hSliderSystem.goToPosition(1200);
//            start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 600){
//
//            }
//            hSliderSystem.rampDown();
//        }
//
//        if (gamepad1.b){
//            // transfer
//            vSliderSystem.goToPosition(500);
//            long start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 300){
//
//            }
//            vSliderSystem.transferPos();
//            start = System.currentTimeMillis();
//            hSliderSystem.rampUp();
//            while (System.currentTimeMillis() - start < 300){
//
//            }
//            hSliderSystem.goToPosition(0);
//            while (hSliderSystem.getCurrentPos()>0){
//
//            }
//            vSliderSystem.goToPosition(30);
//
//        }
//    }
//
//    void gamepad2Ctrl() {
//
//        if (gamepad2.right_trigger > 0.2) {
//            vSliderSystem.vSlideManualEg(vSliderSystem.getCurrentVPos()+30);
//        } else if (gamepad2.left_trigger > 0.2) {
//            vSliderSystem.vSlideManualEg(vSliderSystem.getCurrentVPos()-30);
//        }
//
//        if (gamepad2.left_stick_y != 0) {
//            vSliderSystem.tiltArmManualControl(-gamepad2.left_stick_y * 0.01);
//        }
//
//        if (gamepad2.right_stick_y != 0) {
//            vSliderSystem.tilt2.setPosition(vSliderSystem.tilt2.getPosition() - gamepad2.right_stick_y * 0.01);
//        }
//
//        if (gamepad2.left_stick_x != 0) {
//            vSliderSystem.RollManualControl(gamepad2.left_stick_x * 0.01);
//        }
//
//        if (gamepad2.x) {
//            vSliderSystem.closeClaw();
//            long start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 300) {
//            }
//            vSliderSystem.VSlideHighBasket();
//            isHighBasket = true;
//
//        } else if (gamepad2.y) {
//            vSliderSystem.closeClaw();
//            long start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 300) {
//            }
//            vSliderSystem.VSlideHighRung();
//            isHighBasket = false;
//        } else if (gamepad2.a) {
//
//            if(!isHighBasket) {//if it is high rung
//                vSliderSystem.vSlideDrop();
//                vSliderSystem.transferPos();
//                long start = System.currentTimeMillis();
//                while (System.currentTimeMillis() - start < 1000) {
//                }
//                vSliderSystem.goToPosition(0);
//            }else{
//                vSliderSystem.openClaw();
//            }
//        } else if (gamepad2.b) {
//            vSliderSystem.openClaw();
//            long start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 300) {
//            }
//            vSliderSystem.transferPos();
//            start = System.currentTimeMillis();
//            while (System.currentTimeMillis() - start < 1000) {
//            }
//            vSliderSystem.goToPosition(30);
//
//        }
//
//        if(gamepad2.dpad_left){
//            vSliderSystem.pickUpFromWall();
//        }
//
//        if(gamepad2.dpad_down){
//            //opexn claw
//            vSliderSystem.closeClaw();
//        }
//
//        if(gamepad2.dpad_up){
//            //close claw
//            vSliderSystem.openClaw();
////        }
////    }
//    public void updateTime(){
//        totalTime = System.currentTimeMillis();
//    }
    }