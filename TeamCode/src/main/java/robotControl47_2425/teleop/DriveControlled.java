package robotControl47_2425.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "46TeleOp", group = "Test")
public class DriveControlled extends LinearOpMode {

    private HSlideController hSliderSystem;
    private VSlideController vSliderSystem;
    private boolean hSlideExtended = false;
    private boolean vSlideExtended = false;
    private boolean previousAState = false;
    private boolean previousBState = false;
    private boolean previousXState = false;
    private boolean previousYState = false;
    private boolean previousDpadUpState = false;
    private boolean previousDpadDownState = false;
    private boolean previousDpadLeftState = false;
    private boolean previousDpadRightState = false;
    private boolean clawOpen = false;
    private boolean armTiltedUp = false;
    private boolean clawRolledUp = false;
    private DcMotor fl, fr, bl, br;

    private long totalTime = 0;
    private int position_buttonA =0;
    private boolean isHighBasket = false;



    @Override
    public void runOpMode() {

        hSliderSystem = new HSlideController(hardwareMap, this);
        vSliderSystem = new VSlideController(hardwareMap, this);

        hSliderSystem.initialize();
        vSliderSystem.initializeMotors();

        //----------------Yea, don't touch-----------------
        InitializeMotors();
        vSliderSystem.goToPosition(700);
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis()-start<2000){

        }
        vSliderSystem.transferPos();
        start = System.currentTimeMillis();
        while(System.currentTimeMillis()-start<2000){

        }
        vSliderSystem.goToPosition(30);
        //====================================================


        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            totalTime = System.currentTimeMillis();
//            hSlideManualControl();
//            handleServoControl();
            updateTelemetry();
            telemetryDrive();
//            vSliderCtrl();
//            armSyncCtrl();
            gamepad1Ctrl();
            gamepad2Ctrl();
        }
    }

    private void armSyncCtrl(){
        // bring vslider into transfer pos
        if (gamepad2.a){
            vSliderSystem.transferPos();
        }
    }

    private void hSlideManualControl() {
        // Control HSlide with gamepad1.a
        if (gamepad1.a && !previousAState) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() + 15);
        }
        previousAState = gamepad1.a;

        // Control VSlide with gamepad1.b
        if (gamepad1.b && !previousBState) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() - 15);
        }
        previousBState = gamepad1.b;
    }

    private void handleServoControl() {
        // Toggle claw open/close with gamepad1.x
        if (gamepad1.x && !previousXState) {
            toggleClaw();
        }
        previousXState = gamepad1.x;

        // Toggle arm tilt up/down with gamepad1.y or dpad_up/down
        if ((gamepad1.y && !previousYState) || (gamepad1.dpad_up && !previousDpadUpState) || (gamepad1.dpad_down && !previousDpadDownState)) {
            toggleArmTilt();
        }
        previousYState = gamepad1.y;
        previousDpadUpState = gamepad1.dpad_up;
        previousDpadDownState = gamepad1.dpad_down;

        // Toggle claw roll up/down with gamepad1.dpad_left/right
        if ((gamepad1.dpad_left && !previousDpadLeftState) || (gamepad1.dpad_right && !previousDpadRightState)) {
            toggleClawRoll();
        }
        previousDpadLeftState = gamepad1.dpad_left;
        previousDpadRightState = gamepad1.dpad_right;
    }

    private void toggleHSlide() {
        if (!hSlideExtended) {
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos());
        } else {
            hSliderSystem.goToPosition(0);
        }
        hSlideExtended = !hSlideExtended;
    }

    private void vSliderCtrl() {
        if (gamepad1.right_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(10);
        } else if (gamepad1.left_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(-10);
        }
    }

    private void toggleVSlide() {
        if (!vSlideExtended) {
            vSliderSystem.goToPosition(vSliderSystem.getMaxPosition());
        } else {
            vSliderSystem.goToPosition(0);
        }
        vSlideExtended = !vSlideExtended;
    }

    private void toggleClaw() {
        if (!clawOpen) {
            vSliderSystem.openClaw();
        } else {
            vSliderSystem.closeClaw();
        }
        clawOpen = !clawOpen;
    }

    private void toggleArmTilt() {
        if (!armTiltedUp) {
            vSliderSystem.tiltArmUp();
        } else {
            vSliderSystem.tilt1ArmZero();
        }
        armTiltedUp = !armTiltedUp;
    }

    private void toggleClawRoll() {
        if (!clawRolledUp) {
            vSliderSystem.rollClawUp();
        } else {
            vSliderSystem.rollClawDown();
        }
        clawRolledUp = !clawRolledUp;
    }

    private void updateTelemetry() {
        telemetry.addData("HSlide Position", hSliderSystem.getCurrentPos());
        telemetry.addData("HSlide Extended", hSlideExtended);
        telemetry.addData("VSlide Position", vSliderSystem.getCurrentVPos());
        telemetry.addData("VSlide Extended", vSlideExtended);

        telemetry.addData("Front Left Power", fl.getPower());
        telemetry.addData("Front Right Power", fr.getPower());
        telemetry.addData("Back Left Power", bl.getPower());
        telemetry.addData("Back Right Power", br.getPower());



        telemetry.update();
    }

    public void InitializeMotors() {
        fl = hardwareMap.get(DcMotor.class, "lf");
        fr = hardwareMap.get(DcMotor.class, "rf");
        bl = hardwareMap.get(DcMotor.class, "lr");
        br = hardwareMap.get(DcMotor.class, "rr");

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void telemetryDrive() {
        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;


        double frontLeftPower = (y + x + rx);
        double backLeftPower = (y - x + rx);
        double frontRightPower = (y - x - rx);
        double backRightPower = (y + x - rx);

        double denominator = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(backLeftPower), Math.max(Math.abs(frontRightPower), Math.max(Math.abs(backRightPower), 1))));

        fl.setPower(frontLeftPower / denominator);
        bl.setPower(backLeftPower / denominator);
        fr.setPower(frontRightPower / denominator);
        br.setPower(backRightPower / denominator);
    }

    void gamepad1Ctrl(){
        // manual h-slider
        if (gamepad1.right_trigger > 0.2){
            hSliderSystem.rampUp();
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() + 50);
        }
        else if (gamepad1.left_trigger > 0.2){
            hSliderSystem.rampUp();
            hSliderSystem.goToPosition(hSliderSystem.getCurrentPos() - 50);
        }
        else if(hSliderSystem.getCurrentPos()>300){
            hSliderSystem.rampDown();
        }
        // intake
        if(gamepad1.right_bumper){
            hSliderSystem.intaking();
        }
        else if(gamepad1.left_bumper){
            hSliderSystem.outtaking();
        }
        else{
            hSliderSystem.setIntakePower(0);
        }

        // automatic
        if (gamepad1.a){
            vSliderSystem.goToPosition(500);
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 1000){

            }
            vSliderSystem.transferPos();
            hSliderSystem.rampUp();
            hSliderSystem.goToPosition(1500);
            start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 600){

            }
            hSliderSystem.rampDown();
        }

        if (gamepad1.b){
            // transfer
            vSliderSystem.goToPosition(500);
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 300){

            }
            vSliderSystem.transferPos();
            start = System.currentTimeMillis();
            hSliderSystem.rampUp();
            while (System.currentTimeMillis() - start < 300){

            }
            hSliderSystem.goToPosition(0);
            while (hSliderSystem.getCurrentPos()>0){

            }
            vSliderSystem.goToPosition(30);

        }
    }

    void gamepad2Ctrl() {

        if (gamepad2.right_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(10);
        } else if (gamepad2.left_trigger > 0.2) {
            vSliderSystem.vSlideManualEg(-10);
        }

        if (gamepad2.left_stick_y != 0) {
            vSliderSystem.tiltArmManualControl(-gamepad2.left_stick_y * 0.01);
        }

        if (gamepad2.right_stick_y != 0) {
            vSliderSystem.tilt2.setPosition(vSliderSystem.tilt2.getPosition() - gamepad2.right_stick_y * 0.01);
        }

        if (gamepad2.left_stick_x != 0) {
            vSliderSystem.RollManualControl(gamepad2.left_stick_x * 0.01);
        }

        if (gamepad2.x) {
            vSliderSystem.closeClaw();
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 300) {
            }
            vSliderSystem.VSlideHighBasket();
            isHighBasket = true;

        } else if (gamepad2.y) {
            vSliderSystem.closeClaw();
            vSliderSystem.VSlideHighRung();
            isHighBasket = false;
        } else if (gamepad2.a) {

            if(!isHighBasket) {
                vSliderSystem.vSlideDrop();
                vSliderSystem.transferPos();
                long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < 1000) {
                }
                vSliderSystem.goToPosition(0);
            }else{
                vSliderSystem.openClaw();
            }
        } else if (gamepad2.b) {
            vSliderSystem.transferPos();
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 1000) {
            }
            vSliderSystem.goToPosition(40);

        }

        if(gamepad2.dpad_down){
            //placeholder
        }
    }
    public void updateTime(){
        totalTime = System.currentTimeMillis();
    }
}