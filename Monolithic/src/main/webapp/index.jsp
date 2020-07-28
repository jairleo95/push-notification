<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <title>Test Notify</title>
        <style type="text/css"> 
            input#chat {
                width: 410px
            }

            #console-container {
                width: 100%;
            }
            #console {
                border: 1px solid #CCCCCC;
                border-right-color: #999999;
                border-bottom-color: #999999;
                height: auto;
                overflow-y: scroll;
                padding: 5px;
                width: 100%;
            }

            #console p {
                padding:0;
                margin: 0;
            }
            #token_{
                color: blue;
            }
        </style>
    </head>
    <body>
        <div>
            Current User SessionId: <label id="token_" > </label> (Random value)
            <p><input type="text" placeholder="type a token user target" id="targetUser" />
                <input type="text" placeholder="type a title notify" id="title" />
                <input type="text" placeholder="type a content notify" id="chat" /></p>
            <div id="console-container"><div id="console"/></div>
        </div>
        <script type="application/javascript"> 
      function makeToken(){
              var text = "";
              var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

              for( var i=0; i < 5; i++ )
                  text += possible.charAt(Math.floor(Math.random() * possible.length));

              return text;
          }
          var sessionId=makeToken();
            document.getElementById('token_').innerHTML=sessionId;
               
      var Chat = {};
      Chat.socket = null;
      Chat.connect = (function(host) {
          if ('WebSocket' in window) {
              Chat.socket = new WebSocket(host);
          } else if ('MozWebSocket' in window) {
              Chat.socket = new MozWebSocket(host);
          } else {
              Console.log('Error: WebSocket is not supported by this browser.');
              return;
          }
          Chat.socket.onopen = function () {
              Console.log('Info: WebSocket connection opened.');
               var data ={
                            "sessionId": sessionId,
                            "action":"setUserAttributes"
                             };  
                   Chat.sendMessage(data);
              document.getElementById('chat').onkeydown = function(event) {
                  
                  if (event.keyCode  == 13) {
                       var data ={
                            "content":document.getElementById('chat').value,
                            "sessionId":document.getElementById('targetUser').value,
                            "title":document.getElementById('title').value,
                            "action":"notify"
                             };  
                      Chat.sendMessage(data);
                  }
              };
          };
          Chat.socket.onclose = function () {
              document.getElementById('chat').onkeydown = null;
              Console.log('Info: WebSocket closed.');
          };
          Chat.socket.onmessage = function (message) {
              Console.log(message.data);
          };
      });

      Chat.initialize = function() {
          if (window.location.protocol == 'http:') {
              Chat.connect('ws://' + window.location.host + '/Monolithic/notify');
          } else {
              Chat.connect('wss://' + window.location.host + '/Monolithic/notify');
          }
      };
      Chat.sendMessage = function(data) {
              Chat.socket.send(JSON.stringify(data));
              document.getElementById('chat').value = '';
      };
      var Console = {};
      Console.log = (function(message) {
          var console = document.getElementById('console');
          var p = document.createElement('p');
          p.style.wordWrap = 'break-word';
          p.innerHTML = message;
          console.appendChild(p);
          while (console.childNodes.length > 25) {
              console.removeChild(console.firstChild);
          }
          console.scrollTop = console.scrollHeight;
      });
      Chat.initialize();
        </script>
    </body>
</html>
