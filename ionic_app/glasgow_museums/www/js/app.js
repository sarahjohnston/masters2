// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'starter.directives', 'ngCordova'])

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
  });
})

.config(function($stateProvider, $urlRouterProvider) {

  $stateProvider

  .state('startup', {
    url: '/startup',
    templateUrl: 'templates/startup.html',
    controller: 'StartUpCtrl'
  })

  // setup an abstract state for the tabs directive
    .state('tab', {
    url: '/tab',
    abstract: true,
    templateUrl: 'templates/tab.html'
  })


  .state('tab.home', {
    url: '/',
    views: {
      'tab-home': {
        templateUrl: 'templates/tab-home.html',
        controller: 'HomeCtrl'
      }
    }
  })

  .state('tab.today', {
      url: '/today',
      views: {
        'tab-today': {
          templateUrl: 'templates/tab-today.html',
          controller: 'TodayCtrl'
        }
      }
    })

  .state('newsdetail', {
    url: '/newsdetails/:newsId/:parentView',
    templateUrl: 'templates/newsdetail.html',
    controller: 'NewsDetailCtrl'
  })
  
  .state('tabs', {
    url: '/tabs',
    abstract: true,
    templateUrl: 'templates/tabs.html'
  })

  .state('tabs.info', {
    url: '/info/:placeId',
    cache: false,
    views: {
      'tabs-info': {
        templateUrl: 'templates/tabs-info.html',
        controller: 'InfoCtrl'
      }
    }
  })

  .state('tabs.hours', {
    url: '/hours',
    views: {
      'tabs-hours': {
        templateUrl: 'templates/tabs-hours.html',
        controller: 'HoursCtrl'
      }
    }
  })

  .state('tabs.map', {
    url: '/map',
    views: {
      'tabs-map': {
        templateUrl: 'templates/tabs-map.html',
        controller: 'MapCtrl'
      }
    }
  })

  .state('directions', {
    url: '/directions',
    templateUrl: 'templates/tabs-directions.html',
    controller: 'DirectionsCtrl'
  })

  .state('result', {
    url: '/result',
    templateUrl: 'templates/tabs-directions-result.html',
    controller: 'DirectionsResultCtrl'

  })

  .state('tabs.news', {
    url: '/news',
    views: {
      'tabs-news': {
        templateUrl: 'templates/tabs-news.html',
        controller: 'NewsCtrl'
      }
    }
  })
  .state('news-details', {
    url: '/news-details/:newsId/:parentView',   
    templateUrl: 'templates/newsdetail.html',
    controller: 'NewsDetailCtrl'

  })

  .state('review', {
     url: '/review/:placeId',
     templateUrl: 'templates/review.html',
     controller: 'ReviewCtrl',
  });

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/startup');

});
