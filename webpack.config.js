const path = require("path");
const ParallelUglifyPlugin = require('webpack-parallel-uglify-plugin');
module.exports = {
  mode: "development",
  devtool: "source-map",
  entry: ['@babel/polyfill', "./src/main/resources/ics/static/maptracking/js/index.js"],
  output: {
    path: __dirname + "/src/main/resources/ics/static/maptracking/js",
    filename: "testbundle.js"
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
          // drop_console: true
        }
      }
    })
  ],
  module: {
    rules: [
      // all files with a `.ts` or `.tsx` extension will be handled by `ts-loader`
      { test: /\.tsx?$/, loader: "ts-loader" },
      {
        test: /\.js$/,
        exclude: /node_modules/, 
         loader: "babel-loader"
      },
      {
        test: /\.scss$/,
        use: [
         {
          loader: "style-loader" // 将 JS 字符串生成为 style 节点
         },
         {
          loader: "css-loader" // 将 CSS 转化成 CommonJS 模块
         },
         {
          loader: "sass-loader" // 将 Sass 编译成 CSS
         }
        ]
       },
       {
        test: /\.(png|jpg|gif)$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 5000
            }
          }
        ]
      }
    ]
  }
};