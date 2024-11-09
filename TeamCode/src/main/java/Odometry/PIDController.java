package Odometry;

public class PIDController {
    private double kP, kI, kD;
    private double prevError, integral;
    private double setpoint;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.prevError = 0;
        this.integral = 0;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        this.prevError = 0;
        this.integral = 0;
    }

    public double calculate(double current) {
        double error = setpoint - current;
        integral += error;
        double derivative = error - prevError;
        prevError = error;
        return (kP * error) + (kI * integral) + (kD * derivative);
    }
}
