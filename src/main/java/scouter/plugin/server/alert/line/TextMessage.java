package scouter.plugin.server.alert.line;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 12. 20.
 */
public class TextMessage {
    private String content;

    public TextMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
