package robotControl47_2425;

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
    private VSlideController vSliderSystem;
    private DcMotor slideVL, slideVR;

    @Override
    public void runOpMode() {
        // Initialize motors
//        slideVL = hardwareMap.get(DcMotor.class, "lvslide");
//        slideVR = hardwareMap.get(DcMotor.class, "rvslide");

        chassis = new Chassis(this, 0.36 / 2, -0.315 / 2);
        chassis.startOdomThread();
        sleep(2000);
        telemetry.addLine("Ready");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        resetRuntime();


        // Run autonomous-specific code here
//        HSlide.start();
//        VSlide.start();
//        chassis = new Chassis(this, 0.36 / 2, -0.315 / 2);

//        while (opModeIsActive()){
//            if (gamepad1.a){
//                chassis.p2pDrive(0.5, 0.5, 180, 0.9, 1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);
//                timeout(8000, chassis);
////                chassis.p2pDrive(0, 0, 0, 0.9, 1.2, 2, 0.04, 0.05, 0.4);
////                timeout(8000, chassis);
//            }
//            else{
//                telemetry.addData("ang", chassis.getAngle());
//                telemetry.addData("pos", chassis.getGlobalPos());
//                telemetry.update();
//            }
//        }


        chassis.p2pDrive(0.8, -0.13, 0, 3000, 0.2, 1.1, 0.2, 0.5, 0.02, 2, 1.15, 2, 1.15, 2, 0.03, 0.04);
        timeout(3000, chassis);

        chassis.p2pDrive(0.75, -0.75, 100, 0.9, 1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);
        timeout(3000, chassis);
//        // h slide intakes and outtakes
//
        for(int i = 0; i < 3; i++){
            // go into human player
            chassis.p2pDrive(0.22, -0.9, 0, 0.9,
                    1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);
            timeout(5000, chassis);

            // crash into wall, virtual point outside of field, increase dis and angle tolerance
            // tolerance, timeout, min power
            // put kp kd in function, max_speed fast is 1.1, slow is 0.5,

            chassis.p2pDrive(0.85, -0.13, 0, 0.9, 1.15, 2, 2, 2, 0.03, 0.04, 0.4);
            timeout(5000, chassis);
        }
        chassis.p2pDrive(0.25, -0.9, 0, 0.9, 1.15, 1.3, 2, 2, 0.03, 0.04, 0.4);

//
//        chassis.p2pDrive(0.25, -0.9, 0, 0.7, 2, 2, 0.3, 0.3, 0.3);
        timeout(5000, chassis);

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

    private void timeout(int ms, Chassis chassis){
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

    public void VSlideHighRung() {
        slideVL.setTargetPosition(-50); // PLACEHOLDER
        slideVR.setTargetPosition(50); // PLACEHOLDER
        slideVL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideVL.setPower(0.5);
        slideVR.setPower(0.5);
        vSliderSystem.rollClawUp();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 700) {
            // Wait for 700 milliseconds
        }
    }
}