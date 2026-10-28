package geni.withercore.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TranslationUtil {
	
    public static String getDefaultTranslationFrom(String name)
    {
        return Arrays.stream(name.split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
    
    public static Supplier<String> getDefaultDescTranslationFrom(Supplier<String> supplier)
    {
        return () -> supplier.get() + ".desc";
    }
    
    public static String getName(String paragaph, String name)
    {
        return paragaph + Arrays.stream(name.split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
