package org.dimdev.jeid;

import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ASMException extends RuntimeException {
    private static final long serialVersionUID = -8581611883691404427L;

    public ASMException(String message) {
        super("JustEnoughIDs - Class transformation error\n"+message);
    }

    public ASMException(String message, ClassNode node) {
        this(message+"\n"+getStringDescriptor(node));
    }

    public ASMException(String message, MethodNode node) {
        this(message+"\n"+getStringDescriptor(node));
    }

    private static String getStringDescriptor(MethodNode node) {
        StringBuilder sb = new StringBuilder();
        Iterator<AbstractInsnNode> i = node.instructions.iterator();
        while (i.hasNext()) {
            AbstractInsnNode n = i.next();
            sb.append("["+n.getOpcode()+"] - "+getInsnDesc(n)+"\n");
        }
        return sb.toString();
    }

    private static String getInsnDesc(AbstractInsnNode n) {
        if (n instanceof LdcInsnNode) {
            return "LDC - "+((LdcInsnNode)n).cst.toString();
        }
        if (n instanceof LabelNode) {
            return "Label - "+((LabelNode)n).getLabel().toString();
        }
        if (n instanceof LineNumberNode) {
            return "Line - "+((LineNumberNode)n).line;
        }
        if (n instanceof IntInsnNode) {
            return "Int - "+((IntInsnNode)n).operand;
        }
        if (n instanceof MethodInsnNode) {
            MethodInsnNode m = (MethodInsnNode)n;
            return "Method - "+m.name+", "+m.desc+", "+m.owner;
        }
        return n.getClass().getName();
    }

    private static String getStringDescriptor(ClassNode node) {
        StringBuilder sb = new StringBuilder();
        node.methods.forEach(m -> sb.append(m.name+": "+m.desc+"\n"));
        return sb.toString();
    }
}
