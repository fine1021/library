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

remove nineoldandroid library from my old version, since there are some bugs on android 2.3 between nineoldandroid and fragment layout, but now it still can be used on android 2.3. I also use the hand-indicator to make showcaseview more beautiful, you can use `setHandEnable()` method to enable, but the hand-indicator is not suitable for all scenes

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

# support library

a support library for android, include xmlhelper,asynctask,inageloader,widget (such as GestureView etc.) and some utils

--
Reference the library in your Android projects using this dependency in your module's  `build.gradle`  file:

```Gradle
dependencies {
    compile 'com.yxkang.android:support:0.0.7'
}
```

License
-------
    Copyright 2015 fine1021

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
