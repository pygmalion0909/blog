export class EchoResponder {
  constructor(callback) {
    this.callback = callback;
  }
  fireAndForget(payload) {
    this.callback(payload);
  }  
}