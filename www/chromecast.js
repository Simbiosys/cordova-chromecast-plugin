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

module.exports = new ChromecastPlugin()
