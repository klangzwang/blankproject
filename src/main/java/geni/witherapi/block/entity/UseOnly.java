package geni.witherapi.block.entity;

import net.neoforged.fml.LogicalSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface UseOnly {
	
    LogicalSide value();
}
