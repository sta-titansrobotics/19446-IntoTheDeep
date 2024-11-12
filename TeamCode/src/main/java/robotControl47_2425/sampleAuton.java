package robotControl47_2425;
import robotControl47_2425.Sliders.*;


    import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
    import com.qualcomm.robotcore.hardware.DcMotor;

    import robotControl47_2425.Sliders.HSlide;
    import robotControl47_2425.Sliders.HSlideController;
    import robotControl47_2425.Sliders.VSlide;
    import robotControl47_2425.Sliders.VSlideController;

    @Autonomous
public class sampleAuton extends LinearOpMode {
        // initialize chassis, current robot pos, odometry, sliders, etc.
      private  RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    private DcMotor slideH, slideV;
    private VSlide sliderV;
    private HSlide sliderH;

    private VSlideController verticalslideController;
    private HSlideController horizontalSlideController;

        @Override
        public void runOpMode() {
            // Initialize hardware devices and controllers here
            slideV = hardwareMap.get(DcMotor.class, "vSlide");
            slideH = hardwareMap.get(DcMotor.class, "hSlide");

            // Initialize sliders and their controllers
            sliderH = new HSlide(slideH);
            horizontalSlideController = new HSlideController(sliderH);

            sliderV = new VSlide(slideV);
            verticalslideController = new VSlideController(sliderV);

            // Wait for the game to start (driver presses PLAY)
            waitForStart();

            // Run autonomous-specific code here

        }

    }



