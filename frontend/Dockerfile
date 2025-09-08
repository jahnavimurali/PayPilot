# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies first for better caching
COPY package*.json ./
RUN npm ci --no-audit --no-fund

# Copy sources and build
COPY . .
RUN npm run build

# Serve with Nginx
FROM nginx:1.27-alpine
COPY --from=build /app/build /usr/share/nginx/html
# If your build output is in 'dist', change the path accordingly
# COPY --from=build /app/dist /usr/share/nginx/html

# Copy a default nginx config optimized for SPAs (fallback to index.html)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
