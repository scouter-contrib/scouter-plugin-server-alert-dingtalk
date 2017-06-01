package scouter.plugin.server.alert.dingtalk;

/**
 * @author Gun Lee (gunlee01@gmail.com)
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
