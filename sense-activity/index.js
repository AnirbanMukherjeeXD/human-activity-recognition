const http = require('http');
//const {parse} = require('querystring');
const hostname = '0.0.0.0';
const port = process.env.PORT || 3000;

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/html');
  res.end('<h1>Hello Raj!</h1><br>Port: '+process.env.PORT);
});
// if (req.method === 'POST') {
//     var data = '';
//     req.on('data', function(chunk) {
//       data += chunk.toString();
	  
//     });

//     req.on('end', function() {
//       // parse the data
//       if(parseInt(parse(data)['field1'])>3){
// 		  console.log("Jumped!");
// 	  }else{
// 		  console.log('\033[2J');
// 	  }
//     });
//   }

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});
