package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Shooter {
    public static final double MOTOR_1_TARGET_VELOCITY = -600.0;
    public static final double MOTOR_2_TARGET_VELOCITY = 600.0;
    public static final double VELOCITY_TOLERANCE = 0.05;
    public static final double GATE_CLOSED_POSITION = 0.2;
    public static final double GATE_OPEN_POSITION = 0.4;

    private final DcMotorEx shooterMotor1;
    private final DcMotorEx shooterMotor2;
    private final Servo gate;
    private boolean gateOpen;

    public Shooter(HardwareMap hardwareMap) {
        shooterMotor1 = hardwareMap.get(DcMotorEx.class, "shootermotor1");
        shooterMotor2 = hardwareMap.get(DcMotorEx.class, "shootermotor2");
        gate = hardwareMap.get(Servo.class, "servo");
        shooterMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        stop();
    }

    public void update(boolean held) {
        if (!held) {
            stop();
            return;
        }

        shooterMotor1.setVelocity(MOTOR_1_TARGET_VELOCITY);
        shooterMotor2.setVelocity(MOTOR_2_TARGET_VELOCITY);
        gateOpen = Math.abs(getMotor1Velocity() - MOTOR_1_TARGET_VELOCITY)
                <= Math.abs(MOTOR_1_TARGET_VELOCITY) * VELOCITY_TOLERANCE
                && Math.abs(getMotor2Velocity() - MOTOR_2_TARGET_VELOCITY)
                <= Math.abs(MOTOR_2_TARGET_VELOCITY) * VELOCITY_TOLERANCE;
        gate.setPosition(gateOpen ? GATE_OPEN_POSITION : GATE_CLOSED_POSITION);
    }

    public void stop() {
        gateOpen = false;
        gate.setPosition(GATE_CLOSED_POSITION);
        shooterMotor1.setVelocity(0.0);
        shooterMotor2.setVelocity(0.0);
        shooterMotor1.setPower(0.0);
        shooterMotor2.setPower(0.0);
    }

    public double getMotor1Velocity() {
        return shooterMotor1.getVelocity();
    }

    public double getMotor2Velocity() {
        return shooterMotor2.getVelocity();
    }

    public boolean isGateOpen() {
        return gateOpen;
    }
}
