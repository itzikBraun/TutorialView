[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-TutorialView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1408)

[![Release](https://img.shields.io/github/release/itzikBraun/TutorialView.svg?label=JitPack)](https://jitpack.io/#itzikBraun/TutorialView)

TutorialView
===============
[![Join the chat at https://gitter.im/itzikBraun/TutorialView](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/itzikBraun/TutorialView?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![alt tag](http://raw.github.com/ItzikBraun/TutorialView/master/screen_shots/example.gif)

An Android library project providing `Activity` with an explanation about views in your app.

The `TutorialActivity` can be used as a walkthrough for the entire screen that is currently visible, Or just for one view on the screen.

To get the intent to start the `TutorialActivity` you need to use the `TutorialIntentBuilder`,
The builder will help you build the intent to start the activity, You would have to pass a `Tutorial` object to the intent builder.

If you use any kind of analytics to track your user behavior in your app you can start the `TutorialActivity` for result and
so when it returns you can collect the data whether the user skipped the tutorials or watched it fully.
You can also retrieve the amount of tutorials the user have viewed before skipping the walkthrough using `TutorialActivity.VIEWED_TUTORIALS`.

###Usage
####Tutorial
The tutorial object holds the tutorial info and attributes. You can create a Tutorial by using the `TutorialBuilder`.
You can customize the following:
* Title - Will appear on the top of the view, If the view that is surrounded is on top it will be shown below it.
* TutorialText - The explanation of about the view, It will appear above or below the view.
* BackgroundColor - The background color of the view.
* TutorialTextSize - The size that will be used for the tutorial explanation text.
* TypefaceName - The path to the wanted typeface to use for all text view in the tutorial, Example: "/fonts/arial.ttf".
* AnimationDuration - the duration time in milliseconds that will be used for the animation.
* InfoPosition - The position of the info text, This could be Above, Below, LeftOf and Right of all relevant to the view that need to be surrounded. Values are stored in `Tutorial.InfoPosition`
* GotItPosition - The position of the "GotIt" button, This could be Top(If has a title it will be below it) and Bottom. Values are stored in `Tutorial.GotItPosition`
* ~~AnimationType - the animation that will be used for showing and hiding the tutorial~~ This is a work in progress currently not working.

Each tutorial that was passed holds it's position on the screen, title, background color, the text explanation and more customizable attributes.
By default the "StatusBar" and the "NavigationBar" are also being colored on Lollipop devices, You can change the default behavior by using:
```
TutorialIntentBuilder builder = new TutorialIntentBuilder(MainActivity.this);

builder.changeSystemUiColor(false);
```       
######Example of creating a simple tutorial.

``` java
TutorialIntentBuilder builder = new TutorialIntentBuilder(MainActivity.this);
            
TutorialBuilder tBuilder = new TutorialBuilder();
            
tBuilder.setTitle("The Title")
            .setViewToSurround(v)
            .setInfoText("This is the explanation about the view.")
            .setBackgroundColor(randomColor())
            .setTutorialTextColor(Color.WHITE)
            .setTutorialTextTypeFaceName("fonts/test_name.ttf")
            .setTutorialTextSize(25)
            .setAnimationDuration(500);

builder.setTutorial(tBuilder.build());

startActivity(builder.getIntent());
            
// Override the default animation of the entering activity.
// This will allow the nice wrapping of the view by the tutorial activity.
overridePendingTransition(R.anim.dummy, R.anim.dummy);
```
######Example of creating a walk through from two Tutorials.

```java
ArrayList<Tutorial> tutorials = new ArrayList<>();
tutorials.add(tutorial);
tutorials.add(tutorial2);
tutorials.add(tutorial3);
tutorials.add(tutorial4);

builder.skipTutorialOnBackPressed(true);
 
builder.setWalkThroughList(tutorials);

// Starting the activity with an intent from the builder.
startActivity(builder.getIntent());
overridePendingTransition(R.anim.dummy, R.anim.dummy);
```

####Important!
You should override the `Activity` pending transition animation like this, If you wont override it the `TutorialActivity` would animate itself in and will ruin the view animation. (Call it after you call `startActivity(Intent)` ).


``` java

startActivity(builder.getIntent());
overridePendingTransition(R.anim.dummy, R.anim.dummy);

```

###INCLUDING IN YOUR PROJECT
######From Maven Central

Add as a dependency to your build.gradle:

````
dependencies{
    compile 'com.github.itzikbraun:TutorialView:0.8.1'
}
````

######Downloading the source code
Import the "tutorial_view" module to your project, 
Then in your build.gradle file add this.(You probably already have the *dependencies* so just add the "*compile project(':tutorial_view')*" ).
```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile project(':tutorial_view')
}
```

You can also copy past it to your project and before adding it to your dependencies you need to add it to *settings.gradle* so the system will understand it's a module like this:
```
// Notice that in your settings file there would be more names so don't delete them, Those are your other modules.
include':tutorial_view'
```

###TODO:
* add the roboto font family to the app assets so it will work on lower versions
* Make a sample app and publish it on Google Play

###Author
[Itzik Braun - Google+](https://plus.google.com/+ItzikBraunster)

###LICENSE
    Copyright (C) 2014  Itzik Braun

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
