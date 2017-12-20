package Ares.Common.Commands;

public abstract class Command {

    public static final String STR_CONNECT = "CONNECT";
    public static final String STR_END_TURN = "END_TURN";
    public static final String STR_MOVE = "MOVE";
    public static final String STR_OBSERVE = "OBSERVE";
    public static final String STR_SAVE_SURV = "SAVE_SURV";
    public static final String STR_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String STR_SLEEP = "SLEEP";
    public static final String STR_TEAM_DIG = "TEAM_DIG";
    public static final String STR_CONNECT_OK = "CONNECT_OK";
    public static final String STR_DISCONNECT = "DISCONNECT";
    public static final String STR_UNKNOWN = "UNKNOWN";
    public static final String STR_CMD_RESULT_END = "CMD_RESULT_END";
    public static final String STR_CMD_RESULT_START = "CMD_RESULT_START";
    public static final String STR_DEATH_CARD = "DEATH_CARD";
    public static final String STR_FWD_MESSAGE = "FWD_MESSAGE";
    public static final String STR_MESSAGES_END = "MESSAGES_END";
    public static final String STR_MESSAGES_START = "MESSAGES_START";
    public static final String STR_MOVE_RESULT = "MOVE_RESULT";
    public static final String STR_OBSERVE_RESULT = "OBSERVE_RESULT";
    public static final String STR_ROUND_END = "ROUND_END";
    public static final String STR_ROUND_START = "ROUND_START";
    public static final String STR_SAVE_SURV_RESULT = "SAVE_SURV_RESULT";
    public static final String STR_SLEEP_RESULT = "SLEEP_RESULT";
    public static final String STR_TEAM_DIG_RESULT = "TEAM_DIG_RESULT";

    @Override
    public abstract String toString();
}
