App42_Push_Unity_Lib
====================

# About Library Sample

1. This Library Sample project is used to get PushNotification in our Unity 3D android game.
2. This is used to generate a jar file with coreesponding android package name of your Unity android game.

# Building Project Files

1. First download this library project from [here] (https://github.com/shephertz/App42_Push_Unity_Lib/archive/master.zip)
2. Import Face-Book SDK project in you eclipse and make it library project.
3. Now You have to find you package name of Unity 3D game , to do this follow these steps.

```
A. Open Build Settings from File option (use Ctrl+Shift+B from keyboard).
B. Select Player Settings option.
C. Now player Settings popUp open than Select Other Settings option in it.
D. Change the Bundle Identifier with your game package name.
```
4.&nbsp; Chnage\Refactor the package name of android library project source with you game package name.<br/>
5.&nbsp; Also Replace all previous package name with your package name in AndroidManifest.xml file.<br/>
6.&nbsp; Add this library project into your sample android application.<br/>
7.&nbsp; Build your library project.<br/>
8.&nbsp; Copy App42PushService.jar and AndroidManifest.xml file from your bin folder of library Project.<br/>
9.&nbsp; Replace/paste it into Assets\plugins\Android your Unity Gaming Project.</br>
