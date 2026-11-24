package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Mecanum Teleop",group="interactive opmode")
public class MecanumTeleOp extends OpMode {
    private MecanumDrive drive;
    // todo: write your code here
    private DcMotor intakemotor;
    private Shooter shooter;

    private Servo gate;

    boolean intakebutton;
    boolean gateButton;
    boolean intakeOn = false;
    boolean lastIntakeButton = false;
    boolean gateOpen = false;
    boolean lastGateButton = false;
    @Override
    public void init() {
        drive=new MecanumDrive(hardwareMap);

        intakemotor = hardwareMap.get(DcMotor.class, "intakemotor");
        shooter = new Shooter(hardwareMap);
        gate = hardwareMap.get(Servo.class, "servo");
        gateOpen = false;
        lastGateButton = false;


        intakeOn = false;
        lastIntakeButton = false;
        intakemotor.setPower(0.0); // make sure power is 0
        gate.setPosition(0.2);


        telemetry.addLine("TeleOp ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Motor control
        double forward  = -gamepad1.left_stick_y;
        double strafe   = gamepad1.left_stick_x;
        double rotate   = gamepad1.right_stick_x;
        drive.driveRobot(forward, strafe, rotate);

        gateButton = gamepad1.x;
        intakebutton = gamepad1.a;

        if (gateButton && !lastGateButton) {
            gateOpen = !gateOpen; // this just flips the true/false using the !
        }
        lastGateButton = gateButton;
        gate.setPosition(gateOpen? 0.4 : 0.2);

        if (intakebutton && !lastIntakeButton) {
            intakeOn = !intakeOn; // this just flips the true/false using the !
        }
        lastIntakeButton = intakebutton;
        intakemotor.setPower(intakeOn ? 1.0 : 0.0); // this is shorthand if statement

        shooter.update(gamepad1.b);

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
        telemetry.addData("Shooter 1 target / actual", "%.0f / %.0f ticks/s",
                gamepad1.b ? Shooter.MOTOR_1_TARGET_VELOCITY : 0.0, shooter.getMotor1Velocity());
        telemetry.addData("Shooter 2 target / actual", "%.0f / %.0f ticks/s",
                gamepad1.b ? Shooter.MOTOR_2_TARGET_VELOCITY : 0.0, shooter.getMotor2Velocity());
        telemetry.addData("Shooter Gate", gateOpen ? "OPEN" : "CLOSED");
        telemetry.update();
    }

    @Override
    public void stop() {
        shooter.stop();
        intakemotor.setPower(0.0);
        drive.stop();
    }
}
