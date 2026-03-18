package com.github.johypark97.varchivemacro.lib.desktop;

import java.awt.event.KeyEvent;

public enum InputKey {
    // @formatter:off

    // 0
    UNDEFINED("Undefined", "", KeyEvent.VK_UNDEFINED),

    // 3
    CANCEL("Cancel", "", KeyEvent.VK_CANCEL),

    // 8 - 10
    BACK_SPACE("Backspace", "", KeyEvent.VK_BACK_SPACE),
    TAB("Tab", "", KeyEvent.VK_TAB),
    ENTER("Enter", "", KeyEvent.VK_ENTER),

    // 12
    CLEAR("Clear", "", KeyEvent.VK_CLEAR),

    // 16 - 21
    SHIFT("Shift", "", KeyEvent.VK_SHIFT),
    CONTROL("Ctrl", "", KeyEvent.VK_CONTROL),
    ALT("Alt", "", KeyEvent.VK_ALT),
    PAUSE("Pause", "", KeyEvent.VK_PAUSE),
    CAPS_LOCK("Caps Lock", "", KeyEvent.VK_CAPS_LOCK),
    KANA("Kana", "", KeyEvent.VK_KANA),

    // 24 - 25
    FINAL("Final", "", KeyEvent.VK_FINAL),
    KANJI("Kanji", "", KeyEvent.VK_KANJI),

    // 27 - 40
    ESCAPE("Escape", "Esc", KeyEvent.VK_ESCAPE),
    CONVERT("Convert", "", KeyEvent.VK_CONVERT),
    NONCONVERT("No Convert", "", KeyEvent.VK_NONCONVERT),
    ACCEPT("Accept", "", KeyEvent.VK_ACCEPT),
    MODECHANGE("Mode Change", "", KeyEvent.VK_MODECHANGE),
    SPACE("Space", "", KeyEvent.VK_SPACE),
    PAGE_UP("Page Up", "", KeyEvent.VK_PAGE_UP),
    PAGE_DOWN("Page Down", "", KeyEvent.VK_PAGE_DOWN),
    END("End", "", KeyEvent.VK_END),
    HOME("Home", "", KeyEvent.VK_HOME),
    LEFT("Left", "", KeyEvent.VK_LEFT),
    UP("Up", "", KeyEvent.VK_UP),
    RIGHT("Right", "", KeyEvent.VK_RIGHT),
    DOWN("Down", "", KeyEvent.VK_DOWN),

    // 44 - 57
    COMMA("Comma", ",", KeyEvent.VK_COMMA),
    MINUS("Minus", "-", KeyEvent.VK_MINUS),
    PERIOD("Period", ".", KeyEvent.VK_PERIOD),
    SLASH("Slash", "/", KeyEvent.VK_SLASH),
    DIGIT0("0", "", KeyEvent.VK_0),
    DIGIT1("1", "", KeyEvent.VK_1),
    DIGIT2("2", "", KeyEvent.VK_2),
    DIGIT3("3", "", KeyEvent.VK_3),
    DIGIT4("4", "", KeyEvent.VK_4),
    DIGIT5("5", "", KeyEvent.VK_5),
    DIGIT6("6", "", KeyEvent.VK_6),
    DIGIT7("7", "", KeyEvent.VK_7),
    DIGIT8("8", "", KeyEvent.VK_8),
    DIGIT9("9", "", KeyEvent.VK_9),

    // 59
    SEMICOLON("Semicolon", ";", KeyEvent.VK_SEMICOLON),

    // 61
    EQUALS("Equals", "=", KeyEvent.VK_EQUALS),

    // 65 - 93
    A("A", "", KeyEvent.VK_A),
    B("B", "", KeyEvent.VK_B),
    C("C", "", KeyEvent.VK_C),
    D("D", "", KeyEvent.VK_D),
    E("E", "", KeyEvent.VK_E),
    F("F", "", KeyEvent.VK_F),
    G("G", "", KeyEvent.VK_G),
    H("H", "", KeyEvent.VK_H),
    I("I", "", KeyEvent.VK_I),
    J("J", "", KeyEvent.VK_J),
    K("K", "", KeyEvent.VK_K),
    L("L", "", KeyEvent.VK_L),
    M("M", "", KeyEvent.VK_M),
    N("N", "", KeyEvent.VK_N),
    O("O", "", KeyEvent.VK_O),
    P("P", "", KeyEvent.VK_P),
    Q("Q", "", KeyEvent.VK_Q),
    R("R", "", KeyEvent.VK_R),
    S("S", "", KeyEvent.VK_S),
    T("T", "", KeyEvent.VK_T),
    U("U", "", KeyEvent.VK_U),
    V("V", "", KeyEvent.VK_V),
    W("W", "", KeyEvent.VK_W),
    X("X", "", KeyEvent.VK_X),
    Y("Y", "", KeyEvent.VK_Y),
    Z("Z", "", KeyEvent.VK_Z),
    OPEN_BRACKET("Open Bracket", "[", KeyEvent.VK_OPEN_BRACKET),
    BACK_SLASH("Back Slash", "\\", KeyEvent.VK_BACK_SLASH),
    CLOSE_BRACKET("Close Bracket", "]", KeyEvent.VK_CLOSE_BRACKET),

    // 96 - 123
    NUMPAD0("NumPad 0", "", KeyEvent.VK_NUMPAD0),
    NUMPAD1("NumPad 1", "", KeyEvent.VK_NUMPAD1),
    NUMPAD2("NumPad 2", "", KeyEvent.VK_NUMPAD2),
    NUMPAD3("NumPad 3", "", KeyEvent.VK_NUMPAD3),
    NUMPAD4("NumPad 4", "", KeyEvent.VK_NUMPAD4),
    NUMPAD5("NumPad 5", "", KeyEvent.VK_NUMPAD5),
    NUMPAD6("NumPad 6", "", KeyEvent.VK_NUMPAD6),
    NUMPAD7("NumPad 7", "", KeyEvent.VK_NUMPAD7),
    NUMPAD8("NumPad 8", "", KeyEvent.VK_NUMPAD8),
    NUMPAD9("NumPad 9", "", KeyEvent.VK_NUMPAD9),
    MULTIPLY("NumPad *", "", KeyEvent.VK_MULTIPLY),
    ADD("NumPad +", "", KeyEvent.VK_ADD),
    SEPARATOR("NumPad ,", "", KeyEvent.VK_SEPARATOR),
    SUBTRACT("NumPad -", "", KeyEvent.VK_SUBTRACT),
    DECIMAL("NumPad .", "", KeyEvent.VK_DECIMAL),
    DIVIDE("NumPad /", "", KeyEvent.VK_DIVIDE),
    F1("F1", "", KeyEvent.VK_F1),
    F2("F2", "", KeyEvent.VK_F2),
    F3("F3", "", KeyEvent.VK_F3),
    F4("F4", "", KeyEvent.VK_F4),
    F5("F5", "", KeyEvent.VK_F5),
    F6("F6", "", KeyEvent.VK_F6),
    F7("F7", "", KeyEvent.VK_F7),
    F8("F8", "", KeyEvent.VK_F8),
    F9("F9", "", KeyEvent.VK_F9),
    F10("F10", "", KeyEvent.VK_F10),
    F11("F11", "", KeyEvent.VK_F11),
    F12("F12", "", KeyEvent.VK_F12),

    // 127 - 145
    DELETE("Delete", "", KeyEvent.VK_DELETE),
    DEAD_GRAVE("Dead Grave", "", KeyEvent.VK_DEAD_GRAVE),
    DEAD_ACUTE("Dead Acute", "", KeyEvent.VK_DEAD_ACUTE),
    DEAD_CIRCUMFLEX("Dead Circumflex", "", KeyEvent.VK_DEAD_CIRCUMFLEX),
    DEAD_TILDE("Dead Tilde", "", KeyEvent.VK_DEAD_TILDE),
    DEAD_MACRON("Dead Macron", "", KeyEvent.VK_DEAD_MACRON),
    DEAD_BREVE("Dead Breve", "", KeyEvent.VK_DEAD_BREVE),
    DEAD_ABOVEDOT("Dead Above Dot", "", KeyEvent.VK_DEAD_ABOVEDOT),
    DEAD_DIAERESIS("Dead Diaeresis", "", KeyEvent.VK_DEAD_DIAERESIS),
    DEAD_ABOVERING("Dead Above Ring", "", KeyEvent.VK_DEAD_ABOVERING),
    DEAD_DOUBLEACUTE("Dead Double Acute", "", KeyEvent.VK_DEAD_DOUBLEACUTE),
    DEAD_CARON("Dead Caron", "", KeyEvent.VK_DEAD_CARON),
    DEAD_CEDILLA("Dead Cedilla", "", KeyEvent.VK_DEAD_CEDILLA),
    DEAD_OGONEK("Dead Ogonek", "", KeyEvent.VK_DEAD_OGONEK),
    DEAD_IOTA("Dead Iota", "", KeyEvent.VK_DEAD_IOTA),
    DEAD_VOICED_SOUND("Dead Voiced Sound", "", KeyEvent.VK_DEAD_VOICED_SOUND),
    DEAD_SEMIVOICED_SOUND("Dead Semivoiced Sound", "", KeyEvent.VK_DEAD_SEMIVOICED_SOUND),
    NUM_LOCK("Num Lock", "", KeyEvent.VK_NUM_LOCK),
    SCROLL_LOCK("Scroll Lock", "", KeyEvent.VK_SCROLL_LOCK),

    // 150 - 157
    AMPERSAND("Ampersand", "&", KeyEvent.VK_AMPERSAND),
    ASTERISK("Asterisk", "*", KeyEvent.VK_ASTERISK),
    QUOTEDBL("Double Quote", "\"", KeyEvent.VK_QUOTEDBL),
    LESS("Less", "<", KeyEvent.VK_LESS),
    PRINTSCREEN("Print Screen", "", KeyEvent.VK_PRINTSCREEN),
    INSERT("Insert", "", KeyEvent.VK_INSERT),
    HELP("Help", "", KeyEvent.VK_HELP),
    META("Meta", "", KeyEvent.VK_META),

    // 160 - 162
    GREATER("Greater", ">", KeyEvent.VK_GREATER),
    BRACELEFT("Left Brace", "{", KeyEvent.VK_BRACELEFT),
    BRACERIGHT("Right Brace", "}", KeyEvent.VK_BRACERIGHT),

    // 192
    BACK_QUOTE("Back Quote", "`", KeyEvent.VK_BACK_QUOTE),

    // 222
    QUOTE("Quote", "'", KeyEvent.VK_QUOTE),

    // 224 - 227
    KP_UP("NumPad Up", "", KeyEvent.VK_KP_UP),
    KP_DOWN("NumPad Down", "", KeyEvent.VK_KP_DOWN),
    KP_LEFT("NumPad Left", "", KeyEvent.VK_KP_LEFT),
    KP_RIGHT("NumPad Right", "", KeyEvent.VK_KP_RIGHT),

    // 240 - 245
    ALPHANUMERIC("Alphanumeric", "", KeyEvent.VK_ALPHANUMERIC),
    KATAKANA("Katakana", "", KeyEvent.VK_KATAKANA),
    HIRAGANA("Hiragana", "", KeyEvent.VK_HIRAGANA),
    FULL_WIDTH("Full-Width", "", KeyEvent.VK_FULL_WIDTH),
    HALF_WIDTH("Half-Width", "", KeyEvent.VK_HALF_WIDTH),
    ROMAN_CHARACTERS("Roman Characters", "", KeyEvent.VK_ROMAN_CHARACTERS),

    // 256 - 263
    ALL_CANDIDATES("All Candidates", "", KeyEvent.VK_ALL_CANDIDATES),
    PREVIOUS_CANDIDATE("Previous Candidate", "", KeyEvent.VK_PREVIOUS_CANDIDATE),
    CODE_INPUT("Code Input", "", KeyEvent.VK_CODE_INPUT),
    JAPANESE_KATAKANA("Japanese Katakana", "", KeyEvent.VK_JAPANESE_KATAKANA),
    JAPANESE_HIRAGANA("Japanese Hiragana", "", KeyEvent.VK_JAPANESE_HIRAGANA),
    JAPANESE_ROMAN("Japanese Roman", "", KeyEvent.VK_JAPANESE_ROMAN),
    KANA_LOCK("Kana Lock", "", KeyEvent.VK_KANA_LOCK),
    INPUT_METHOD_ON_OFF("Input Method On/Off", "", KeyEvent.VK_INPUT_METHOD_ON_OFF),

    // 512 - 525
    AT("At", "@", KeyEvent.VK_AT),
    COLON("Colon", ":", KeyEvent.VK_COLON),
    CIRCUMFLEX("Circumflex", "^", KeyEvent.VK_CIRCUMFLEX),
    DOLLAR("Dollar", "$", KeyEvent.VK_DOLLAR),
    EURO_SIGN("Euro", "", KeyEvent.VK_EURO_SIGN),
    EXCLAMATION_MARK("Exclamation Mark", "!", KeyEvent.VK_EXCLAMATION_MARK),
    INVERTED_EXCLAMATION_MARK("Inverted Exclamation Mark", "", KeyEvent.VK_INVERTED_EXCLAMATION_MARK),
    LEFT_PARENTHESIS("Left Parenthesis", "(", KeyEvent.VK_LEFT_PARENTHESIS),
    NUMBER_SIGN("Number Sign", "#", KeyEvent.VK_NUMBER_SIGN),
    PLUS("Plus", "+", KeyEvent.VK_PLUS),
    RIGHT_PARENTHESIS("Right Parenthesis", ")", KeyEvent.VK_RIGHT_PARENTHESIS),
    UNDERSCORE("Underscore", "_", KeyEvent.VK_UNDERSCORE),
    WINDOWS("Windows", "", KeyEvent.VK_WINDOWS),
    CONTEXT_MENU("Context Menu", "", KeyEvent.VK_CONTEXT_MENU),

    // 61440 - 61451
    F13("F13", "", KeyEvent.VK_F13),
    F14("F14", "", KeyEvent.VK_F14),
    F15("F15", "", KeyEvent.VK_F15),
    F16("F16", "", KeyEvent.VK_F16),
    F17("F17", "", KeyEvent.VK_F17),
    F18("F18", "", KeyEvent.VK_F18),
    F19("F19", "", KeyEvent.VK_F19),
    F20("F20", "", KeyEvent.VK_F20),
    F21("F21", "", KeyEvent.VK_F21),
    F22("F22", "", KeyEvent.VK_F22),
    F23("F23", "", KeyEvent.VK_F23),
    F24("F24", "", KeyEvent.VK_F24),

    // 65312
    COMPOSE("Compose", "", KeyEvent.VK_COMPOSE),

    // 65368
    BEGIN("Begin", "", KeyEvent.VK_BEGIN),

    // 65406
    ALT_GRAPH("Alt Graph", "", KeyEvent.VK_ALT_GRAPH),

    // 65480 - 65483
    STOP("Stop", "", KeyEvent.VK_STOP),
    AGAIN("Again", "", KeyEvent.VK_AGAIN),
    PROPS("Props", "", KeyEvent.VK_PROPS),
    UNDO("Undo", "", KeyEvent.VK_UNDO),

    // 65485
    COPY("Copy", "", KeyEvent.VK_COPY),

    // 65487 - 65489
    PASTE("Paste", "", KeyEvent.VK_PASTE),
    FIND("Find", "", KeyEvent.VK_FIND),
    CUT("Cut", "", KeyEvent.VK_CUT);

    // @formatter:on

    private final String c;
    private final String s;
    private final int awtKeyCode;

    InputKey(String s, String c, int awtKeyCode) {
        this.awtKeyCode = awtKeyCode;
        this.c = c;
        this.s = s;
    }

    public static InputKey from(int awtKeyCode) {
        return switch (awtKeyCode) {
            case KeyEvent.VK_CANCEL -> CANCEL;
            case KeyEvent.VK_BACK_SPACE -> BACK_SPACE;
            case KeyEvent.VK_TAB -> TAB;
            case KeyEvent.VK_ENTER -> ENTER;
            case KeyEvent.VK_CLEAR -> CLEAR;
            case KeyEvent.VK_SHIFT -> SHIFT;
            case KeyEvent.VK_CONTROL -> CONTROL;
            case KeyEvent.VK_ALT -> ALT;
            case KeyEvent.VK_PAUSE -> PAUSE;
            case KeyEvent.VK_CAPS_LOCK -> CAPS_LOCK;
            case KeyEvent.VK_KANA -> KANA;
            case KeyEvent.VK_FINAL -> FINAL;
            case KeyEvent.VK_KANJI -> KANJI;
            case KeyEvent.VK_ESCAPE -> ESCAPE;
            case KeyEvent.VK_CONVERT -> CONVERT;
            case KeyEvent.VK_NONCONVERT -> NONCONVERT;
            case KeyEvent.VK_ACCEPT -> ACCEPT;
            case KeyEvent.VK_MODECHANGE -> MODECHANGE;
            case KeyEvent.VK_SPACE -> SPACE;
            case KeyEvent.VK_PAGE_UP -> PAGE_UP;
            case KeyEvent.VK_PAGE_DOWN -> PAGE_DOWN;
            case KeyEvent.VK_END -> END;
            case KeyEvent.VK_HOME -> HOME;
            case KeyEvent.VK_LEFT -> LEFT;
            case KeyEvent.VK_UP -> UP;
            case KeyEvent.VK_RIGHT -> RIGHT;
            case KeyEvent.VK_DOWN -> DOWN;
            case KeyEvent.VK_COMMA -> COMMA;
            case KeyEvent.VK_MINUS -> MINUS;
            case KeyEvent.VK_PERIOD -> PERIOD;
            case KeyEvent.VK_SLASH -> SLASH;
            case KeyEvent.VK_0 -> DIGIT0;
            case KeyEvent.VK_1 -> DIGIT1;
            case KeyEvent.VK_2 -> DIGIT2;
            case KeyEvent.VK_3 -> DIGIT3;
            case KeyEvent.VK_4 -> DIGIT4;
            case KeyEvent.VK_5 -> DIGIT5;
            case KeyEvent.VK_6 -> DIGIT6;
            case KeyEvent.VK_7 -> DIGIT7;
            case KeyEvent.VK_8 -> DIGIT8;
            case KeyEvent.VK_9 -> DIGIT9;
            case KeyEvent.VK_SEMICOLON -> SEMICOLON;
            case KeyEvent.VK_EQUALS -> EQUALS;
            case KeyEvent.VK_A -> A;
            case KeyEvent.VK_B -> B;
            case KeyEvent.VK_C -> C;
            case KeyEvent.VK_D -> D;
            case KeyEvent.VK_E -> E;
            case KeyEvent.VK_F -> F;
            case KeyEvent.VK_G -> G;
            case KeyEvent.VK_H -> H;
            case KeyEvent.VK_I -> I;
            case KeyEvent.VK_J -> J;
            case KeyEvent.VK_K -> K;
            case KeyEvent.VK_L -> L;
            case KeyEvent.VK_M -> M;
            case KeyEvent.VK_N -> N;
            case KeyEvent.VK_O -> O;
            case KeyEvent.VK_P -> P;
            case KeyEvent.VK_Q -> Q;
            case KeyEvent.VK_R -> R;
            case KeyEvent.VK_S -> S;
            case KeyEvent.VK_T -> T;
            case KeyEvent.VK_U -> U;
            case KeyEvent.VK_V -> V;
            case KeyEvent.VK_W -> W;
            case KeyEvent.VK_X -> X;
            case KeyEvent.VK_Y -> Y;
            case KeyEvent.VK_Z -> Z;
            case KeyEvent.VK_OPEN_BRACKET -> OPEN_BRACKET;
            case KeyEvent.VK_BACK_SLASH -> BACK_SLASH;
            case KeyEvent.VK_CLOSE_BRACKET -> CLOSE_BRACKET;
            case KeyEvent.VK_NUMPAD0 -> NUMPAD0;
            case KeyEvent.VK_NUMPAD1 -> NUMPAD1;
            case KeyEvent.VK_NUMPAD2 -> NUMPAD2;
            case KeyEvent.VK_NUMPAD3 -> NUMPAD3;
            case KeyEvent.VK_NUMPAD4 -> NUMPAD4;
            case KeyEvent.VK_NUMPAD5 -> NUMPAD5;
            case KeyEvent.VK_NUMPAD6 -> NUMPAD6;
            case KeyEvent.VK_NUMPAD7 -> NUMPAD7;
            case KeyEvent.VK_NUMPAD8 -> NUMPAD8;
            case KeyEvent.VK_NUMPAD9 -> NUMPAD9;
            case KeyEvent.VK_MULTIPLY -> MULTIPLY;
            case KeyEvent.VK_ADD -> ADD;
            case KeyEvent.VK_SEPARATOR -> SEPARATOR;
            case KeyEvent.VK_SUBTRACT -> SUBTRACT;
            case KeyEvent.VK_DECIMAL -> DECIMAL;
            case KeyEvent.VK_DIVIDE -> DIVIDE;
            case KeyEvent.VK_F1 -> F1;
            case KeyEvent.VK_F2 -> F2;
            case KeyEvent.VK_F3 -> F3;
            case KeyEvent.VK_F4 -> F4;
            case KeyEvent.VK_F5 -> F5;
            case KeyEvent.VK_F6 -> F6;
            case KeyEvent.VK_F7 -> F7;
            case KeyEvent.VK_F8 -> F8;
            case KeyEvent.VK_F9 -> F9;
            case KeyEvent.VK_F10 -> F10;
            case KeyEvent.VK_F11 -> F11;
            case KeyEvent.VK_F12 -> F12;
            case KeyEvent.VK_DELETE -> DELETE;
            case KeyEvent.VK_DEAD_GRAVE -> DEAD_GRAVE;
            case KeyEvent.VK_DEAD_ACUTE -> DEAD_ACUTE;
            case KeyEvent.VK_DEAD_CIRCUMFLEX -> DEAD_CIRCUMFLEX;
            case KeyEvent.VK_DEAD_TILDE -> DEAD_TILDE;
            case KeyEvent.VK_DEAD_MACRON -> DEAD_MACRON;
            case KeyEvent.VK_DEAD_BREVE -> DEAD_BREVE;
            case KeyEvent.VK_DEAD_ABOVEDOT -> DEAD_ABOVEDOT;
            case KeyEvent.VK_DEAD_DIAERESIS -> DEAD_DIAERESIS;
            case KeyEvent.VK_DEAD_ABOVERING -> DEAD_ABOVERING;
            case KeyEvent.VK_DEAD_DOUBLEACUTE -> DEAD_DOUBLEACUTE;
            case KeyEvent.VK_DEAD_CARON -> DEAD_CARON;
            case KeyEvent.VK_DEAD_CEDILLA -> DEAD_CEDILLA;
            case KeyEvent.VK_DEAD_OGONEK -> DEAD_OGONEK;
            case KeyEvent.VK_DEAD_IOTA -> DEAD_IOTA;
            case KeyEvent.VK_DEAD_VOICED_SOUND -> DEAD_VOICED_SOUND;
            case KeyEvent.VK_DEAD_SEMIVOICED_SOUND -> DEAD_SEMIVOICED_SOUND;
            case KeyEvent.VK_NUM_LOCK -> NUM_LOCK;
            case KeyEvent.VK_SCROLL_LOCK -> SCROLL_LOCK;
            case KeyEvent.VK_AMPERSAND -> AMPERSAND;
            case KeyEvent.VK_ASTERISK -> ASTERISK;
            case KeyEvent.VK_QUOTEDBL -> QUOTEDBL;
            case KeyEvent.VK_LESS -> LESS;
            case KeyEvent.VK_PRINTSCREEN -> PRINTSCREEN;
            case KeyEvent.VK_INSERT -> INSERT;
            case KeyEvent.VK_HELP -> HELP;
            case KeyEvent.VK_META -> META;
            case KeyEvent.VK_GREATER -> GREATER;
            case KeyEvent.VK_BRACELEFT -> BRACELEFT;
            case KeyEvent.VK_BRACERIGHT -> BRACERIGHT;
            case KeyEvent.VK_BACK_QUOTE -> BACK_QUOTE;
            case KeyEvent.VK_QUOTE -> QUOTE;
            case KeyEvent.VK_KP_UP -> KP_UP;
            case KeyEvent.VK_KP_DOWN -> KP_DOWN;
            case KeyEvent.VK_KP_LEFT -> KP_LEFT;
            case KeyEvent.VK_KP_RIGHT -> KP_RIGHT;
            case KeyEvent.VK_ALPHANUMERIC -> ALPHANUMERIC;
            case KeyEvent.VK_KATAKANA -> KATAKANA;
            case KeyEvent.VK_HIRAGANA -> HIRAGANA;
            case KeyEvent.VK_FULL_WIDTH -> FULL_WIDTH;
            case KeyEvent.VK_HALF_WIDTH -> HALF_WIDTH;
            case KeyEvent.VK_ROMAN_CHARACTERS -> ROMAN_CHARACTERS;
            case KeyEvent.VK_ALL_CANDIDATES -> ALL_CANDIDATES;
            case KeyEvent.VK_PREVIOUS_CANDIDATE -> PREVIOUS_CANDIDATE;
            case KeyEvent.VK_CODE_INPUT -> CODE_INPUT;
            case KeyEvent.VK_JAPANESE_KATAKANA -> JAPANESE_KATAKANA;
            case KeyEvent.VK_JAPANESE_HIRAGANA -> JAPANESE_HIRAGANA;
            case KeyEvent.VK_JAPANESE_ROMAN -> JAPANESE_ROMAN;
            case KeyEvent.VK_KANA_LOCK -> KANA_LOCK;
            case KeyEvent.VK_INPUT_METHOD_ON_OFF -> INPUT_METHOD_ON_OFF;
            case KeyEvent.VK_AT -> AT;
            case KeyEvent.VK_COLON -> COLON;
            case KeyEvent.VK_CIRCUMFLEX -> CIRCUMFLEX;
            case KeyEvent.VK_DOLLAR -> DOLLAR;
            case KeyEvent.VK_EURO_SIGN -> EURO_SIGN;
            case KeyEvent.VK_EXCLAMATION_MARK -> EXCLAMATION_MARK;
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK -> INVERTED_EXCLAMATION_MARK;
            case KeyEvent.VK_LEFT_PARENTHESIS -> LEFT_PARENTHESIS;
            case KeyEvent.VK_NUMBER_SIGN -> NUMBER_SIGN;
            case KeyEvent.VK_PLUS -> PLUS;
            case KeyEvent.VK_RIGHT_PARENTHESIS -> RIGHT_PARENTHESIS;
            case KeyEvent.VK_UNDERSCORE -> UNDERSCORE;
            case KeyEvent.VK_WINDOWS -> WINDOWS;
            case KeyEvent.VK_CONTEXT_MENU -> CONTEXT_MENU;
            case KeyEvent.VK_F13 -> F13;
            case KeyEvent.VK_F14 -> F14;
            case KeyEvent.VK_F15 -> F15;
            case KeyEvent.VK_F16 -> F16;
            case KeyEvent.VK_F17 -> F17;
            case KeyEvent.VK_F18 -> F18;
            case KeyEvent.VK_F19 -> F19;
            case KeyEvent.VK_F20 -> F20;
            case KeyEvent.VK_F21 -> F21;
            case KeyEvent.VK_F22 -> F22;
            case KeyEvent.VK_F23 -> F23;
            case KeyEvent.VK_F24 -> F24;
            case KeyEvent.VK_COMPOSE -> COMPOSE;
            case KeyEvent.VK_BEGIN -> BEGIN;
            case KeyEvent.VK_ALT_GRAPH -> ALT_GRAPH;
            case KeyEvent.VK_STOP -> STOP;
            case KeyEvent.VK_AGAIN -> AGAIN;
            case KeyEvent.VK_PROPS -> PROPS;
            case KeyEvent.VK_UNDO -> UNDO;
            case KeyEvent.VK_COPY -> COPY;
            case KeyEvent.VK_PASTE -> PASTE;
            case KeyEvent.VK_FIND -> FIND;
            case KeyEvent.VK_CUT -> CUT;
            default -> UNDEFINED;
        };
    }

    public int toAwtKeyCode() {
        return awtKeyCode;
    }

    public String toChar() {
        return c;
    }

    @Override
    public String toString() {
        return s;
    }
}
