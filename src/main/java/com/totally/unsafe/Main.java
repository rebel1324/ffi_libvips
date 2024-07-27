package com.totally.unsafe;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.annotations.In;
import jnr.ffi.byref.PointerByReference;
import jnr.ffi.util.EnumMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * SUPER Unsafe FFI with JNR (Java Native Runtime)
 * tbh, current setup has no big difference  between
 * forking new process with argument. (ofc, creating new process is WAY more expensive...)
 * still there should be more exception catches or it will burn the server down.
 */
@Slf4j
public class Main {
    // let's start with this
    /// https://www.libvips.org/API/current/libvips-vips.html
    public interface LibvipsFFIInterface { // A representation of libC in Java
        int vips_version();

        String vips_version_string();

        int vips_init(String argv0);

        String vips_get_prgname();

        // method1: C API is bad for Java, how about calling vips_call?
        int vips_call(String operation_name, Object... params);

        int vips_vipsload(String filename, PointerByReference out);

        int vips_vipssave(Pointer in, String filename, Object... params);

        int vips_thumbnail_image(Pointer in, Pointer out, int width, Object... options);

        Pointer vips_image_new_from_file(String filename, Object... options);

        Pointer vips_operation_new(String operationName);

        Pointer vips_cache_operation_build(Pointer in);

        void vips_object_get_property(Pointer gobject, int property_id, Pointer value, Pointer pspec);

        void vips_object_set_property(Pointer gobject, int property_id, Pointer value, Pointer pspec);

        String vips_error_buffer();

        void vips_error_clear();

        int vips_object_set(Pointer gobject, Object ...values);

        Pointer vips_object_local_array(Pointer parent, int size);
    }

    public static enum VipArgumentFlags implements EnumMapper.IntegerEnum {
        VIPS_ARGUMENT_NONE(0),
        VIPS_ARGUMENT_REQUIRED(1),
        VIPS_ARGUMENT_CONSTRUCT(2),
        VIPS_ARGUMENT_SET_ONCE(4),
        VIPS_ARGUMENT_SET_ALWAYS(8),
        VIPS_ARGUMENT_INPUT(16),
        VIPS_ARGUMENT_OUTPUT(32),
        VIPS_ARGUMENT_DEPRECATED(64),
        VIPS_ARGUMENT_MODIFY(128),
        VIPS_ARGUMENT_NON_HASHABLE(256);

        private final int value;

        VipArgumentFlags(int value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return value;
        } // mapping function
    }

    enum VipsSize {
        VIPS_SIZE_BOTH,
                VIPS_SIZE_UP,
                VIPS_SIZE_DOWN,
                VIPS_SIZE_FORCE,
                VIPS_SIZE_LAST
    }

    enum VipsInteresting {
        VIPS_INTERESTING_NONE,
                VIPS_INTERESTING_CENTRE,
                VIPS_INTERESTING_ENTROPY,
                VIPS_INTERESTING_ATTENTION,
                VIPS_INTERESTING_LOW,
                VIPS_INTERESTING_HIGH,
                VIPS_INTERESTING_ALL,
                VIPS_INTERESTING_LAST
    }

    /**
     * I aint gonna make this extensible because this is PoC
     * I'm already studying hard in weekend
     */
    @Builder
    @Getter
    @With
    public static class ThumbnailTask {
        private final String input;
        private final String output;

        /**
         * planned for saving error information
         * left alone because i need to sleep
         */
        private final String whyAreYouDoingThisToMe;

        private final Pointer inputImagePointer;
        /**
         * automatically deref'd when {@link this#inputImagePointer} gets dereferenced.
         */
        private final Pointer outputPointer;

        /**
         * "THE" Desperate appeal of "I care about memory, really, trust me bro"
         */
        private static final ThreadLocal<Object[]> CommandRefs = ThreadLocal.withInitial(()-> FIXED_THUMBNAIL_PARAM_LIST.toArray());

        @Override
        public String toString() {
            return "%s".formatted(this.getInput());
        }
    }

    private final static Integer OUTPUT_SIZE = 512;
    // Yes, this will copy a list every single time when you try to get anything from this.
    // Yes, that is not good for memory.
    // At least no one can touch this thing, at all. in any chance.
    private final static List<Object> FIXED_THUMBNAIL_PARAM_LIST = Collections.unmodifiableList(List.of("height", OUTPUT_SIZE, "crop", VipsInteresting.VIPS_INTERESTING_ATTENTION));

    public static void main(String[] args) {
        try {
            log.info("hi");
            //<editor-fold desc="gave up gg">
            LibraryLoader<LibvipsFFIInterface> loader = LibraryLoader.create(LibvipsFFIInterface.class);
            LibvipsFFIInterface vips = loader.load("libvips-42");
            Runtime runtime = Runtime.getRuntime(vips);
            int version = vips.vips_version(); // prints "Hello World!" to console
            String versionStr = vips.vips_version_string(); // prints "Hello World!" to console
            log.info("libvips version: {}", version);
            log.info("libvips version: {}", versionStr);
            int result = vips.vips_init("");
            if (result != 0) {
                log.error("failed to initialize libvips.");
                return;
            }
            String programName = vips.vips_get_prgname();
            log.info("initialized libvips!: {} (as {})", result, programName);

            var ctx = ThumbnailTask.builder()
                    .input("C:/Users/***/Documents/projects/ffi_libvips/hihi.png")
                    .output("C:/Users/***/Documents/projects/ffi_libvips/outputValue.jpg")
                    .build();
            var tasks = List.of(ctx);
            //</editor-fold>

            // I didn't asked how long the map chain is, I said I cast fireball deadass
            //noinspection ReactiveStreamsTooLongSameOperatorsChain
            var results = Flux.fromIterable(tasks)
                    .map(t -> Optional.ofNullable(t.getInput())
                            .filter(Predicate.not(String::isEmpty))
                            .map(vips::vips_image_new_from_file)
                            .map(t::withInputImagePointer)
                            .orElseThrow(VipsException.withString("Image does not exists")))
                    .map(t -> t.withOutputPointer(vips.vips_object_local_array(t.getInputImagePointer(), 1)))
                    .map(t -> {
                        // *beep* *beep* *beep* WARNING: EXTREME HEAT DAMAGE DETECTED
                        // also, libvips says this has some disadvantage.
                        // because it utilizes image object directly rather than streaming feature built in vips_thumbnail.
                        Integer rs = vips.vips_thumbnail_image(
                                t.getInputImagePointer(),
                                t.getOutputPointer(),
                                OUTPUT_SIZE,
                                ThumbnailTask.CommandRefs.get());
                        return Optional.of(rs)
                                .filter(PredicateUtil.isSuccess)
                                .map(x->t) // kek
                                .orElseThrow(VipsUtil.wellShitHappened(vips, log));
                    })
                    .map(t-> {
                        var ptr = t.getOutputPointer().getPointer(0);
                        Integer saveResult = vips.vips_vipssave(ptr, t.getOutput());
                        Optional.of(saveResult)
                                .filter(PredicateUtil.isSuccess)
                                .orElseThrow(VipsUtil.wellShitHappened(vips, log));
                        return t;
                    })
                    .doOnNext(x->log.info("saved to {}", x.getOutput()))
                    .onErrorContinue((t, v)->{
                        log.info("processing failed: ({}) {}", v, t);
                    })
                    .subscribe();

            // what can be improved
            // 1. using Top level API is discouraged. (*but i don't want to write JNI* *ssssh*)
            // 2. trying to put bytestream to libvips and achieve faster streaming image processing than ImageMagick or
            //    Image Monkey
            // 3. add context and traces to measure detailed information about each step and tasks with Tracing and Metrics.
            // 4. do not make it panic.
            // 5. support modern formats like AVIF/HEIC
        } catch (Throwable e) {
           log.error("wtf how", e);
        }
   }
}