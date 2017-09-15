import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.AbstractList;
import java.util.ListIterator;

public class InsCounter {
    public static void main(final String args[]) throws Exception {
        FileInputStream is = new FileInputStream(args[0]);
        byte[] b;

        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassAdapter(cw);
        cr.accept(cv, 0);
        b = cw.toByteArray();

        FileOutputStream fos = new FileOutputStream(args[1]);
        fos.write(b);
        fos.close();
    }
}

class ClassAdapter extends ClassVisitor implements Opcodes {
    public ClassAdapter(final ClassVisitor cv) {
        super(ASM5, cv);
    }
//
//    @Override
//    public FieldVisitor(int access, String name, String desc, String signature, Object value) {
//        return new FieldAdapter(access, name, desc, signature, value);
//    }
//
//
//    @Override
//    public void visitOuterClass(String owner, String name, String desc) {
//        if (this.cv != null) {
//            this.cv.visitOuterClass(owner, name, desc);
//        }
//    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
//        if(name.equals("main"))
//            return new MethodAdapter(access, name, desc, signature, exceptions, mv);
//        else
//            return mv;
        return new MethodAdapter(access, name, desc, signature, exceptions, mv);
    }
}

class MethodAdapter extends MethodNode {
    public MethodAdapter(int access, String name, String desc,
                         String signature, String[] exceptions, MethodVisitor mv) {
        super(Opcodes.ASM5, access, name, desc, signature, exceptions);
        this.mv = mv;
    }
//
//    @Override
//    public void visitCode() {
//    }

    @Override
    public void visitEnd() {
        ListIterator<AbstractInsnNode> itr = instructions.iterator();

        System.out.println("There are " + instructions.size() + "instructions inside the method.");

        AbstractInsnNode First = instructions.getFirst();
        InsnList definCount = new InsnList();
        definCount.add(new InsnNode(Opcodes.ICONST_0));
        definCount.add(new VarInsnNode(Opcodes.ISTORE, 1));
        instructions.insertBefore(First, definCount);
        maxStack = Math.max(2, maxStack);

        while (itr.hasNext()) {
//            System.out.println("One Instruction " + itr.toString());
            AbstractInsnNode node = itr.next();
//            System.out.println(node.toString());
//            if(node.getOpcode() == Opcodes.INVOKEVIRTUAL)
//            {
//                MethodInsnNode next = (MethodInsnNode)node;
//                next.name
//            }
            InsnList list = new InsnList();
            list.add(new IincInsnNode(1, 1));
            instructions.insert(node, list);
            maxStack = Math.max(2, maxStack);
        }

        AbstractInsnNode Last = instructions.getLast();
        InsnList printCount = new InsnList();
        printCount.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        printCount.add(new VarInsnNode(Opcodes.ILOAD, 1));
        printCount.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false));
        instructions.insert(Last, printCount);
        maxStack = Math.max(2, maxStack);

        accept(mv);
    }
}
