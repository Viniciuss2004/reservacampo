import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './test/setupTests.ts',
        css: false,
    },
    build: {
        outDir: '../resources/static',
        emptyOutDir: true,
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8080'
        }
    }
})
