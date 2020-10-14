package com.zyq.logic;

import com.zyq.logic.core.LogicInterFace;

public class Test {
    public static void main(String[] args) {
        new LogicMain<String>()
                .addCover(new CoverImp())
                .addCover(new CoverImp())
                .execute();
    }

    public static class CoverImp implements LogicInterFace<String> {

        @Override
        public boolean cover(LogicMain<String> obj) {
            return obj.addCover(new CoverImp1()).execute();
        }
    }

    public static class CoverImp1 implements LogicInterFace<String> {

        @Override
        public boolean cover(LogicMain<String> obj) {
            return true;
        }
    }
}
