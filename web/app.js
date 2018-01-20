var Couchbase = require("couchbase");
var Express = require("express");
var UUID = require("uuid");
var BodyParser = require("body-parser");
var Bcrypt = require("bcryptjs");

CREATE INDEX `blogbyuser` ON `default`(type, pid);

var app = Express();
var N1qlQuery = Couchbase.N1qlQuery;
 
app.use(BodyParser.json());
app.use(BodyParser.urlencoded({ extended: true }));
 
var cluster = new Couchbase.Cluster("couchbase://localhost");
var bucket = cluster.openBucket("default", "");
 
var server = app.listen(3000, () => {
    console.log("Listening on port " + server.address().port + "...");
});