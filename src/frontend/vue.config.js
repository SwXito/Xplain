module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081',  // Adresse du serveur backend Quarkus
        changeOrigin: true,
        pathRewrite: { '^/api': '' },     // Supprimer le préfixe `/api`
      },
    },
  },
};
