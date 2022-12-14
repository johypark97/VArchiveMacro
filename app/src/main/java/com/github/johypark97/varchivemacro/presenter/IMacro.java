package com.github.johypark97.varchivemacro.presenter;

public interface IMacro {
    public interface Presenter {
        void addLog(String message);

        void showLicense();

        void viewOpened();

        void viewClosed();
    }

    public interface View {
        enum SliderKey {
            CAPTURE_DURATION, COUNT, INPUT_DURATION, MOVING_DELAY
        }

        enum AnalyzeKey {
            ALT_F11, ALT_F12, ALT_HOME, ALT_INS
        }

        enum DirectionKey {
            DOWN, UP
        }

        void setSliderDefault(int count, int movingDelay, int captureDuration, int inputDuration);

        void setValues(int count, int movingDelay, int captureDuration, int inputDuration,
                AnalyzeKey analyzeKey, DirectionKey directionKey);

        int getCount();

        int getMovingDelay();

        int getCaptureDuration();

        int getInputDuration();

        AnalyzeKey getAnalyzeKey();

        DirectionKey getDirectionKey();

        void setDirectionKey(DirectionKey key);

        void showDialog(String title, Object[] messages, int messageType);

        void addLog(String message);
    }
}
