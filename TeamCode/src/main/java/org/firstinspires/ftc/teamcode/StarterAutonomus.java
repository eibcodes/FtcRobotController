package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

// https://www.youtube.com/watch?v=cGE30PPDXbo

@Autonomous(name="Starter FSM by EB")
public class StarterAutonomus extends OpMode {
    private MecanumDrive drive;

    // All 16 BIOBUZZ AprilTag IDs (36h11 family)
    public static final int[] ALL_BIOBUZZ_TAGS = {
            30, 31, 32, 33, // Red Scoring (opposite audience)
            34, 35, 36, 37, // Red Audience
            38, 39, 40, 41, // Blue Audience
            42, 43, 44, 45  // Blue Scoring (opposite audience)
    };

    // Grouped by Alliance
    public static final int[] RED_ALLIANCE_TAGS  = {30, 31, 32, 33, 34, 35, 36, 37};
    public static final int[] BLUE_ALLIANCE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45};

    // Grouped by HIVE CELL Cluster (4 tags per cell)
    public static final int[] RED_SCORING_CELL  = {30, 31, 32, 33};
    public static final int[] RED_AUDIENCE_CELL = {34, 35, 36, 37};
    public static final int[] BLUE_AUDIENCE_CELL = {38, 39, 40, 41};
    public static final int[] BLUE_SCORING_CELL  = {42, 43, 44, 45};

    private enum State {
        LEAVE, SEARCH, APPROACH, AQUIRE, POSITION, SHOOT, PARK, DONE
    }
    private State state = State.LEAVE;

    private final ElapsedTime stateTimer = new ElapsedTime();
    private final ElapsedTime autoTimer = new ElapsedTime();

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap);

        // todo initialize shooter and intake motors here as well

        telemetry.addLine("Autonomous ready!");
        telemetry.update();
    }

    @Override
    public void start() {
        state = State.LEAVE;
        stateTimer.reset();
        autoTimer.reset();
    }

    @Override
    public void loop() {
        if (autoTimer.seconds() >= 29.0) {
            changeState(State.DONE);
        }

        // give yourself 5 seconds to park?
        if (autoTimer.seconds() >= 25.0) {
            changeState(State.PARK);
        }

        switch (state) {
            case LEAVE:
//                drive.driveRobot(.5, 0, 0);
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.SEARCH);
                break;
            case SEARCH:
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.APPROACH);
                break;
            case APPROACH:
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.AQUIRE);
                break;
            case AQUIRE:
                if (stateTimer.seconds() >= 2.0)
                    // have x # of balls?
                    changeState(State.POSITION);
                break;
            case POSITION:
//                drive.driveRobot(0, 0.3, 0);
                if (stateTimer.seconds() >= 1.0)
                    changeState(State.SHOOT);
                break;
            case SHOOT:
                drive.stop();
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.SEARCH);
                break;
            case PARK:
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.PARK);
                break;
            case DONE:
                drive.stop();
                break;
        }

        telemetry.addData("State", state);
        telemetry.addData("State time", stateTimer.seconds());
        telemetry.addData("Auto time", autoTimer.seconds());
        telemetry.update();
    }

    private void changeState(State next) {
        if (state == next) return; // dont reset timer every loop
        drive.stop();
        state = next;
        stateTimer.reset();
    }

    @Override
    public void stop() {
        if (drive != null) drive.stop();
        // todo stop intake and shooters too
    }
}
