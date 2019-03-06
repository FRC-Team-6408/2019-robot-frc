package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
//import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;

public class Robot extends TimedRobot {
    private SpeedControllerGroup left = new SpeedControllerGroup(new Spark(0), new Spark(1));
    private SpeedControllerGroup right = new SpeedControllerGroup(new Spark(2), new Spark(3));
    private final DifferentialDrive m_robotDrive = new DifferentialDrive(left, right);
    private final Joystick m_stick = new Joystick(0);  // Make sure controller is number 0;
    private final Timer m_timer = new Timer();

    public BuiltInAccelerometer bia = new BuiltInAccelerometer();

    private Spark plugMotor = new Spark(4);
    private Spark clawL = new Spark(5);
    private Spark clawR = new Spark(6);

    private boolean x_toggle = false;
    private double speedMod = 0.9; 

    //public Servo actuator = new Servo(7); 

    public UsbCamera usbCam1;
    public UsbCamera usbCam2;

    public int g_ticksElapsed = 0;

    public double rampSpeed = 0.25;
    public double clawSpeed = 0.5;

    @Override
    public void robotInit() {
        //Set up the two cameras.
    	usbCam1 = CameraServer.getInstance().startAutomaticCapture("front-cam", 0);
        usbCam2 = CameraServer.getInstance().startAutomaticCapture("back-cam", 1);

        SmartDashboard.putNumber("Random Number", 123);
        SmartDashboard.putString("SayHello", "Hello");
    }
    
    @Override
    public void autonomousInit() {
        //m_timer.reset();
        //m_timer.start();
    }

    @Override
    public void autonomousPeriodic() {
        teleopPeriodic();  // This means no auto.
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
        m_robotDrive.tankDrive(-m_stick.getY() * speedMod, -m_stick.getRawAxis(5) * speedMod);
        
        // Claw.
        if (m_stick.getRawButton(2)) {
            System.out.println("CLOSE - CLAW");
            clawL.setSpeed(-clawSpeed);
            clawR.setSpeed(clawSpeed);

        } else if (m_stick.getRawButton(1)) {
            System.out.println("OPEN - CLAW");
            clawL.setSpeed(clawSpeed);
            clawR.setSpeed(-clawSpeed);

        } else {
            clawL.setSpeed(0);
            clawR.setSpeed(0);
        }

        // Let ramp go down.
        if (m_stick.getRawButton(5) && m_stick.getRawButton(6)) {
            System.out.println("Dropping Ramp");
            plugMotor.setSpeed(-rampSpeed);

            m_stick.setRumble(RumbleType.kLeftRumble, 1.0);
            m_stick.setRumble(RumbleType.kRightRumble, 1.0);

        } else {
            plugMotor.setSpeed(0);

            m_stick.setRumble(RumbleType.kLeftRumble, 0);
            m_stick.setRumble(RumbleType.kRightRumble, 0);
        }
        
        // Toggles the speed.
        if ((m_stick.getRawButton(3)==true) && (x_toggle==true)) {
            if(x_toggle) {
                if (speedMod == 0.5){
                    speedMod = 0.9;
                } else {
                    speedMod = 0.5;
                }
            }
            x_toggle = false;
        } else if ((m_stick.getRawButton(3)==false) && (x_toggle==false)) {
            x_toggle = true;
        }
        
    }

    @Override
    public void testPeriodic() { 
        g_ticksElapsed++;
        SmartDashboard.putNumber("Ticks Elapsed--Test", g_ticksElapsed);

        SmartDashboard.putNumber("Accel : X", bia.getX());
        SmartDashboard.putNumber("Accel : Y", bia.getY());
        SmartDashboard.putNumber("Accel : Z", bia.getZ());
    }
}
