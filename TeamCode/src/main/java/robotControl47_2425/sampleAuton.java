package robotControl47_2425;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.HSlide;
import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class sampleAuton extends LinearOpMode {
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    private DcMotor slideH;
    private Chassis chassis;
    private Odometry odometry;
    private HSlide sliderH;

    private VSlideController VSlide;
    private HSlideController HSlide;

    @Override
    public void runOpMode() {
        // Initialize hardware devices and controllers here
        slideH = hardwareMap.get(DcMotor.class, "hSlide"); //only 1 motor for both Hsliders physically

        // Initialize sliders
        sliderH = new HSlide(slideH);

        //Inistialize the 2 controllers
        HSlide = new HSlideController(sliderH);
        VSlide = new VSlideController(this.hardwareMap, this);

        //Initialize chasis and Odometry
        odometry = new Odometry(this);
        chassis = new Chassis(this, odometry);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run autonomous-specific code here
        HSlide.start();
        VSlide.start();
        while (opModeIsActive() && !isStopRequested()) {
            //This is moving in terms of centimeters
            chassis.toPoint(100, 100, );
            sleep(5000);
            chassis.moveToPosition(0, 30, 230);

            telemetry.update();
        }
        HSlide.stop();
        VSlide.stop();
    }
}