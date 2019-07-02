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

ChromecastPlugin.prototype.getCastState = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'getCastState', [])
}

ChromecastPlugin.prototype.castBtnClick = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'castBtnClick', [])
}

ChromecastPlugin.prototype.loadRemoteMedia = function (url, streamType, contentType, position = 0, autoPlay = true, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'loadRemoteMedia', [url, streamType, contentType, position, autoPlay])
}

ChromecastPlugin.prototype.play = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'play', [])
}

ChromecastPlugin.prototype.pause = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'pause', [])
}

ChromecastPlugin.prototype.stop = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'stop', [])
}

ChromecastPlugin.prototype.endCurrentSession = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'endCurrentSession', [])
}

ChromecastPlugin.prototype.getPlayerState = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'getPlayerState', [])
}

module.exports = new ChromecastPlugin()
