
var express = require('express');
var app = express();
var bodyParser = require('body-parser');
 
// 创建 application/x-www-form-urlencoded 编码解析
var urlencodedParser = bodyParser.urlencoded({ extended: false })
 
app.use(express.static('public'));
 
app.get('/index.html', function (req, res) {
   res.sendFile( __dirname + "/" + "index.html" );
   //res.write("abc");
})


var xyz;

app.get("/autocomplete",function(req,res){
		
		var url = "https://app.ticketmaster.com/discovery/v2/suggest?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&keyword="+req.query.keyword;
		console.log(url);
		 const request = require('request');
		 request( url , ( err, response, body ) => { 
		 
res.send(body);
} )

});


app.get('/here', urlencodedParser, function (req, res) 	{
 
   // 输出 JSON 格式
  // var response = {
      // "first_name":req.body.xml
      // "last_name":req.body.last_name
  // };
 

   console.log(req.query.keyword);
   console.log(req.query.category);
   console.log(req.query.distance);
   console.log(req.query.miles);
	console.log(req.query.lat);
	console.log(req.query.lon);
	var lat = req.query.lat;
	var lon = req.query.lon;
    var geohash = require('ngeohash');
var geopoint = geohash.encode(lat,lon);
 const request = require('request')
 //var url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&keyword="+req.query.keyword+"&segmentId="+req.query.category+"&radius="+req.query.distance+"&unit="+req.query.miles+"&geoPoint="+geopoint;
 //console.log(url);
console.log("di1");
	

//console.log(body);
//event(req.query.keyword,req.query.category,req.query.distance,req.query.miles,geopoint);
 var url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&keyword="+req.query.keyword+"&segmentId="+req.query.category+"&radius="+req.query.distance+"&unit="+req.query.miles+"&geoPoint="+geopoint+"&sort=date,asc";
 console.log("di2");
 console.log(url);

	request( url , ( err, response, body ) => { 
xyz = body;
//console.log(body);
//console.log("xyz   "+xyz);
res.send(xyz);
} )






})










	
		app.get('/else', urlencodedParser, function (req, res) {
 
   // 输出 JSON 格式
  // var response = {
      // "first_name":req.body.xml
      // "last_name":req.body.last_name
  // };
 //var lat;
 //var lon;

   console.log(req.query.keyword);
   console.log(req.query.category);
   console.log(req.query.distance);
   console.log(req.query.miles);
   console.log(req.query.where);
 
   const request = require('request')
	var url = "https://maps.googleapis.com/maps/api/geocode/json?address="+req.query.where+"&key=AIzaSyCk6r6eLf8FHBwjdwPKO0FyukeJhrIqYWA";
	console.log(url);
	request( url , ( err, response, body ) => { var x = body
	if (x != null)
	{
var xx = JSON.parse(x)
if (xx==null || xx.results==null || xx.results[0]==null || xx.results[0].geometry==null || xx.results[0].geometry.location==null || xx.results[0].geometry.location.lat==null)
{
	res.send("")
}
else
{
	lat = xx.results[0].geometry.location.lat;
lon = xx.results[0].geometry.location.lng;

console.log("lat:"+lat);
   console.log("lon:"+lon);
   var geohash = require('ngeohash');
var geopoint = geohash.encode(lat,lon);

console.log(geopoint);
console.log("di1");
//event(req.query.keyword,req.query.category,req.query.distance,req.query.miles,geopoint);

 var url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&keyword="+req.query.keyword+"&segmentId="+req.query.category+"&radius="+req.query.distance+"&unit="+req.query.miles+"&geoPoint="+geopoint+"&sort=date,asc";
 console.log("di2");
 console.log(url);

	request( url , ( err, response, body ) => { 
xyz = body;
//console.log(body);
res.send(xyz);

} )
	
}

	}
	else
	{
		res.send(body)
	}

} )
console.log("cao");

   //res.end(JSON.stringify(response));
})
	


/*
	
		app.get("/buttonClicked",function(req,res){
		
		var data = "send to client"
		//console.log("xyz   "+x);
		console.log("di3");
		//console.log("xyz"+xyz);
		res.send(xyz);
		res.end();
		
})
	*/	
	
app.get("/detail",function(req,res){
		
		//console.log("id:   "+req.query.id);
		 const request = require('request')
	url = "https://app.ticketmaster.com/discovery/v2/events?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&id="+req.query.id;
	console.log(url);
	request( url , ( err, response, body ) => { 

//console.log("1414");
res.send(body);

} )
})
app.get("/venue",function(req,res){
		
		console.log("venue:   "+req.query.venue);
		 const request = require('request')
		 url = "https://app.ticketmaster.com/discovery/v2/venues?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&keyword="+req.query.venue;
	console.log(url);
	request( url , ( err, response, body ) => { 

//console.log("1414");
res.send(body);

} )
})
app.get("/upcoming",function(req,res){
		
		//console.log("venue:   "+req.query.venue);
		 const request = require('request')
		 url = "https://api.songkick.com/api/3.0/search/venues.json?query="+req.query.venue+"&apikey=evie2liBj2o56LCa";
	console.log(url);
	
	request( url , ( err, response, body ) => { 

//console.log("1414");
if (body != null)
{
jsonObj = JSON.parse(body);

if (jsonObj==null || jsonObj.resultsPage==null ||jsonObj.resultsPage.results==null || jsonObj.resultsPage.results.venue==null ||jsonObj.resultsPage.results.venue[0]==null || jsonObj.resultsPage.results.venue[0].id==null)
{

	res.send("");
}
else
{
console.log("xxxx    "+jsonObj.resultsPage.results.venue[0].id);
const request = require('request')
		 url = "https://api.songkick.com/api/3.0/venues/"+jsonObj.resultsPage.results.venue[0].id+"/calendar.json?apikey=evie2liBj2o56LCa";
	console.log(url);
	
	request( url , ( err, response, body ) => { 

//console.log("1414");
res.send(body);

} )}
	}
	else
	{
			res.send(body);
	}
} )
})
app.get("/artist",function(req,res){
		
		//console.log("id:   "+req.query.id);
		// const request = require('request')
	
	//console.log(req.query.artist[0]);
	var SpotifyWebApi = require('spotify-web-api-node');

var clientId = '79e5fc6b99b447198197b9e537e00295',
  clientSecret = '8692fe8d553c4db5a4b997646d7f43f2';

// Create the api object with the credentials
var spotifyApi = new SpotifyWebApi({
  clientId: clientId,
  clientSecret: clientSecret
});



spotifyApi.clientCredentialsGrant().then(
  function(data) {
    console.log('The access token expires in ' + data.body['expires_in']);
    console.log('The access token is ' + data.body['access_token']);

    // Save the access token so that it's used in future calls
    spotifyApi.setAccessToken(data.body['access_token']);
	console.log("1");
	
	console.log("2");
console.log(req.query.artistming)
	spotifyApi.searchArtists(req.query.artistming)
  .then(function(data) {
    console.log('Search artists by "Love"', data.body);
	
	res.send(data.body);

  }, function(err) {
    console.error(err);
  });

  },
  function(err) {
    console.log('Something went wrong when retrieving an access token', err);
  }
);

})
app.get("/photo",function(req,res){
		
		console.log("id:   "+req.query.id);
		 const request = require('request')
		 key = "AIzaSyAx0oJAmo46GYK8rlxfwTV77spBPH_ukt4";
		 searchid= "005750293206203916456:7k73rwclcls";
	//url = "https://app.ticketmaster.com/discovery/v2/events?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&id="+req.query.id;
	
	url = "https://www.googleapis.com/customsearch/v1?q="+req.query.id+"&cx=005750293206203916456:7k73rwclcls&imgSize=huge&num=8&searchType=image&key=AIzaSyAx0oJAmo46GYK8rlxfwTV77spBPH_ukt4"
	console.log(url);
	request( url , ( err, response, body ) => { 

//console.log("1414");
res.send(body);

} )
})
/*
async.series([task1,task2],function(err,result){
 
		console.log("series");
 
		if (err) {
			console.log(err);
		}
 
		console.log(result);
	})*/
	/*
function event(a,b,c,d,e)
{

	 const request = require('request')
 var url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=95GRkZSv5Ofblx0izItwcF2wR8H9DuPl&keyword="+a+"&segmentId="+b+"&radius="+c+"&unit="+d+"&geoPoint="+e+"&sort=date,asc";
 console.log("di2");
 console.log(url);

	request( url , ( err, response, body ) => { 
xyz = body;
//console.log(body);


} )
*/
//sleep(1);
//console.log("cca  "+xyz);
//x1(xyz);


var server = app.listen(8081, function () {
 
  var host = server.address().address
  var port = server.address().port
	
  console.log("应用实例，访问地址为 http://%s:%s", host, port)
	
})
