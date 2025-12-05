export default function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('karma-junit-reporter'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],

    client: {
      jasmine: {},
      clearContext: false
    },

    jasmineHtmlReporter: {
      suppressAll: true
    },

    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/buy01'),
      subdir: '.',
      reporters: [{ type: 'html' }, { type: 'text-summary' }]
    },

    junitReporter: {
      outputDir: 'reports/junit',
      outputFile: 'front-tests.xml',
      useBrowserName: false
    },

    reporters: ['progress', 'kjhtml', 'junit'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: false,

    // 🔥 IMPORTANT : Custom headless launcher for Jenkins
    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-gpu',
          '--disable-dev-shm-usage'
        ]
      }
    },

    // 🔥 Use our NoSandbox headless browser in CI ////
    browsers: ['ChromeHeadlessNoSandbox'],

    singleRun: true,
    restartOnFileChange: false
  });
};