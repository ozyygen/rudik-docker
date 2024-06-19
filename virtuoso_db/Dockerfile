# Dockerfile for virtuoso_db
FROM pkleef/virtuoso-opensource-7

# Install git
RUN apt-get update && apt-get install -y git

# Clone the repository
RUN git clone https://github.com/openlink/vos-docker-bulkload-example.git

# Copy the bulk load script to the container
COPY scripts/10-bulkload.sql /opt/virtuoso-opensource/database/10-bulkload.sql

# Ensure the script is executable
RUN chmod +x /opt/virtuoso-opensource/database/10-bulkload.sql

# Expose necessary ports
EXPOSE 1111 8890

# CMD to run the database server and keep the container running
CMD ["/bin/bash", "-c", "virtuoso-t +wait && tail -f /dev/null"]
