angular.module('starter.services', [])

.factory('DB', function($q, $cordovaSQLite) {
    var self = this;
    self.db = null;

    self.init = function() {
        self.db = $cordovaSQLite.openDB( "museums.db", 1 );
    };

    self.query = function(query, bindings) {
        bindings = typeof bindings !== 'undefined' ? bindings : [];
        var deferred = $q.defer();

        self.db.transaction(function(transaction) {
            transaction.executeSql(query, bindings, function(transaction, result) {
                deferred.resolve(result);
            }, function(transaction, error) {
                deferred.reject(error);
            });
        });

        return deferred.promise;
    };

    self.fetchAll = function(result) {
        var output = [];

        for (var i = 0; i < result.rows.length; i++) {
            output.push(result.rows.item(i));
        }
        
        return output;
    };

    self.fetch = function(result) {
        return result.rows.item(0);
    };

    return self;
})

.factory('Museums', function(DB, $rootScope) {

    var currentMuseum;
    var opening = [];

    function openingTimes (opening, closing) {
    if (opening == 'closed') {
      return 'closed';
    }
    else {
      return opening + " - " + closing;
    }
  }
    
    return {
      all: function() {
        return DB.query('SELECT _id, MuseumName, MainPhoto FROM museums_info')
        .then(function(result){
            return DB.fetchAll(result);
        });  
      },
      getById: function(id) {
        return DB.query('SELECT * FROM museums_info WHERE _id = ?', [id])
        .then(function(result){
            currentMuseum = null;
            currentMuseum = DB.fetch(result);
            var addressArray = [currentMuseum.StreetAddress1, currentMuseum.StreetAddress2, currentMuseum.City, currentMuseum.County, currentMuseum.PostCode];
            var cleanedArray = addressArray.filter(function(v){return v!=='';});
            currentMuseum.address = cleanedArray.join(", ");
            //console.log(JSON.stringify(currentMuseum));
            $rootScope.$broadcast('museum:updated',currentMuseum);
            return currentMuseum;
        });
      },
      getCurrent: function() {
        return currentMuseum;
      },
      getHours: function() {
        //empty the array first
        opening.length = 0;
        opening.push(openingTimes (currentMuseum.Sunday_open, currentMuseum.Sunday_close));
        opening.push(openingTimes (currentMuseum.Monday_open, currentMuseum.Monday_close));
        opening.push(openingTimes (currentMuseum.Tuesday_open, currentMuseum.Tuesday_close));
        opening.push(openingTimes (currentMuseum.Wednesday_open, currentMuseum.Wednesday_close));
        opening.push(openingTimes (currentMuseum.Thursday_open, currentMuseum.Thursday_close));
        opening.push(openingTimes (currentMuseum.Friday_open, currentMuseum.Friday_close));
        opening.push(openingTimes (currentMuseum.Saturday_open, currentMuseum.Saturday_close));

        currentMuseum.opening = opening;
        return currentMuseum;
      },
      getId: function() {
        return currentMuseum._id;
      },
      getName: function() {
        return currentMuseum.MuseumName;
      },
      getLocation: function() {
        var location = [currentMuseum.Latitude, currentMuseum.Longitude];
        return location;
      }
    };
})

.factory('News', function($http, $q) {

  var cachedData;
  var cachedSingle;

  function getData(id, callback) {
    //store URL for the web service in the variable queryUrl
    var queryUrl = "ENTER WEB SERVICE URL HERE";
    //console.log("ID: " + id);
    if (id != 0) {
      queryUrl += "?museum_id=" + id;
    }    

    $http.get(queryUrl).success(function(data) {
      if (data.status == 200) {
        //console.log(JSON.stringify(data));
        if (id == 0) {
          cachedData = data.exhibitions;
          //console.log(JSON.stringify(cachedData));  
        }
        else {
          cachedSingle = data.exhibitions;
        }
        callback(data.exhibitions); 
      }
      
    })
    .error(function (data, status, headers, config) {
      
      var errMsg = "Sorry no what\'s on information found. Please note internet connection is required to fetch news.";
      var results = {};
      results.errorMsg = errMsg;
      callback(results);

    });
  }

  return {
    allNews: function(id,callback) {
      getData(id, callback);
    },
    findNews: function(id, referrer, callback) {
      console.log(id);
      var newsItem;
      console.log("Parent:" + referrer);
      if (referrer == "today") {
        console.log("today");
        newsItem = cachedData.filter(function(entry) {
        return entry.id == id;
      })[0]; 
      }
      else {
        newsItem = cachedSingle.filter(function(entry) {
        return entry.id == id;
      })[0];
      }
      
      callback(newsItem);
    },
    getSaved: function() {
        return cachedSingle;
      }
  };
  
})
.factory('Reviews', function($http, $q) {

  function Rating(id, callback) {
    if (id>1) {
      id = (id * 10) - 9;
    }
    //store URL for web service in variable
    var queryUrl = "ENTER WEB SERVICE URL HERE" + id;
    $http.get(queryUrl).success(function(data) {

      //get ratings and number of reviews

      //console.log(data);
      var parser = new DOMParser();
      var xmlDoc = parser.parseFromString(data,"text/xml");

      var numberReviews = xmlDoc.getElementsByTagName("NumberReviews")[0].childNodes[0].nodeValue;
      var totalRatings = xmlDoc.getElementsByTagName("TotalRatings")[0].childNodes[0].nodeValue;

      var rating = parseFloat(totalRatings / numberReviews);
      rating = Math.round(rating * 2) / 2;
      callback(rating);
      
    });
  }

  function fetchReviews(id,callback) {

    if (id>1) {
      id = (id * 10) - 9;
    }
    //store URL for web service in variable
    var queryUrl = "ENTER WEB SERVICE URL HERE" + id;
    var results = {};
    var reviews = [];
    var errMsg = "Sorry no reviews were found. Please note internet connection is required to view and post reviews.";
    
    $http.get(queryUrl).success(function (data, status, headers, config) {
      //console.log("DATA: " + data);

      var parser = new DOMParser();
      var xmlDoc = parser.parseFromString(data,"text/xml");
      var xmlReviews = xmlDoc.getElementsByTagName("review");

      if (xmlReviews.length < 1) {
        //no results
        results.status = 404;
        results.errorMsg = errMsg; 
      }

      else {

        //have results
        //console.log("REVIEWS: " + reviews);
        for (var i=0; i < xmlReviews.length; i++) {
          var review = {};
          review.Headline = xmlReviews[i].getElementsByTagName("Headline")[0].childNodes[0].nodeValue; 
          review.ReviewText = xmlReviews[i].getElementsByTagName("ReviewText")[0].childNodes[0].nodeValue;
          review.ReviewDate = xmlReviews[i].getElementsByTagName("Date")[0].childNodes[0].nodeValue;
          review.Rating = xmlReviews[i].getElementsByTagName("Rating")[0].childNodes[0].nodeValue;
          reviews.push(review); 
        }

        results.status = status;
        results.reviews = reviews;

      }
            
      callback(results);
    })
    .error(function (data, status, headers, config) {
      
      console.log("STATUS: " + status);
      console.log("DATA: " + data);
      results.status = status;
      results.errorMsg = errMsg;
      callback(results);

    });
  }

  function createXMLString(review) {
    var reviewDate = new Date().toISOString().slice(0,10);
    var xmlString = "<review xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.datacontract.org/2004/07/ApiReviews.Models\">\n";
    xmlString += "<Date>" + reviewDate + "T00:00:00</Date>\n";
    xmlString += "<Headline>" + review.headline + "</Headline>\n";
    xmlString += "<PlaceId>" + review.placeId + "</PlaceId>\n";
    xmlString += "<Rating>" + review.rating + "</Rating>\n";
    xmlString += "<ReviewText>" + review.reviewText + "</ReviewText>\n";
    xmlString += "<ReviewerId>200</ReviewerId>\n";
    xmlString += "</review>";

    console.log("CREATED: " + xmlString);
    return xmlString;
  }

  function postReview(review, callback) {

    var xmlBody = createXMLString(review);
    $http({
      method: 'POST',
      //enter URL for web service
      url: 'ENTER WEB SERVICE URL HERE',
      data: xmlBody,
      headers: {"Content-Type": 'application/xml'}
    }).success(function (data, status, headers, config) {
        console.log("STATUS: " + status);
        callback(status);
    })
    .error(function(data, status, headers, config) {
        console.log("STATUS: " + status);
        callback(status);
    });

  }

  return {
      getRating: function(id, callback) {
        Rating(id, callback);
      },
      getReviews: function(id,callback) {
        fetchReviews(id, callback);
      },
      sendReview: function(data, callback) {
        postReview(data, callback);
      }
  };
})
.factory('Directions', function($http, $cordovaGeolocation, $q) {
  //add web service url in place of dummy text
  var apiUrl = "ENTER WEB SERVICE URL HERE" + "?callback=JSON_CALLBACK";
  var errors = {};
  var savedDirections = {};
 
  function fetchDirections(currentlocation, start, destination, mode, callback) {

    processOrigin(currentlocation, start).then(function(data) {
      console.log(data);
      var origin = data;

            if (origin == "Could not get location") {
      //error condition
    }
    else {
      var queryUrl = apiUrl + "&origin=" + origin + "&destination=" + destination + "&mode=" + mode;
      queryUrl = encodeURI(queryUrl);
      //console.log(queryUrl);
      $http.jsonp(queryUrl).success(function (data, status, headers, config) {
        //console.log("DATA: " + JSON.stringify(data));             
        callback(data);
      })
      .error(function (data, status, headers, config) {
        
        //console.log("STATUS: " + status);
        //console.log("DATA: " + data);
        var results = {
          status: status
        };

        callback(results);

      });

    }

    });

  }

  function processOrigin(currentlocation, start) {
    var defer = $q.defer();

    var origin = "";
    if (currentlocation == "Yes") {
      //fetch location
      var options = {timeout: 10000, enableHighAccuracy: true};

      $cordovaGeolocation.getCurrentPosition(options).then(function(position){

        origin = position.coords.latitude.toString() + "," + position.coords.longitude.toString();
        defer.resolve(origin);
     
      }, function(error){
        console.log("Could not get location");
        origin = "Could not get location";
        defer.resolve(origin);

      });
    }
    else {
      origin = start;
      defer.resolve(origin);
    }
    return defer.promise;

  }


  return {
      getDirections: function(currentlocation, start, destination, mode, callback) {
        fetchDirections(currentlocation, start, destination, mode, callback);
      },
      setDirections: function(data) {
        savedDirections = data;
        console.log(savedDirections);
      },
      getSaved: function() {
        return savedDirections;
      }

  };
});
