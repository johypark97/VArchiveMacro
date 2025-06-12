package com.github.johypark97.varchivemacro.macro.common.converter;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.util.EnumSet;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputKeyConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputKeyConverter.class);

    private static final EnumSet<InputKey> INTEROPERABLE_INPUT_KEY_SET =
            EnumSet.of(InputKey.BACK_SPACE, InputKey.TAB, InputKey.ENTER, InputKey.CLEAR,
                    InputKey.SHIFT, InputKey.CONTROL, InputKey.ALT, InputKey.PAUSE,
                    InputKey.CAPS_LOCK, InputKey.ESCAPE, InputKey.SPACE, InputKey.PAGE_UP,
                    InputKey.PAGE_DOWN, InputKey.END, InputKey.HOME, InputKey.LEFT, InputKey.UP,
                    InputKey.RIGHT, InputKey.DOWN, InputKey.COMMA, InputKey.MINUS, InputKey.PERIOD,
                    InputKey.SLASH, InputKey.DIGIT0, InputKey.DIGIT1, InputKey.DIGIT2,
                    InputKey.DIGIT3, InputKey.DIGIT4, InputKey.DIGIT5, InputKey.DIGIT6,
                    InputKey.DIGIT7, InputKey.DIGIT8, InputKey.DIGIT9, InputKey.SEMICOLON,
                    InputKey.EQUALS, InputKey.A, InputKey.B, InputKey.C, InputKey.D, InputKey.E,
                    InputKey.F, InputKey.G, InputKey.H, InputKey.I, InputKey.J, InputKey.K,
                    InputKey.L, InputKey.M, InputKey.N, InputKey.O, InputKey.P, InputKey.Q,
                    InputKey.R, InputKey.S, InputKey.T, InputKey.U, InputKey.V, InputKey.W,
                    InputKey.X, InputKey.Y, InputKey.Z, InputKey.OPEN_BRACKET, InputKey.BACK_SLASH,
                    InputKey.CLOSE_BRACKET, InputKey.NUMPAD0, InputKey.NUMPAD1, InputKey.NUMPAD2,
                    InputKey.NUMPAD3, InputKey.NUMPAD4, InputKey.NUMPAD5, InputKey.NUMPAD6,
                    InputKey.NUMPAD7, InputKey.NUMPAD8, InputKey.NUMPAD9, InputKey.MULTIPLY,
                    InputKey.ADD, InputKey.SUBTRACT, InputKey.DECIMAL, InputKey.DIVIDE, InputKey.F1,
                    InputKey.F2, InputKey.F3, InputKey.F4, InputKey.F5, InputKey.F6, InputKey.F7,
                    InputKey.F8, InputKey.F9, InputKey.F10, InputKey.F11, InputKey.F12,
                    InputKey.DELETE, InputKey.NUM_LOCK, InputKey.SCROLL_LOCK, InputKey.PRINTSCREEN,
                    InputKey.INSERT, InputKey.BACK_QUOTE, InputKey.QUOTE, InputKey.WINDOWS,
                    InputKey.CONTEXT_MENU, InputKey.ALT_GRAPH);

    public static boolean isInteroperable(InputKey inputKey) {
        return INTEROPERABLE_INPUT_KEY_SET.contains(inputKey);
    }

    public static InputKey from(KeyCode keyCode) {
        return InputKey.from(keyCode.getCode());
    }

    public static InputKey from(NativeKeyEvent nativeKeyEvent) {
        return switch (nativeKeyEvent.getKeyCode()) {
            case NativeKeyEvent.VC_ESCAPE -> InputKey.ESCAPE;
            case NativeKeyEvent.VC_F1 -> InputKey.F1;
            case NativeKeyEvent.VC_F2 -> InputKey.F2;
            case NativeKeyEvent.VC_F3 -> InputKey.F3;
            case NativeKeyEvent.VC_F4 -> InputKey.F4;
            case NativeKeyEvent.VC_F5 -> InputKey.F5;
            case NativeKeyEvent.VC_F6 -> InputKey.F6;
            case NativeKeyEvent.VC_F7 -> InputKey.F7;
            case NativeKeyEvent.VC_F8 -> InputKey.F8;
            case NativeKeyEvent.VC_F9 -> InputKey.F9;
            case NativeKeyEvent.VC_F10 -> InputKey.F10;
            case NativeKeyEvent.VC_F11 -> InputKey.F11;
            case NativeKeyEvent.VC_F12 -> InputKey.F12;
            case NativeKeyEvent.VC_F13 -> InputKey.F13;
            case NativeKeyEvent.VC_F14 -> InputKey.F14;
            case NativeKeyEvent.VC_F15 -> InputKey.F15;
            case NativeKeyEvent.VC_F16 -> InputKey.F16;
            case NativeKeyEvent.VC_F17 -> InputKey.F17;
            case NativeKeyEvent.VC_F18 -> InputKey.F18;
            case NativeKeyEvent.VC_F19 -> InputKey.F19;
            case NativeKeyEvent.VC_F20 -> InputKey.F20;
            case NativeKeyEvent.VC_F21 -> InputKey.F21;
            case NativeKeyEvent.VC_F22 -> InputKey.F22;
            case NativeKeyEvent.VC_F23 -> InputKey.F23;
            case NativeKeyEvent.VC_F24 -> InputKey.F24;
            case NativeKeyEvent.VC_BACKQUOTE -> InputKey.BACK_QUOTE;
            case NativeKeyEvent.VC_1 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT1;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD1;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_2 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT2;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD2;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_3 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT3;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD3;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_4 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT4;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD4;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_5 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT5;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD5;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_6 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT6;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD6;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_7 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT7;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD7;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_8 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT8;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD8;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_9 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT9;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD9;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_0 -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.DIGIT0;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.NUMPAD0;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_MINUS -> InputKey.MINUS;
            case NativeKeyEvent.VC_EQUALS -> InputKey.EQUALS;
            case NativeKeyEvent.VC_BACKSPACE -> InputKey.BACK_SPACE;
            case NativeKeyEvent.VC_TAB -> InputKey.TAB;
            case NativeKeyEvent.VC_CAPS_LOCK -> InputKey.CAPS_LOCK;
            case NativeKeyEvent.VC_A -> InputKey.A;
            case NativeKeyEvent.VC_B -> InputKey.B;
            case NativeKeyEvent.VC_C -> InputKey.C;
            case NativeKeyEvent.VC_D -> InputKey.D;
            case NativeKeyEvent.VC_E -> InputKey.E;
            case NativeKeyEvent.VC_F -> InputKey.F;
            case NativeKeyEvent.VC_G -> InputKey.G;
            case NativeKeyEvent.VC_H -> InputKey.H;
            case NativeKeyEvent.VC_I -> InputKey.I;
            case NativeKeyEvent.VC_J -> InputKey.J;
            case NativeKeyEvent.VC_K -> InputKey.K;
            case NativeKeyEvent.VC_L -> InputKey.L;
            case NativeKeyEvent.VC_M -> InputKey.M;
            case NativeKeyEvent.VC_N -> InputKey.N;
            case NativeKeyEvent.VC_O -> InputKey.O;
            case NativeKeyEvent.VC_P -> InputKey.P;
            case NativeKeyEvent.VC_Q -> InputKey.Q;
            case NativeKeyEvent.VC_R -> InputKey.R;
            case NativeKeyEvent.VC_S -> InputKey.S;
            case NativeKeyEvent.VC_T -> InputKey.T;
            case NativeKeyEvent.VC_U -> InputKey.U;
            case NativeKeyEvent.VC_V -> InputKey.V;
            case NativeKeyEvent.VC_W -> InputKey.W;
            case NativeKeyEvent.VC_X -> InputKey.X;
            case NativeKeyEvent.VC_Y -> InputKey.Y;
            case NativeKeyEvent.VC_Z -> InputKey.Z;
            case NativeKeyEvent.VC_OPEN_BRACKET -> InputKey.OPEN_BRACKET;
            case NativeKeyEvent.VC_CLOSE_BRACKET -> InputKey.CLOSE_BRACKET;
            case NativeKeyEvent.VC_BACK_SLASH -> InputKey.BACK_SLASH;
            case NativeKeyEvent.VC_SEMICOLON -> InputKey.SEMICOLON;
            case NativeKeyEvent.VC_QUOTE -> InputKey.QUOTE;
            case NativeKeyEvent.VC_ENTER -> InputKey.ENTER;
            case NativeKeyEvent.VC_COMMA -> InputKey.COMMA;
            case NativeKeyEvent.VC_PERIOD -> InputKey.PERIOD;
            case NativeKeyEvent.VC_SLASH -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.SLASH;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.DIVIDE;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_SPACE -> InputKey.SPACE;
            case NativeKeyEvent.VC_PRINTSCREEN -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_STANDARD -> InputKey.PRINTSCREEN;
                case NativeKeyEvent.KEY_LOCATION_NUMPAD -> InputKey.MULTIPLY;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_SCROLL_LOCK -> InputKey.SCROLL_LOCK;
            case NativeKeyEvent.VC_PAUSE -> InputKey.PAUSE;
            case NativeKeyEvent.VC_INSERT -> InputKey.INSERT;
            case NativeKeyEvent.VC_DELETE -> InputKey.DELETE;
            case NativeKeyEvent.VC_HOME -> InputKey.HOME;
            case NativeKeyEvent.VC_END -> InputKey.END;
            case NativeKeyEvent.VC_PAGE_UP -> InputKey.PAGE_UP;
            case NativeKeyEvent.VC_PAGE_DOWN -> InputKey.PAGE_DOWN;
            case NativeKeyEvent.VC_UP -> InputKey.UP;
            case NativeKeyEvent.VC_LEFT -> InputKey.LEFT;
            case NativeKeyEvent.VC_CLEAR -> InputKey.CLEAR;
            case NativeKeyEvent.VC_RIGHT -> InputKey.RIGHT;
            case NativeKeyEvent.VC_DOWN -> InputKey.DOWN;
            case NativeKeyEvent.VC_NUM_LOCK -> InputKey.NUM_LOCK;
            case NativeKeyEvent.VC_SEPARATOR -> InputKey.DECIMAL;
            case NativeKeyEvent.VC_SHIFT -> InputKey.SHIFT;
            case NativeKeyEvent.VC_CONTROL -> InputKey.CONTROL;
            case NativeKeyEvent.VC_ALT -> switch (nativeKeyEvent.getKeyLocation()) {
                case NativeKeyEvent.KEY_LOCATION_LEFT -> InputKey.ALT;
                case NativeKeyEvent.KEY_LOCATION_RIGHT -> InputKey.ALT_GRAPH;
                default -> logUnknownNativeKey(nativeKeyEvent);
            };
            case NativeKeyEvent.VC_META -> InputKey.WINDOWS;
            case NativeKeyEvent.VC_CONTEXT_MENU -> InputKey.CONTEXT_MENU;
            // case NativeKeyEvent.VC_POWER ->
            // case NativeKeyEvent.VC_SLEEP ->
            // case NativeKeyEvent.VC_WAKE ->
            // case NativeKeyEvent.VC_MEDIA_PLAY ->
            // case NativeKeyEvent.VC_MEDIA_STOP ->
            // case NativeKeyEvent.VC_MEDIA_PREVIOUS ->
            // case NativeKeyEvent.VC_MEDIA_NEXT ->
            // case NativeKeyEvent.VC_MEDIA_SELECT ->
            // case NativeKeyEvent.VC_MEDIA_EJECT ->
            // case NativeKeyEvent.VC_VOLUME_MUTE ->
            // case NativeKeyEvent.VC_VOLUME_UP ->
            // case NativeKeyEvent.VC_VOLUME_DOWN ->
            // case NativeKeyEvent.VC_APP_MAIL ->
            // case NativeKeyEvent.VC_APP_CALCULATOR ->
            // case NativeKeyEvent.VC_APP_MUSIC ->
            // case NativeKeyEvent.VC_APP_PICTURES ->
            // case NativeKeyEvent.VC_BROWSER_SEARCH ->
            // case NativeKeyEvent.VC_BROWSER_HOME ->
            // case NativeKeyEvent.VC_BROWSER_BACK ->
            // case NativeKeyEvent.VC_BROWSER_FORWARD ->
            // case NativeKeyEvent.VC_BROWSER_STOP ->
            // case NativeKeyEvent.VC_BROWSER_REFRESH ->
            // case NativeKeyEvent.VC_BROWSER_FAVORITES ->
            // case NativeKeyEvent.VC_KATAKANA ->
            // case NativeKeyEvent.VC_UNDERSCORE ->
            // case NativeKeyEvent.VC_FURIGANA ->
            case NativeKeyEvent.VC_KANJI -> InputKey.INPUT_METHOD_ON_OFF;
            // case NativeKeyEvent.VC_HIRAGANA ->
            // case NativeKeyEvent.VC_YEN ->
            // case NativeKeyEvent.VC_SUN_HELP ->
            // case NativeKeyEvent.VC_SUN_STOP ->
            // case NativeKeyEvent.VC_SUN_PROPS ->
            // case NativeKeyEvent.VC_SUN_FRONT ->
            // case NativeKeyEvent.VC_SUN_OPEN ->
            // case NativeKeyEvent.VC_SUN_FIND ->
            // case NativeKeyEvent.VC_SUN_AGAIN ->
            // case NativeKeyEvent.VC_SUN_UNDO ->
            // case NativeKeyEvent.VC_SUN_COPY ->
            // case NativeKeyEvent.VC_SUN_INSERT ->
            // case NativeKeyEvent.VC_SUN_CUT ->
            case NativeKeyEvent.VC_UNDEFINED -> InputKey.UNDEFINED;
            case 3658 -> InputKey.SUBTRACT;
            case 3662 -> InputKey.ADD;
            default -> logUnknownNativeKey(nativeKeyEvent);
        };
    }

    public static KeyCode toKeyCode(InputKey inputKey) {
        return switch (inputKey) {
            case UNDEFINED -> KeyCode.UNDEFINED;
            case CANCEL -> KeyCode.CANCEL;
            case BACK_SPACE -> KeyCode.BACK_SPACE;
            case TAB -> KeyCode.TAB;
            case ENTER -> KeyCode.ENTER;
            case CLEAR -> KeyCode.CLEAR;
            case SHIFT -> KeyCode.SHIFT;
            case CONTROL -> KeyCode.CONTROL;
            case ALT -> KeyCode.ALT;
            case PAUSE -> KeyCode.PAUSE;
            case CAPS_LOCK -> KeyCode.CAPS;
            case KANA -> KeyCode.KANA;
            case FINAL -> KeyCode.FINAL;
            case KANJI -> KeyCode.KANJI;
            case ESCAPE -> KeyCode.ESCAPE;
            case CONVERT -> KeyCode.CONVERT;
            case NONCONVERT -> KeyCode.NONCONVERT;
            case ACCEPT -> KeyCode.ACCEPT;
            case MODECHANGE -> KeyCode.MODECHANGE;
            case SPACE -> KeyCode.SPACE;
            case PAGE_UP -> KeyCode.PAGE_UP;
            case PAGE_DOWN -> KeyCode.PAGE_DOWN;
            case END -> KeyCode.END;
            case HOME -> KeyCode.HOME;
            case LEFT -> KeyCode.LEFT;
            case UP -> KeyCode.UP;
            case RIGHT -> KeyCode.RIGHT;
            case DOWN -> KeyCode.DOWN;
            case COMMA -> KeyCode.COMMA;
            case MINUS -> KeyCode.MINUS;
            case PERIOD -> KeyCode.PERIOD;
            case SLASH -> KeyCode.SLASH;
            case DIGIT0 -> KeyCode.DIGIT0;
            case DIGIT1 -> KeyCode.DIGIT1;
            case DIGIT2 -> KeyCode.DIGIT2;
            case DIGIT3 -> KeyCode.DIGIT3;
            case DIGIT4 -> KeyCode.DIGIT4;
            case DIGIT5 -> KeyCode.DIGIT5;
            case DIGIT6 -> KeyCode.DIGIT6;
            case DIGIT7 -> KeyCode.DIGIT7;
            case DIGIT8 -> KeyCode.DIGIT8;
            case DIGIT9 -> KeyCode.DIGIT9;
            case SEMICOLON -> KeyCode.SEMICOLON;
            case EQUALS -> KeyCode.EQUALS;
            case A -> KeyCode.A;
            case B -> KeyCode.B;
            case C -> KeyCode.C;
            case D -> KeyCode.D;
            case E -> KeyCode.E;
            case F -> KeyCode.F;
            case G -> KeyCode.G;
            case H -> KeyCode.H;
            case I -> KeyCode.I;
            case J -> KeyCode.J;
            case K -> KeyCode.K;
            case L -> KeyCode.L;
            case M -> KeyCode.M;
            case N -> KeyCode.N;
            case O -> KeyCode.O;
            case P -> KeyCode.P;
            case Q -> KeyCode.Q;
            case R -> KeyCode.R;
            case S -> KeyCode.S;
            case T -> KeyCode.T;
            case U -> KeyCode.U;
            case V -> KeyCode.V;
            case W -> KeyCode.W;
            case X -> KeyCode.X;
            case Y -> KeyCode.Y;
            case Z -> KeyCode.Z;
            case OPEN_BRACKET -> KeyCode.OPEN_BRACKET;
            case BACK_SLASH -> KeyCode.BACK_SLASH;
            case CLOSE_BRACKET -> KeyCode.CLOSE_BRACKET;
            case NUMPAD0 -> KeyCode.NUMPAD0;
            case NUMPAD1 -> KeyCode.NUMPAD1;
            case NUMPAD2 -> KeyCode.NUMPAD2;
            case NUMPAD3 -> KeyCode.NUMPAD3;
            case NUMPAD4 -> KeyCode.NUMPAD4;
            case NUMPAD5 -> KeyCode.NUMPAD5;
            case NUMPAD6 -> KeyCode.NUMPAD6;
            case NUMPAD7 -> KeyCode.NUMPAD7;
            case NUMPAD8 -> KeyCode.NUMPAD8;
            case NUMPAD9 -> KeyCode.NUMPAD9;
            case MULTIPLY -> KeyCode.MULTIPLY;
            case ADD -> KeyCode.ADD;
            case SEPARATOR -> KeyCode.SEPARATOR;
            case SUBTRACT -> KeyCode.SUBTRACT;
            case DECIMAL -> KeyCode.DECIMAL;
            case DIVIDE -> KeyCode.DIVIDE;
            case F1 -> KeyCode.F1;
            case F2 -> KeyCode.F2;
            case F3 -> KeyCode.F3;
            case F4 -> KeyCode.F4;
            case F5 -> KeyCode.F5;
            case F6 -> KeyCode.F6;
            case F7 -> KeyCode.F7;
            case F8 -> KeyCode.F8;
            case F9 -> KeyCode.F9;
            case F10 -> KeyCode.F10;
            case F11 -> KeyCode.F11;
            case F12 -> KeyCode.F12;
            case DELETE -> KeyCode.DELETE;
            case DEAD_GRAVE -> KeyCode.DEAD_GRAVE;
            case DEAD_ACUTE -> KeyCode.DEAD_ACUTE;
            case DEAD_CIRCUMFLEX -> KeyCode.DEAD_CIRCUMFLEX;
            case DEAD_TILDE -> KeyCode.DEAD_TILDE;
            case DEAD_MACRON -> KeyCode.DEAD_MACRON;
            case DEAD_BREVE -> KeyCode.DEAD_BREVE;
            case DEAD_ABOVEDOT -> KeyCode.DEAD_ABOVEDOT;
            case DEAD_DIAERESIS -> KeyCode.DEAD_DIAERESIS;
            case DEAD_ABOVERING -> KeyCode.DEAD_ABOVERING;
            case DEAD_DOUBLEACUTE -> KeyCode.DEAD_DOUBLEACUTE;
            case DEAD_CARON -> KeyCode.DEAD_CARON;
            case DEAD_CEDILLA -> KeyCode.DEAD_CEDILLA;
            case DEAD_OGONEK -> KeyCode.DEAD_OGONEK;
            case DEAD_IOTA -> KeyCode.DEAD_IOTA;
            case DEAD_VOICED_SOUND -> KeyCode.DEAD_VOICED_SOUND;
            case DEAD_SEMIVOICED_SOUND -> KeyCode.DEAD_SEMIVOICED_SOUND;
            case NUM_LOCK -> KeyCode.NUM_LOCK;
            case SCROLL_LOCK -> KeyCode.SCROLL_LOCK;
            case AMPERSAND -> KeyCode.AMPERSAND;
            case ASTERISK -> KeyCode.ASTERISK;
            case QUOTEDBL -> KeyCode.QUOTEDBL;
            case LESS -> KeyCode.LESS;
            case PRINTSCREEN -> KeyCode.PRINTSCREEN;
            case INSERT -> KeyCode.INSERT;
            case HELP -> KeyCode.HELP;
            case META -> KeyCode.META;
            case GREATER -> KeyCode.GREATER;
            case BRACELEFT -> KeyCode.BRACELEFT;
            case BRACERIGHT -> KeyCode.BRACERIGHT;
            case BACK_QUOTE -> KeyCode.BACK_QUOTE;
            case QUOTE -> KeyCode.QUOTE;
            case KP_UP -> KeyCode.KP_UP;
            case KP_DOWN -> KeyCode.KP_DOWN;
            case KP_LEFT -> KeyCode.KP_LEFT;
            case KP_RIGHT -> KeyCode.KP_RIGHT;
            case ALPHANUMERIC -> KeyCode.ALPHANUMERIC;
            case KATAKANA -> KeyCode.KATAKANA;
            case HIRAGANA -> KeyCode.HIRAGANA;
            case FULL_WIDTH -> KeyCode.FULL_WIDTH;
            case HALF_WIDTH -> KeyCode.HALF_WIDTH;
            case ROMAN_CHARACTERS -> KeyCode.ROMAN_CHARACTERS;
            case ALL_CANDIDATES -> KeyCode.ALL_CANDIDATES;
            case PREVIOUS_CANDIDATE -> KeyCode.PREVIOUS_CANDIDATE;
            case CODE_INPUT -> KeyCode.CODE_INPUT;
            case JAPANESE_KATAKANA -> KeyCode.JAPANESE_KATAKANA;
            case JAPANESE_HIRAGANA -> KeyCode.JAPANESE_HIRAGANA;
            case JAPANESE_ROMAN -> KeyCode.JAPANESE_ROMAN;
            case KANA_LOCK -> KeyCode.KANA_LOCK;
            case INPUT_METHOD_ON_OFF -> KeyCode.INPUT_METHOD_ON_OFF;
            case AT -> KeyCode.AT;
            case COLON -> KeyCode.COLON;
            case CIRCUMFLEX -> KeyCode.CIRCUMFLEX;
            case DOLLAR -> KeyCode.DOLLAR;
            case EURO_SIGN -> KeyCode.EURO_SIGN;
            case EXCLAMATION_MARK -> KeyCode.EXCLAMATION_MARK;
            case INVERTED_EXCLAMATION_MARK -> KeyCode.INVERTED_EXCLAMATION_MARK;
            case LEFT_PARENTHESIS -> KeyCode.LEFT_PARENTHESIS;
            case NUMBER_SIGN -> KeyCode.NUMBER_SIGN;
            case PLUS -> KeyCode.PLUS;
            case RIGHT_PARENTHESIS -> KeyCode.RIGHT_PARENTHESIS;
            case UNDERSCORE -> KeyCode.UNDERSCORE;
            case WINDOWS -> KeyCode.WINDOWS;
            case CONTEXT_MENU -> KeyCode.CONTEXT_MENU;
            case F13 -> KeyCode.F13;
            case F14 -> KeyCode.F14;
            case F15 -> KeyCode.F15;
            case F16 -> KeyCode.F16;
            case F17 -> KeyCode.F17;
            case F18 -> KeyCode.F18;
            case F19 -> KeyCode.F19;
            case F20 -> KeyCode.F20;
            case F21 -> KeyCode.F21;
            case F22 -> KeyCode.F22;
            case F23 -> KeyCode.F23;
            case F24 -> KeyCode.F24;
            case COMPOSE -> KeyCode.COMPOSE;
            case BEGIN -> KeyCode.BEGIN;
            case ALT_GRAPH -> KeyCode.ALT_GRAPH;
            case STOP -> KeyCode.STOP;
            case AGAIN -> KeyCode.AGAIN;
            case PROPS -> KeyCode.PROPS;
            case UNDO -> KeyCode.UNDO;
            case COPY -> KeyCode.COPY;
            case PASTE -> KeyCode.PASTE;
            case FIND -> KeyCode.FIND;
            case CUT -> KeyCode.CUT;
        };
    }

    private static InputKey logUnknownNativeKey(NativeKeyEvent nativeKeyEvent) {
        LOGGER.atDebug().log("Unknown native key - keyCode: {}, keyLocation: {}",
                nativeKeyEvent.getKeyCode(), nativeKeyEvent.getKeyLocation());

        return InputKey.UNDEFINED;
    }
}
