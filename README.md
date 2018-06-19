![](media/two.png)

# Pix   (WhatsApp Style Image Picker)

Pix is a Whatsapp image picker replica. with this you can integrate a image picker just like whatsapp.


[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PixImagePicker-blue.svg?style=flat-square)](https://android-arsenal.com/details/1/6935)
[![](https://jitpack.io/v/akshay2211/PixImagePicker.svg?style=flat-square)](https://jitpack.io/#akshay2211/PixImagePicker)
[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat-square)](https://android-arsenal.com/api?level=16)

## Demo

![](media/media.gif)
![](media/one.png)

## Usage
 
```groovy
          Pix.start(Context,                    //Activity or Fragment Instance
                    RequestCode,                //Request code for activity results
                    NumberOfImagesToSelect);    //Number of images to restict selection count
```
or just use
```groovy
          Pix.start(Context,
                    RequestCode);
```
for fetching only a single picture.

Use onActivityResult method to get results
```groovy
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == Activity.RESULT_OK && requestCode == RequestCode) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            }
        }
```
## Customise

include these items in colors.xml with custom color codes
```xml
    <resources>
        <color name="colorPrimaryPix">#075e54</color>
        <color name="colorPrimaryLightPix">#80075e54</color>
    </resources>
```

## Thanks to

  - [Glide]
  - [FastScroll]
  - [Header-decor]

## Download

 [ ![Download](https://api.bintray.com/packages/fxn769/android_projects/Pix/images/download.svg) ](https://bintray.com/fxn769/android_projects/Pix/_latestVersion)  or grab via Gradle:
```groovy
        compile 'com.fxn769:pix:1.0.7'
```
or Maven:
```xml
        <dependency>
          <groupId>com.fxn769</groupId>
          <artifactId>pix</artifactId>
          <version>1.0.7</version>
          <type>pom</type>
        </dependency>
```
or ivy:
```xml
        <dependency org='com.fxn769' name='pix' rev='1.0.7'>
          <artifact name='pix' ext='pom' ></artifact>
        </dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].



## License
Licensed under the Apache License, Version 2.0, [click here for the full license](/LICENSE).

## Author & support
This project was created by [Akshay Sharma](https://akshay2211.github.io/).

> If you appreciate my work, consider buying me a cup of :coffee: to keep me recharged :metal: by [PayPal](https://www.paypal.me/akshay2211)

> I love using my work and I'm available for contract work. Freelancing helps to maintain and keep [my open source projects](https://github.com/akshay2211/) up to date!

   [Glide]: <https://github.com/bumptech/glide>
   [FastScroll]: <https://github.com/L4Digital/FastScroll>
   [Header-decor]: <https://github.com/edubarr/header-decor>
