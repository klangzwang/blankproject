package geni.withercore.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import codechicken.lib.math.MathHelper;
import geni.withercore.math.Vec3D;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public class Utils {

    public static final String SELECT = "\u00A7";
    private static DecimalFormat energyValue = new DecimalFormat("###,###,###,###,###", DecimalFormatSymbols.getInstance(Locale.ROOT));
    
    private Utils() 
    {
    }
    
    @Deprecated
    public static String formatNumber(double value) {
        if (Math.abs(value) < 1000D) return String.valueOf(value);
        else if (Math.abs(value) < 1000000D) return addCommas((int) value); //I mean whats the ploint of displaying 1.235K instead of 1,235?
        else if (Math.abs(value) < 1000000000D) return String.valueOf(Math.round(value / 1000D) / 1000D) + "M";
        else if (Math.abs(value) < 1000000000000D) return String.valueOf(Math.round(value / 1000000D) / 1000D) + "B";
        else return String.valueOf(Math.round(value / 1000000000D) / 1000D) + "T";
    }

    @Deprecated
    public static String formatNumber(long value) {
        if (value == Long.MIN_VALUE) value = Long.MAX_VALUE;
        if (Math.abs(value) < 1000L) return String.valueOf(value);
        else if (Math.abs(value) < 1000000L) return Utils.addCommas(value); //I mean whats the ploint of displaying 1.235K instead of 1,235?
        else if (Math.abs(value) < 1000000000L) return String.valueOf(Math.round(value / 100000L) / 10D) + "M";
        else if (Math.abs(value) < 1000000000000L) return String.valueOf(Math.round(value / 100000000L) / 10D) + "G";
        else if (Math.abs(value) < 1000000000000000L) return String.valueOf(Math.round(value / 1000000000L) / 1000D) + "T";
        else if (Math.abs(value) < 1000000000000000000L) return String.valueOf(Math.round(value / 1000000000000L) / 1000D) + "P";
        else if (Math.abs(value) <= Long.MAX_VALUE) return String.valueOf(Math.round(value / 1000000000000000L) / 1000D) + "E";
        else return "Something is very broken!!!!";
    }

    /**
     * Add commas to a number e.g. 161253126 > 161,253,126
     */
    public static String addCommas(int value) {
        return energyValue.format(value);
    }

    /**
     * Add commas to a number e.g. 161253126 > 161,253,126
     */
    public static String addCommas(long value) {
        return energyValue.format(value);
    }

    /**
     * Calculates the exact distance between two points in 3D space
     *
     * @param x1 point A x
     * @param y1 point A y
     * @param z1 point A z
     * @param x2 point B x
     * @param y2 point B y
     * @param z2 point B z
     * @return The distance between point A and point B
     */
    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }

    @Deprecated
    public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2) {
        return getDistance(x1, y1, z1, x2, y2, z2);
    }

    @Deprecated
    public static double getDistanceAtoB(Vec3D pos1, Vec3D pos2) {
        return getDistance(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

    public static double getDistance(Vec3D pos1, Vec3D pos2) {
        return getDistance(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

    public static int getCardinalDistance(BlockPos pos1, BlockPos pos2) {
        int x = Math.abs(pos2.getX() - pos1.getX());
        int y = Math.abs(pos2.getY() - pos1.getY());
        int z = Math.abs(pos2.getZ() - pos1.getZ());
        return Math.max(Math.max(x, y), z);
    }

    public static boolean inRangeSphere(BlockPos posA, BlockPos posB, int range) {
        if (Math.abs(posA.getX() - posB.getX()) > range || Math.abs(posA.getY() - posB.getY()) > range || Math.abs(posA.getZ() - posB.getZ()) > range) {
            return false;
        } else
            return getDistanceSq(posA.getX(), posA.getY(), posA.getZ(), posB.getX(), posB.getY(), posB.getZ()) <= range * range;
    }


    @Deprecated
    public static double getDistanceAtoB(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dz * dz));
    }

    /**
     * Calculates the exact distance between two points in 2D space
     *
     * @param x1 point A x
     * @param z1 point A z
     * @param x2 point B x
     * @param z2 point B z
     * @return The distance between point A and point B
     */
    public static double getDistance(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dz * dz));
    }

    public static double getDistanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    public static double getDistanceSq(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return dx * dx + dz * dz;
    }

    public static int determineOrientation(int x, int y, int z, LivingEntity entity) {
        if (Mth.abs((float) entity.getX() - (float) x) < 2.0F && Mth.abs((float) entity.getZ() - (float) z) < 2.0F) {
            double d0 = entity.getY() + 1.82D - (double) entity.getVisualRotationYInDegrees();

            if (d0 - (double) y > 2.0D) return 0;

            if ((double) y - d0 > 0.0D) return 1;
        }

        int l = Mth.floor((double) (entity.getYRot() * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
    }

    public static int toInt(double d) {
        return (int) d;
    }

    public static int parseInt(String s) {
        return parseInt(s, true);
    }

    public static int parseInt(String s, boolean catchException) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            if (catchException) {
                return 0;
            }
            throw e;
        }
    }

    public static double parseDouble(String s, boolean catchException) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            if (catchException) {
                return 0;
            }
            throw e;
        }
    }

    public static double parseDouble(String s) {
        return parseDouble(s, true);
    }

    public static int parseHex(String s) {
        return parseHex(s, true);
    }

    public static int parseHex(String s, boolean catchException) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        if (catchException) {
            try {
                return (int) Long.parseLong(s, 16);
            } catch (Exception e) {
                return 0;
            }
        } else {
            return (int) Long.parseLong(s, 16);
        }
    }

    public static boolean validInteger(String value) {
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean validDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Nullable
    public static Player getClosestPlayer(Level world, double posX, double posY, double posZ, double distance) {
        return getClosestPlayer(world, posX, posY, posZ, distance, true);
    }

    @Nullable
    public static Player getClosestPlayer(Level world, double posX, double posY, double posZ, double distance, boolean includeCreative) {
        return getClosestPlayer(world, posX, posY, posZ, distance, includeCreative, false);
    }


    @Nullable
    @Deprecated //Check the world method
    public static Player getClosestPlayer(Level world, double posX, double posY, double posZ, double distance, boolean includeCreative, boolean includeSpectators) {
        double d0 = -1.0D;
        Player closestPlayer = null;

        for (int i = 0; i < world.players().size(); ++i) {
            Player player = world.players().get(i);

            if ((!player.isCreative() || includeCreative) && (!player.isSpectator() || includeSpectators)) {
                double d1 = player.distanceToSqr(posX, posY, posZ);

                if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0)) {
                    d0 = d1;
                    closestPlayer = player;
                }
            }
        }

        return closestPlayer;
    }

    /**
     * This is mostly just here as a reminder to myself.<br>
     * This check is the reverse of the usual object instance of TargetClass check so i always get it backwards.<br>
     * This is more like TargetClass instanceof ObjectClass.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean checkClassInstanceOf(Class clazz, Class instanceOfThis) {
        if (clazz == null || instanceOfThis == null) {
            return clazz == null && instanceOfThis == null;
        }
        return instanceOfThis.isAssignableFrom(clazz);
    }

    public static String trimString(String input, int length, String trimExtension) {
        if (input.length() <= length) {
            return input;
        } else {
            return input.substring(0, length) + trimExtension;
        }
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception var1) {}

        return "";
    }

    public static void setClipboardString(String copyText) {
        if (!StringUtils.isEmpty(copyText)) {
            try {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
    }

    public static void openWebLink(URI url) {
        Util.getPlatform().openUri(url);
    }

    public static int parseColourRGB(String value) {
        if (value.startsWith("0x") || value.startsWith("#")) {
            value = value.replace("0x", "").replace("#", "");
            return parseHex(value, false);
        } else if (value.contains(",")) {
            String[] vals = value.split(",");
            if (vals.length != 3)
                throw new NumberFormatException("Number must be a hex using the format 0xRRGGBB or #RRGGBB");
            int r = vals[0].contains(".") ? (int) (Double.parseDouble(vals[0]) * 255) : Integer.parseInt(vals[0]);
            int g = vals[1].contains(".") ? (int) (Double.parseDouble(vals[1]) * 255) : Integer.parseInt(vals[1]);
            int b = vals[2].contains(".") ? (int) (Double.parseDouble(vals[2]) * 255) : Integer.parseInt(vals[2]);
            return r << 16 | g << 8 | b;
        } else {
            throw new NumberFormatException("Number must be a hex using the format 0xRRGGBB or #RRGGBB");
        }
    }

    public static int parseColourARGB(String value) {
        if (value.startsWith("0x") || value.startsWith("#")) {
            value = value.replace("0x", "").replace("#", "");
            return parseHex(value, false);
        } else if (value.contains(",")) {
            String[] vals = value.split(",");
            if (vals.length < 3 || vals.length > 4) {
                throw new NumberFormatException("Number must be a hex using the format 0xAARRGGBB or #AARRGGBB");
            }
            int r = vals[0].contains(".") ? (int) (Double.parseDouble(vals[0]) * 255) : Integer.parseInt(vals[0]);
            int g = vals[1].contains(".") ? (int) (Double.parseDouble(vals[1]) * 255) : Integer.parseInt(vals[1]);
            int b = vals[2].contains(".") ? (int) (Double.parseDouble(vals[2]) * 255) : Integer.parseInt(vals[2]);
            int a = vals.length == 4 ? vals[3].contains(".") ? (int) (Double.parseDouble(vals[3]) * 255) : Integer.parseInt(vals[3]) : 0xFF;
            return a << 24 | r << 16 | g << 8 | b;
        } else {
            throw new NumberFormatException("Number must be a hex using the format 0xRRGGBB or #RRGGBB");
        }
    }

    private static BiMap<Block, Fluid> fluidBlocks = null;

    public static Fluid lookupFluidForBlock(Block block)
    {
        if (fluidBlocks == null)
        {
            BiMap<Block, Fluid> tmp = HashBiMap.create();
            for (var entry : BuiltInRegistries.FLUID.entrySet())
            {
            	Fluid fluid = entry.getValue();
                Block fluidBlock = fluid.defaultFluidState().createLegacyBlock().getBlock();
                if (fluidBlock != Blocks.AIR)
                {
                    tmp.put(fluidBlock, fluid);
                }
            }
            fluidBlocks = tmp;
        }
        return fluidBlocks.get(block);
    }

    public static String getTextFormatString(String stringIn) {
        StringBuilder stringbuilder = new StringBuilder();
        int i = -1;
        int j = stringIn.length();

        while ((i = stringIn.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                ChatFormatting textformatting = ChatFormatting.getByCode(stringIn.charAt(i + 1));
                if (textformatting != null) {
                    if (!textformatting.isFormat()) {
                        stringbuilder.setLength(0);
                    }

                    if (textformatting != ChatFormatting.RESET) {
                        stringbuilder.append((Object) textformatting);
                    }
                }
            }
        }

        return stringbuilder.toString();
    }

    public static long safeAdd(long x, long y) {
        long r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            return Long.MAX_VALUE;
        }
        return r;
    }

    public static int scaleToTPS(Level world, int min, int max) {
        if (!(world instanceof ServerLevel)) return max;
        long[] times = world.getServer().getTickTime(world.dimension());
        if (times == null) return max;

        double worldTickTime = mean(times) * 0.000001;
        double worldTPS = Math.min(1000.0 / worldTickTime, 20);

        return codechicken.lib.math.MathHelper.clip((int) MathHelper.map(worldTPS, 5, 20, min, max), min, max);
    }

    public static long mean(long[] values) {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }

    public static void hollowCube(BlockPos min, BlockPos max, Consumer<BlockPos> callback) {
        hollowCube(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), callback);
    }

    public static void hollowCube(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax, Consumer<BlockPos> callback) {
        if (xmax - xmin < 2 || ymax - ymin < 2 || zmax - zmin < 2) {
            betweenClosed(xmin, ymin, zmin, xmax, ymax, zmax, callback);
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = xmin; x <= xmax; x++) {
            for (int y = ymin + 1; y <= ymax - 1; y++) {
                for (int z = zmin; z <= zmax; z += zmax - zmin) {
                    callback.accept(cursor.set(x, y, z));
                }
            }

            for (int z = zmin; z <= zmax; z++) {
                for (int y = ymin; y <= ymax; y += ymax - ymin) {
                    callback.accept(cursor.set(x, y, z));
                }
            }
        }


        for (int z = zmin + 1; z <= zmax - 1; z++) {
            for (int y = ymin + 1; y <= ymax - 1; y++) {
                for (int x = xmin; x <= xmax; x += xmax - xmin) {
                    callback.accept(cursor.set(x, y, z));
                }
            }
        }
    }

    public static void betweenClosed(BlockPos min, BlockPos max, Consumer<BlockPos> callback) {
        betweenClosed(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()), Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()), callback);
    }

    public static void betweenClosed(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax, Consumer<BlockPos> callback) {
        int width = xmax - xmin + 1;
        int height = ymax - ymin + 1;
        int depth = zmax - zmin + 1;
        int total = width * height * depth;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int index = 0; index < total; index++) {
            int x = index % width;
            int j1 = index / width;
            int y = j1 % height;
            int z = j1 / height;
            callback.accept(cursor.set(xmin + x, ymin + y, zmin + z));
        }
    }

    public static boolean isInRect(double x, double y, double xSize, double ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX < x + xSize) && (mouseY >= y && mouseY < y + ySize));
    }

    public static double distToRect(double x, double y, double xSize, double ySize, double mouseX, double mouseY) {
        if (isInRect(x, y, xSize, ySize, mouseX, mouseY)) {
            return 0;
        }

        //Inside Height (Simply distance from Left or Right)
        if (mouseY >= y && mouseY < y + ySize) {
            return mouseX >= x + xSize ? mouseX - (x + xSize) : x - mouseX;
        }

        //Inside Width (Simply distance from Top or Bottom)
        if (mouseX >= x && mouseX < x + xSize) {
            return mouseY < y ? y - mouseY : mouseY - (y + ySize);
        }

        //Distance from corner
        double cx = mouseX < x ? x : x + xSize;
        double cy = mouseY < y ? y : y + ySize;
        return Utils.getDistance(cx, cy, mouseX, mouseY);
    }

    public static MessageSignature uuidToSig(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.position(32);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return new MessageSignature(byteBuffer.array());
    }
    
    @SuppressWarnings("resource")
	public static Level getWorld()
    {
        return Minecraft.getInstance().level;
    }

    public static boolean inWorld()
    {
        return Minecraft.getInstance().getConnection() != null;
    }

    public static String getServerIP()
    {
        try
        {
            Connection networkManager = Minecraft.getInstance().getConnection().getConnection();
            String s = networkManager.getRemoteAddress().toString();
            s = s.substring(s.indexOf("/") + 1);
            return s;
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isModLoaded(String modid) {

        return ModList.get().isLoaded(modid);
    }

    public static boolean isClientWorld(Level world) {

        return world.isClientSide;
    }

    public static boolean isServerWorld(Level world) {

        return !world.isClientSide;
    }

    public static boolean isFakePlayer(Entity entity) {

        return entity instanceof FakePlayer;
    }

    public static boolean isCreativePlayer(Entity entity) {

        return entity instanceof Player player && player.isCreative();
    }

    public static EquipmentSlot handToEquipSlot(InteractionHand hand) {

        return hand.equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    public static InteractionHand otherHand(InteractionHand hand) {

        return hand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public static EquipmentSlot otherHand(EquipmentSlot slot) {

        return slot.equals(EquipmentSlot.MAINHAND) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
    }

    public static String createPrettyJSON(String jsonString) {

        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }

    public static boolean spawnLightningBolt(Level world, BlockPos pos) {

        return spawnLightningBolt(world, pos, null);
    }

    public static boolean spawnLightningBolt(Level world, BlockPos pos, Entity caster) {

        if (Utils.isServerWorld(world)) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
            bolt.moveTo(Vec3.atBottomCenterOf(pos));
            bolt.setCause(caster instanceof ServerPlayer player ? player : null);
            world.addFreshEntity(bolt);
        }
        return true;
    }

    public static void spawnXpOrbs(Level world, int xp, Vec3 pos) {

        if (world == null) {
            return;
        }
        while (xp > 0) {
            int orbAmount = ExperienceOrb.getExperienceValue(xp);
            xp -= orbAmount;
            world.addFreshEntity(new ExperienceOrb(world, pos.x, pos.y, pos.z, orbAmount));
        }
    }

    public static boolean destroyBlock(Level world, BlockPos pos, boolean dropBlock, @Nullable Entity entityIn) {

        BlockState state = world.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(world, pos) < 0 || (entityIn instanceof Player player && state.getDestroyProgress(player, world, pos) < 0)) {
            return false;
        } else {
            FluidState ifluidstate = world.getFluidState(pos);
            if (dropBlock) {
                BlockEntity tileentity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                Block.dropResources(state, world, pos, tileentity, entityIn, ItemStack.EMPTY);
            }
            return world.setBlock(pos, ifluidstate.createLegacyBlock(), 3);
        }
    }

    @Nullable
    public static String getKeynameFromKeycode(int code) {

        return GLFW.glfwGetKeyName(code, -1);
    }

    @Nullable
    public static String getKeyNameFromScanCode(int code) {

        return GLFW.glfwGetKeyName(-1, code);
    }

    public static boolean hurt(Entity target, DamageSource source, float amount, int invuln) {

        if (target instanceof PartEntity<?> part) {
            target = part.getParent();
        }
        int time = target.invulnerableTime;
        boolean hurt = target.hurt(source, amount);
        target.invulnerableTime = Math.max(time, invuln);
        return hurt;
    }

    public static DamageSource source(DamageSources sources, ResourceKey<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, Vec3 location) {

        return new DamageSource(sources.damageTypes.getHolderOrThrow(type), directEntity, causingEntity, location);
    }

    public static Item.Properties itemProperties() {

        return new Item.Properties();
    }

    // region TIME CHECKS
    public static final int TIME_CONSTANT = 40;
    public static final int TIME_CONSTANT_HALF = TIME_CONSTANT / 2;
    public static final int TIME_CONSTANT_QUARTER = TIME_CONSTANT / 4;

    private static int timeConstant = 0;
    private static int timeConstantHalf = 0;
    private static int timeConstantQuarter = 0;

    public static void tickTimeConstants() {

        if (++timeConstant >= TIME_CONSTANT) {
            timeConstant = 0;
        }
        if (++timeConstantHalf >= TIME_CONSTANT_HALF) {
            timeConstantHalf = 0;
        }
        if (++timeConstantQuarter >= TIME_CONSTANT_QUARTER) {
            timeConstantQuarter = 0;
        }
    }

    public static boolean timeCheck() {

        return timeConstant == 0;
    }

    public static boolean timeCheckHalf() {

        return timeConstantHalf == 0;
    }

    public static boolean timeCheckQuarter() {

        return timeConstantQuarter == 0;
    }
    // endregion

    // region PARTICLE UTILS
    public static void spawnBlockParticlesClient(Level world, ParticleOptions particle, BlockPos pos, RandomSource rand, int count) {

        for (int i = 0; i < count; ++i) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble();
            double d2 = (double) pos.getZ() + rand.nextDouble();
            double d3 = (rand.nextDouble() - 0.5D) * 0.5D;
            double d4 = (rand.nextDouble() - 0.5D) * 0.5D;
            double d5 = (rand.nextDouble() - 0.5D) * 0.5D;
            world.addParticle(particle, d0, d1, d2, d3, d4, d5);
        }
    }

    public static void spawnParticles(Level world, ParticleOptions particle, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {

        if (isServerWorld(world)) {
            ((ServerLevel) world).sendParticles(particle, posX, posY + 1.0D, posZ, particleCount, xOffset, yOffset, zOffset, speed);
        } else {
            world.addParticle(particle, posX + xOffset, posY + yOffset, posZ + zOffset, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void spawnParticles(Level level, ParticleOptions particle, int count, Vec3 pos, float posVar, Vec3 velocity, float velVar) {

        for (int i = 0; i < count; ++i) {
            spawnParticles(level, particle, pos, posVar, velocity, velVar);
        }
    }

    public static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, float posVar, Vec3 velocity, float velVar) {

        RandomSource rand = level.getRandom();
        spawnParticles(level, particle, pos.add(rand.nextFloat() * posVar * 2 - posVar, rand.nextFloat() * posVar * 2 - posVar, rand.nextFloat() * posVar * 2 - posVar),
                velocity.add(rand.nextFloat() * velVar * 2 - velVar, rand.nextFloat() * velVar * 2 - velVar, rand.nextFloat() * velVar * 2 - velVar));
    }

    public static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, Vec3 velocity) {

        level.addParticle(particle, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    }
    // endregion

    // region ENTITY UTILS
    public static boolean addToPlayerInventory(Player player, ItemStack stack) {

        if (stack.isEmpty() || player == null) {
            return false;
        }
        if (stack.getItem() instanceof ArmorItem armorItem) {
            int index = armorItem.getEquipmentSlot().getIndex();
            if (player.getInventory().armor.get(index).isEmpty()) {
                player.getInventory().armor.set(index, stack);
                return true;
            }
        }
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.items.size(); ++i) {
            if (inv.items.get(i).isEmpty()) {
                inv.items.set(i, stack.copy());
                return true;
            }
        }
        return false;
    }

    public static boolean dropItemStackIntoWorld(ItemStack stack, Level world, Vec3 pos) {

        return dropItemStackIntoWorld(stack, world, pos, false);
    }

    public static boolean dropItemStackIntoWorldWithRandomness(ItemStack stack, Level world, BlockPos pos) {

        return dropItemStackIntoWorld(stack, world, Vec3.atCenterOf(pos), true);
    }

    public static boolean dropItemStackIntoWorldWithRandomness(ItemStack stack, Level world, Vec3 pos) {

        return dropItemStackIntoWorld(stack, world, pos, true);
    }

    public static boolean dropItemStackIntoWorld(ItemStack stack, Level world, Vec3 pos, boolean velocity) {

        if (stack.isEmpty()) {
            return false;
        }
        float x2 = 0.5F;
        float y2 = 0.0F;
        float z2 = 0.5F;

        if (velocity) {
            x2 = world.random.nextFloat() * 0.8F + 0.1F;
            y2 = world.random.nextFloat() * 0.8F + 0.1F;
            z2 = world.random.nextFloat() * 0.8F + 0.1F;
        }
        ItemEntity entity = new ItemEntity(world, pos.x + x2, pos.y + y2, pos.z + z2, stack.copy());

        if (velocity) {
            entity.setDeltaMovement(world.random.nextGaussian() * 0.05F, world.random.nextGaussian() * 0.05F + 0.2F, world.random.nextGaussian() * 0.05F);
        } else {
            entity.setDeltaMovement(-0.05, 0, 0);
        }
        world.addFreshEntity(entity);

        return true;
    }

    public static boolean dropDismantleStackIntoWorld(ItemStack stack, Level world, BlockPos pos) {

        if (stack.isEmpty() || Utils.isClientWorld(world)) {
            return false;
        }
        float f = 0.3F;
        double x2 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
        double y2 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
        double z2 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
        ItemEntity dropEntity = new ItemEntity(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, stack);
        dropEntity.setPickUpDelay(10);
        world.addFreshEntity(dropEntity);

        return true;
    }

    public static boolean teleportEntityTo(Entity entity, BlockPos pos) {

        return teleportEntityTo(entity, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    }

    public static boolean teleportEntityTo(Entity entity, double x, double y, double z) {

        if (entity instanceof LivingEntity) {
            return teleportEntityTo((LivingEntity) entity, x, y, z);
        } else {
            entity.moveTo(x, y, z, entity.getYRot(), entity.getXRot());
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
        return true;
    }

    public static ResourceLocation getRegistryName(Block block)
    {

        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation getRegistryName(Item item)
    {

        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation getRegistryName(Fluid fluid)
    {

        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    public static ResourceLocation getRegistryName(EntityType<?> entity)
    {

        return BuiltInRegistries.ENTITY_TYPE.getKey(entity);
    }

    public static ResourceLocation getRegistryName(MobEffect effect)
    {

        return BuiltInRegistries.MOB_EFFECT.getKey(effect);
    }

    public static String getModId(Block block) {

        ResourceLocation loc = getRegistryName(block);
        return loc == null ? "" : loc.getNamespace();
    }

    public static String getName(Block block) {

        ResourceLocation loc = getRegistryName(block);
        return loc == null ? "" : loc.getPath();
    }

    public static String getModId(Item item) {

        ResourceLocation loc = getRegistryName(item);
        return loc == null ? "" : loc.getNamespace();
    }

    public static String getModId(ItemStack stack) {

        ResourceLocation loc = getRegistryName(stack.getItem());
        return loc == null ? "" : loc.getNamespace();
    }

    public static String getName(Item item) {

        ResourceLocation loc = getRegistryName(item);
        return loc == null ? "" : loc.getPath();
    }

    public static String getName(ItemStack stack) {

        ResourceLocation loc = getRegistryName(stack.getItem());
        return loc == null ? "" : loc.getPath();
    }

    public static String getModId(Fluid fluid) {

        ResourceLocation loc = getRegistryName(fluid);
        return loc == null ? "" : loc.getNamespace();
    }

    public static String getModId(FluidStack stack) {

        ResourceLocation loc = getRegistryName(stack.getFluid());
        return loc == null ? "" : loc.getNamespace();
    }

    public static String getName(Fluid fluid) {

        ResourceLocation loc = getRegistryName(fluid);
        return loc == null ? "" : loc.getPath();
    }

    public static String getName(FluidStack stack) {

        ResourceLocation loc = getRegistryName(stack.getFluid());
        return loc == null ? "" : loc.getPath();
    }
    
	@SuppressWarnings("resource")
	public static Vec3 getEyePosition(Player player)
	{
		Vec3 vec = new Vec3(player.getX(), player.getY(), player.getZ());
		if (player.level().isClientSide)
		{
			vec.add(0, player.getEyeHeight() - player.getEyeY(), 0);
		}
		else
		{
			vec.add(0, player.getEyeHeight(), 0);
			if (player instanceof AbstractClientPlayer && player.isCrouching())
			{
				vec.add(0, 0.08, 0);
			}
		}
		return vec;
	}
	@SuppressWarnings("resource")
	public static Vector3d getEyePositionWU(Player player)
	{
		Vector3d res = new Vector3d(player.getX(), player.getY(), player.getZ());
		if (player.level().isClientSide)
		{
			res.y += player.getEyeHeight() - player.getEyeY();
		}
		else
		{
			res.y += player.getEyeHeight();
			if (player instanceof AbstractClientPlayer && player.isCrouching())
			{
				res.y -= 0.08;
			}
		}
		return res;
	}
	public static Vector3d getLookVecWU(Player player)
	{
		Vec3 lv = player.getLookAngle();
		return new Vector3d(lv.x, lv.y, lv.z);
	}
}
