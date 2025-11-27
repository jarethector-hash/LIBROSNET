package Proyecto_Final;

import java.util.Stack;

public class PilaAcciones {
    private Stack<Accion> stack;

    public PilaAcciones() {
        stack = new Stack<>();
    }

    public void push(Accion a) {
        stack.push(a);
    }

    public Accion pop() {
        if (stack.isEmpty()) return null;
        return stack.pop();
    }

    public boolean vacia() {
        return stack.isEmpty();
    }
}
