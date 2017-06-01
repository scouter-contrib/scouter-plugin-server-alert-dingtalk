package scouter.plugin.server.alert.line;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 12. 20.
 */
public class DingtalkPushFormat {
    private String msgtype = "text";
    private TextMessage text;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextMessage getText() {
        return text;
    }

    public void setText(TextMessage text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = new TextMessage(text);
    }
}
