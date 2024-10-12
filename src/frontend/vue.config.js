module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Le port de Quarkus
        changeOrigin: true
      }
    }
  }
};
