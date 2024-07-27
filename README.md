# Java 21: Example for using Libvips via FFI 
## Summary 

> This code ignores every single advices that libvips dev team made *(for now)* <br/>
> You're suppose to use properly developed libvips JNI wrapper for Java (that does not exists anywhere)

This example code is made to prove that native libraries with FFI can be used without developing JNI c library.

This project contains codes for processing images via [libvips Image Processing Native Library](https://www.libvips.org/)  which is being used for many projects around the world.

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
