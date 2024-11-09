package Odometry;

public class PIDController {
    private double kP, kI, kD;
    private double turnKp, turnKi, turnKd;
    private double prevError, integral;
    private double setpoint;
    private double maxSpeed;
    private double turnMaxSpeed;

    private boolean isTurning;

    public PIDController(double kP, double kI, double kD, double maxSpeed,
                         double turnKp, double turnKi, double turnKd, double turnMaxSpeed) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.maxSpeed = maxSpeed;

        this.turnKp = turnKp;
        this.turnKi = turnKi;
        this.turnKd = turnKd;
        this.turnMaxSpeed = turnMaxSpeed;

        this.isTurning = false; // Default to non-turning mode
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

        double output;

        if (isTurning) {
            // Use turning PID constants and maximum speed
            output = (turnKp * error) + (turnKi * integral) + (turnKd * derivative);
            output = Math.min(turnMaxSpeed, Math.abs(output)) * Math.signum(output);
        } else {
            // Use non-turning PID constants and maximum speed
            output = (kP * error) + (kI * integral) + (kD * derivative);
            output = Math.min(maxSpeed, Math.abs(output)) * Math.signum(output);
        }

        return output;
    }

    // Methods to dynamically switch between turning and non-turning
    public void setTurning(boolean isTurning) {
        this.isTurning = isTurning;
        // Reset integral and previous error to avoid carryover between modes
        this.integral = 0;
        this.prevError = 0;
    }

    // Methods to update max speed if needed
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setTurnMaxSpeed(double turnMaxSpeed) {
        this.turnMaxSpeed = turnMaxSpeed;
    }
}
