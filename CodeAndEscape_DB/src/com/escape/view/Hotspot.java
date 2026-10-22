package com.escape.view;

import java.awt.*;

public class Hotspot {
    public enum Action { EXAMINE, PICKUP, INTERACT, ROOM_SWITCH }

    public Rectangle bounds;
    public Action action;
    public String id;
    public String target;
    public Runnable onClick;

    public Hotspot(String id, int x, int y, int w, int h, Action action, String target, Runnable onClick) {
        this.id = id;
        this.bounds = new Rectangle(x, y, w, h);
        this.action = action;
        this.target = target;
        this.onClick = onClick;
    }
}