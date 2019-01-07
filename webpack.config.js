const webpack = require('webpack');
const miniCssExtractPlugin = require("mini-css-extract-plugin");//用来抽离单独抽离css文件
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');//压缩css插件
module.exports = {
  // mode: "production",
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
    new miniCssExtractPlugin({
      filename: "index.css"
    }),//抽离出来以后的css文件名称
    new OptimizeCssAssetsPlugin()//执行压缩抽离出来的css
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
        test:/\.css/,
        exclude: /node_modules/, 
        use:[miniCssExtractPlugin.loader,"css-loader",{
            loader: "postcss-loader",
            options: {
                plugins: () => [require('autoprefixer')]
            }
        }]
      },
      {
        test:/\.scss$/,
        exclude: /node_modules/, 
        use:[miniCssExtractPlugin.loader,"css-loader",{
            loader: "postcss-loader",
            options: {
                plugins: () => [require('autoprefixer')]
            }
        },"sass-loader"]
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