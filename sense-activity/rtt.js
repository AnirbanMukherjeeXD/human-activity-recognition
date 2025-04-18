
var http = require('http');
const hostname = '0.0.0.0';
const port = process.env.PORT || 3000;


const {parse} = require('querystring');

var count=0;

var st=0;
http.createServer((request, response)=> {
  
  if (request.method == 'POST') {
  console.log('POST');
  var d = new Date();var n = d.getTime()
  var body = ''
    request.on('data', function(data) {
      body += data;
	  st=parseInt(body.substr(body.indexOf("=") + 1));
	  //st=221;
	//  response.write('Ya');
	response.write('<br>Time 1 : '+st+'<br>');
      console.log(body);
	  var d = new Date();
		var n = d.getTime();
	  console.log('Received-time-'+n);
    })
  
  console.log(body);
  }
	
  response.setHeader("Access-Control-Allow-Origin", "*");
  response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  response.setHeader('Access-Control-Allow-Credentials', true);
  console.log("Running");
  response.statusCode = 200;
  response.setHeader('Content-Type', 'text/html');
  //response.end('<h1>Hello! Checking RTT </h1><br>Port: '+process.env.PORT);
  //console.log(request.url);
  
  response.statusCode = 200;
  response.setHeader('Content-Type', 'text/html');
  //response.write('<h1>Hello Raj!</h1><br>');
  response.write('Time 2 : <script>var d1='+n+';var d = new Date();var n = d.getTime();document.write(n+"<br>");</script>');
}).listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});

