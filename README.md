![Preview image](media/header.gif)

# Pix (WhatsApp Style Image and Video Picker)

Pix is a WhatsApp image picker replica. with this you can integrate a image picker just like WhatsApp.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/887abd593a5a499495c4f071accb132a)](https://app.codacy.com/app/akshay2211/PixImagePicker?utm_source=github.com&utm_medium=referral&utm_content=akshay2211/PixImagePicker&utm_campaign=Badge_Grade_Dashboard)
[![](https://img.shields.io/badge/Android%20Arsenal-PixImagePicker-blue.svg?style=flat-square)](https://android-arsenal.com/details/1/6935)
[![](https://img.shields.io/badge/Medium-Pix-black.svg?style=flat-square)](https://medium.com/@fxn769/pix-media-picker-android-library-1ec3c5e5f91a)
[![](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat-square)](https://android-arsenal.com/api?level=16)
[![](https://img.shields.io/badge/Awesome%20Android-PixImagePicker-green.svg?style=flat-square)](https://android.libhunt.com/piximagepicker-alternatives)
[![Pix Image Picker](https://www.appbrain.com/stats/libraries/shield/pix-image-picker.svg)](https://www.appbrain.com/stats/libraries/details/pix-image-picker/pix-image-picker)
<img src="http://img.shields.io/liberapay/receives/akshay2211.svg?logo=liberapay">
[![xscode](https://img.shields.io/badge/Available%20on-xs%3Acode-blue?style=?style=plastic&logo=appveyor&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRF////////VXz1bAAAAAJ0Uk5T/wDltzBKAAAAlUlEQVR42uzXSwqAMAwE0Mn9L+3Ggtgkk35QwcnSJo9S+yGwM9DCooCbgn4YrJ4CIPUcQF7/XSBbx2TEz4sAZ2q1RAECBAiYBlCtvwN+KiYAlG7UDGj59MViT9hOwEqAhYCtAsUZvL6I6W8c2wcbd+LIWSCHSTeSAAECngN4xxIDSK9f4B9t377Wd7H5Nt7/Xz8eAgwAvesLRjYYPuUAAAAASUVORK5CYII=)](https://xscode.com/akshay2211/piximagepicker)

## New in PixImagePicker
1. Androidx Camera API integration
2. Scoped storage to support Android Version 30
3. Minimum SDK from 19 to 21
4. Migration from Java to Kotlin
5. Ability to use it as a Fragment
6. Re-structuring from scratch


 
## Demo
![](media/two.gif)

## Usage
set configuration as
```kotlin
    val options = Options().apply{
        ratio = Ratio.RATIO_AUTO                                    //Image/video capture ratio
        count = 1                                                   //Number of images to restrict selection count
        spanCount = 4                                               //Number for columns in grid
        path = "Pix/Camera"                                         //Custom Path For media Storage
        isFrontFacing = false                                       //Front Facing camera on start
        videoDurationLimitInSeconds = 10                            //Duration for video recording
        mode = Mode.All                                             //Option to select only pictures or videos or both
        flash = Flash.Auto                                          //Option to select flash type
        preSelectedUrls = ArrayList<Uri>()                          //Pre selected Image Urls
    }

```
Ratio can be
```kotlin
  RATIO_4_3, RATIO_16_9, RATIO_AUTO
```
Mode to to select the media type can be as
```kotlin
  All, Picture, Video
```
Then pass this config to the pix fragment either via
```kotlin
     addPixToActivity(R.id.container, options) {
          when (it.status) {
              PixEventCallback.Status.SUCCESS -> //use results as it.data
              PixEventCallback.Status.BACK_PRESSED -> // back pressed called
          }
      }
```
or plain fragment can be retrieved via
```kotlin
private val pixFragment = pixFragment(options)
```
The results can be retrieved via the constructor callback from the fragment
```kotlin
    pixFragment(options){
        when (it.status) {
            PixEventCallback.Status.SUCCESS -> //use results as it.data
            PixEventCallback.Status.BACK_PRESSED -> // back pressed called
        }
    }
```
Or can be retrieved by anywhere in the Application from the state flow eventbus
```kotlin
    PixBus.results {
        when (it.status) {
             PixEventCallback.Status.SUCCESS ->  //use results as it.data
             PixEventCallback.Status.BACK_PRESSED -> // back pressed called
        }
    }
```
For detailed usage kindly refer to the below samples
 - [FragmentSample](app/src/main/java/io/ak1/pixsample/samples/FragmentSample.kt) for Plain Fragment implementation
 - [NavControllerSample](app/src/main/java/io/ak1/pixsample/samples/NavControllerSample.kt) for Fragments with NavController implementation
 - [ViewPager2Sample](app/src/main/java/io/ak1/pixsample/samples/ViewPager2Sample.kt) for Fragments with ViewPager2 implementation

## Customise
### Theme
include these items in colors.xml with custom color codes
```xml
<resources>
    <color name="video_counter_color_pix">#E53935</color>
    <color name="primary_color_pix">#075e54</color>
    <color name="primary_light_color_pix">#80075e54</color>
    <color name="surface_color_pix">#ffffff</color>
    <color name="text_color_pix">#807f7f</color>
</resources>
```

## Thanks to
  - [Glide]
  - [FastScroll]
  - [Header-decor]
  - [CameraX]
  - [Coroutines]

## Backers
Become a backer and help us sustain our activities! 🙏🙏
<a href="https://opencollective.com/piximagepicker#backers" target="_blank"><img src="https://opencollective.com/piximagepicker/backers.svg?width=890"></a>

## Download
[![Download](https://search.maven.org/artifact/io.ak1.pix/piximagepicker)](https://search.maven.org/artifact/io.ak1.pix/piximagepicker) or grab via Gradle:
 
include in app level build.gradle
 ```groovy
 repositories {
    mavenCentral()
 }
 ```
```groovy
 implementation  'io.ak1.pix:piximagepicker:1.6.2'
```
or Maven:
```xml
<dependency>
  <groupId>io.ak1.pix</groupId>
  <artifactId>piximagepicker</artifactId>
  <version>1.6.2</version>
  <type>pom</type>
</dependency>
```
or ivy:
```xml
<dependency org='io.ak1.pix' name='piximagepicker' rev='1.6.2'>
  <artifact name='pix' ext='pom' ></artifact>
</dependency>
```

#### Find docs for old versions in wiki [1.5.6](https://github.com/akshay2211/PixImagePicker/wiki/Documendation-ver-1.5.6) and [1.2.5](https://github.com/akshay2211/PixImagePicker/wiki/Documendation-ver-1.2.5)

## License
Licensed under the Apache License, Version 2.0, [click here for the full license](/LICENSE).

## Author & support
This project was created by [Akshay Sharma](https://akshay2211.github.io/).

> If you appreciate my work, consider buying me a cup of :coffee: to keep me recharged :metal: by [PayPal](https://www.paypal.me/akshay2211)

> I love using my work and I'm available for contract work. Freelancing helps to maintain and keep [my open source projects](https://github.com/akshay2211/) up to date!

[Glide]: <https://github.com/bumptech/glide>
[FastScroll]: <https://github.com/L4Digital/FastScroll>
[Header-decor]: <https://github.com/edubarr/header-decor>
[CameraX]: <https://developer.android.com/training/camerax>
[Coroutines]: <https://developer.android.com/kotlin/coroutines>
