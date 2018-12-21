const path = require("path");
const ParallelUglifyPlugin = require('webpack-parallel-uglify-plugin');
module.exports = {
  mode: "development",
  devtool: "source-map",
  entry: "./src/main/resources/ics/static/maptracking/js/index.ts",
  output: {
    path: __dirname + "/src/main/resources/ics/static/maptracking/js",
    filename: "bundle.js"
  },
  resolve: {
    // Add `.ts` and `.tsx` as a resolvable extension.
    extensions: [".ts", ".tsx", ".js"]
  },
  plugins: [
    new ParallelUglifyPlugin({
      uglifyJS:{
        output: {
          comments: false,
          beautify: false
        },
        compress: {
          warnings: false,
          drop_debugger: true,
          drop_console: true
        }
      }
    })
  ],
  module: {
    rules: [
      // all files with a `.ts` or `.tsx` extension will be handled by `ts-loader`
      { test: /\.tsx?$/, loader: "ts-loader" }
    ]
  }
};