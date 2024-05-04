module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: [
    "plugin:vue/vue3-essential",
    "eslint:recommended",
    "@vue/typescript/recommended",
    "plugin:prettier/recommended",
    // "prettier"
  ],
  parserOptions: {
    ecmaVersion: 2020,
  },
  rules: {
    // "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
    // "no-debugger": process.env.NODE_ENV === "production" ? "warn" : "off",
    "prettier/prettier": "off",//禁用掉相关的格式规则  效果和在vue.config.js里面添加 lintOnSave:false,关闭eslint检测，是一样的。
  },
};
