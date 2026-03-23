# Frontend Dockerfile
FROM node:18-alpine as builder

WORKDIR /app

COPY frontend/package*.json ./

RUN npm ci

COPY frontend/src src/
COPY frontend/public public/

ENV REACT_APP_API_URL=http://localhost:8080/api

RUN npm run build

# Runtime
FROM nginx:alpine

COPY --from=builder /app/build /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
