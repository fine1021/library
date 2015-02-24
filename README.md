# shimmer library

use the nineoldandroid library to make it work well on android 2.3. 

--
Reference the library in your Android projects using this dependency in your module's  `build.gradle`  file:

```Gradle
dependencies {
    compile 'cn.edu.nuaa.Sunday:shimmer:1.0.0'
}
```

# showcaseview library

remove nineoldandroid library from my old version, since there are some bugs on android 2.3 between nineoldandroid and fragment layout, but now it still can be used on android 2.3. I also use the hand-indicator to make showcaseview more beautiful, you can use `setHandEnable()` method to enable, but this method must be called before `setTarget()`. the hand-indicator is not suitable for all scenes

--
Reference the library in your Android projects using this dependency in your module's  `build.gradle`  file:

```Gradle
dependencies {
    compile 'cn.edu.nuaa.Sunday:showcaseview:1.0.2'
}
```

# viewpagerindicator library 

build the library from ADT

--
Reference the library in your Android projects using this dependency in your module's  `build.gradle`  file:

```Gradle
dependencies {
    compile 'cn.edu.nuaa.Sunday:viewpagerindicator:1.0.0'
}
```
