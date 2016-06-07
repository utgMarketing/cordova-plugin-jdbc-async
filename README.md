# Cordova Plugin for JDBC

This plugin allows you to execute simple queries against any database with a JDBC driver using Android. Due to the requirement of adding a driver JAR, it's not compatible with automated build services (like PhoneGap Build).

## Preparing Your Project

Verify you've added the Android platform to your Cordova project using `cordova platform ls`. If Android isn't listed as installed, add it with `cordova platform add android --save`.

Add the following hook to your project's `config.xml`:

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

Make a `libs` folder in your project and add the driver JAR appropriate for your database. Add the plugin to your project with `cordova plugin add cordova-plugin-jdbc`. Finally, build your project with the command `cordova build`. Verify your driver JAR was copied to `platforms/android/libs`.

## Using the Plugin

Calls to this plugin live under `window.jdbc` or just `jdbc`. All `jdbc` calls have a `success` and `error` callback as the final two function parameters. If the `error` callback is triggered, the sole parameter will be a string containing an error message.

### `load(driver, success, error)`

Begin by loading your JDBC driver. Its documentation should reference the class name of the driver, which you'll pass as the first parameter. For instance, the PostgreSQL driver name is `org.postgresql.Driver`. You would load it like so:

```javascript
jdbc.load('org.postgresql.Driver', success, error);
```

If loading is successful, the `success` callback is triggered.

### `connect(url, user, password, success, error)`

Uses a driver specific JDBC url, user name, and password to connect to your database. For example:

```javascript
jdbc.connect('jdbc:postgresql://10.0.2.2/mydb', 'testuser', 'securepassword', success, error);
```

If the connection is successful, the `success` callback is triggered. Any existing connection is disconnected.

### `disconnect(success, error)`

Manually disconnect from the existing connection. Connections will be closed automatically when the app stops, but if you want to explicity close the connection, you can do so with the following code:

```
jdbc.disconnect(success, error);
```

Upon successful disconnection, the `success` callback is triggered.

### `isConnected(success, error)`

Test if the JDBC plugin is connected to a database. Useful if your app stops, then resumes and you need to reestablish a connection.

```javascript
jdbc.isConnected(success, error);
```

Upon completion, the `success` callback is triggered with a boolean parameter set to true if the plugin is connected, and false otherwise.

### `execute(sql, success, error)`

Execute arbitrary SQL against the connected database. For example:

```javascript
jdbc.execute('SELECT * FROM users', success, error)
```

Upon successful execution, the `success` callback will triggered with an array as its first parameter. If the statement produced results, each element of the array will contain an object with keys and values matching one row from the results. For example:

```json
[
  {
    "id": 1,
    "name": "james",
    "last_login": "2016-05-22"
  },
  {
    "id": 2,
    "name": "george",
    "last_login": "2016-06-01"
  }
]
```

## Warnings

Cordova apps are generally not difficult to decompile. This means that your database host, name, user, and password could easily be exposed by a knowledgable person if your app is public. If you use this plugin to access sensitive data, it's very important you restrict the rights of the database user so they can only perform the bare minimum of tasks needed for the app to function. You should assume that curious, or perhaps malicious people may connect to your database without using your app. Secure your data accordingly.

Additionally, the `execute` method has no protection against SQL injection. Be sure to sanitize your input appropriately for the underlying database.
