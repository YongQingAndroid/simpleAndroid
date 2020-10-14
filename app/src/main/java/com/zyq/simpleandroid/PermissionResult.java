package com.zyq.simpleandroid;


public class PermissionResult {
  public  State state;

    PermissionResult(String[] allow, String[] disAllow) {
        if (disAllow == null || disAllow.length == 0) {
            state = State.DONE;
        } else {
            state = State.FAIL;
        }
        state.setFail(disAllow);
        state.setPermission(allow);
    }
}
