package com.github.johypark97.varchivemacro.macro.gui.view.components;

import java.awt.Desktop;
import java.io.IOException;
import java.io.Serial;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class UrlLabel extends JEditorPane {
    @Serial
    private static final long serialVersionUID = -4878841348713489761L;

    public UrlLabel() {
        setEditable(false);
        setOpaque(false);

        addHyperlinkListener(e -> {
            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
                return;
            }

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException ignored) {
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        HTMLEditorKit kit = new HTMLEditorKit();
        setEditorKit(kit);

        StyleSheet ssh = kit.getStyleSheet();
        ssh.addRule("a { color: black; font: 1em sans-serif; text-decoration: underline; }");
    }

    public void setUrl(String url) {
        setUrl(url, url);
    }

    public void setUrl(String url, String text) {
        setText("<a href=\"" + url + "\">" + text + "</a>");
    }
}
