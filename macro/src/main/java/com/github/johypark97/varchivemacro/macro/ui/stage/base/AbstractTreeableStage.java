package com.github.johypark97.varchivemacro.macro.ui.stage.base;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.common.Treeable;
import javafx.stage.Stage;

public abstract class AbstractTreeableStage extends Treeable<AbstractTreeableStage>
        implements TreeableStage {
    protected final Stage stage;

    public AbstractTreeableStage(AbstractTreeableStage parent) {
        this(new Stage());

        parent.addChild(this);
        stage.initOwner(parent.stage);
    }

    public AbstractTreeableStage(Stage stage) {
        this.stage = stage;

        Mvp.hookWindowCloseRequest(this.stage, event -> stopStage());
    }

    protected abstract boolean onStopStage();

    @Override
    protected final AbstractTreeableStage self() {
        return this;
    }

    @Override
    public final boolean stopStage() {
        if (!getChildList().stream().allMatch(AbstractTreeableStage::stopStage) || !onStopStage()) {
            return false;
        }

        if (getParent() != null) {
            getParent().removeChild(this);
        }

        stage.hide();

        return true;
    }
}
