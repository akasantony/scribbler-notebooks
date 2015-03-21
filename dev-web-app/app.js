var id;
var title;
var description1;
var description2;
var category;
var logoImg = { fieldname: '',
     originalname: '',
     name: '',
     encoding: '',
     mimetype: '',
     path: '',
     extension: '',
     size: null,
     truncated: null,
     buffer: null };
var coverImg = { fieldname: '',
     originalname: '',
     name: '',
     encoding: '',
     mimetype: '',
     path: '',
     extension: '',
     size: null,
     truncated: null ,
     buffer: null};

var express = require('express');
var path = require('path');
var http = require('http');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var multer = require('multer');
var fs = require('fs');
var routes = require('./routes/index');
var users = require('./routes/users');

var app = express();

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'root',
  database : 'scribblernotebooks'
});

connection.connect();


//// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(multer({ dest: './temp/'}));

//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());

app.use(express.static(__dirname + '/public/'));
app.use(express.static(__dirname + '/images/'));
app.use('/users', users);

app.get('/', function(req,res) {
     res.sendFile(__dirname + '/public/index.html');
});


//Handle insert request from web-app
app.post('/add', function(req, res) {
    id = req.body.adid;
    title = req.body.title;
    category = req.body.category;
    description1 = req.body.description1;
    description2 = req.body.description2;
    coverImg = req.files.coverimage;
    logoImg = req.files.logoimage;
    
    
    
    fs.renameSync(__dirname + '/'+logoImg.path, __dirname + '/public/images/logo/'+id+'_logo'+'.png');
    
//    fs.unlink(__dirname + '/'+logoImg.path, function (err) {
//        if (err)
//            console.error(err);
//        else
//            console.log('successfully deleted cover image');
//        });
//    
    
    
    fs.renameSync(__dirname + '/'+coverImg.path, __dirname + '/public/images/cover/'+id+'_cover'+'.png');    
            
//    fs.unlink(__dirname + '/'+coverImg.path, function (err) {
//        if (err)
//            console.error(err);
//        else
//            console.log('successfully deleted logo image');
//        });
    
    logoImg.path = '/images/logo/'+id+'_logo'+'.png';
    coverImg.path = '/images/cover/'+id+'_cover'+'.png';
    console.log(logoImg.path);
    
    connection.query('INSERT INTO ad_data SET ?', {AdID : id,Title : title,Category : category,Desc1 : description1,Desc2: description2,logoPath : logoImg.path,coverPath : coverImg.path}, function(err, rows, fields) {
//        connection.end();
  if (!err)
    console.log('The solution is: ', rows);
  else
    console.log('Error while performing Query.',err);
});


});

//Handle get requests from mobile application
app.get('/deals',function(req, res){
    connection.query('SELECT AdID,Title,Category,Desc1,logoPath FROM ad_data', function(err, rows){
        console.log(rows);
    res.send(rows);
  });
});

app.get('/deal/:id',function(req, res){
    var ad_id = req.params.id;
    console.log("Param:"+ad_id);
    connection.query('SELECT AdID,Title,Category,Desc2,coverPath FROM ad_data WHERE AdID = ?',[ad_id], function(err, rows){
        res.send(rows);
        console.log(rows);
  });
});

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});




app.listen(3000);
module.exports = app;
