package com.totally.unsafe;

import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VipsUtil {
    public static Runnable damnShitHappened(Main.LibvipsFFIInterface vips, Logger log) {
        return ()->{
            String error = vips.vips_error_buffer();
            log.error("checking error : {}", error);
            vips.vips_error_clear();
        };
    }

    // todo: this shit does not looks like threadsafe.
    // probably needs to be dumped to "log stream listener"
    // but at this time gonna put things in here anyway
    public static Supplier<VipsException> wellShitHappened(Main.LibvipsFFIInterface vips, Logger log) {
        return ()->{
            String error = Optional
                    .ofNullable(vips.vips_error_buffer())
                    .filter(Predicate.not(String::isEmpty))
                    .orElse("<No reason provided>");
            var ext = new VipsException(error);
            vips.vips_error_clear();
            return ext;
        };
    }
}
