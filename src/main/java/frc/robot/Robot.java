package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;

import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {
    private SpeedControllerGroup left = new SpeedControllerGroup(new Spark(0), new Spark(1));
    private SpeedControllerGroup right = new SpeedControllerGroup(new Spark(2), new Spark(3));
    private final DifferentialDrive m_robotDrive = new DifferentialDrive(left, right);

    private final Joystick m_stick = new Joystick(0);  // Make sure controller is number 0.

    public BuiltInAccelerometer bia = new BuiltInAccelerometer();

    public UsbCamera usbCam1;
    public UsbCamera usbCam2;

    private Spark plugMotor = new Spark(4);
    private Spark clawL = new Spark(5);
    private Spark clawR = new Spark(6);

    public int g_ticksElapsed = 0;

    // Component Speeds // 
    
    public double rampSpeed = 0.25;
    public double idlePressure = 0.08;

    public boolean clawClosed = true;
    public boolean clawButtonToggle = true;
    public double clawSpeed = 0.5;

    private boolean speedToggle = false;
    private double speedMod = 0.6; 

    // Main Functions // 

    @Override
    public void robotInit() {
        // Setup the two cameras.
    	usbCam1 = CameraServer.getInstance().startAutomaticCapture("front-cam", 0);
        usbCam2 = CameraServer.getInstance().startAutomaticCapture("back-cam", 1);
    }
    
    @Override
    public void autonomousInit() { }

    @Override
    public void autonomousPeriodic() {
        teleopPeriodic();  // We are using driver, camera controls for sandstorm.
    }

    @Override
    public void teleopInit() { }

    @Override
    public void teleopPeriodic() {
        m_robotDrive.tankDrive(-m_stick.getY() * speedMod, -m_stick.getRawAxis(5) * speedMod);
        
        // Claw Control --> toggle
        if (m_stick.getRawButton(2)==true && clawButtonToggle==true) {  // Case: Switch the motor direction. (in / out)
            clawClosed = !clawClosed;
            clawButtonToggle = false;

        } else if(m_stick.getRawButton(2)==false) {  // Case: keep pressure on the claw. 
            clawButtonToggle = true;
            if (clawClosed == true) {
                clawL.setSpeed(-idlePressure);
                clawR.setSpeed(idlePressure);
            } else {
                clawL.setSpeed(idlePressure);
                clawR.setSpeed(-idlePressure);
            }
        } 
        
        // Case: do movement of claw.
        if (m_stick.getRawButton(2)==true && clawButtonToggle==false) { 
            if (clawClosed == true) {
                clawL.setSpeed(-clawSpeed);
                clawR.setSpeed(clawSpeed);
            } else {
                clawL.setSpeed(clawSpeed);
                clawR.setSpeed(-clawSpeed);
            }
        }

        // Pull the plug and drop the ramp.
        if (m_stick.getRawButton(5) && m_stick.getRawButton(6)) {
            System.out.println("!!DROPPING RAMP!!");
            plugMotor.setSpeed(-rampSpeed);

            m_stick.setRumble(RumbleType.kLeftRumble, 1.0);
            m_stick.setRumble(RumbleType.kRightRumble, 1.0);

        } else {
            plugMotor.setSpeed(0);

            m_stick.setRumble(RumbleType.kLeftRumble, 0);
            m_stick.setRumble(RumbleType.kRightRumble, 0);
        }
        
        // Toggle for speed.
        if (m_stick.getRawButton(4)==true && speedToggle==true) {
            if(speedToggle) {
                if (speedMod == 0.6) {
                    speedMod = 0.9;
                } else {
                    speedMod = 0.6;
                }
            }
            speedToggle=false;
        } else if (m_stick.getRawButton(4)==false && speedToggle==false) {
            speedToggle=true;
        }
        
    }

    @Override
    public void testPeriodic() { 
        g_ticksElapsed++;
        SmartDashboard.putNumber("Ticks Elapsed--Test", g_ticksElapsed);
    }
}
