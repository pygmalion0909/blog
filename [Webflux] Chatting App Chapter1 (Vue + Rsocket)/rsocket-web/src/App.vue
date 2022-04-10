<template>
  <div>
    <h1>Chatting!!!##</h1>
    <input type="text" v-model="message">
    <button @click="send">전송</button>
    <ul>
      <li v-for="item in messages" :key="item.id">
        {{item}}
      </li>
    </ul>
  </div>
</template>

<script>
import {
  RSocketClient
  , JsonSerializer
  , IdentitySerializer
} from "rsocket-core";
import RSocketWebSocketClient from "rsocket-websocket-client";
import { EchoResponder } from "./responder";

export default {
  data() {
    return {
      message: "",
      socket: null,
      messages: [],
      responder: new EchoResponder(this.messageReceiver)
    }
  },
  created() {
    this.connect();
  },
  methods: {
    messageReceiver(payload) {
      this.messages.push(payload.data);
    },
    send() {
      // requestResponse
      this.socket.requestResponse({
        data: {
          username: "Superpil",
          message: this.message
        },
        metadata: String.fromCharCode('message'.length) + 'message',
      }).subscribe({
        onComplete: (com) => {
          console.log('com : ', com);
        },
        onError: error => {
          console.log(error);
        },
        onNext: payload => {
          console.log(payload.data);
        },
        onSubscribe: subscription => {
          console.log("subscription", subscription)
        },
      });
    },
    connect() {
      let client = new RSocketClient({
        serializers: {
          data: JsonSerializer,
          metadata: IdentitySerializer
        },
        setup: {
          // ms btw sending keepalive to server
          keepAlive: 60000,
          // ms timeout if no keepalive response
          lifetime: 180000,
          // format of `data`
          dataMimeType: 'application/json',
          // format of `metadata`
          metadataMimeType: 'message/x.rsocket.routing.v0',
        },
        responder: this.responder,
        transport: new RSocketWebSocketClient({ 
          url: 'ws://localhost:6565/rs' 
        }),
      });
      client.connect().subscribe({
        onComplete: socket => {
          this.socket = socket;
        },
        onError: error => {
          console.log(error);
        },
        onSubscribe: cancel => {
          console.log(cancel)
        }
      });

    },
  },
};
</script>