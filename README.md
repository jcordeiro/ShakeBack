# ShakeBack

ShakeBack is an Android library that you can add to your app to make it easy for your users to send you emails with their feedback by simply shaking their device.

SCREENSHOT WILL GO HERE



### Download

##### Gradle

```
repositories {
  maven {
    url "https://jitpack.io"
  }
}

dependencies {
  compile'com.github.jcordeiro:ShakeBack:1.0'
}
```

##### Maven
```
<dependency>
    <groupId>com.github.User</groupId>
    <artifactId>Repo name</artifactId>
    <version>Release tag</version>
</dependency>

<dependency>
    <groupId>com.github.jcordeiro</groupId>
    <artifactId>ShakeBack</artifactId>
    <version>1.0</version>
</dependency>
```


### Usage

There are 2 ways to use the library. The first (and recommended) way to is to have your activities extend the `ShakeBackActivity` class and then calling `ShakeBack.Initialize(context, emailAddress, emailSubjectLine)`

```
public class MainActivity extends ShakeBackActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ShakeBack.initialize(this, "github@joncordeiro.com, "App feedback");
  }
```

<br />
If you don't wish to extend `ShakeBackActivity` you can still call `ShakeBack.initialize(context, emailAddress, emailSubjectLine)` like normal. However you **must** ensure you call `ShakeBack.activate()` and `ShakeBack.deactivate()` in your activity's `onResume()` and `onPause()` methods.

```  
@Override
protected void onResume() {
  super.onResume();
  ShakeBack.activate();
}

@Override
protected void onPause() {
  super.onPause();
  ShakeBack.deactivate();
}
```

By activating and deactivating this helps save device resources (*CPU, battery, etc*)

<br />

### Options
There are many options and settings available to customize the alert dialog and modify the library behavior


| Method                        | Usage                                                                                                 | Default Value                                                                                                                                                                  |
| ----------------------------- | ------------------- | ------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `setVibrationEnabled(boolean enabled)`              | Whether or not the phone vibrates when the alert dialog appears                 | false                                                                                                                                                                          |
| `setVibrationDuration(int duration)`                | The duration of the vibration (in milliseconds)                                 | 250                                                                                                                                                                            |
| `setShakeSensitivity(ShakeSensitivity sensitivity)` | The level of sensitivty for the device to respond the the user's shake gestures | `ShakeSensitivty.NORMAL`                                                                                                                                                       |
| `setEmailAddress(String emailAddress)`              | The email address the feedback emails will be sent to                           | Blank                                                                                                                                                                          |
| `setEmailSubject(String emailSubject)`              | The subject line of the feedback emails                                         | "Feedback: " + application package name                                                                                                                                        |
| `setDialogIcon(int drawableResId)`                  | The drawable resource used for the icon at the top of the alert dialog          | [R.drawable.ic_phone_shake](https://github.com/jcordeiro/ShakeBack/blob/master/library/src/main/res/drawable/ic_phone_shake.png)                                               |
| `setDialogTitle(String title)`                      | The title to be displayed at the top of the alert dialog                        | "Send feedback to developer?"                                                                                                                                                  |
| `setDialogMessage(String message)`                  | The message to be displayed in the alert dialog                                 |"By shaking the device you can send us some feedback and your thoughts about our app. Your feedback helps us improve the app and continue building a better experience for you" |

To customize ShakeBack, after calling `ShakeBack.initialize` just chain the method calls together.


```
ShakeBack.initialize(this, "contact@joncordeiro.com", "Feedback")
    .setVibrationEnabled(true)
    .setVibrationDuration(600)
    .setDialogIcon(R.drawable.ic_launcher)
    .setDialogTitle("Title here!")
    .setDialogMessage("MESSAGE IS HERE!")
    .setShakeSensitivity(ShakeBack.ShakeSensitivity.EASY);
```


### ShakeSensitivity
There are three values you can use to set ShakeBack's shake sensitivty:

`ShakeSensitivty.EASY` == Twice as easy to trigger a shake event as `NORMAL`

`ShakeSensitivty.NORMAL` == The device's default shake sensitivity

`ShakeSensitivty.HARD` == Twice as hard to trigger a shake event as `NORMAL`


### Vibration
If you choose to enable the vibration option by calling `setVibrationEnabled(true)`
You **must** make sure you include the proper permission in your Android manifest

`<uses-permission android:name="android.permission.VIBRATE"/>`

### Contributing & Feedback
If you would like to help contribute to the library please feel free to fork it and send me pull requests, or open up issues with any bugs you may find.

Also, please feel free to [send me an email](mailto:github@joncordeiro.com) or [contact me on Twitter](https://twitter.com/joncordeiro) if you have any suggestions or feedback. Thanks!



### LICENSE
This source code is released under the GNU General Public License v2.0. Please check [LICENSE](http://www.github.com/jcordeiro/ShakeBack/LICENSE) for more details.

```
Copyright (C) 2015  Jon Cordeiro

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
```
