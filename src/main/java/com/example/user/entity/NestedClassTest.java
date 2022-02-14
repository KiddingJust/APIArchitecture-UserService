package com.example.user.entity;

public class NestedClassTest {

    private int outerVar;

    static class Nest1 {
        private int nest1Var;
    }

    class Nest2 {
        private int nest2Var;

        public int add() {

            return outerVar + nest2Var;
        }
}
