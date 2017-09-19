package com.InsnCounter;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.ListIterator;

class MethodAdapter extends MethodNode {
    public MethodAdapter(int access, String name, String desc,
                         String signature, String[] exceptions, MethodVisitor mv) {
        super(Opcodes.ASM5, access, name, desc, signature, exceptions);
        this.mv = mv;
    }

    @Override
    public void visitEnd() {
        ListIterator<AbstractInsnNode> itr = instructions.iterator();

        // core insert
        while (itr.hasNext()) {

            AbstractInsnNode node = itr.next();

            InsnList numCounting = new InsnList();

            // insert count++
            numCounting.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/lvxiaoxin/staticDemo", "count", "I"));
            numCounting.add(new InsnNode(Opcodes.ICONST_1));
            numCounting.add(new InsnNode(Opcodes.IADD));
            numCounting.add(new FieldInsnNode(Opcodes.PUTSTATIC, "com/lvxiaoxin/staticDemo", "count", "I"));

            // insert the callme function
            numCounting.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/lvxiaoxin/staticDemo", "callme", "()V", false));

            instructions.insert(node, numCounting);
            maxStack = Math.max(5, maxStack);
        }

        accept(mv);
    }
}


