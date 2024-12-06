package robotControl47_2425;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class sampleAuton extends LinearOpMode {
    private Chassis chassis;
    ElapsedTime timer = new ElapsedTime();
    private Odometry odometry;
    private VSlideController vSliderSystem;
    private DcMotor slideVL, slideVR;

    @Override
    public void runOpMode() {
        // Initialize motors
        slideVL = hardwareMap.get(DcMotor.class, "slideVL");
        slideVR = hardwareMap.get(DcMotor.class, "slideVR");


        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run autonomous-specific code here
//        HSlide.start();
//        VSlide.start();
//        chassis = new Chassis(this);
        chassis.startOdomThread();
        chassis.p2pDrive(0, 0, 0, 0.7, 3, 2, 1, 0, 0.3);
        timeout(3000, chassis);
        chassis.p2pDrive(0.5, -0.5, 180, 0.7, 3, 2, 1, 0, 0.3);
        timeout(3000, chassis);

        waitForStart();

        // VSlide Thread
        Thread vSlideThread = new Thread(new Runnable() {
            @Override
            public void run() {
                VSlideHighRung();
            }
        });
        vSlideThread.start();

        for (int i = 0; i < 3; i++){
            chassis.p2pDrive(0, -0.4, 0, 0.7, 2, 2, 0.1, 0.1, 0.3);
            timeout(5000, chassis);
            chassis.p2pDrive(0, 0, 0, 0.7, 2, 2, 0.1, 0.1, 0.3);
            timeout(5000, chassis);
        }

        chassis.stopAllThreads();

        // Wait for the VSlideHighRung thread to finish
        try {
            vSlideThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updateTelemetry() {
        telemetry.addData("Position (m): ", chassis.getGlobalPos());
        telemetry.update();
    }

    private void timeout(int ms, Chassis chassis) {
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < ms) {
            telemetry.addData("w", timer.milliseconds());
            telemetry.addData("isBusy", chassis.getBusyState());
            telemetry.update();
        }
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