# CameraKit for Android

CameraKit is a library for communication with the TomTom Bandit Action Camera's media server. The library is written in Java and a detailed description of the camera media server API is available on the [Camera Media Server API](http://developer.tomtom.com/products/sports/cameramediaserver) pages. These pages contain not just the API specification but also more information about the video file, sensor data and tags format.

The library was created as a part of the TomTom Bandit mobile application project which is used as a mobile companion to [TomTom Bandit action camera](https://www.tomtom.com/en_gb/action-camera/action-camera/).

To get you started with the library quickly, there's an example project included which demos some of the basic features of the camera and the library.

We hope you'll have fun using it as much as we had creating it!


## Requirements

- Android API 16 and above
- Android Studio 2.1 and above

**NOTE:** In order to use the CameraKit you need a TomTom Bandit Action Camera with firmware version 1.57.500 or newer.


## 3rd Party dependency

The BanditCameraKit relies on 3rd party libraries for communication with the camera.

- [Retrofit](https://github.com/square/retrofit)
- [OkHttp](https://github.com/square/okhttp)
- [Gson](https://github.com/google/gson)
- [Guava](https://github.com/google/guava)
- [Android Support Annotations](http://tools.android.com/tech-docs/support-annotations)

All of them are licensed under the Apache 2.0 licence.


## Communication

- If you **need help**, use [Stack Overflow](http://stackoverflow.com/questions/tagged/banditcamera). (Tag 'banditcamera')
- If you'd like to **ask a general question**, use [Stack Overflow](http://stackoverflow.com/questions/tagged/banditcamera).
- If you've **found a bug** get back to us at <developersupport.camera@tomtom.com>


## Installation

### Manual install

If you prefer not to use Gradle dependencies, you can integrate CameraKit into your project manually. Just include aar file in your project library folder.

You can find all releases on our Github [Releases](https://github.com/tomtom-international/BanditCameraKit-Android/releases/) page.

### Gradle build file

Add jcenter() to repositories in your application module.

```gradle
repositories {
    jcenter()
}
```
And add it to your dependencies list.
```gradle
dependencies{
    compile 'com.tomtom.camera:camerakit:1.0.0'
}
```

## Usage

The BanditCameraKit covers all communication channels with the camera.

- REST
- Viewfinder stream
- Preview stream
- Backchannel notifications


### REST

The REST layer of the CameraKit uses [Retrofit](https://github.com/square/retrofit) for executing HTTP calls. It is used to check current camera state, control the camera and to fetch resources from the camera. Each resource is defined in the `com.tomtom.camera.api.model` package. By using our models you don't have to worry about serialisation and deserialisation of JSON responses. The complete list of HTTP calls is available on [Camera Media Server API](http://developer.tomtom.com/products/sports/cameramediaserver) pages.
In order to communicate with the camera's media server you have to create an instance of *CameraApi* either with default Factory method:

```java
CameraApi cameraApi = CameraApi.Factory.create();
```
or by providing `CameraApiVersion` :
```java
CameraApi.Version.getApiVersion(new CameraApiCallback<CameraApiVersion> cameraApiCallback {
    @Override
    public void success(CameraApiVersion cameraApiVersion) {
        CameraApi cameraApi = CameraApi.Factory.create(cameraApiVersion);
    }
    ...
```
*The camera's media server works on address ***192.168.1.101*** on port ***80***.*

You can find some examples on how to use the REST layer below:

#### Sample status call

```java
cameraApi.getCameraStatus(new CameraApiCallback<CameraStatus>{...});
```

#### Switching to video mode

```java
cameraApi.switchToVideoMode(Video.Mode.TIME_LAPSE, new CameraApiCallback<Settings> callback {...});
```

or with detailed settings:

```java
CameraVideoMode cameraVideoMode = new CameraVideoMode.Builder()
    .videoMode(Video.Mode.NORMAL)
    .framerate(Framerate.FPS_30)
    .resolution(Resolution.RES_FHD)
    .build();
cameraApi.setVideoMode(cameraVideoMode, new CameraApiCallback<Void> {...});
```

 A list of supported modes is described in the Capabilities section of the [Camera Media Server API](http://developer.tomtom.com/products/sports/cameramediaserver/capabilities) and it's available over *getRecordingCapabilities()* call.

#### Downloading video files

To download a file you need to define the path where you want to save the file. In addition to success and failure closures you can track download progress in progress closure.

```java
Response downloadVideoResponse = cameraApi.downloadVideo(videoId);
FileOutputStream fos = null;
InputStream is = null;
try {
    fos = new FileOutputStream(outputFile);
    is = response.getBody().in();
    BigDecimal length = BigDecimal.valueOf(response.getBody().length() > 0 ? response.getBody().length() : 0);
    for (retrofit.client.Header header : response.getHeaders()) {
        if (header.getName() != null && header.getName().equals(CONTENT_LENGTH)) {
            length = new BigDecimal(header.getValue());
            break;
        }
    }

    int nRead;
    byte[] data = new byte[4096];

    BigDecimal totalRead = BigDecimal.ZERO;
    while ((nRead = is.read(data, 0, data.length)) != -1) {
        fos.write(data, 0, nRead);
        totalRead = totalRead.add(BigDecimal.valueOf(nRead));
    }
} catch (IOException e) {
    // Handle exception
} finally {
    if (is != null) {
        try {
            is.close();
        } catch (IOException e) {
            
        }
    }

    if (fos != null) {
        try {
            fos.close();
        } catch (IOException e) {
            
        }
    }
}
```

### Viewfinder stream

The Viewfinder stream delivers JPGs at 30 frames per second. JPEG resolution is set to 768x432px. Images are delivered over UDP protocol. Together with the image, the camera sends the presentation timestamp (PTS) of each image. If recording is in progress the PTS matches the PTS of the frame in the recorded video. Otherwise PTS is **0**. The Viewfinder uses 4001 port as default port.

To receive images you need to implement *Viewfinder.OnImageReceivedListener* interface and start to listen with *viewfinder.start()*. The camera starts to send images over UDP as soon as the setViewfinderStatus(...) REST call is invoked.

```java
Viewfinder viewfinder = new Viewfinder();
viewfinder.setOnImageReceivedListener(new Viewfinder.OnImageReceivedListener() {
    @Override
    public void onImageReceived(float timeSecs, @Nullable byte[] image) {
        // Draw image on view
    }
});
viewfinder.start();
```

### Preview stream

The Preview stream receiver is used to start a client side server which receives video and audio frames from the camera for a requested video. Preview stream frames are delivered over TCP protocol which guarantees that no frames will be dropped. Each frame has its data and corresponding presentation timestamp.

*PreviewVideo* needs to be created and listening started. Example of the video preview can be found in the sample *app* module of project.

```java
// Given that you already have Video and VideoSurface implementation...
PreviewVideo previewVideo = new PreviewVideo(new SamplePreviewApiClient(), null, null);
previewVideo.preparePreview(video);
previewVideo.setVideoSurface(videoSurface);
mPreviewVideo.startPreview();
```

PreviewApiClient needs to call `previewVideo.oncameraPreviewStarted(true/false)` when start request was successful/failed and `previewVideo.onCameraPreviewStopped(true/false)` when stop request was successful/failed.

### Backchannel notifications

Backchannel is used to receive notifications about changes on the camera. For example, if a user presses the Record button on the camera you receive a *recording_started* notification. System alerts are also sent through the backchannel. For example, if the memory or SD card is full you receive a *memory_low* notification. Backchannel communication is performed over TCP protocol. Together with a notification identifier some additional information can be sent from the camera. For a detailed list of notifications take a look at [Backchannel notification](http://developer.tomtom.com/products/sports/cameramediaserver/backchannelnotifications) section of the [Camera Media Server API](http://developer.tomtom.com/products/sports/cameramediaserver) document.
To receive notifications you need to implement the *BackchannelNotificationCallback*, set it to listener and provide correct backchannel parser (use *BackchannelNotificationsParser.Creator* for it). The connection port is defined by the camera and it is defined as part of the *CameraStatus* model.

```java
BackchannelNotificationListener backchannelNotificationListener = new BackchannelNotificationListener();
backchannelNotificationListener.setBackchannelNotificationCallback(backChannelNotificationCallbackImpl);
backchannelNotificationListener.setBackchannelNotificationParser(BackchannelNotificationsParser.Creator.newInstance(cameraApiVersion));
backchannelNotificationListener.start(portFromCameraStatus);
```

### Limitations

While you are free to use all communication channels with the TomTom Bandit Action Camera there are some limitations that you should be aware of as follows:
- Only one viewfinder stream can be started. Trying to start a viewfinder stream while one is already active will result in an error.
- You can preview only one video at any given time.
- There is a limit of 5 concurrent connections to the camera. If you are, for example, downloading thumbnails for all videos make sure you do it in batches or sequentially. Starting too many REST calls at the same time will result in an error.


## Licence

Licensed under the Apache Licence, Version 2.0 (the "Licence");
you may not use this file except in compliance with the Licence.
You may obtain a copy of the Licence at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the Licence is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and
limitations under the Licence.

## Authors
The library was originally developed by Vladimir Bokan, Nebojsa Licina, Marko Savic, Ivana Vujovic and Miodrag Milosevic.

## Copyright (C) 2015-2016, TomTom International BV. All rights reserved.
----
