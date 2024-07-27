# Java 21: Example for using Libvips via FFI 
## Summary 

> This code ignores every single advices that libvips dev team made *(for now)* <br/>
> You're suppose to use properly developed libvips JNI wrapper for Java (that does not exists anywhere)

This example code is made to prove that native libraries with FFI can be used without developing JNI c library.

This project contains codes for processing images via [libvips Image Processing Native Library](https://www.libvips.org/)  which is being used for many projects around the world.

## Intentions and Goals

1. Using native libraries with efficiently with less works. 
2. Not creating 30 paged installation documentation for fellow developers
3. Checking out if libvips actually works with Java. All the other languages has wrapper but java. Aww
4. (todo) Setting up libvips component as Bean, creating logback consumer for troubleshooting (with correlation - tbh it seems near impossible)
5. (todo) achieving threadsafe FFI calls 
6. acknowledging and understanding what the hell am i doing right now correctly. (Let's not learn bad practices)

## Setting up 
1. Download [Libvips Binaries from libvips.org](https://github.com/libvips/libvips/releases) 
2. put the content into `proejct root` and rename as `vips`
3. Make sure `"${project.rootDir}/vips/bin;${System.getenv("PATH")}"` path works
4. current `PATH`  variable is for `Windows` so you need to adjust the variable for now if you're using another OS like *nix or Apple.

## Running the code
```shell
./gradlew :Main.main()
```

## Testing
* It works on my computer, trust me

## Cautions
1. Any native runtime is *NOT SAFE* and this project does not shows how to mitigate dangers of native function calls.
2. This project does not have "ensured" thread-safe codes yet. Do not use this code in any kind of production environment as it is.
3. Binaries are not included. *batteries sold separately*
4. Do not try to code in weekend. I tried it and I'm on regret.