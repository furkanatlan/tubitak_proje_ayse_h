import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// /api ile başlayan istekler backend'e yönlendirilir (CORS sorunu yaşanmaz).
// Docker içinde BACKEND_URL=http://backend:8080, bilgisayarda doğrudan çalıştırırken localhost:18080.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': process.env.BACKEND_URL || 'http://localhost:18080',
    },
    watch: { usePolling: true }, // Windows + Docker bind mount'ta dosya değişikliğini yakalamak için
  },
})
