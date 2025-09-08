FROM nginx:latest

# Remove default config
RUN rm /etc/nginx/nginx.conf

# Copy custom config
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]
