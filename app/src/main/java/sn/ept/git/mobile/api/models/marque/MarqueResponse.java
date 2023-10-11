package sn.ept.git.mobile.api.models.marque;

public class MarqueResponse {
    private Marque marque;
    private String msg;

    public MarqueResponse() { }

    public MarqueResponse(Marque marque) { this.marque = marque; }

    public MarqueResponse(Marque marque, String msg) {
        this.marque = marque;
        this.msg = msg;
    }

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
    }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }
}
