FROM nginx:latest

# Remove default config
RUN rm /etc/nginx/nginx.conf
RUN rm -rf /etc/nginx/conf.d/*

# Copy custom config
COPY nginx.conf /etc/nginx/nginx.conf
COPY conf.d/ /etc/nginx/conf.d/

# Expose port 80
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]
