package robotControl47_2425;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous
public class sampleAuton extends LinearOpMode {
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    //private DcMotor hSlide;
    private Chassis chassis;
    ElapsedTime timer = new ElapsedTime();
    private Odometry odometry;
    //private HSlide sliderH;

    //private VSlideController VSlide;
    //private HSlideController HSlide;

    @Override
    public void runOpMode() {
//
//        //Inistialize the 2 controllers
//        HSlide = new HSlideController(sliderH);
//        VSlide = new VSlideController(this.hardwareMap, this);

        //Initialize chasis and Odometry
        //odometry = new Odometry(this);


        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run autonomous-specific code here
//        HSlide.start();
//        VSlide.start();
//        chassis = new Chassis(this, 0.36 / 2, -0.315 / 2);
        chassis = new Chassis(this, 0.36 / 2, -0.315 / 2);
        chassis.startOdomThread();
        chassis.p2pDrive(0, 0, 0, 0.7, 3, 2, 1, 0, 0.3);
        timeout(3000, chassis);
        chassis.p2pDrive(0.5, -0.5, 180, 0.7, 3, 2, 1, 0, 0.3);
        timeout(3000, chassis);



//
//        chassis.p2pDrive(0.85, -0.315/2, 0, 0.7, 2, 2, 0.1, 0.1, 0.2);
//        timeout(3000, chassis);
//        chassis.p2pDrive(0.75, -0.75, 90, 0.7, 2, 2, 0.3, 0.3, 0.2);
//        timeout(3000, chassis);
//        // h slide intakes and outtakes
//
//        for(int i = 0; i < 3; i++){
//            chassis.p2pDrive(0.22, -0.9, 0, 0.7, 3, 2, 0.3, 0.3, 0.3);
//            timeout(5000, chassis);
//
//            chassis.p2pDrive(0.85, -0.315, 0, 0.7, 3, 2, 0.3, 0.3, 0.3);
//            timeout(5000, chassis);
//        }
//
//        chassis.p2pDrive(0.25, -0.9, 0, 0.7, 2, 2, 0.3, 0.3, 0.3);
//        timeout(5000, chassis);

//        chassis.p2pDrive(0.5, -0.4, -90, 0.6, 2, 2, 0.1, 0.1, 0.3);
//        timeout(1200, chassis);


        chassis.stopAllThreads();

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
        while (opModeIsActive() && timer.milliseconds() < ms){
            // && chassis.isBusy && timer.milliseconds() < ms
            telemetry.addData("w", timer.milliseconds());
            telemetry.addData("isBusy", chassis.getBusyState());
            telemetry.update();
        }
    }
}