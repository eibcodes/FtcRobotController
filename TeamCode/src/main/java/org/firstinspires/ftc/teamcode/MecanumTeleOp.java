package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Mecanum Teleop",group="interactive opmode")
public class MecanumTeleOp extends OpMode {
    private MecanumDrive drive;
    // todo: write your code here
    private DcMotor intakemotor;
    private Shooter shooter;

    boolean intakebutton;

    boolean intakeOn = false;
    boolean lastIntakeButton = false;

    @Override
    public void init() {
        drive=new MecanumDrive(hardwareMap);

        intakemotor = hardwareMap.get(DcMotor.class, "intakemotor");
        shooter = new Shooter(hardwareMap);

        intakeOn = false;
        lastIntakeButton = false;
        intakemotor.setPower(0.0); // make sure power is 0


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

        intakebutton = gamepad1.a;
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
        telemetry.addData("Shooter Gate", shooter.isGateOpen() ? "OPEN" : "CLOSED");
        telemetry.update();
    }

    @Override
    public void stop() {
        shooter.stop();
        intakemotor.setPower(0.0);
        drive.stop();
    }
}
