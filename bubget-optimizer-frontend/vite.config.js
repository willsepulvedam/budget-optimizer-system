import { defineConfig } from 'vite'

export default defineConfig({
  root: './src',     // 📂 indica que tu carpeta principal está dentro de src
  base: './',
  server: {
    port: 5173,
    host: true
  },
  build: {
    outDir: '../dist',
    assetsDir: 'assets',
    emptyOutDir: true
  }
})
