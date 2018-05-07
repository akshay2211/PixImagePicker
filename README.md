# Pix
Pix is a Whatsapp image picker replica. with this you can integrate a image picker just like whatsapp.

## Demo

![](media/media.gif)
![](media/one.png)

## Usage
 
```groovy
          Pix.start(Context,
                    RequestCode,
                    NumberOfImagesToSelect);  
```
or just 
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


## Download

 [ ![Download](https://api.bintray.com/packages/fxn769/android_projects/Pix/images/download.svg) ](https://bintray.com/fxn769/android_projects/Pix/_latestVersion)  or grab via Gradle:
```groovy
        compile 'com.fxn769:pix:1.0'
```
or Maven:
```xml
        <dependency>
          <groupId>com.fxn769</groupId>
          <artifactId>pix</artifactId>
          <version>1.0</version>
          <type>pom</type>
        </dependency>
```
or ivy:
```xml
        <dependency org='com.fxn769' name='pix' rev='2.0'>
          <artifact name='pix' ext='pom' ></artifact>
        </dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].



## License
Licensed under the Apache License, Version 2.0, [click here for the full license](/LICENSE.txt).

## Author & support
This project was created by [Akshay Sharma](https://akshay2211.github.io/).

> If you appreciate my work, consider buying me a cup of :coffee: to keep me recharged :metal: by [PayPal](https://www.paypal.me/akshay2211)

> I love using my work and I'm available for contract work. Freelancing helps to maintain and keep [my open source projects](https://github.com/akshay2211/) up to date!
