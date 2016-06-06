/*
 * Copyright 2016 Ars Mentis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var exec = require('cordova/exec');

module.exports = {
  connect: function (url, user, password, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Jdbc", "connect",
         [url, user, password]);
  },

  disconnect: function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Jdbc", "disconnect", []);
  },

  execute: function (sql, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Jdbc", "execute", [sql]);
  },

  load: function (driver, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Jdbc", "load", [driver]);
  },

  isConnected: function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "Jdbc", "isConnected", []);
  }
}
