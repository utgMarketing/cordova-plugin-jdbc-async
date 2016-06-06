# Cordova Plugin for JDBC

This plugin allows you to execute simple queries against any database with a JDBC driver using Android. Due to the requirement of adding a driver JAR, it's not compatible with automated build services (like PhoneGap Build).

## Preparing Your Project

Verify you've added the Android platform to your Cordova project using `cordova platform ls`. If Android isn't listed as installed, add it with `cordova platform add android --save`.

Add the following hook to your `config.xml`:

```xml
    <platform name="android">
        <hook type="after_prepare" src="scripts/copyDriver.js" />
    </platform>
```

If a `platform` tag for Android already exists, you can simply add the `hook` tag to the existing `platform` tag.

Make a `scripts` folder in your project, and add `copyDriver.js` to the folder with the following contents:

```javascript
var fs = require('fs');
var path = require('path');

module.exports = function(context) {
  var libsPath = path.join(context.opts.projectRoot, 'libs');
  var platformLibsPath = path.join(context.opts.projectRoot, 'platforms',
                                   'android', 'libs');
  var libs = fs.readdirSync(libsPath);

  libs.forEach(function (lib) {
    console.log('Copying libs/%s to platforms/android/libs...', lib);
    fs.createReadStream(path.join(libsPath, lib))
      .pipe(fs.createWriteStream(path.join(platformLibsPath, lib)));
  });
};
```

Make a `libs` folder in your project and add the driver JAR appropriate for your database. Add the plugin to your project with `cordova plugin add https://github.com/arsmentis/cordova-plugin-jdbc.git`

Finally, build your project with the command `cordova build`. Verify your driver JAR was copied to `platforms/android/libs`.

## Using the Plugin

Coming soob.
