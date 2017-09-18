import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InsnCounter {
    public static void main(final String[] args) throws Exception {
        InsnCounter self = new InsnCounter();
        JarFile is = new JarFile("Hello.jar");
        self.parserJar(is);
    }

    public void parserJar(JarFile jarFile) throws Exception {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if(entryName.endsWith(".class")) {
//
//                ClassNode classNode = new ClassNode();
//
//                InputStream classFileInputStream = jarFile.getInputStream(entry);
//                try {
//                    ClassReader classReader = new ClassReader(classFileInputStream);
//                    classReader.accept(classNode, 0);
//                } finally {
//                    classFileInputStream.close();
//                }
//                System.out.println("access");
//

                InputStream classFile = jarFile.getInputStream(entry);
                try {
                    byte[] b;
                    byte[] staticDemoByte;
                    ClassReader cr = new ClassReader(classFile);
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
                    FileOutputStream staFile = new FileOutputStream("staticDemo.class");
                    staFile.write(staticDemoByte);
                    staFile.close();

                    ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

                    ClassVisitor cv = new ClassAdapter(cw2);
                    cr.accept(cv, 0);
                    b = cw2.toByteArray();

                    System.out.println(entryName);
                    File cur = new File(entryName);
//                    System.out.println(cur.getParent());
//                    System.out.println(cur.getName());
                    if(cur.getParent() != null) {
                        if (!cur.getParentFile().exists()) {
                            cur.getParentFile().mkdirs();
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(entryName);
                    fos.write(b);
                    fos.close();
                } finally {
                    classFile.close();
                }
            }
        }
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
