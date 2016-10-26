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

package com.arsmentis.cordova.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Jdbc extends CordovaPlugin {
    private static final String TAG = "Jdbc";

    private Connection connection;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("connect".equals(action)) {
            String url = args.getString(0);
            String user = args.getString(1);
            String password = args.getString(2);

            try {
                connect(url, user, password);
                callbackContext.success();
            } catch (SQLException e) {
                callbackContext.error(e.toString());
            }

            return true;
        } else if ("disconnect".equals(action)) {
            try {
                disconnect();
                callbackContext.success();
            } catch (SQLException e) {
                callbackContext.error(e.toString());
            }

            return true;
        } else if ("execute".equals(action)) {
            String sql = args.getString(0);

            try {
                JSONArray results = execute(sql);
                callbackContext.success(results);
            } catch (SQLException e) {
                callbackContext.error(e.toString());
            } catch (JSONException e) {
                callbackContext.error(e.toString());
            }

            return true;
        } else if ("load".equals(action)) {
            String driver = args.getString(0);

            try {
                Class.forName(driver);
                callbackContext.success();
            } catch (ClassNotFoundException e) {
                callbackContext.error(e.toString());
            }

            return true;
        } else if ("isConnected".equals(action)) {
            try {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, isConnected()));
            } catch (SQLException e) {
                callbackContext.error(e.toString());
            }

            return true;
        }

        return false;
    }

    // close the stored connection if the app is stopped
    @Override
    public void onStop() {
        // not neccesary to call super because CordovaPlugin isn't an Activty
        try {
            disconnect();
        } catch (SQLException e) {
            Log.i(TAG, "A database error occurred while automatically closing the connection due to activity stop.", e);
        }
    }

    private void connect(String url, String user, String password) throws SQLException {
        disconnect();
        connection = DriverManager.getConnection(url, user, password);
    }

    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private JSONArray execute(String sql) throws SQLException, JSONException {
        if (connection == null) {
            throw new SQLException("Not connected");
        }

        JSONArray results = new JSONArray();
        Statement statement = connection.createStatement();

        if (statement.execute(sql)) {
            ResultSet resultSet = statement.getResultSet();
            ResultSetMetaData columns = resultSet.getMetaData();

            while (resultSet.next()) {
                JSONObject row = new JSONObject();

                for (int i = 1; i <= columns.getColumnCount(); i++) {
                    row.put(columns.getColumnName(i), resultSet.getObject(i));
                }
                results.put(row);
            }

            resultSet.close();
        }

        statement.close();

        return results;
    }

    private boolean isConnected() throws SQLException {
        if (connection != null) {
            return !connection.isClosed();
        }

        return false;
    }
}
