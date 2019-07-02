const cordova = require('cordova')
const exec = require('cordova/exec')
const channel = require('cordova/channel')

function ChromecastPlugin () {}

// Subscribe to native code events on plugin initialization
channel.onCordovaReady.subscribe(function () {
  // Send an 'exec' to native code
  exec(function (event) {
    cordova.fireDocumentEvent(event)
  }, function (e) {
    console.log('Error subscribing to cast session events')
  }, 'ChromecastPlugin', 'subscribeToSessionEvents', [])
})

ChromecastPlugin.prototype.castBtnClick = function (callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'castBtnClick', [])
}

ChromecastPlugin.prototype.loadRemoteMedia = function (url, streamType, contentType, position = 0, autoPlay = true, callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'loadRemoteMedia', [url, streamType, contentType, position, autoPlay])
}

ChromecastPlugin.prototype.play = function (callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'play', [])
}

ChromecastPlugin.prototype.pause = function (callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'pause', [])
}

ChromecastPlugin.prototype.stop = function (callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'stop', [])
}

ChromecastPlugin.prototype.endCurrentSession = function (callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'endCurrentSession', [])
}

ChromecastPlugin.prototype.getPlayerState = function (callback) {
  exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'getPlayerState', [])
}

module.exports = new ChromecastPlugin()
