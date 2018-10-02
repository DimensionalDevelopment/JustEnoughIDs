package org.dimdev.jeid;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * This class was borrowed from Zabi94's MaxPotionIDExtender
 * until we can figure out the issue with two of our Mixin
 * classes with mappings. All credit in this class goes to Zabi
 * and his incredible work on figuring out how to make this work.
 *
 * https://github.com/zabi94/MaxPotionIDExtender
 */
public class PotionTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.network.play.server.SPacketEntityEffect")) {
            return transformSPacketEntityEffect(basicClass);
        }
        if (transformedName.equals("net.minecraft.network.play.server.SPacketRemoveEntityEffect")) {
            return transformSPacketRemoveEntityEffect(basicClass);
        }
        if (transformedName.equals("net.minecraft.nbt.NBTTagCompound")) {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            if (!cn.name.equals(Obf.NBTTagCompound)) {
                throw new RuntimeException("The class NBTTagCompound has broken mappings, should be "+cn.name);
            }
        }
        if (transformedName.equals("net.minecraft.network.PacketBuffer")) {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            if (!cn.name.equals(Obf.PacketBuffer)) {
                throw new RuntimeException("The class PacketBuffer has broken mappings, should be "+cn.name);
            }
        }
        return basicClass;
    }

    private static MethodNode locateMethod(ClassNode cn, String desc, String nameIn, String deobfNameIn) {
        return cn.methods.parallelStream()
                .filter(n -> n.desc.equals(desc) && (n.name.equals(nameIn) || n.name.equals(deobfNameIn)))
                .findAny().orElseThrow(() -> new RuntimeException((nameIn +" ("+deobfNameIn+"): "+desc+" cannot be found in "+cn.name)));
    }

    private static AbstractInsnNode locateTargetInsn(MethodNode mn, Predicate<AbstractInsnNode> filter) {
        AbstractInsnNode target = null;
        Iterator<AbstractInsnNode> i = mn.instructions.iterator();
        while (i.hasNext() && target == null) {
            AbstractInsnNode n = i.next();
            if (filter.test(n)) {
                target = n;
            }
        }
        if (target==null) {
            //throw new ASMException("Can't locate target instruction in "+mn.name, mn);
        }
        return target;
    }

    private byte[] transformSPacketRemoveEntityEffect(byte[] basicClass) {
        //Log.i("Patching SPacketRemoveEntityEffect");
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        String descriptors = "(L"+Obf.PacketBuffer+";)V";
        MethodNode rpd = locateMethod(cn, descriptors, "readPacketData", "a");
        AbstractInsnNode target = locateTargetInsn(rpd, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode)n).name.equals("readUnsignedByte"));
        rpd.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Obf.PacketBuffer, "readInt", "()I", false));
        rpd.instructions.remove(target);

        MethodNode wpd = locateMethod(cn, descriptors, "writePacketData", "b");
        target = locateTargetInsn(wpd, n -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode)n).name.equals("writeByte"));
        wpd.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Obf.PacketBuffer, "writeInt", "(I)Lio/netty/buffer/ByteBuf;", false));
        wpd.instructions.remove(target);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        //Log.i("Patch Successful");
        return cw.toByteArray();
    }

    private byte[] transformSPacketEntityEffect(byte[] basicClass) {
        //Log.i("Patching SPacketEntityEffect");
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        if (!Obf.SPacketEntityEffect.equals(cn.name)) {
            //throw new ASMException("Mapping mismatch! SPacketEntityEffect is "+cn.name+", not "+Obf.SPacketEntityEffect);
        }

        //Adding a new field, int effectInt
        cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "effectInt", "I", null, 0));

        //Initialize this field in the constructor
        MethodNode mn_init = locateMethod(cn, "(IL"+Obf.PotionEffect+";)V", "<init>", "<init>");
        Iterator<AbstractInsnNode> i = mn_init.instructions.iterator();
        AbstractInsnNode targetNode = null;
        int line = 0;
        while (i.hasNext() && targetNode == null) {
            AbstractInsnNode node = i.next();
            if (node instanceof LineNumberNode) {
                if (line == 1) {
                    targetNode = node;
                }
                line++;
            }
        }

        if (targetNode == null) {
            //throw new ASMException("Can't find target node for SPacketEntityEffect constructor");
        }

        //These are reversed, they get pushed down the stack
        mn_init.instructions.insert(targetNode, new FieldInsnNode(Opcodes.PUTFIELD, Obf.SPacketEntityEffect, "effectInt", "I"));
        mn_init.instructions.insert(targetNode, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(this.getClass()), "getIdFromPotEffect", "(L"+Obf.PotionEffect+";)I", false));
        mn_init.instructions.insert(targetNode, new VarInsnNode(Opcodes.ALOAD, 2));
        mn_init.instructions.insert(targetNode, new VarInsnNode(Opcodes.ALOAD, 0));

        MethodNode mn_empty_init = locateMethod(cn, "()V", "<init>", "<init>");

        AbstractInsnNode tgt = locateTargetInsn(mn_empty_init, n -> n.getOpcode()==Opcodes.RETURN);
        mn_empty_init.instructions.insertBefore(tgt, new VarInsnNode(Opcodes.ALOAD, 0));
        mn_empty_init.instructions.insertBefore(tgt, new LdcInsnNode(0));
        mn_empty_init.instructions.insertBefore(tgt, new FieldInsnNode(Opcodes.PUTFIELD, Obf.SPacketEntityEffect, "effectInt", "I"));

        //Patch readPacketData
        MethodNode mn_readPacket = locateMethod(cn, "(L"+Obf.PacketBuffer+";)V", "readPacketData", "a");
        String readVarInt_name = (Obf.isDeobf()?"readVarInt":"g");

        AbstractInsnNode target = locateTargetInsn(mn_readPacket, n -> n.getOpcode()==Opcodes.RETURN).getPrevious().getPrevious();
        mn_readPacket.instructions.insertBefore(target, new VarInsnNode(Opcodes.ALOAD, 0));
        mn_readPacket.instructions.insertBefore(target, new VarInsnNode(Opcodes.ALOAD, 1));
        mn_readPacket.instructions.insertBefore(target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Obf.PacketBuffer, readVarInt_name, "()I", false));
        mn_readPacket.instructions.insertBefore(target, new FieldInsnNode(Opcodes.PUTFIELD, Obf.SPacketEntityEffect, "effectInt", "I"));

        //Patch writePacketData
        MethodNode mn_writePacket = locateMethod(cn, "(L"+Obf.PacketBuffer+";)V", "writePacketData", "b");
        String writeVarInt_name = (Obf.isDeobf()?"writeVarInt":"d");
        AbstractInsnNode wp_target = locateTargetInsn(mn_writePacket, n -> n.getOpcode()==Opcodes.RETURN).getPrevious().getPrevious();

        mn_writePacket.instructions.insertBefore(wp_target, new VarInsnNode(Opcodes.ALOAD, 1));
        mn_writePacket.instructions.insertBefore(wp_target, new VarInsnNode(Opcodes.ALOAD, 0));
        mn_writePacket.instructions.insertBefore(wp_target, new FieldInsnNode(Opcodes.GETFIELD, Obf.SPacketEntityEffect, "effectInt", "I"));
        mn_writePacket.instructions.insertBefore(wp_target, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Obf.PacketBuffer, writeVarInt_name, "(I)L"+Obf.PacketBuffer+";", false));
        mn_writePacket.instructions.insertBefore(wp_target, new InsnNode(Opcodes.POP));

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        //Log.i("Patch Successful");
        return cw.toByteArray();
    }

    public int getIdFromPotEffect(PotionEffect pe) {
        return Potion.getIdFromPotion(pe.getPotion());
    }

}

class Obf {


    public static boolean isPotionClass(String s) {
        if (s.endsWith(";")) {
            s = s.substring(1, s.length()-1);
        }
        return s.equals(Type.getInternalName(Potion.class)) || s.equals("uz");
    }

    public static boolean isDeobf() {
        return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    public static void loadData() {
        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            NBTTagCompound = "net/minecraft/nbt/NBTTagCompound";
            PotionEffect ="net/minecraft/potion/PotionEffect";
            SPacketEntityEffect = "net/minecraft/network/play/server/SPacketEntityEffect";
            PacketBuffer = "net/minecraft/network/PacketBuffer";
        } else {
            NBTTagCompound = "fy";
            PotionEffect = "va";
            SPacketEntityEffect = "kw";
            PacketBuffer = "gy";
        }
    }

    public static String NBTTagCompound;
    public static String PotionEffect;
    public static String SPacketEntityEffect;
    public static String PacketBuffer;
}
