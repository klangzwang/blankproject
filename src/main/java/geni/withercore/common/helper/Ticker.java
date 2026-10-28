package geni.withercore.common.helper;

import net.minecraft.nbt.CompoundTag;

public class Ticker {
	
    private double maxTicks;
    private double ticks;
    private double typeTicks;
    private double prevTicks;
    
    private TickerType type = TickerType.NONE;
    
    public Ticker(double max) {
        this.maxTicks = max;
    }

    public Ticker(double max, TickerType type) {
        this.maxTicks = max;
        this.type = type;
    }
    
    public Ticker setType(TickerType type)
    {
        this.type = type;
        return this;
    }
    
    public static Ticker empty() {
        return new Ticker(0);
    }

    public boolean isEmpty() {
        return this.ticks <= 0;
    }

    public boolean ended() {
        return this.ticks >= this.maxTicks;
    }
    
    public boolean atStart() {
        return this.ticks <= 0;
    }
    
    public void add(double ticks) {
        this.ticks = Math.min(Math.max(0, this.ticks + ticks), this.maxTicks);
    }

    @SuppressWarnings("incomplete-switch")
	public void onward()
    {
        if (this.ticks < this.maxTicks)
        {
        	this.ticks++;
        	
        	if(type != TickerType.NONE)
        	{
        		switch(this.type)
        		{
					case x1:
						this.typeTicks = easeInOutElastic(this.ticks);
						break;
					case x2:
						this.typeTicks = easeInExpo(this.ticks);
						break;
					case x3:
						break;
					case x4:
						break;
					case x5:
						break;
        		}
        	}
        }
    }

    @SuppressWarnings("incomplete-switch")
	public void back() {
        if (this.ticks > 0) {
            this.ticks--;
        }
        
		if(this.ticks == 0)
			this.typeTicks = 0;
		
    	if(type != TickerType.NONE && this.typeTicks > 0)
    	{
    		switch(this.type)
    		{
				case x1:
					this.typeTicks = easeInOutElastic(this.ticks);
					break;
				case x2:
					this.typeTicks = easeInExpo(this.ticks);
					break;
				case x3:
					break;
				case x4:
					break;
				case x5:
					break;
    		}
    	}
    }

    public void back(double value) {
        if (this.ticks > 0) {
            this.ticks -= Math.min(this.ticks, value);
        }
    }

    public void reset() {
        this.ticks = 0;
    }

    public static boolean delayed(double delay) {
        return System.currentTimeMillis() % (delay * 5) == 0;
    }

    public void read(CompoundTag compound, String key) {
        this.ticks = compound.getDouble(key + "_ticks");
        this.maxTicks = compound.getDouble(key + "_max_ticks");
    }

    public void write(CompoundTag compound, String key) {
        compound.putDouble(key + "_ticks", this.ticks);
        compound.putDouble(key + "_max_ticks", this.maxTicks);
    }

    public double getMax() {
        return this.maxTicks;
    }

    public void setMax(double max) {
        this.maxTicks = max;
    }

    public double getTicks() {
        return this.ticks;
    }

    public double getTypeTicks() {
        return this.typeTicks;
    }
    
    public void setTicks(double ticks) {
        this.ticks = ticks;
    }

    public void setAll(double ticks) {
        this.maxTicks = ticks;
        this.ticks = ticks;
    }

    public double getEmpty() {
        return this.maxTicks - this.ticks;
    }

    public double perCent() {
        return this.maxTicks > 0 ? this.ticks * 100.0D / this.maxTicks : 0;
    }

    public float subSized() {
        return this.maxTicks > 0 ? (float) (this.ticks / this.maxTicks) : 0;
    }
    
    public enum TickerType {
    	NONE,
    	x1,
    	x2,
    	x3,
    	x4,
    	x5;
    	
    	private TickerType()
    	{
    		
    	}
    }
    
	private double easeInOutElastic(double ticks)
	{
		double c5 = (2 * Math.PI) / 4.5;
		return ticks == 0
		  ? 0
		  : ticks == this.maxTicks
		  ? 1
		  : ticks < this.maxTicks / 2
		  ? -(Math.pow(2, 20 * (ticks / this.maxTicks) - 13) * Math.sin((20 * (ticks / this.maxTicks) - 12.125) * c5)) / 2
		  : (Math.pow(2, -20 * (ticks / this.maxTicks) + 13) * Math.sin((20 * (ticks / this.maxTicks) - 12.125) * c5)) / 2 + 1;
	}
	
	private double easeInExpo(double ticks)
	{
		return ticks == 0 ? 0 : Math.pow(2, 10 * (ticks / this.maxTicks) - 10);
	}
	
	public boolean isTicking()
	{
		return this.ticks > 0 && this.ticks < maxTicks;
	}
	
	/*
	 * 
	 * SLIDETICKER
	 * 
	 */
	public void onTickerSliding(boolean isPowered)
	{
		prevTicks = getTicks();
        if(isPowered)
        {
            if(getTicks() < Math.max(0, getMax()))
            {
            	onward();
            }
        }
        else if(!atStart())
        {
        	back();
        }
	}
	
    public float getSlideTick(float modifier)
    {
        float partialSlideTick = (float) (prevTicks + (getTicks() - prevTicks)) * modifier;
        float output = 1.0f * (1.0F - ((float) Math.sin(Math.toRadians(90.0 + 180.0 * partialSlideTick / getMax())) / 2.0F + 0.5F));
        return output;
    }
}
