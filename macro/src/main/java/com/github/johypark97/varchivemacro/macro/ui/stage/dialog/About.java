package com.github.johypark97.varchivemacro.macro.ui.stage.dialog;

import com.github.johypark97.varchivemacro.macro.common.converter.ZonedDateTimeConverter;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.UpdateCheckContext;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class About extends Alert {
    public About(Window parent, String sourceCodeUrl, GlobalContext globalContext,
            UpdateCheckContext updateCheckContext) throws IOException {
        super(AlertType.INFORMATION);

        Language language = Language.INSTANCE;

        initOwner(parent);
        setHeaderText(null);
        setTitle(language.getString("home.about.windowTitle"));

        VBox box = new VBox();
        box.setPadding(new Insets(20));
        box.setSpacing(10);
        {
            box.getChildren().add(new Label(
                    language.getFormatString("home.about.programVersion", BuildInfo.version)));

            box.getChildren().add(new Label(
                    language.getFormatString("home.about.programDataVersion",
                            ZonedDateTimeConverter.format(
                                    updateCheckContext.programVersionService.getProgramDataVersion()))));

            box.getChildren().add(new Label(
                    language.getFormatString("home.about.buildDate", BuildInfo.date)));

            HBox sourceCodeBox = new HBox();
            sourceCodeBox.setAlignment(Pos.CENTER_LEFT);
            {
                sourceCodeBox.getChildren()
                        .add(new Label(language.getString("home.about.sourceCode")));

                Hyperlink hyperlink = new Hyperlink(sourceCodeUrl);
                hyperlink.setOnAction(event -> globalContext.webBrowserService.open(sourceCodeUrl));

                sourceCodeBox.getChildren().add(hyperlink);
            }
            box.getChildren().add(sourceCodeBox);
        }

        getDialogPane().setContent(box);
    }
}
