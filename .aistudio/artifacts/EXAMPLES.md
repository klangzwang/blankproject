# WitherUtils Mod Class Examples

Here you will find various examples of already existing classes which you must use as a blueprint in order to maintain a specific pattern.

---

### Block Example
package geni.witherutils.common.world.level.block;

public class ExampleBlock extends WitherAbstractBlock implements WitherEntityBlock {
	
	public ExampleBlock(BlockBehaviour.Properties props)
	{
		super(props);
        this.registerDefaultState(...);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ExampleBlockEntity(pos, state);
	}
}

---

### BlockEntity Example
package geni.witherutils.common.world.level.block.entity;

public class ExampleBlockEntity extends WitherMachineBlockEntity {

    public static final SingleSlotAccess INPUT = new SingleSlotAccess();

    public ExampleBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(WUTBlockEntities.EXAMPLE, worldPosition, blockState);
		addDataSlot(NetworkDataSlot.ITEM_STACK.create(() -> INPUT.getItemStack(this), f -> INPUT.setStackInSlot(this, f)));
    }

	@Override
    public MachineInventoryLayout getInventoryLayout()
    {
        return MachineInventoryLayout.builder().inputSlot().slotAccess(INPUT).setStackLimit(64).build();
    }
	
	@Override
	protected MachineInventory createMachineInventory(MachineInventoryLayout layout)
	{
		return new MachineInventory(layout)
		{
            protected void onContentsChanged(int slot)
            {
                super.onContentsChanged(slot);
                if (level == null)
                    return;
                if (slot == 0)
                {
                	...
                }
                setChanged();
            }

	        @Override
	        public int getSlotLimit(int slot)
	        {
	            return 64;
	        }
		};
	}
	
	/*
     * 
     * ITEM
     * 
     */
    @Override
    public boolean hasItemCapability()
    {
        return true;
    }
    @Override
    public IItemHandler getItemHandler(Direction dir)
    {
    	if(dir == Direction.?)
    		return ...;
    	else if(dir == Direction.?)
    		return getInventory();
    	else
    		return null;
    }
	
	
    /*
     * 
     * ENERGY
     * 
     */
    @Override
    public boolean hasEnergyCapability()
    {
        return true;
    }
    @Override
    public IEnergyStorage getEnergyHandler(@Nullable Direction dir)
    {
    	if(dir == Direction.?)
    		return ...;
    	else if(dir == Direction.?)
    		return ...;
    	else
    		return null;
    }
    
    /*
     * 
     * FLUID
     * 
     */
    @Override
    public boolean hasFluidCapability()
    {
        return true;
    }
	@Override
	public IFluidHandler getFluidHandler(@Nullable Direction dir)
	{
    	if(dir == Direction.?)
    		return ...;
    	else if(dir == Direction.?)
    		return ...;
    	else
    		return null;
	}

    @Override
    public void serverTick()
    {
        super.serverTick();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
    }
}

---

### BERenderer Example
package geni.witherutils.client.block.render.entity;

public class ExampleBlockEntityRenderer extends AbstractBlockEntityRenderer<ExampleBlockEntity> {

    public ExampleBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(ExampleBlockEntity te, float partialTick, PoseStack matrix, MultiBufferSource buffer, Minecraft mc, ClientLevel level, LocalPlayer player, int light, int overlayLight)
    {
        if (te.getLevel() == null)
            return;
        
		matrix.pushPose();
        ...
        matrix.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(ExampleBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }
}

---

### EntityRenderer Example
package geni.witherutils.client.entity.render;

@OnlyIn(Dist.CLIENT)
public class ExampleEntityRenderer extends EntityRenderer<ExampleEntity> {
	
    public ExampleEntityRenderer(EntityRendererProvider.Context pContext)
    {
        super(pContext);
    }

    @Override
    public void render(ExampleEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight)
    {
    }

    @SuppressWarnings("deprecation")
	@Override
    public ResourceLocation getTextureLocation(ExampleEntity pEntity)
    {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

---

### Screen Example
package geni.witherutils.client.screen;

public class ExampleScreen extends WitherAbstractEntityScreen<ExampleMenu, ExampleBlockEntity> {

    public ExampleScreen(ExampleMenu gui, Inventory inv, Component title) {
        super(gui, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, GuiToolkit toolKit, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    protected String getScreenName() {
        return "EXAMPLE";
    }
}

---

### Menu Example
package geni.witherutils.common.menu;

public class ExampleMenu extends WitherMachineMenu<ExampleBlockEntity> {

    public ExampleMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, extraData));
    }

    public ExampleMenu(int containerId, Inventory playerInventory, @Nullable ExampleBlockEntity blockEntity) {
        super(WUTMenus.EXAMPLE_MENU.get(), containerId, playerInventory, blockEntity);
    }
}

---

### Item Example
package geni.witherutils.common.world.item;

public class ExampleItem extends WitherItem {

	public ExampleItem(Item.Properties props)
	{
		super(props);
	}
}
