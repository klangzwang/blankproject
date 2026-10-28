package geni.witherdata.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import geni.withercore.init.WUTEntities;
import geni.withercore.util.TranslationUtil;
import geni.witherutils.WitherUtils;
import geni.withercore.init.WUTBlocks;
import geni.withercore.init.WUTItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

import javax.annotation.Nullable;

public class WitherUtilsLanguages extends LanguageProvider {

    private final Map<Supplier<String>, String> langEntries = new HashMap<>();
    public void add(Map<Supplier<String>, String> entries)
    {
        this.langEntries.putAll(entries);
    }

    public WitherUtilsLanguages(PackOutput gen, String modid, String locale)
    {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations()
    {
        addSystemTranslations();
        addItemTranslations();
    }

    protected void addSystemTranslations()
    {
        // Shields Dialogue & Chat Messages
        add("message.witherutils.shield.soul_prefix", "§5§l[Shield-Soul]§r ");

        // Frontal Damage Reactions
        add("message.witherutils.shield.rotten.hit1", "§d»Ouch! Right on my nose! Learn to block properly, you clumsy fool!«");
        add("message.witherutils.shield.rotten.hit2", "§d»Ha! Missed completely! You'll break your teeth on my rotten plating!«");
        add("message.witherutils.shield.rotten.hit3", "§d»Clang! Hey, my paint was freshly polished! Well... three hundred years ago!«");
        add("message.witherutils.shield.rotten.hit4", "§d»Is that all?! My grandmother in the End hits harder than that!«");

        // Backstab Warning
        add("message.witherutils.shield.rotten.backstab", "§c»Ouch! I told you your backside was wide open!!«");

        // Idle & Environment Comments
        add("message.witherutils.shield.rotten.water_direct", "§d»EEEEK! Is that WATER?! Get this wet nightmare away from me, it burns, it burns!!«");
        add("message.witherutils.shield.rotten.water_nearby", "§d»I smell MOISTURE over there... keep me far away from that terrifying liquid!«");
        add("message.witherutils.shield.rotten.enemy_behind", "§d»Hey genius! Turn around! There's a guy sneaking right behind your unprotected back!«");

        // Biome Comments
        add("message.witherutils.shield.rotten.biome1", "§d»You'll never make it out alive from «%s»! Everything looks exactly the same!«");
        add("message.witherutils.shield.rotten.biome2", "§d»«%s»... ugh, this air tastes foul, like fresh grass and non-End stuff!«");

        // Random Enderman Chatter
        add("message.witherutils.shield.rotten.chatter1", "§d»Why are we still crawling around like primitive snails?! Just throw a pearl, bam, and you're there!«");
        add("message.witherutils.shield.rotten.chatter2", "§d»Just don't look them in the eyes... hehe... wait, I don't even have eyes anymore!«");
        add("message.witherutils.shield.rotten.chatter3", "§d»If you throw me forward hard enough, maybe we'll both fly through the Void... give it a try!«");
        add("message.witherutils.shield.rotten.chatter4", "§d»Do you have a juicy Ender Pearl in your pocket? No? Why are you even alive then?!«");
        add("message.witherutils.shield.rotten.chatter5", "§d»Teleporting is like sneezing, except you end up somewhere else afterwards. Ever tried it?«");

        // Ore Scanner Reactions
        add("message.witherutils.shield.rotten.ore.redstone", "§d»I smell crackling red powder in the walls... don't touch it, it tingles!«");
        add("message.witherutils.shield.rotten.ore.diamond", "§d»Oooh... something cold and blue is sparkling down there. Glints like the eyes of the Ancients!«");
        add("message.witherutils.shield.rotten.ore.debris", "§d»Ancient debris buried deep in the rock! How did that stuff even survive the heat?!«");
        add("message.witherutils.shield.rotten.ore.gold", "§d»Shiny yellow in the rock! The pigs in the Nether would fight over this...«");
        add("message.witherutils.shield.rotten.ore.iron", "§d»Rusty metal in the walls... who needs iron anyway? I'd prefer obsidian!«");
        add("message.witherutils.shield.rotten.ore.coal", "§d»Black coal ash in the rock... musty and dusty down here!«");

        add("witherutils.pack.ctm", "WitherUtils CTM");

        add("tooltip.witherutils.drone_item.place", "Right-click on a block surface to deploy quadcopter");
        add("tooltip.witherutils.drone_item.wrench", "Shift + Right-click with a Wrench to dismantle and retrieve");

        add("subtitles.witherutils.entity.drone.system_beep", "Drone system check beeps");
        add("subtitles.witherutils.entity.drone.motor_start", "Drone rotors spool up");
        add("subtitles.witherutils.entity.drone.hover_loop", "Drone rotors hum");

        add("tooltip.witherutils.hint.extended", ">>> §9SHIFT§r Key for Tooltips.");
        add("tooltip.witherutils.hint.ponder", "Hold [§bW§8] to Ponder (3D Guide)");
        add("tooltip.witherutils.ponder.holding", "Pondering");

        add("ponder.witherutils.title", "WitherUtils 3D Ponder");
        add("ponder.witherutils.header_subtitle", "╘ BlockHead Industry — Interactive 3D Engineering Manual");
        add("ponder.witherutils.drag_hint", "Drag to Rotate • Scroll to Zoom");
        add("ponder.witherutils.replay", "Replay");
        add("ponder.witherutils.close", "Close");

        add("ponder.witherutils.solar.step1.title", "1. BlockHead Roof Technology");
        add("ponder.witherutils.solar.step1.text", "While the average BlockHead is worrying about climate change, BlockHead Industry brings the latest solar technology directly to your roof. Now with Alexa support and Red Giant alerts directly to your Smartphone.");
        add("ponder.witherutils.solar.step2.title", "2. Continuous RF/t Output");
        add("ponder.witherutils.solar.step2.text", "Generates up to %s RF/t during daytime. The high-efficiency photovoltaic surface pulses while converting sunlight into pure electricity.");
        add("ponder.witherutils.solar.step3.title", "3. Sun Angle & Sky Access");
        add("ponder.witherutils.solar.step3.text", "Power output scales dynamically with the current Sun Angle and requires a clear, unobstructed view of the sky above the panel.");
        add("ponder.witherutils.solar.step4.title", "4. Connected Solar Network");
        add("ponder.witherutils.solar.step4.text", "Place Solar Panels next to each other to automatically merge their frames into a single shared network (Combined: %s RF/t).");
        add("ponder.witherutils.solar.step5.title", "5. Bottom Power Extraction");
        add("ponder.witherutils.solar.step5.text", "Tap the network from BELOW! Because all adjacent panels are linked together, you only need a single cable underneath one panel to drain the entire array.");

        add("ponder.witherutils.shield.title", "Steel Shield Combat Tactics");
        add("ponder.witherutils.shield.header_subtitle", "╘ BlockHead Industry — Tactical Defensive Engineering");
        add("ponder.witherutils.shield.step1.title", "1. Portable Fortress");
        add("ponder.witherutils.shield.step1.text", "Crafted from hardened withersteel alloy. Blocks 100% of frontal attacks without durability loss. Slows movement slightly while actively shielding your face from harm.");
        add("ponder.witherutils.shield.step2.title", "2. Charge & Dash-Bash (500ms)");
        add("ponder.witherutils.shield.step2.text", "Hold Right-Click to block, then press & hold Left-Click to charge for 500 ms. Release to sprint-dash forward and unleash an abrupt, bone-shattering frontal ram!");
        add("ponder.witherutils.shield.step3.title", "3. Perfect Counter (Parry)");
        add("ponder.witherutils.shield.step3.text", "Raise your shield within 350 ms right before an incoming strike hits. Negates all damage and immediately retaliates with a concussive counter-strike!");
        add("ponder.witherutils.shield.step4.title", "4. Frisbee-Boomerang (Main Hand)");
        add("ponder.witherutils.shield.step4.text", "Equip the shield in your Main Hand and click Left-Click (no charge needed). Throws the shield flat like a rotating razor frisbee that slices enemies and returns to your hand!");
        add("ponder.witherutils.shield.step5.title", "5. Combat Controls Summary");
        add("ponder.witherutils.shield.step5.text", "[RMB + LMB Hold] Charge Dash-Bash | [RMB Timing] Counter-Parry | [Main-Hand LMB] Frisbee Throw. Automatic swap-back protection prevents lost items!");

        add("ponder.witherutils.anvil.title", "Cursed Anvil Forging");
        add("ponder.witherutils.anvil.header_subtitle", "╘ BlockHead Industry — Metallurgy & Kinetic Assembly Manual");
        add("ponder.witherutils.anvil.step1.title", "1. Primary Tool: Steel Hammer");
        add("ponder.witherutils.anvil.step1.text", "Before any advanced alloy can be forged, you must craft the Steel Hammer using 2 Sticks/Rods, 1 Iron Block, and 2 Iron Ingots. The hammer is the kinetic heart of all WitherUtils crafting.");
        add("ponder.witherutils.anvil.step2.title", "2. The Awakening: Smashing Vanilla Anvils");
        add("ponder.witherutils.anvil.step2.text", "Right-click a Vanilla Anvil (or Cauldron) with your Steel Hammer to strike it down! Cursed lightning surges through the block, igniting a ring of Soul Fire and transforming it into the superheated WitherUtils Anvil!");
        add("ponder.witherutils.anvil.step3.title", "3. Loading the Workpiece");
        add("ponder.witherutils.anvil.step3.text", "Right-click compatible raw materials (such as Iron Ingots, Withersteel Ingots, Plates, or Bones) directly onto the anvil face. Empty hands can right-click anytime to safely extract the current item.");
        add("ponder.witherutils.anvil.step4.title", "4. Kinetic Hammer Strikes");
        add("ponder.witherutils.anvil.step4.text", "Right-click the loaded anvil repeatedly with your Steel Hammer. Each rhythmic strike sparks bright embers, radiates heat, and compresses the atomic structure toward its forged state.");
        add("ponder.witherutils.anvil.step5.title", "5. Heart of Manufacturing");
        add("ponder.witherutils.anvil.step5.text", "Once all required strikes land, your newly forged component pops out: Iron -> Withersteel Nuggets, Ingots -> Plates, Plates -> Gears, and Nuggets -> Rods. The foundation of your entire tech empire!");

        add("ponder.witherutils.angel.title", "Angel Block: Spatial Scaffold & Updraft");
        add("ponder.witherutils.angel.header_subtitle", "╘ BlockHead Industry — Aerostatic Construction & Elevator Manual");
        add("ponder.witherutils.angel.step1.title", "1. Mid-Air Anchoring");
        add("ponder.witherutils.angel.step1.text", "Right-click in mid-air with empty space ahead. The Angel Block instantly anchors itself in thin air without needing any support blocks or scaffolding!");
        add("ponder.witherutils.angel.step2.title", "2. Updraft Jump-Elevator");
        add("ponder.witherutils.angel.step2.text", "Stand beneath an Angel Block (up to 6 blocks below) and press Jump. The kinetic thermal updraft instantly beams you straight up to the top surface!");
        add("ponder.witherutils.angel.step3.title", "3. Structural Instability");
        add("ponder.witherutils.angel.step3.text", "Right-clicking an unsupported Angel Block with an empty hand or standing on it for more than 2.5 seconds will destabilize it, causing it to plummet to the ground as a falling block!");
        add("ponder.witherutils.angel.step4.title", "4. The Feather Cushion");
        add("ponder.witherutils.angel.step4.text", "If a falling Angel Block bonks a player on the head, it shatters into soft soul fragments and blesses them with Slow Falling (Feather Buff) for a full minute!");

        add("ponder.witherutils.rack.title", "High-Density RACK Multiblock");
        add("ponder.witherutils.rack.header_subtitle", "╘ BlockHead Industry — Modular Bulk Storage Multiblock Architecture");
        add("ponder.witherutils.rack.step1.title", "1. Hollow 3x3x3 Framework");
        add("ponder.witherutils.rack.step1.text", "Construct a hollow 3x3x3 cube using reinforced Rack Cases. The outer shell forms the structural containment shield, leaving the exact center block (1, 1, 1) completely empty.");
        add("ponder.witherutils.rack.step2.title", "2. Central Core Controller");
        add("ponder.witherutils.rack.step2.text", "Insert exactly ONE Controller into the dead center of the 3x3x3 structure: Item Controller for bulk item stockpiles, Fluid Controller for colossal liquid tanks, or Energy Controller for high-density power banks.");
        add("ponder.witherutils.rack.step3.title", "3. Installing the Terminal Port");
        add("ponder.witherutils.rack.step3.text", "Install at least one Rack Terminal in the center of an exterior wall face, with its screen oriented outward toward the room. The terminal serves as the input, output, and pipe connection hub.");
        add("ponder.witherutils.rack.step4.title", "4. Structural Formation");
        add("ponder.witherutils.rack.step4.text", "Once all 27 block positions are properly validated, a burst of poof particles signals activation! The Terminal screen lights up, uniting all casing units into a unified quantum multiblock.");
        add("ponder.witherutils.rack.step5.title", "5. Automation & High-Density Piping");
        add("ponder.witherutils.rack.step5.text", "Connect item or fluid pipes, energy conduits, or right-click the Terminal to manage your bulk storage. Multiple terminals can be added to different faces for high-throughput factory lines!");

        add("ponder.witherutils.sensor.title", "Sensor Automation & Security");
        add("ponder.witherutils.sensor.header_subtitle", "╘ BlockHead Industry — Detection Systems & Camouflage Engineering");
        add("ponder.witherutils.sensor.step1.title", "1. Smart Floor Scanning");
        add("ponder.witherutils.sensor.step1.text", "The Floor Sensor monitors the airspace above it with customizable entity filtering: Players, Hostile Monsters, or Passive Animals. Emits a strong downward redstone signal when triggered.");
        add("ponder.witherutils.sensor.step2.title", "2. Camouflage Disguise");
        add("ponder.witherutils.sensor.step2.text", "Right-click the Floor Sensor with any solid full block (such as stone, concrete, or wood) to disguise it completely! It adopts the texture of surrounding flooring. Sneak-right-click with a Wrench to unmask.");
        add("ponder.witherutils.sensor.step3.title", "3. Wall Sensor Motion Cone");
        add("ponder.witherutils.sensor.step3.text", "Mount the Wall Sensor on any wall face. It casts an adjustable pyramidal motion detection zone with customizable range, beam width, downward pitch, and horizontal yaw.");
        add("ponder.witherutils.sensor.step4.title", "4. Hands-Free Automation");
        add("ponder.witherutils.sensor.step4.text", "Walk into the motion cone to trigger automatic hands-free actions: slide open heavy security doors, activate lighting grids, or ring audible chimes and alarms without touching a switch!");

        add("ponder.witherutils.greenhouse.title", "Greenhouse Crop Canopy");
        add("ponder.witherutils.greenhouse.header_subtitle", "╘ BlockHead Industry — Solar Concentration & Accelerated Botanical Canopy");
        add("ponder.witherutils.greenhouse.step1.title", "1. Greenhouse Canopy Ceiling");
        add("ponder.witherutils.greenhouse.step1.text", "Place Greenhouse Glass above your farmland. The canopy harnesses natural light to stimulate photosynthetic cycles and dramatically accelerate crop growth without requiring power or fuel.");
        add("ponder.witherutils.greenhouse.step2.title", "2. Direct Sky Access");
        add("ponder.witherutils.greenhouse.step2.text", "Ensure the Greenhouse blocks have an unobstructed vertical line of sight to the open sky! Any opaque ceilings or obstructions above will block the solar ray concentration and halt growth boosts.");
        add("ponder.witherutils.greenhouse.step3.title", "3. Seamless Connected Grid");
        add("ponder.witherutils.greenhouse.step3.text", "Expand the canopy across your entire field! Neighboring Greenhouse blocks automatically form connected structural glass frames with corner support rods, creating an expansive greenhouse dome.");
        add("ponder.witherutils.greenhouse.step4.title", "4. Accelerated Crop Harvests");
        add("ponder.witherutils.greenhouse.step4.text", "Underneath the active greenhouse ceiling, wheat, carrots, potatoes, and other botanical flora mature in record time. Perfect for automated farming complexes and compact sustainable gardens!");

        add("ponder.witherutils.catwalk.title", "Industrial Steel Infrastructure");
        add("ponder.witherutils.catwalk.header_subtitle", "╘ BlockHead Industry — Structural Steel & Safe Catwalk Engineering");
        add("ponder.witherutils.catwalk.step1.title", "1. Vertical Support Columns");
        add("ponder.witherutils.catwalk.step1.text", "Stack Steel Poles to erect load-bearing pillars of any height. Sneak-placing against floors or ceilings anchors heavy foundation feet and reinforced diagonal braces.");
        add("ponder.witherutils.catwalk.step2.title", "2. Load-Bearing Cross Beams");
        add("ponder.witherutils.catwalk.step2.text", "Steel Beams span horizontally between pillars. When placed against solid walls, they automatically anchor with heavy industrial wall plates, creating rigid double-T beam bridges.");
        add("ponder.witherutils.catwalk.step3.title", "3. Grated Catwalk Deck");
        add("ponder.witherutils.catwalk.step3.text", "Snap Catwalk slabs directly onto beams or poles. Adjacent catwalks automatically connect into a seamless non-slip industrial deck with dynamic outer corner transitions.");
        add("ponder.witherutils.catwalk.step4.title", "4. Rapid Forward Bridging");
        add("ponder.witherutils.catwalk.step4.text", "Right-clicking an existing Catwalk with another Catwalk instantly places it one block forward in your looking direction. Rapidly span across terrifying chasms without crouching or risk of slipping!");
        add("ponder.witherutils.catwalk.step5.title", "5. Shock Absorption & Railings");
        add("ponder.witherutils.catwalk.step5.text", "Catwalk steel grating absorbs 50% of all fall impact damage with metallic spark bursts! Mount Steel Railings along walkway perimeters for a 100% OSHA-compliant facility.");

        add("ponder.witherutils.metal_door.title", "Motorized Bulkhead Blast Door");
        add("ponder.witherutils.metal_door.header_subtitle", "╘ BlockHead Industry — Motorized Bulkhead & Hangar Door Engineering");
        add("ponder.witherutils.metal_door.step1.title", "1. Motorized Vertical Split Mechanism");
        add("ponder.witherutils.metal_door.step1.text", "The Metal Door is an industrial vertical guillotine blast gate. When triggered, its lower half slides down into the floor while the upper half retracts into the ceiling, interlocking heavy teeth into the frame.");
        add("ponder.witherutils.metal_door.step2.title", "2. Multi-Block Chain Linking");
        add("ponder.witherutils.metal_door.step2.text", "Place Metal Doors side-by-side to construct massive hangar gates up to 8 blocks wide! Adjacent doors automatically synchronize into a continuous seamless bulkhead with left, middle, and right segments.");
        add("ponder.witherutils.metal_door.step3.title", "3. Synchronized Collective Operation");
        add("ponder.witherutils.metal_door.step3.text", "Right-clicking any single panel in the chain opens or closes the entire multi-block gate simultaneously! When opened, the passage becomes completely passable with zero collision obstructions.");
        add("ponder.witherutils.metal_door.step4.title", "4. Hands-Free Sensor Automation");
        add("ponder.witherutils.metal_door.step4.text", "Wire redstone directly or mount a Wall Sensor above the portal. The gate opens effortlessly as you approach, allowing smooth passage for players, automated machinery, and transport carts.");

        add("ponder.witherutils.doors.title", "Industrial Security Swing Doors");
        add("ponder.witherutils.doors.header_subtitle", "╘ BlockHead Industry — Reinforced Architectural Doors & Perimeter Security");
        add("ponder.witherutils.doors.step1.title", "1. Five Industrial Archetypes");
        add("ponder.witherutils.doors.step1.text", "Choose from 5 distinct heavy-duty doors: Cased Vault Door, Hazard-Striped Warning Door, Creeper Blast Door, Heavy Steel Plate Door, and Modern Light Iron Grid Door.");
        add("ponder.witherutils.doors.step2.title", "2. Adaptive Wall Framing");
        add("ponder.witherutils.doors.step2.text", "Industrial doors automatically detect adjacent masonry or metal wall surfaces and deploy a reinforced outer steel casing frame, ensuring a flush and airtight architectural aesthetic.");
        add("ponder.witherutils.doors.step3.title", "3. Dual-Mode Access: Hand & Redstone");
        add("ponder.witherutils.doors.step3.text", "Forget finicky levers and wooden buttons! Unlike vanilla iron doors, WitherUtils security doors can be opened smoothly by hand with a simple right-click, or automated via standard redstone signals.");
        add("ponder.witherutils.doors.step4.title", "4. Symmetrical Double Doors");
        add("ponder.witherutils.doors.step4.text", "Place two doors side-by-side to automatically mirror their hinges into a magnificent grand double portal. Both leaves swing open outward, providing wide clearance for high-traffic corridors.");

        add("ponder.witherutils.bricks.title", "Industrial Dark & Lava Bricks");
        add("ponder.witherutils.bricks.header_subtitle", "╘ BlockHead Industry — Heavy Refractory Masonry & Magma Dynamics");
        add("ponder.witherutils.bricks.step1.title", "1. Heavy Industrial Masonry");
        add("ponder.witherutils.bricks.step1.text", "Dark Bricks provide blast-resistant, heavy structural containment. Perfect for heavy boiler framing, smelter hearths, and industrial foundry foundations.");
        add("ponder.witherutils.bricks.step2.title", "2. Molten Lava Infusion");
        add("ponder.witherutils.bricks.step2.text", "Crafted by infusing Dark Bricks with pure lava. Glowing Lava Bricks emit full Light Level 15 and radiant warmth, providing stylish volcanic illumination without relying on torches.");
        add("ponder.witherutils.bricks.step3.title", "3. Hot Floor Hazard");
        add("ponder.witherutils.bricks.step3.text", "Running carelessly across glowing lava bricks will blister your boots with fiery damage! Hold Sneak [Shift] to safely traverse the incandescent surface without singeing your toes.");
        add("ponder.witherutils.bricks.step4.title", "4. Rain Quenching & Reheating");
        add("ponder.witherutils.bricks.step4.text", "When open to rain, the molten veins violently sizzle and quench into dark dormant stone with billowing steam. Once the storm clears, internal volcanic pressure reheats them back to full glow!");

        add("ponder.witherutils.slicedconcrete.title", "Sliced Concrete Panels");
        add("ponder.witherutils.slicedconcrete.header_subtitle", "╘ BlockHead Industry — Sledgehammer Extraction & Multiface Veneers");
        add("ponder.witherutils.slicedconcrete.step1.title", "1. Sledgehammer Extraction");
        add("ponder.witherutils.slicedconcrete.step1.text", "Sliced Concrete cannot be crafted at a workbench! Place a standard vanilla Concrete block and smash it with the Steel Hammer to violently shatter it into 8 ultra-thin sliced panels.");
        add("ponder.witherutils.slicedconcrete.step2.title", "2. Three Industrial Tones");
        add("ponder.witherutils.slicedconcrete.step2.text", "Extract slices from Black, Gray, or White vanilla concrete blocks. Each color retains high-contrast metallic groove inlays for sleek industrial and sterile laboratory styling.");
        add("ponder.witherutils.slicedconcrete.step3.title", "3. Multiface Architectural Veneer");
        add("ponder.witherutils.slicedconcrete.step3.text", "As a Multiface panel, Sliced Concrete can be applied to floors, walls, and ceilings alike without consuming a full block volume. Clings flush against any solid masonry or casing.");
        add("ponder.witherutils.slicedconcrete.step4.title", "4. Sterile Cleanrooms & Bunkers");
        add("ponder.witherutils.slicedconcrete.step4.text", "Fully waterloggable and blast-toughened! Combine Black, Gray, and White panels to design modern cleanrooms, research labs, high-tech server vaults, and subterranean bunker corridors.");

        add("ponder.witherutils.cutter.title", "Handheld Architectural Cutter");
        add("ponder.witherutils.cutter.header_subtitle", "╘ BlockHead Industry — Portable Stonecutting & CTM Facades");
        add("ponder.witherutils.cutter.step1.title", "1. Handheld Workshop Crafting");
        add("ponder.witherutils.cutter.step1.text", "Crafted from WitherSteel ingots and iron. The Cutter is a portable, ultra-compact architectural stonecutter tool that fits directly in your toolbelt.");
        add("ponder.witherutils.cutter.step2.title", "2. Portable Slicing UI");
        add("ponder.witherutils.cutter.step2.text", "Right-click anywhere to open the handheld cutting menu! Insert raw base materials (such as smooth stone, concrete, or deepslate) and select your desired architectural pattern.");
        add("ponder.witherutils.cutter.step3.title", "3. Dynamic CutterBlock System");
        add("ponder.witherutils.cutter.step3.text", "The Cutter dynamically supports all CutterBlocks in the mod—both present and future! As new architectural CTM tiles are registered, the Cutter automatically provides precision slicing recipes.");
        add("ponder.witherutils.cutter.step4.title", "4. Connected Texture Facades (CTM)");
        add("ponder.witherutils.cutter.step4.text", "Blocks sliced with the Cutter feature seamless Connected Texture Mapping (CTM). Place them into expansive walls and facades to witness automatic border-joining and corner welding!");

        add("tooltip.witherutils.shield.hint", "Hold Right-Click + Left-Click to charge Dash-Bash. Right-Click timing before hit to Parry. Left-Click in Main Hand to throw Frisbee.");

        add("itemGroup.witherutils.items", "§9WitherUtils - §fItems");
        add("itemGroup.witherutils.blocks", "§9WitherUtils - §fBlocks");
        add("itemGroup.witherutils.decos", "§9WitherUtils - §fDecos");

        add("gui.witherutils.namesquares", "□■□ ");
        add("gui.witherutils.blockheads", "╘ Blockheads Inc.");

        add("gui.witherutils.progress", "Progress %s%%");
        add("gui.witherutils.redstone_mode", "Redstone Mode");
        add("gui.witherutils.redstone_always_active", "Always active");
        add("gui.witherutils.redstone_active_with_signal", "Active with signal");

        add("tooltip.witherutils.yellowicon", "§l§e 🗲 §r §e Energy §r");
        add("tooltip.witherutils.orangeicon", "§l§6 👾 §r §6 Item §r");
        add("tooltip.witherutils.redicon", "§l§c 💀 §r §c Damage §r");

        add("tooltip.witherutils.durability", "Durability");
        add("tooltip.witherutils.ispowered", "Powered");
        add("tooltip.witherutils.isnotpowered", "NotPowered");
        add("tooltip.witherutils.powerstatus", "Status");
        add("tooltip.witherutils.stored", "Stored");

        add("tooltip.witherutils.energy_amount", "%s FE");
        add("tooltip.witherutils.soulbank.needed", "Insert SoulBank");
        add("tooltip.witherutils.soulbank.base", "Bank Type: %s");
        add("tooltip.witherutils.soulbank.energy_capacity", "Energy Capacity: %s");
        add("tooltip.witherutils.soulbank.energy_transfer", "Energy Transfer: %s");
        add("tooltip.witherutils.soulbank.energy_use", "Energy Use: %s");

        add("tooltip.witherutils.watering_can.infinite", "Infinite Water Supply");
        add("tooltip.witherutils.watering_can.desc", "Right-click to hydrate farmland and accelerate crop growth (3x3)");

        add("tooltip.witherutils.minion_staff.mode", "Summon Mode");
        add("tooltip.witherutils.minion_mode.melee", "Fleischgolem (Melee)");
        add("tooltip.witherutils.minion_mode.ranged", "Splitterschreck (Ranged)");
        add("tooltip.witherutils.minion_staff.desc1", "Hold Right-Click near a corpse to summon");
        add("tooltip.witherutils.minion_staff.desc2", "Left-Click to sacrifice minions for health");

        add("message.witherutils.cast_cancelled", "Ritual summon cancelled.");
        add("message.witherutils.no_corpses_nearby", "No fresh corpses nearby!");
        add("message.witherutils.flesh_golem_limit", "You already control an active Fleischgolem!");

        add("message.witherutils.ritual.kneel", "§4§lKnie nieder!");
        add("message.witherutils.ritual.welcome", "§4§lWelcome!");
        add("message.witherutils.ritual.midnight_only", "§8[§4!§8] §7Die Geister schweigen... Das Ritual funktioniert nur zur Geisterstunde um Mitternacht.");
        add("message.witherutils.ritual.timeout", "§8Das Ritual verblasst im Nichts...");

        add("info.witherutils.nobinding", "This Card is already bound to %s!");
    }

    protected void addItemTranslations()
    {
        for (var entry : BuiltInRegistries.ITEM.entrySet())
        {
            var id = entry.getKey();
            if (id.location().getNamespace().equals(WitherUtils.MODID))
            {
                var item = entry.getValue();

                String formatName = TranslationUtil.getDefaultTranslationFrom(BuiltInRegistries.ITEM.getKey(item).getPath());
                add(item, "§3" + formatName);

                if(getDescriptionForItem(item, null) != "")
                {
                    add(item.getDescriptionId() + ".desc", getDescriptionForItem(item, null));
                }
            }
        }
        for (var entry : BuiltInRegistries.ENTITY_TYPE.entrySet())
        {
            var id = entry.getKey();
            if (id.location().getNamespace().equals(WitherUtils.MODID))
            {
                var entity = entry.getValue();

                String formatName = TranslationUtil.getDefaultTranslationFrom(BuiltInRegistries.ENTITY_TYPE.getKey(entity).getPath());
                add(entity, "§3" + formatName);

                if(getDescriptionForItem(null, entity) != null)
                {
                    add(entity.getDescriptionId() + ".desc", getDescriptionForItem(null, entity));
                }
            }
        }
    }

    public String getDescriptionForItem(@Nullable Item item, @Nullable EntityType<?> entity)
    {
        if (item == WUTItems.SHIELDBASIC.get())
            return "BlockHead Industry's reliable entry-level shield for Crafting. Forged from solid steel plates to absorb front-facing impacts without breaking a sweat.";
        if (item == WUTItems.SHIELDHERO.get())
            return "An upgraded heavy industrial shield designed to completely mitigate frontal attacks. Built tough enough to make durability loss a thing of the past.";
        if (item == WUTItems.SHIELDROTTEN.get())
            return "A cursed, living shield bound to a talkative Enderman soul. Blocks frontal damage with ease, but has a habit of mocking your tactics, dreading water, and commenting on nearby ores.";
        if (item == WUTItems.SHIELDENERGY.get())
            return "State-of-the-art energy barrier technology powered by Forge Energy (FE). Absorbs frontal strikes completely and automatically purges harmful status effects at the cost of stored power.";
        if(entity == WUTEntities.DRONE_DELIVER.get())
            return "DRONE_DELIVER.";
        if(item == WUTItems.WRENCH.get())
            return ".";
        if(item == WUTItems.DRONE_DELIVER.get())
            return ".";
        if(item == WUTItems.ANCHOR.get())
            return "A heavy spatial anchor module designed by BlockHead Industry to lock coordinates and keep your machinery firmly grounded in reality.";
        if(item == WUTItems.CARD.get())
            return "BlockHead Industry's universal smart ID and filter chip. Right-click to bind your Player ID, left-click a creature to capture its Mob ID, or sneak-right-click to configure an item filter list for your machines.";
        if(item == WUTBlocks.COLLECTOR.get().asItem())
            return "Tired of bending your BlockHead back to pick up dropped loot? BlockHead Industry presents the ultimate vacuum cleaner. Pulls in nearby items automatically. Insert a bound Card to filter specific items, or enable overflow mode to incinerate excess junk.";
        if(item == WUTBlocks.FLOORSENSOR.get().asItem())
            return "Step right up! This smart pressure plate scans the area above it for Players, Hostile Mobs, or Passive Creatures and emits a signal when triggered. Insert any solid block into its ghost slot to camouflage it completely.";
        if(item == WUTBlocks.WALL_SENSOR.get().asItem())
            return "Personal space is important for every BlockHead. Mount this motion sensor on any wall to detect nearby players and automatically emit a redstone signal—perfect for hands-free doors and secret base entrances.";
        if(item == WUTBlocks.RESERVOIR.get().asItem())
            return "Tired of walking to the nearest river with a bucket? Place at least two Reservoirs next to each other and fill them with two buckets of water to create an infinite water supply for your machines and pipes.";
        if(item == WUTBlocks.GREENHOUSE.get().asItem())
            return "BlockHead Industry's answer to slow photosynthesis. Place these glass panels above your watered farmland where they can see the sky to concentrate sunlight and significantly speed up crop growth underneath.";
        if(item == WUTBlocks.SOULFIRE.get().asItem())
            return "An eerie, cursed flame born from lightning and dark steel. Emits a cold blue glow without burning down your base.";
        if(item == WUTBlocks.BRICKSDARK.get().asItem())
            return "Heavy-duty dark industrial bricks for the style-conscious BlockHead. Blast-resistant, grim, and guaranteed to make your factory look 200% more intimidating.";
        if(item == WUTBlocks.BRICKSLAVA.get().asItem())
            return "Infused with molten magma for that cozy industrial glow. Watch your step—walking on these without sneaking will singe your BlockHead toes, and rain will temporarily cool them down.";
        if(item == WUTBlocks.STEEL_POLE.get().asItem() || item == WUTBlocks.STEEL_BEAM.get().asItem())
            return "Sturdy industrial steel support pole. Stack them up to build towering streetlights, support beams, or safety structures for your factory.";
        if(item == WUTBlocks.STEEL_RAILING.get().asItem())
            return "Because workplace safety regulations finally reached BlockHead Industry. Prevents clumsy BlockHeads from accidentally walking off ledges and catwalks.";
        if(item == WUTBlocks.SLICEDCONCRETEBLACK.get().asItem() || item == WUTBlocks.SLICEDCONCRETEGRAY.get().asItem() || item == WUTBlocks.SLICEDCONCRETEWHITE.get().asItem())
            return "Reinforced industrial concrete with sleek metal grooves. Ideal for clean lab floors, modern bunkers, and heavy machine rooms.";
        if(item == WUTBlocks.CASED_DOOR.get().asItem() || item == WUTBlocks.STRIPED_DOOR.get().asItem() ||
                item == WUTBlocks.STEEL_DOOR.get().asItem() || item == WUTBlocks.LIRON_DOOR.get().asItem() ||
                item == WUTBlocks.CREEP_DOOR.get().asItem() || item == WUTBlocks.METAL_DOOR.get().asItem())
            return "Heavy-duty industrial security door crafted for BlockHead facilities. Keeps unwanted guests out and opens smoothly by hand or via a redstone signal.";
        if(item == WUTBlocks.RACK_CASE.get().asItem())
            return "The structural backbone of BlockHead Industry's high-density storage. Build a hollow 3x3x3 frame out of Rack Cases, place a Controller in the center, and attach a Rack Terminal to the outer wall to form a massive storage multiblock.";
        if(item == WUTBlocks.RACK_TERMINAL.get().asItem())
            return "The access port for your Rack storage structure. Integrate it into the outer wall of a Rack Case frame facing the central Controller to form the multiblock and connect your item or fluid pipes.";
        if(item == WUTBlocks.RACKITEM_CONTROLLER.get().asItem())
            return "The brain of the Item Rack multiblock. Place this controller in the center of a 3x3x3 Rack Case frame with a Terminal to unlock high-capacity bulk item storage.";
        if(item == WUTBlocks.RACKFLUID_CONTROLLER.get().asItem())
            return "The heart of the Fluid Rack multiblock. Place this controller in the center of a 3x3x3 Rack Case frame with a Terminal to store massive amounts of fluids in a single structure.";
        if(item == WUTBlocks.ANVIL.get().asItem())
            return "A cursed anvil that can be used to create new items. Place an original anvil and use a hammer with your right mouse button. But be careful, it might get a bit shaky. To use the anvil, place an item on it and right-click with a Hammer. After a few hits, you may get a new item.";
        if(item == WUTBlocks.ANGEL.get().asItem())
            return "Tired of building towers just to float a block in mid-air? Fair warning: this block is extremely unstable. On the bright side, if it hits you, your Blockhead gets a Feather Buff";
        if(item == WUTBlocks.STEEL_CATWALK.get().asItem())
            return "Industrial steel catwalk platform with grated flooring. Safely build walkways across high gaps, dampens fall impact, and snaps forward when placing.";
        if(item == WUTItems.HAMMER.get())
            return "The Hammer is essential for breaking rocks and shaping metal. You'll need it to create more items.";
        if(item == WUTBlocks.SOLARBASIC.get().asItem() || item == WUTBlocks.SOLARADV.get().asItem() || item == WUTBlocks.SOLARULTRA.get().asItem())
            return "While the average BlockHead is worrying about climate change, BlockHead Industry brings the latest solar technology directly to your roof. Now also with Alexa support and Red Giant alerts directly to your Smartphone. Generates electricity during the day. Place the solar panels next to each other to form a network. This way, you only need one cable and they are all connected.";
        if(item == WUTBlocks.CREATIVE_GENERATOR.get().asItem())
            return "Creative Handler Block for develop testing.";
        if(item == WUTBlocks.SOLARCASE.get().asItem() || item == WUTBlocks.WITHERSTEEL_BLOCK.get().asItem() ||
                item == WUTItems.WITHERSTEEL_CASE.get() || item == WUTItems.WITHERSTEEL_GEAR.get() || item == WUTItems.WITHERSTEEL_INGOT.get() || item == WUTItems.WITHERSTEEL_NUGGET.get() ||
                item == WUTItems.WITHERSTEEL_PLATE.get() || item == WUTItems.WITHERSTEEL_ROD.get() || item == WUTItems.SOULBANK_CASE.get())
            return "This Item is required to craft tools or other Items.";
        if(item == WUTItems.SOULBANK_BASIC.get() || item == WUTItems.SOULBANK_ADVANCED.get() || item == WUTItems.SOULBANK_ULTRA.get())
            return "This object is needed to supply tools or machines with electricity or charge. A stronger unit can increase capacity or transmission.";
        return "";
    }
}
