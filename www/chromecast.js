const cordova = require('cordova')
const exec = require('cordova/exec')
const channel = require('cordova/channel')

function ChromecastPlugin () {
  this.streamTypes = {
    STREAM_TYPE_INVALID: -1,
    STREAM_TYPE_NONE: 0,
    STREAM_TYPE_BUFFERED: 1,
    STREAM_TYPE_LIVE: 2
  }
  this.castStates = {
    NO_DEVICES_AVAILABLE: 1,
    NOT_CONNECTED: 2,
    CONNECTING: 3,
    CONNECTED: 4
  }
  this.playerStates = {
    STOPPED: 1,
    PLAYING: 2,
    PAUSED: 3
  }
  this.repeatModes = {
    REPEAT_MODE_REPEAT_OFF: 0,
    REPEAT_MODE_REPEAT_ALL: 1,
    REPEAT_MODE_REPEAT_SINGLE: 2,
    REPEAT_MODE_REPEAT_ALL_AND_SHUFFLE: 3
  }
}

// Subscribe to native code events on plugin initialization
channel.onCordovaReady.subscribe(function () {
  // Send an 'exec' to native code
  exec(function (event) {
    cordova.fireDocumentEvent(event)
  }, function (e) {
    console.log('Error subscribing to cast session events')
  }, 'ChromecastPlugin', 'subscribeToSessionEvents', [])
})

ChromecastPlugin.prototype.getStreamTypes = function () {
  return this.streamTypes
}

ChromecastPlugin.prototype.getCastStates = function () {
  return this.castStates
}

ChromecastPlugin.prototype.getPlayerStates = function () {
  return this.playerStates
}

ChromecastPlugin.prototype.getRepeatModes = function () {
  return this.repeatModes
}

ChromecastPlugin.prototype.getCastState = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'getCastState', [])
}

ChromecastPlugin.prototype.getCastDeviceFriendlyName = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'getCastDeviceFriendlyName', [])
}

ChromecastPlugin.prototype.castBtnClick = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'castBtnClick', [])
}

ChromecastPlugin.prototype.loadRemoteMedia = function (url, streamType, contentType, position = 0, autoPlay = true, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'loadRemoteMedia', [url, streamType, contentType, position, autoPlay])
}

ChromecastPlugin.prototype.loadRemoteMediaQueue = function (urls, streamType, contentType, startIndex, repeatMode, position, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'loadRemoteMediaQueue', [urls, streamType, contentType, startIndex, repeatMode, position])
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

ChromecastPlugin.prototype.stepForward = function (step, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'stepForward', [step])
}

ChromecastPlugin.prototype.stepBackward = function (step, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'stepBackward', [step])
}

ChromecastPlugin.prototype.forwards = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'forwards', [])
}

ChromecastPlugin.prototype.backwards = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'backwards', [])
}

ChromecastPlugin.prototype.endCurrentSession = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'endCurrentSession', [])
}

ChromecastPlugin.prototype.getPlayerState = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'ChromecastPlugin', 'getPlayerState', [])
}

module.exports = new ChromecastPlugin()
