//package robotControl47_2425.auto;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//
//import robotControl47_2425.Sliders.HSlideController;
//import robotControl47_2425.Sliders.VSlideController;
//
//@Autonomous(name = "AutoInit", group = "Test")
//public class AutoInit extends LinearOpMode {
//
//    private HSlideController hSliderSystem;
//    private VSlideController vSliderSystem;
//    private DcMotor fl, fr, bl, br;
//
//    @Override
//    public void runOpMode() {
//        hSliderSystem = new HSlideController(hardwareMap, this);
////        vSliderSystem = new VSlideController(hardwareMap, this);
//
//        hSliderSystem.initialize();
//        vSliderSystem.initializeMotors();
//
//        InitializeMotors();
//
//        // Initialization sequence
//        vSliderSystem.goToPosition(700);
//        long start = System.currentTimeMillis();
//        while (System.currentTimeMillis() - start < 2000) {
//
//        }
////        vSliderSystem.transferPos();
//        start = System.currentTimeMillis();
//        while (System.currentTimeMillis() - start < 2000) {
//
//        }
//        vSliderSystem.goToPosition(20);
//
//        waitForStart();
//
//        // Autonomous actions can be added here
//
//        while (opModeIsActive() && !isStopRequested()) {
//            // Autonomous loop
//        }
//    }
//
//    public void InitializeMotors() {
//        fl = hardwareMap.get(DcMotor.class, "lf");
//        fr = hardwareMap.get(DcMotor.class, "rf");
//        bl = hardwareMap.get(DcMotor.class, "lr");
//        br = hardwareMap.get(DcMotor.class, "rr");
//
//        fl.setDirection(DcMotor.Direction.REVERSE);
//        bl.setDirection(DcMotor.Direction.REVERSE);
//
//        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    }
//}