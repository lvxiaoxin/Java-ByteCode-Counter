import com.sun.org.apache.bcel.internal.generic.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.LocalVariablesSorter;
import jdk.internal.org.objectweb.asm.tree.*;
import sun.misc.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.AbstractList;
import java.util.ListIterator;

public class InsCounter {
    public static void main(final String args[]) throws Exception {
        FileInputStream is = new FileInputStream(args[0]);
        byte[] b;
        byte[] staticDemoByte;

        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        cw.visit(52, Opcodes.ACC_SUPER, "staticDemo",
                null, "java/lang/Object", null);
        FieldVisitor fv = cw.visitField(Opcodes.ACC_STATIC, "count", "I", null, null);
        fv.visitEnd();

        MethodVisitor con = cw.visitMethod(0, "<init>", "()V", null, null);
        con.visitCode();
        con.visitVarInsn(Opcodes.ALOAD, 0);
        con.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        con.visitInsn(Opcodes.RETURN);
        con.visitMaxs(1, 1);
        con.visitEnd();

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_STATIC, "callme", "()V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("count: ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "staticDemo", "count", "I");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(3, 0);
        mv.visitEnd();

        cw.visitEnd();

        staticDemoByte = cw.toByteArray();
        FileOutputStream staFile = new FileOutputStream(args[2]);
        staFile.write(staticDemoByte);
        staFile.close();

        ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new ClassAdapter(cw2);
        cr.accept(cv, 0);
        b = cw2.toByteArray();

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

    @Override
    public void visitEnd() {
        ListIterator<AbstractInsnNode> itr = instructions.iterator();

//        System.out.println("There are " + instructions.size() + " instructions inside the method.");

        while (itr.hasNext()) {

            AbstractInsnNode node = itr.next();

            InsnList numCounting = new InsnList();
            numCounting.add(new FieldInsnNode(Opcodes.GETSTATIC, "staticDemo", "count", "I"));
            numCounting.add(new InsnNode(Opcodes.ICONST_1));
            numCounting.add(new InsnNode(Opcodes.IADD));
            numCounting.add(new FieldInsnNode(Opcodes.PUTSTATIC, "staticDemo", "count", "I"));
            numCounting.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "staticDemo", "callme", "()V", false));
            instructions.insert(node, numCounting);
            maxStack = Math.max(5, maxStack);
        }

        accept(mv);
    }
}
