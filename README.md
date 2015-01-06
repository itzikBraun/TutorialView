##TutorialView

An Android library project providing Activity with explenation about views in your app.

The TutorialActivity can be used as a walk through for the entire screen that is currently visible, Or just for one view on the screen.

To get the intent to start the TutorialActivity you need to use the TutorialIntentBuilder, The builder will help you build the intent to start the activity, You can set each paramater using the builder or pass to it a Toturial with all you customizations. (You can pass a listt of Tutorials to create a walkthrough).

Each tutorial that was passed holds it's position on screen, title, background color, the text explenation and more customizable attributes.

###Usage
######Example of creating a walk through from two Tutorials.

```java
// The Intent Builder
TutorialIntentBuilder builder = new TutorialIntentBuilder(this);

//Title is optional
Tutorial tutorial = new Tutorial(viewThatYouWantToSurround, "Title");
tutorial.setInfoText("Explanation on the view");
tutorial.setBackgroundColor(Color.BLACK);
Tutorial tutorial2 = new Tutorial(viewThatYouWantToSurround2, "Second Title");
tutorial2 .setInfoText("Explanation on the view");
tutorial2.setBackgroundColor(Color.BLUE);
 
ArrayList<Tutorial> tutorials = new ArrayList<>();
tutorials.add(tutorial);
tutorials.add(tutorial2);
 
builder.setWalkThroughList(tutorials);

// Starting the activity with an intent from the builder.
startActivity(builder.getIntent());
overridePendingTransition(R.anim.dummy, R.anim.dummy);
```

######Example of creating a simple tutorial.

``` java
TutorialIntentBuilder builder = new TutorialIntentBuilder(MainActivity.this);
Tutorial tutorial = new Tutorial(viewThatYouWantToSurround2, "Title");
tutorial.setInfoText("Explanation about the view");

// Background as white
tutorial.setBackgroundColor(Color.WHITE);

// All text will be black
tutorial.setTutorialTextColor(Color.BLACK);

builder.setTutorial(tutorial);

startActivity(builder.getIntent());
overridePendingTransition(R.anim.dummy, R.anim.dummy);
```

####Important!
You should override the activity pending transition aniamtion like this, If you wont override it the TutorialActivity would animate itself in and will ruin the view animation. (Call it after you call startActivity(Intent) ).


``` java

startActivity(builder.getIntent());
overridePendingTransition(R.anim.dummy, R.anim.dummy);

```

###INCLUDING IN YOUR PROJECT
Import the "tutorial_view" module to your project, Then in your build.gradle file add this.(You probably already have the *dependencies* so just add the "*compile project(':tutorial_view')*" ).
```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile project(':tutorial_view')
}
```

You can also copy past it to your project and before adding it to your dependencies you need to add it to *settings.gradle* so the system will understand it's a module like this:
```
// Notice that in your settings file there would be more names so dont delete them, This are your other modules.
include':tutorial_view'
```


###TODO:
* Return result when TutorialActivity finishes so you could know when it was done and if was skipped.

###Author
[Itzik Braun - Google+](https://plus.google.com/115008798239626950774/posts?hl=iw)

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