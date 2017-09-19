package com.InsnCounter;

import jdk.internal.org.objectweb.asm.*;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InsnCounter {
    public static void main(String[] args) throws Exception {

        // init Counter
        InsnCounter self = new InsnCounter();
        self.initRecord();

        // Jar Input
        JarFile is = new JarFile(args[0]);
        self.parserJar(is);
        is.close();
    }

    public void initRecord() throws Exception {

        byte[] staticDemoByte;
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        cw.visit(52, Opcodes.ACC_SUPER + Opcodes.ACC_PUBLIC, "com/lvxiaoxin/staticDemo",
                null, "java/lang/Object", null);
        FieldVisitor fv = cw.visitField(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "count", "I", null, null);
        fv.visitEnd();

        MethodVisitor con = cw.visitMethod(0, "<init>", "()V", null, null);
        con.visitCode();
        con.visitVarInsn(Opcodes.ALOAD, 0);
        con.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        con.visitInsn(Opcodes.RETURN);
        con.visitMaxs(1, 1);
        con.visitEnd();

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "callme", "()V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("count: ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/lvxiaoxin/staticDemo", "count", "I");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(3, 0);
        mv.visitEnd();

        cw.visitEnd();

        staticDemoByte = cw.toByteArray();
        File demoPath = new File("com/lvxiaoxin/");
        if (!demoPath.exists()) {
            demoPath.mkdirs();
        }
        FileOutputStream staFile = new FileOutputStream("com/lvxiaoxin/staticDemo.class");
        staFile.write(staticDemoByte);
        staFile.close();
    }


    public void parserJar(JarFile jarFile) throws Exception {

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {

            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.endsWith(".class")) {
                if (entryName.equals("com/lvxiaoxin/staticDemo.class")) {
                    continue;
                }
                InputStream classFile = jarFile.getInputStream(entry);
                try {
                    byte[] b;
                    ClassReader cr = new ClassReader(classFile);

                    ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {

                        @Override
                        protected String getCommonSuperClass(String var1, String var2) {
                            return "java/lang/Object";
                        }
                    };

                    ClassVisitor cv = new ClassAdapter(cw2);
                    cr.accept(cv, 0);
                    b = cw2.toByteArray();

                    System.out.println(entryName);
                    File cur = new File(entryName);
                    if (cur.getParent() != null) {
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
