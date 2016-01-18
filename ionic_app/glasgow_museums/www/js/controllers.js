angular.module('starter.controllers', [])

.controller('StartUpCtrl', function($scope, $ionicPlatform, $ionicLoading, $location, $ionicHistory, $cordovaSQLite, $state, DB) {
  
  $ionicHistory.nextViewOptions({
      disableAnimate: true,
      disableBack: true
  });
  $ionicPlatform.ready(function() {
      $ionicLoading.show({ template: 'Loading...' });
      if(window.cordova) {
          window.plugins.sqlDB.copy("museums.db", 0, function() {
              DB.init();
              $state.go('tab.home');
              $ionicLoading.hide();
          }, function(error) {
              console.error("Error copying the database: " + error);
              DB.init();
              $state.go('tab.home');
              $ionicLoading.hide();
          });
      } else {
          console.log("No SQLITE:");
          $state.go('tab.home');
          $ionicLoading.hide();
      }
  });  
})
.controller('HomeCtrl', function($scope, $state, Museums) {
  
  $scope.setMuseum = function(id) {
    Museums.getById(id).then(function(museum){
      $state.go('tabs.info');
    });
  };
  Museums.all().then(function(museums){
        $scope.museums = museums;
    });

})


.controller('TodayCtrl', function($scope, $state, $http, $ionicLoading, $ionicPlatform, News) {
  
  $ionicPlatform.ready(function() {

    $scope.news = {};
    $scope.parentView = "today";

    
    $scope.searchNews = function(id) {
      $ionicLoading.show({ template: 'Fetching what\'s on information...' });
      News.allNews(id, function(news) {

        if (!news.errorMsg) {
        $scope.news = news;
        $scope.dataError = null;
        $ionicLoading.hide();
      }
      else {
        $scope.dataError = news.errorMsg;
        $ionicLoading.hide();  
      }
      });
      
    };
    $scope.searchNews(0);   

  });
})

.controller('NewsDetailCtrl', function($scope, $ionicPlatform, $http, $stateParams, $cordovaSocialSharing, News) {

  $scope.share = function() {
    var message = $scope.newsItem.exhibition_name + " at " + $scope.newsItem.museum_name;
    var ExhibDate = document.getElementById('date-detail').innerHTML;
    message += " on " + ExhibDate + " (via Glasgow Museums app)";
    $cordovaSocialSharing.share(message);
  };

  $scope.$on('$ionicView.beforeEnter', function (event, viewData) {
    viewData.enableBack = true;
    console.log("param:" + $stateParams.parentView);
    News.findNews($stateParams.newsId, $stateParams.parentView, function(newsItem) {
    $scope.newsItem = newsItem;
  });
  });

})

.controller('HoursCtrl', function($scope, Museums) {
  $scope.$on('$ionicView.beforeEnter', function(e) {
    $scope.museum = Museums.getHours();
  });

})

.controller('InfoCtrl', function($scope, $ionicPlatform, $sce, Museums, Reviews) {

  $scope.displayRatings = function(numStars, item) {

    var i = 0;
    var full = "ion-android-star"; 
    var half = "ion-android-star-half";
    var outline = "ion-android-star-outline";
    var numHalfStars = 0;
    
    if (numStars % 1 != 0) {
      numStars -= 0.5;
      numHalfStars = 1;        
    }
    var numOutlineStars = 5 - numHalfStars - numStars;
    
    while (numStars > 0) {
      item[i].className = full + " rating-icon";
      ++i;
      --numStars;
    }
    if (numHalfStars == 1) {
      item[i].className = half + " rating-icon";
      ++i;
    } 
    if (i<5) {
      while (i < 5) {
        item[i].className = outline + " rating-icon";
        ++i;
      }  
    } 
    document.getElementById("rating-stars").style.opacity = "1";    

  };

  $scope.rating = function(myId) {

    Reviews.getRating(myId, function(rating) {
      $scope.currentRating = rating;
      $scope.displayRatings(rating, $scope.stars);

    });

  };

  $scope.$on('$ionicView.loaded', function(e) {
    $scope.museum = Museums.getCurrent();
    var desc = $scope.museum.Description;
    $scope.descript = $sce.trustAsHtml(desc);
    $scope.stars = document.getElementsByClassName("rating-icon");

  });

})



.controller('ReviewCtrl', function($scope, $stateParams, $state, $ionicPlatform, $ionicPopup, $ionicLoading, $ionicModal, Museums, Reviews) {
  $scope.$on('$ionicView.beforeEnter', function (event, viewData) {
    $scope.id = $stateParams.placeId;
    viewData.enableBack = true;
  });


  $scope.id = $stateParams.placeId;
  $scope.myreview = {};
  $scope.myreview.rating = 0;
  if ($scope.id == 1) {
    $scope.myreview.placeId = $scope.id;  
  }
  else {
    $scope.myreview.placeId = ($scope.id * 10) - 9;
  }
  
  $scope.title = Museums.getName();

  $scope.rateFunction = function(rating) {
      $scope.rating = rating;
    };

  $scope.searchReviews = function(id) {

    $ionicLoading.show({ template: 'Fetching reviews...' });
    Reviews.getReviews($scope.id, function(results) {


      if (results.status == 200) {
        // add reviews to scope
        $scope.reviews = results.reviews;
        $scope.dataError = null;
        $ionicLoading.hide();
      }
      else {
        $scope.dataError = results.errorMsg;
        $ionicLoading.hide();  
      }
      
    });
    
  };

  $scope.submitReview = function() {
    $ionicLoading.show({ template: 'Submitting review...' });
    $scope.errors = false;
    if ($scope.myreview.rating < 1) {
      $scope.errors = true;
    }
    if (!$scope.myreview.headline || !$scope.myreview.reviewText) {
      $scope.errors = true;
    }

    if ($scope.errors === true) {
      $ionicLoading.hide();
      // Alert errors
      $scope.showAlert("Error", "Please complete all form fields and try again.");

    }

    else {
      Reviews.sendReview($scope.myreview, function(status) {
        $ionicLoading.hide();
        if (status == 201) {
          //review successfully posted show confirmation
          $ionicPopup.alert({
            title: 'Review Submitted',
            template: 'Thank you for your review.'
          }).then(function(res) {
              $scope.myreview = null;
              $scope.myreview = {};
              $scope.closeModal();
              $scope.searchReviews($scope.id);
          });
          
        }
        else {
          //something went wrong show error
          $scope.showAlert("Error", "Sorry there was a problem submitting your review, please check your internet connection and try again.");
        }
      });

      
    }
  };

  $scope.showAlert = function(title, message) {
    $ionicPopup.alert({
        title: title,
        template: message
      }); 
  };

  $ionicModal.fromTemplateUrl('templates/review-modal.html', {
    scope: $scope,
    animation: 'slide-in-up'
  }).then(function(modal) {
    $scope.modal = modal;
  });  

  $scope.openModal = function() {
    $scope.modal.show();
  };

  $scope.closeModal = function() {
    $scope.modal.hide();
  };

  $scope.$on('$destroy', function() {
    $scope.modal.remove();
  });



  $scope.searchReviews($scope.id);

})

.controller('MapCtrl', function($scope, $state, $cordovaGeolocation, Museums) {


  $scope.$on('$ionicView.enter', function(e) {
    var latLng;
    var marker;
    $scope.loc = [];
    $scope.loc = Museums.getLocation();
    $scope.title = Museums.getName();
    latLng = new google.maps.LatLng(parseFloat($scope.loc[0]), parseFloat($scope.loc[1]));

    var mapOptions = {
        center: latLng,
        zoom: 15,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        mapTypeControl: false,
        streetViewControl: false
      };

    $scope.map = new google.maps.Map(document.getElementById("map"), mapOptions);

    //Wait until the map is loaded
    google.maps.event.addListenerOnce($scope.map, 'idle', function(){
     
      $scope.marker = new google.maps.Marker({
          title: 'museum',
          animation: google.maps.Animation.DROP,
          position: latLng

      }); 
      $scope.marker.setMap($scope.map);
       
      var infoWindow = new google.maps.InfoWindow({
          content: $scope.title
      });
     
      google.maps.event.addListener(marker, 'click', function () {
          infoWindow.open($scope.map, marker);
      });
      
     
    });

  });

})

.controller('NewsCtrl', function($scope, $ionicPlatform, $ionicLoading, Museums, News) {
$ionicPlatform.ready(function() {
    
    $scope.id = 0;
    console.log("ID" + $scope.id);
    $scope.news = {};
    $scope.newId;
    $scope.$on('$ionicView.beforeEnter', function(e) {
        $scope.title = Museums.getName();
        $scope.news = null;
        $scope.news = {};
        $scope.newId = parseInt(Museums.getId());
        console.log("NEW ID" + $scope.newId);
        if ($scope.id != $scope.newId) {
          $scope.id = $scope.newId;
          console.log("UPDATED" + $scope.id);
          $scope.searchNews($scope.id);

        }
        else {
          $scope.news = News.getSaved();
        }
        
      });

    $scope.searchNews = function(id) {
      $ionicLoading.show({ template: 'Fetching what\'s on information...' });
      News.allNews(id, function(news) {
        //console.log("GOT" + JSON.stringify(news));

        if (!news.errorMsg) {
        $scope.news = news;
        $scope.dataError = null;
        $ionicLoading.hide();
      }
      else {
        $scope.dataError = news.errorMsg;
        $ionicLoading.hide();  
      }
      });
      
    };
    
  });
})
.controller('DirectionsCtrl', function($scope, $state, $ionicLoading, $ionicPopup, $ionicNavBarDelegate, Directions, Museums) {
  $scope.museumName = Museums.getName();
  $scope.location = Museums.getLocation();
  $scope.destination = $scope.location[0] + "," + $scope.location[1];
  $scope.direction = {
    currentlocation: "Yes",
    origin: "",
    mode: "driving"
  };
  $scope.results = {};

  $scope.$on('$ionicView.beforeEnter', function (event, viewData) {
    viewData.enableBack = true;
  });

  $scope.goBack = function() {
    $ionicNavBarDelegate.back();
  };

  $scope.searchDirections = function() {
    $ionicLoading.show({ template: 'Fetching directions...' });
    Directions.getDirections($scope.direction.currentlocation, $scope.direction.origin, $scope.destination, $scope.direction.mode, function(data) {
        if (data.status == "OK") {
        Directions.setDirections(data); 
        $ionicLoading.hide();  
        $state.go('result');      
      }
      else if (data.status == "NOT_FOUND") {
        $ionicLoading.hide();
        $ionicPopup.alert({
           title: 'Error',
           template: 'Sorry no directions were found. Please check the text in the \'from\' field for errors'
         });
      }
      else {
        //show error
        $ionicLoading.hide();
        $ionicPopup.alert({
           title: 'Error',
           template: 'Sorry no directions were found. Please note internet connection is required to get directions.'
         });

      }

      });
  };

})
.controller('DirectionsResultCtrl', function($scope, $sce, Directions, Museums) {
  $scope.museumName = Museums.getName();
  $scope.DirectionData = Directions.getSaved();
  $scope.results = $scope.DirectionData.routes[0].legs[0];
  $scope.formatHTML = function(x){
    return $sce.trustAsHtml(x);
  };
});
