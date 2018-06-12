if (typeof CP === 'undefined') {
  window.CP = {
    shouldStopExecution () { return false },
    exitedLoop() {},
  }
}