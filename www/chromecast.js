const cordova = require('cordova')

function ChromecastPlugin () {}

ChromecastPlugin.prototype.echo = function (msg, callback) {
  cordova.exec(callback, function (err) {
    callback(err)
  }, 'ChromecastPlugin', 'echo', [msg])
}

module.exports = new ChromecastPlugin()
