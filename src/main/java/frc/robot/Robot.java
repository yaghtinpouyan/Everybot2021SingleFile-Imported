package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * This program is configured to control a 2020 Everybot robot.
 * This version would use a pneumatic cylinder for raising and lowering the intake
 * WPILib is setup to automatically call these methods when the specific 
 * mode is enabled, don't change their names.
 */
public class Robot extends TimedRobot {
    // Controller
    XboxController controller = new XboxController(0);

    /* Drive Train - Create 2 motor controllers, one for each side
     * and a DifferentialDrive which lets us easily control the drive train
     */
    VictorSP leftMotor = new VictorSP(0);
    VictorSP rightMotor = new VictorSP(1);
    DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor);

    // Intake - Create a motor controller and a pneumatic solenoid
    VictorSP intakeMotor = new VictorSP(3);
    Solenoid intakePneumatic = new Solenoid(PneumaticsModuleType.CTREPCM, 0);

    // Climb motor
    VictorSP climbMotor = new VictorSP(4);

    // Climb limit switches
    DigitalInput climbInput = new DigitalInput(0);   // bottom sensor
    DigitalInput climbInput2 = new DigitalInput(1);  // top sensor

    @Override
    public void robotInit() {
        // Give names to shuffleboard/livewindow outputs
        SendableRegistry.add(drive, "drive");
        SendableRegistry.add(intakeMotor, "intakeMotor");
        SendableRegistry.add(intakePneumatic, "intakePneumatic");
        SendableRegistry.add(climbMotor, "climb");
    }

    @Override
    public void teleopPeriodic() {
        // Drive control: arcade drive
        drive.arcadeDrive(-1 * controller.getLeftY(), controller.getRightX());

        // Intake pneumatic control
        if (controller.getLeftBumper()) {
            intakePneumatic.set(true);
        } else {
            intakePneumatic.set(false);
        }

        // Intake motor control
        if (controller.getRightBumper()) {
            intakeMotor.set(1.0);
        } else if (controller.getRightTriggerAxis() > 0.5) {
            intakeMotor.set(-1.0);
        } else {
            intakeMotor.stopMotor();
        }

        // ---------------- Climb logic ----------------
        // Read sensors
        boolean bottomLimit = climbInput.get();   // true if bottom sensor is triggered
        boolean topLimit = climbInput2.get();     // true if top sensor is triggered

        if (controller.getAButton()) {
            // Automatic climb while holding A button
            // Motor moves up unless top limit is reached
            if (!topLimit) {
                climbMotor.set(1);  // climb up
            }
            // Motor moves down unless bottom limit is reached
            else if (!bottomLimit) {
                climbMotor.set(-1); // climb down
            }
            // Stop motor if both limits are triggered
            else {
                climbMotor.set(0);
            }
        } else {
            // Manual climb control
            // X button: manual climb up
            if (controller.getXButton()) {
                    climbMotor.set(1);
            }
            // Y button: manual climb down
            else if (controller.getYButton()) {
                    climbMotor.set(-1);
            } 
            // No climb buttons pressed
            else {
                climbMotor.set(0);
            }
        }
        // ------------------------------------------------
    }
}