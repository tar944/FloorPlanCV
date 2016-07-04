# FloorPlanCV
Detection of corners in floor plans using OpenCV in Android.

-----
## Software used:
[**NVIDIA CodeWorks for Android 1R4**](https://developer.nvidia.com/codeworks-android), which includes the following:
- Android SDK r24.4.1
- Android APIs
- Android NDK32 and NDK64 r10e
- Android Build Tools r23.0.2
- Android Platform Tools r23.0.1
- Android Support Library r23.1.1
- Android Support Repository Lbrary r25
- Google USB Driver r11
- JDK 1.7.0_71
- Eclipse 4.4, CDT 8.2.0, ADT 24.0.2
- Apache Ant 1.8.2
- Gradle 2.2.1
Among other tools.

[**OpenCV**](http://opencv.org/) Version 2.4.8.2 included in NVIDIA CodeWorks for Android.

[**OpenCV Manager**](https://play.google.com/store/apps/details?id=org.opencv.engine&hl=es_419) is required. Latest version can be downloaded via Google Play.

-----
## Description
This application detects corners in floor plans using OpenCV. The project is located in the FloorPlanCV folder.

The application starts with the camera. For debug purposes, different menus are available: RGBA, Threshold, Canny, Lines, Corners.
- The RGBA Menu shows the camera without any processing.
- The Threshold Menu shows the image after an Adaptative Threshold filter. The values are inverted, because the lines need to be detected in white over black.
- The Canny Menu shows the image after a Canny filter, used for edge detection. There can be a problem, since each line has two edges.
- The Lines Menu shows what lines are detected using the algorithm HoughLinesP. These are estimated over the Threshold image.
- The Corners Menu is the same as the Lines, except that only the lines' end points are drawn. Ideally, There should be only one line estimated for each wall, and this line's end points would be the corners of the walls.

## Further investigation
To make this method more robust, the [Morphological Skeleton](https://en.wikipedia.org/wiki/Topological_skeleton) approach could work very well. An interesting method could be, for example, [Zhang-Suen](https://github.com/bsdnoobz/zhang-suen-thinning).

-----
## Example Images

<img src="https://cloud.githubusercontent.com/assets/14120807/16566970/dce74aa0-41ef-11e6-84aa-839e6be59a96.png" width="45%"></img> <img src="https://cloud.githubusercontent.com/assets/14120807/16566971/dd1903ce-41ef-11e6-9a91-87a6c02ca653.png" width="45%"></img> <img src="https://cloud.githubusercontent.com/assets/14120807/16566972/dd30dbd4-41ef-11e6-9b95-21ba822ee1a2.png" width="45%"></img> <img src="https://cloud.githubusercontent.com/assets/14120807/16566973/dd38ce20-41ef-11e6-9d46-858c6fdc6a8c.png" width="45%"></img> 