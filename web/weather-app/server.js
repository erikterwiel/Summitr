
'use strict';
const express = require('express');
const bodyParser = require('body-parser');
const request = require('request');
const app = express()

const apiKey = 'ce7f3ce99ed7e8e556e3c0a3a51cb8d1';

app.use(express.static('public'));
app.use(bodyParser.urlencoded({ extended: true }));
app.set('view engine', 'ejs')

app.get('/', function (req, res) {
    res.render('index', {weather: null, error: null});
})

app.post('/', function (req, res) {
    let city = req.body.city;
    let url = `http://api.openweathermap.org/data/2.5/weather?q=${city}&units=imperial&appid=${apiKey}`

    request(url, function (err, response, body) {
        if(err){
            res.render('index', {weather: null, error: 'Error, please try again'});
        } else {
            let weather = JSON.parse(body)
            if(weather.main == undefined){
                res.render('index', {weather: null, error: 'Error, please try again'});
            } else {
                let weatherText = `It's ${weather.main.temp} degrees,
                    ${weather.main.humidity}% humidity,
                    the wind is blowing at ${weather.wind.speed} mph,
                    and it is ${weather.clouds.all}% cloudy
                    in ${weather.name}!`;

                // let weatherCond = 'There are ${weather.clouds.all} clouds in the sky in ${weather.name}!';
                // res.render('index', {weather: weatherCond, error: null});
                res.render('index', {weather: weatherText, error: null});
                
            }
        }
    });
})

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
})