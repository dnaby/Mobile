package sn.ept.git.mobile.api.models.location;

public class LocationResponse {
    private String msg;
    private Location location;

    public LocationResponse() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
