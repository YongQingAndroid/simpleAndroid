package com.zyq.jsimleplepicker.timePicker;

public enum FormatState {
        YYYY(-1),
        MM(-1),
        DD(-1),
        HH(-1),
        mm(-1),
        W(-1),
        CC(0);//自定义
       public int point;
       public int jump=1;
       public FormatState setJump(int jump){
           this.jump=jump;
           return this;
       }
        FormatState(int point) {
            this.point = point;
        }
    }