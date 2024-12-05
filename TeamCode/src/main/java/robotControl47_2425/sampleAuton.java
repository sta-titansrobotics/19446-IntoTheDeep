package robotControl47_2425;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Autonomous
public class sampleAuton extends LinearOpMode {
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    //private DcMotor hSlide;
    private Chassis chassis;
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
        chassis = new Chassis(this);

        while (opModeIsActive() && !isStopRequested()) {
            //This is moving in terms of centimeters

            chassis.p2pDrive(0.2, 0, 0, 0.4, 2, 2, 0.1, 0.1, 0.2, 0);


            updateTelemetry();
            //sleep(10);
        }
        chassis.stopAllThreads();

    }//sjfdjsa
    private void updateTelemetry() {
//
//        telemetry.addData("Front Left Power", fl.getPower());
//        telemetry.addData("Front Right Power", fr.getPower());
//        telemetry.addData("Back Left Power", bl.getPower());
//        telemetry.addData("Back Right Power", br.getPower());

        telemetry.addData("Position", chassis.getGlobalPos());
        telemetry.update();
    }
}